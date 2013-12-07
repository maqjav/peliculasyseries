package es.pys.storage.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import net.java.dev.jaxb.array.LongArray;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.pmstation.shared.soap.api.AccountItem;
import com.pmstation.shared.soap.api.AccountItemArray;
import com.pmstation.shared.soap.api.ApiException;
import com.pmstation.shared.soap.api.DesktopAppJax2;
import com.pmstation.shared.soap.api.DesktopAppJax2Service;

import es.pys.model.Fichero;
import es.pys.storage.FolderType;
import es.pys.storage.Storage;
import es.pys.storage.exceptions.StorageException;
import es.pys.storage.factory.StorageFactory;
import es.pys.storage.factory.StorageType;

@Configuration
@ComponentScan("es.pys.storage.services")
public class _4SyncStorage extends BaseStorage implements Storage {

	@Value("${api.username}")
	private final String usuario = null;

	@Value("${api.password}")
	private final String password = null;

	private HashMap<String, Long> carpetasIds = null;

	@Autowired
	private StorageFactory storageFactory;

	@Override
	public List<String> saveFile(File file) throws StorageException {
		if (login()) {
			List<String> listado = super.saveFile(file);

			// Si no tenemos los identificadores de las carpetas es necesasrio
			// obtenerlas
			if (carpetasIds == null) {
				carpetasIds = loadFolderIds();
			}

			if (carpetasIds == null || carpetasIds.isEmpty() || carpetasIds.size() == 1)
				return null;
			else {
				// Subimos los ficheros de ambas carpetas descomprimidas
				uploadFilesToFolder(listado, listadoFolder);
				uploadFilesToFolder(listado, fichaFolder);

				return listado;
			}
		}

		log.error("No se ha podido realizar el login");
		return null;
	}

	@Override
	public List<String> saveFile(InputStream in) throws StorageException {
		return super.saveFile(in);
	}

	@Override
	public List<String> saveFile(String path) throws StorageException {
		if (login()) {
			return super.saveFile(path);
		}

		return null;
	}

	@Override
	public byte[] loadFile(String fileName, String folderName) throws StorageException {
		// Intentamos devolverlo del disco duro
		// Es posible que exista por problemas de sincronismo con el disco
		try {
			return loadLocalFile(fileName, folderName);
		}
		catch (StorageException e1) {
			DesktopAppJax2 da = new DesktopAppJax2Service().getDesktopAppJax2Port();
			try {
				// Obtenemos el identificador de la imagen que queremos
				Fichero fichero = Fichero.findFicheroByNombre(fileName);
				Long fileId = null;

				if (getFolderType(folderName).equals(FolderType.FICHA))
					fileId = fichero.get_4syncIdF();
				else
					fileId = fichero.get_4syncIdL();

				// En caso de no haber encontrado ningun fichero sincronizamos
				// con otro storage
				if (fileId == null) {
					log.debug("Se va a procesar a sincronizar 4Sync con otro Storage para obtener el ID: " + fileId);
					syncStorage(fichero, folderName);
					FileInputStream fileInputStream;
					if (getFolderType(folderName).equals(FolderType.FICHA))
						fileInputStream = new FileInputStream(new File(getImagesFichaPath(fileName)));
					else
						fileInputStream = new FileInputStream(new File(getImagesListadoPath(fileName)));

					return IOUtils.toByteArray(fileInputStream);
				} else {
					log.debug("Se va a procesar a obtener de 4Sync el ID: " + fileId);
					LongArray fileIds = new LongArray();
					fileIds.getItem().add(fileId);

					AccountItemArray items = da.getFiles(usuario, password, fileIds);

					// Obtenemos el identificador de la imagen que queremos
					AccountItem imagen = null;
					if (items.getItem().isEmpty())
						throw new Exception("No se ha podido recuperar la imagen con el identificador dado: " + fileId);
					else
						imagen = items.getItem().get(0);

					if (imagen != null) {
						String link = da.getDirectLink(usuario, password, imagen.getDownloadLink());
						if (!StringUtils.isEmpty(link) && link.startsWith("http")) {
							URL url = new URL(link);
							log.info("Se ha descargado el fichero " + fileName + " del directorio: " + folderName);

							InputStream is = url.openStream();
							byte[] image;
							try {
								image = IOUtils.toByteArray(is);
							} finally {
								is.close();
							}

							saveLocalFile(fileName, folderName, image);

							return image;
						}
					}
				}
			}
			catch (ApiException e) {
				log.error("No se ha podido descargar el fichero " + fileName + " del directorio: " + folderName, e);
			}
			catch (MalformedURLException e) {
				log.error("No se ha podido descargar el fichero " + fileName + " del directorio: " + folderName, e);
			}
			catch (IOException e) {
				log.error("No se ha podido descargar el fichero: " + fileName + " del directorio: " + folderName, e);
			}
			catch (Exception e) {
				log.error("No se ha podido descargar el fichero: " + fileName + " del directorio: " + folderName, e);
			}

			try {
				return loadErrorImg();
			}
			catch (IOException e) {
				log.error("No se ha podido cargar la imagen de error", e);
				return null;
			}
		}
	}

