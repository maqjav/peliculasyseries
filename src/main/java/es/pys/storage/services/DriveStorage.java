package es.pys.storage.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import es.pys.model.Fichero;
import es.pys.storage.FolderType;
import es.pys.storage.Storage;
import es.pys.storage.exceptions.StorageException;
import es.pys.storage.factory.StorageFactory;
import es.pys.storage.factory.StorageType;

@Service
public class DriveStorage extends BaseStorage implements Storage {

	private static Logger log = LogManager.getRootLogger();

	private Drive service = null;

	/** Email of the Service Account */
	@Value("${api.drive.email}")
	private String SERVICE_ACCOUNT_EMAIL;

	@Value("${api.drive.name}")
	private String APP_NAME;

	/** Path to the Service Account's Private Key file */
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "/certs/drive-privatekey.p12";

	private static final String FOLDER_MIMETYPE = "application/vnd.google-apps.folder";

	private HashMap<String, String> carpetasIds = null;

	@Autowired
	private StorageFactory storageFactory;

	@Override
	public boolean deleteFile(String fileName, String folderName) {
		return super.deleteFile(fileName, folderName);
	}

	@Override
	public byte[] loadFile(String fileName, String folderName) throws StorageException {
		// Intentamos devolverlo del disco duro
		// Es posible que exista por problemas de sincronismo con el disco
		try {
			return loadLocalFile(fileName, folderName);
		}
		catch (StorageException e1) {
			try {
				if (getDriveService() != null) {
					// Obtenemos el identificador de la imagen que queremos
					Fichero fichero = ficheroDao.findFicheroByNombre(fileName);
					String fileId = null;

					if (getFolderType(folderName).equals(FolderType.FICHA))
						fileId = fichero.getGoogleDriveIdF();
					else
						fileId = fichero.getGoogleDriveIdL();

					// En caso de no haber encontrado ningun fichero
					// sincronizamos
					// con otro storage
					if (fileId == null) {
						log
								.debug("Se va a procesar a sincronizar Drive con otro Storage para obtener el ID: "
										+ fileId);
						syncStorage(fichero, folderName);
						if (getFolderType(folderName).equals(FolderType.FICHA))
							return java.nio.file.Files.readAllBytes(new File(getImagesFichaPath(fileName)).toPath());
						else
							return java.nio.file.Files.readAllBytes(new File(getImagesListadoPath(fileName)).toPath());
					} else {
						log.debug("Se va a procesar a obtener de Drive el ID: " + fileId);
						com.google.api.services.drive.model.File file = getDriveService().files().get(fileId).execute();
						log.debug("Obtenido fichero con downloadUrl: " + file.getDownloadUrl());
						if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
							try {
								HttpResponse resp = getDriveService()
										.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
										.execute();
								log.info("Se ha obtenido respuesta de Drive con status code: " + resp.getStatusCode());

								InputStream is = resp.getContent();
								byte[] image;
								try {
									image = IOUtils.toByteArray(is);
									log.debug("El fichero obtenido tiene un tamaño de: " + image.length + " bytes");
								} finally {
									is.close();
								}

								saveLocalFile(fileName, folderName, image);

								return image;
							}
							catch (IOException e) {
								log.error("Se ha producido un error al descargar un fichero de Google Drive.", e);
								throw new StorageException("Se ha producido un error al descargar el fichero.", e);
							}
						} else {
							log.error("No existe fichero almacenado en Google Drive con id: " + fileId);
							return null;
						}
					}
				}
			}
			catch (GeneralSecurityException e) {
				throw new StorageException("Se ha producido un error de seguridad al conectarse a Google Drive", e);
			}
			catch (IOException e) {
				throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
			}
			catch (URISyntaxException e) {
				throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
			}
		}

