package es.pys.storage.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import es.pys.model.Fichero;
import es.pys.storage.FolderType;
import es.pys.storage.Storage;
import es.pys.storage.exceptions.StorageException;

public abstract class BaseStorage implements Storage {

	@Value("${api.listado.folder}")
	protected final String listadoFolder = null;

	@Value("${api.ficha.folder}")
	protected final String fichaFolder = null;
	
	@Value("${api.spring.profiles.variable}")
	protected final String springProfilesVariable = null;
	
	@Value("${api.spring.profiles.openshift}")
	protected final String springOpenshift = null;

	protected static Logger log = LogManager.getRootLogger();

	private static final String IMAGES_FOLDER = "images";

	private static final String IMG_ERROR = "fichero_roto.png";

	@Value("${api.openshift.data.variable}")
	protected final String openShiftData = null;

	@Autowired
	private HttpServletRequest servletRequest;

	@Override
	public List<String> saveFile(InputStream in) throws StorageException {
		if (in != null) {
			File tmp = inputStreamToFile(in);
			return this.saveFile(tmp);
		}

		log.error("No se ha pasado un inputstream con datos");
		return null;
	}

	@Override
	public List<String> saveFile(File file) throws StorageException {
		if (file != null) {
			try {
				return extractFiles(file);
			}
			catch (ZipException e) {
				throw new StorageException("Se ha producido un error al extraer los ficheros", e);
			}
		}

		log.error("No se ha pasado un file con datos");
		return null;
	}

	@Override
	public List<String> saveFile(String path) throws StorageException {
		if (path != null) {
			File file = new File(path);
			return this.saveFile(file);
		}

		log.error("No se ha pasado un path correcto");
		return null;
	}

	@Override
	public byte[] loadFile(String fileName, String folderName) throws StorageException {
		try {
			return loadLocalFile(fileName, folderName);
		}
		catch (StorageException e1) {
			try {
				return loadErrorImg();
			}
			catch (IOException e) {
				log.error("No se ha podido cargar la imagen de error", e);
				throw new StorageException("No se ha podido cargar la imagen de error", e);
			}
		}
	}

	protected byte[] loadLocalFile(String fileName, String folderName) throws StorageException {
		try {
			return IOUtils.toByteArray(new FileInputStream(new File(getImagesPath()
					+ completePath(folderName, fileName))));
		}
		catch (FileNotFoundException e) {
			log.error("No se ha podido cargar el fichero: " + fileName + " del directorio: " + folderName);
			throw new StorageException("No se ha podido cargar el fichero: " + fileName + " del directorio: "
					+ folderName, e);
		}
		catch (IOException e) {
			log.error("No se ha podido cargar el fichero: " + fileName + " del directorio: " + folderName);
			throw new StorageException("No se ha podido cargar el fichero: " + fileName + " del directorio: "
					+ folderName, e);
		}
	}

	@Override
	public boolean deleteFile(String fileName, String folderName) {
		File tmp = new File(getImagesPath() + completePath(folderName, fileName));
		return tmp.delete();
	}

	/**
	 * Obtiene la ruta del directorio de imagenes
	 * 
	 * @return
	 */
	protected String getImagesPath() {
		// En caso de encontrarnos en Openshift, no podemos hacer uso del mismo directorio de despliegue
		if (System.getenv(springProfilesVariable) != null && System.getenv(springProfilesVariable).equals(springOpenshift))
			return System.getenv(openShiftData) + IMAGES_FOLDER;
		// Para cualquier otro caso obtenemos la ruta de despliegue
		else
			return servletRequest.getSession().getServletContext().getRealPath("/") + IMAGES_FOLDER;
	}

	/**
	 * Obtiene la ruta absoluta de un fichero en el directorio de fichas
	 * 
	 * @return
	 */
	protected String getImagesFichaPath(String fileName) {
		return getImagesPath() + IMAGES_FOLDER + File.separator
				+ fichaFolder + File.separator + fileName;
	}

	/**
	 * Obtiene la ruta absoluta de un fichero en el directorio del listado
	 * 
	 * @return
	 */
	protected String getImagesListadoPath(String fileName) {
		return getImagesPath() + IMAGES_FOLDER + File.separator
				+ listadoFolder + File.separator + fileName;
	}

	protected String completePath(String folderName, String fileName) {
		return File.separator + folderName + File.separator + fileName;
	}

	/**
	 * Convierte un inputstream en un fichero
	 * 
	 * @param in
	 * @return
	 */
	protected File inputStreamToFile(InputStream in) {
		OutputStream salida = null;

		try {
			SecureRandom random = new SecureRandom();
			File tmp = File.createTempFile("tmp_upload_" + new BigInteger(130, random).toString(32), ".zip");
			salida = new FileOutputStream(tmp);
			byte[] buf = new byte[1024];
			int len;

			while ((len = in.read(buf)) > 0) {
				salida.write(buf, 0, len);
			}

			return tmp;
		}
		catch (IOException e) {
			log.error("Se ha producido un error al convertir un InputStream en un Fichero.", e);
			return null;
		} finally {
			if (salida != null)
				try {
					salida.close();
				}
				catch (IOException e) {
					// Ignoramos
				}

			if (in != null)
				try {
					in.close();
				}
				catch (IOException e) {
					// Ignoramos
				}
		}
	}

	private List<String> extractFiles(File file) throws ZipException {
		ZipFile zipFile = new ZipFile(file);
		if (zipFile.isEncrypted()) {
			log.error("El fichero tiene contraseña y no se puede descomprimir");
			return null;
		}

		zipFile.extractAll(getImagesPath());
		log.info("Se han extraido todas las imagenes correctamente en: " + getImagesPath());

		// Componemos el listado de ficheros
		@SuppressWarnings("unchecked")
		List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

		// Extraemos el listado de ficheros y evitamos que este repetido
		List<String> listado = new ArrayList<String>();
		int directorios = 0;
		for (FileHeader header : fileHeaderList) {
			if (directorios < 2) {
				if (header.isDirectory()) {
					directorios++;
				} else {
					int index = header.getFileName().indexOf("/") + 1;
					if (index >= 0)
						listado.add(header.getFileName().substring(index));
					else
						listado.add(header.getFileName());
				}
			} else
				break;
		}

		// Almacenamos los ficheros insertados localmente
		Fichero.setFicheros(listado);

		return listado;
	}

	protected byte[] loadErrorImg() throws IOException {
		InputStream errorImg = new FileInputStream(getImagesPath() + "/" + IMG_ERROR);
		return IOUtils.toByteArray(errorImg);
	}

	protected FolderType getFolderType(String directorio) {
		if (directorio.equals(listadoFolder) || directorio.contains(listadoFolder))
			return FolderType.LISTADO;
		else
			return FolderType.FICHA;
	}

	protected void saveLocalFile(String fileName, String folderName, byte[] image) throws FileNotFoundException,
			IOException {
		// Guardamos la imagen en local
		String localName = getImagesPath() + completePath(folderName, fileName);
		File file = new File(localName);
		if (!file.exists()) {
			log.debug("Se va a guardar en local el fichero: " + localName);
			FileOutputStream fileOutputStream = new FileOutputStream(file, false);
			fileOutputStream.write(image);
			fileOutputStream.close();
		} else
			log.debug("No se ha guardado el fichero: " + localName + " en local porque ya existe.");
	}
}