	private void syncStorage(Fichero fichero, String folderName) throws StorageException {
		// Extraemos los directorios
		if (carpetasIds == null || carpetasIds.isEmpty())
			carpetasIds = loadFolderIds();

		// Descargamos el fichero de otro Storage
		if (getFolderType(folderName).equals(FolderType.FICHA) && fichero.getGoogleDriveIdF() != null) {
			storageFactory.getStorage(StorageType.DRIVE).loadFile(fichero.getNombreFichero(), folderName);
			uploadFile(new File(getImagesFichaPath(fichero.getNombreFichero())), carpetasIds.get(folderName),
					FolderType.FICHA);
		} else if (getFolderType(folderName).equals(FolderType.LISTADO) && fichero.getGoogleDriveIdL() != null) {
			storageFactory.getStorage(StorageType.DRIVE).loadFile(fichero.getNombreFichero(), folderName);
			uploadFile(new File(getImagesListadoPath(fichero.getNombreFichero())), carpetasIds.get(folderName),
					FolderType.LISTADO);
		} else
			throw new StorageException(
					"No se ha encontrado el fichero en otro Storage por lo que no se ha podido sincronizar");
	}

	/**
	 * Realiza el login con 4sync
	 * 
	 * @return
	 */
	private boolean login() {
		DesktopAppJax2 da = new DesktopAppJax2Service().getDesktopAppJax2Port();
		try {
			String res = da.login(usuario, password);

			if (StringUtils.isEmpty(res)) {
				log.info("Se ha realizado el login correctamente");
			} else {
				log.info("No se ha podido realizar el login: " + res);
			}

			boolean res1 = da.isAccountActive(usuario, password);
			if (res1)
				return true;
			else
				return false;
		}
		catch (ApiException e) {
			log.error("Se ha producido un error al hacer el login", e);
			return false;
		}
	}