		return null;
	}

	private void syncStorage(Fichero fichero, String folderName) throws StorageException {
		try {
			// Extraemos los directorios
			if (carpetasIds == null || carpetasIds.isEmpty())
				carpetasIds = loadFolderIds();

			// Descargamos el fichero de otro Storage
			if (getFolderType(folderName).equals(FolderType.FICHA) && fichero.get_4syncIdF() != null) {
				storageFactory.getStorage(StorageType._4SYNC).loadFile(fichero.getNombreFichero(), folderName);
				uploadFile(new File(getImagesFichaPath(fichero.getNombreFichero())), carpetasIds.get(folderName),
						FolderType.FICHA);
			} else if (getFolderType(folderName).equals(FolderType.LISTADO) && fichero.get_4syncIdL() != null) {
				storageFactory.getStorage(StorageType._4SYNC).loadFile(fichero.getNombreFichero(), folderName);
				uploadFile(new File(getImagesListadoPath(fichero.getNombreFichero())), carpetasIds.get(folderName),
						FolderType.LISTADO);
			} else
				throw new StorageException(
						"No se ha encontrado el fichero en otro Storage por lo que no se ha podido sincronizar");
		}
		catch (GeneralSecurityException e) {
			throw new StorageException("Se ha producido un error de seguridad al conectarse a Google Drive", e);
		}
		catch (IOException e) {
			throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
		}
		catch (URISyntaxException e) {
			throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
		}
	}

	@Override
	public List<String> saveFile(File file) throws StorageException {
		try {
			if (getDriveService() != null) {
				List<String> listado = super.saveFile(file);

				// Si no tenemos los identificadores de las carpetas es
				// necesasrio obtenerlas
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
		}
		catch (GeneralSecurityException e) {
			throw new StorageException("Se ha producido un error de seguridad al conectarse a Google Drive", e);
		}
		catch (IOException e) {
			throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
		}
		catch (URISyntaxException e) {
			throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
		}

		log.error("No se ha podido acceder al servicio de Google Drive");
		return null;
	}

	@Override
	public List<String> saveFile(InputStream in) throws StorageException {
		return super.saveFile(in);
	}

	@Override
	public List<String> saveFile(String path) throws StorageException {
		try {
			if (getDriveService() != null) {
				return super.saveFile(path);
			}
		}
		catch (GeneralSecurityException e) {
			throw new StorageException("Se ha producido un error de seguridad al conectarse a Google Drive", e);
		}
		catch (IOException e) {
			throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
		}
		catch (URISyntaxException e) {
			throw new StorageException("Se ha producido un error al conectarse a Google Drive", e);
		}

		return null;
	}

	private HashMap<String, String> loadFolderIds() throws IOException, GeneralSecurityException, URISyntaxException {
		log.info("Buscando directorios en Google Drive");

		HashMap<String, String> map = new HashMap<String, String>();

		// Obtenemos todos los directorios
		Files.List request = getDriveService()
				.files().list().setQ("'root' in parents and mimeType='" + FOLDER_MIMETYPE + "' and trashed=false");

		// Buscamos los directorios donde vamos a guardar las imagenes
		FileList folders = request.execute();

		// En caso de no existir los creamos
		boolean folderListadoExist = false;
		boolean folderFichaExist = false;

		log.debug("Se ha encontrado " + folders.getItems().size() + " directorios en Drive");
		for (com.google.api.services.drive.model.File folder : folders.getItems()) {
			if (folder.getTitle().equals(listadoFolder)) {
				log.debug("Se ha encontrado el directorio en Drive: " + listadoFolder + " con ID: " + folder.getId());
				map.put(folder.getTitle(), folder.getId());
				folderListadoExist = true;
			}

			if (folder.getTitle().equals(fichaFolder)) {
				log.debug("Se ha encontrado el directorio en Drive: " + fichaFolder + " con ID: " + folder.getId());
				map.put(folder.getTitle(), folder.getId());
				folderFichaExist = true;
			}
		}

		if (!folderFichaExist) {
			log.debug("Creando el directorio en Drive: " + fichaFolder);
			String id = createNewFile(fichaFolder, FOLDER_MIMETYPE, null, null);
			map.put(fichaFolder, id);
		}

		if (!folderListadoExist) {
			log.debug("Creando el directorio en Drive: " + listadoFolder);
			String id = createNewFile(listadoFolder, FOLDER_MIMETYPE, null, null);
			map.put(fichaFolder, id);
		}

		return map;
	}

	private String createNewFile(String title, String fileMimeType, String parentId, File fileToUpload)
			throws IOException, GeneralSecurityException, URISyntaxException {
		com.google.api.services.drive.model.File fichero = new com.google.api.services.drive.model.File();
		fichero.setTitle(title);
		fichero.setMimeType(fileMimeType);

		com.google.api.services.drive.model.File file;
		// Fichero
		if (parentId != null && fileToUpload != null) {
			log.debug("Se va a proceder a subir un nuevo fichero: " + title + " con tamaño: " + fileToUpload.length()
					+ " al directorio con ID: " + parentId + " y con mime: " + fileMimeType);
			fichero.setParents(Arrays.asList(new ParentReference().setId(parentId)));
			FileContent fileContent = new FileContent(fileMimeType, fileToUpload);
			file = getDriveService().files().insert(fichero, fileContent).execute();
		}
		// Directorio
		else {
			log.debug("Se va a proceder a crear un directorio nuevo con nombre: " + title);
			file = getDriveService().files().insert(fichero).execute();
		}

		return file.getId();
	}

	private void uploadFilesToFolder(List<String> listado, String directorio) throws IOException,
			GeneralSecurityException, URISyntaxException {
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

	private Fichero uploadFile(File file, String folderId, FolderType folderType) throws IOException,
			GeneralSecurityException, URISyntaxException {
		// Subimos el fichero
		String fileName = file.getName();
		String cloudId = createNewFile(fileName, MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file), folderId, file);

		// Guardamos en la base de datos el identificador
		Fichero imagen = ficheroDao.findFicheroByNombre(fileName);

		if (folderType.equals(FolderType.FICHA))
			imagen.setGoogleDriveIdF(cloudId);
		else
			imagen.setGoogleDriveIdL(cloudId);
		return ficheroDao.merge(imagen);
	}

	private Drive getDriveService() throws GeneralSecurityException, IOException, URISyntaxException {
		if (service == null) {
			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = new JacksonFactory();

			// Certificado
			InputStream certStream = DriveStorage.class.getResourceAsStream(SERVICE_ACCOUNT_PKCS12_FILE_PATH);
			File temp = File.createTempFile("temp", ".p12");
			FileUtils.copyInputStreamToFile(certStream, temp);

			GoogleCredential credential = new GoogleCredential.Builder()
					.setTransport(httpTransport).setJsonFactory(jsonFactory).setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
					.setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
					.setServiceAccountPrivateKeyFromP12File(temp).build();
			service = new Drive.Builder(httpTransport, jsonFactory, null)
					.setApplicationName(APP_NAME).setHttpRequestInitializer(credential).build();
		}

		return service;
	}
}