	/**
	 * Sube un fichero a un directorio
	 * 
	 * @param file
	 *            Fichero
	 * @param folderId
	 *            Identificador del directorio
	 * @param folderBdId
	 *            Identificador del directorio en la base de datos
	 * @return
	 */
	private boolean uploadFile(final File file, Long folderId, FolderType folderType) {
		DesktopAppJax2 da = new DesktopAppJax2Service().getDesktopAppJax2Port();

		String fileName = file.getName();
		// Verificamos si se pueden subir ficheros
		if (!da.hasRightUpload()) {
			log.error("Actualmente no se pueden subir ficheros");
			return false;
		}

		try {
			long cloudId = da.uploadStartFile(usuario, password, folderId, fileName, file.length());
			if (cloudId < 0) {
				log.error("No se ha podido crear el identificador para el fichero.");
				return false;
			}

			int dataCenterId = (int) da.getNewFileDataCenter(usuario, password);
			String sesionKey = da.createUploadSessionKey(usuario, password, -1);
			String upload = da.getUploadFormUrl(dataCenterId, sesionKey);

			InputStream in = new FileInputStream(file);

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(upload);
			MultipartEntity me = new MultipartEntity();
			StringBody rfid = new StringBody("" + cloudId);
			StringBody rfb = new StringBody("" + 0);
			InputStreamBody isb = new InputStreamBody(in, fileName) {

				@Override
				public long getContentLength() {
					return file.length();
				}
			};
			me.addPart("resumableFileId", rfid);
			me.addPart("resumableFirstByte", rfb);
			me.addPart("FilePart", isb);

			post.setEntity(me);
			HttpResponse resp = client.execute(post);
			resp.getEntity();

			String res = da.uploadFinishFile(usuario, password, cloudId, getMd5Digest(file));
			if (StringUtils.isEmpty(res)) {
				System.out.println("Fichero " + fileName + " subido correctamente");

				// Guardamos en la base de datos el identificador
				Fichero imagen = Fichero.findFicheroByNombre(fileName);

				if (folderType.equals(FolderType.FICHA))
					imagen.set_4syncIdF(cloudId);
				else
					imagen.set_4syncIdL(cloudId);
				imagen.merge();

				return true;
			} else {
				System.out.println("Error al subir el fichero " + fileName + ": " + res);
				return false;
			}
		}
		catch (ApiException e) {
			log.error("Se ha producido un error al intentar crear el nuevo identificador para un fichero dado. ", e);
		}
		catch (FileNotFoundException e) {
			log.error("Error al convertir el fichero en un inputstream", e);
		}
		catch (UnsupportedEncodingException e) {
			log.error("Error al subir un fichero", e);
		}
		catch (ClientProtocolException e) {
			log.error("Error al subir un fichero", e);
		}
		catch (IOException e) {
			log.error("Error al subir un fichero", e);
		}

		return false;
	}

	/**
	 * Sube un listado de ficheros a un directorio dado
	 * 
	 * @param listado
	 *            Listado con los nombres de los ficheros a subir
	 * @param directorio
	 *            Nombre del directorio al que se van a subir los ficheros
	 */
	private void uploadFilesToFolder(List<String> listado, String directorio) {
		for (String fichero : listado) {
			File tmp = new File(getImagesPath() + File.separator + directorio + File.separator + fichero);
			if (tmp.exists()) {
				log.info("Se procede a subir el fichero: " + tmp.getAbsolutePath());
				uploadFile(tmp, carpetasIds.get(directorio), getFolderType(directorio));
			} else {
				log.error("No se ha podido subir el fichero porque no existe. Nombre: " + tmp.getAbsolutePath());
				break;
			}
		}
	}

	private HashMap<String, Long> loadFolderIds() {
		log.info("Buscando directorios en 4sync");

		HashMap<String, Long> map = new HashMap<String, Long>();

		DesktopAppJax2 da = new DesktopAppJax2Service().getDesktopAppJax2Port();
		try {
			AccountItemArray folders = da.getAllFolders(usuario, password);
			if (folders.getItem().isEmpty()) {
				log.error("No existen directorios o se ha producido un error en el login.");
			} else {
				for (AccountItem folder : folders.getItem()) {
					map.put(folder.getName(), folder.getId());
					log.info("Se ha encontrado el siguiente directorio en 4sync: " + folder.getName()
							+ " identificador: " + folder.getId());
				}
			}

			return map;
		}
		catch (ApiException e) {
			log.error("Se ha producido un error al intentar obtener los identificadores de los directorios.", e);
			return null;
		}
	}

	private String getMd5Digest(File file) throws IOException {
		InputStream fis = new FileInputStream(file);
		return DigestUtils.md5Hex(fis);
	}
}
