package es.pys.storage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import net.lingala.zip4j.exception.ZipException;
import es.pys.storage.exceptions.StorageException;

/**
 * Interfaz para el manejo del almacenaje de ficheros
 * 
 * @author A171648
 * 
 */
public interface Storage {

	/**
	 * Guarda un fichero en el sistema de ficheros
	 * 
	 * @param path
	 * @return Listado de ficheros guardados
	 * @throws ZipException
	 *             Se ha producido un error al intentar descomprimir el fiche
	 */
	public List<String> saveFile(String path) throws StorageException;

	/**
	 * Guarda un fichero en el sistema de ficheros
	 * 
	 * @param in
	 * @return Listado de ficheros guardados
	 * @throws ZipException
	 *             Se ha producido un error al intentar descomprimir el fiche
	 */
	public List<String> saveFile(InputStream in) throws StorageException;

	/**
	 * Guarda un fichero en el sistema de ficheros
	 * 
	 * @param file
	 * @return Listado de ficheros guardados
	 * @throws ZipException
	 *             Se ha producido un error al intentar descomprimir el fichero
	 */
	public List<String> saveFile(File file) throws StorageException;

	/**
	 * Obtiene un fichero dado su nombre
	 * 
	 * @param fileName
	 *            Nombre del fichero
	 * @param folderName
	 *            Nombre del directorio
	 * @return Array de bytes con el fichero obtenido
	 * @throws StorageException
	 */
	public byte[] loadFile(String fileName, String folderName) throws StorageException;

	/**
	 * Elimina un fichero dado su nombre
	 * 
	 * @param fileName
	 *            Nombre del fichero
	 * @param folderName
	 *            Nombre del directorio
	 */
	public boolean deleteFile(String fileName, String folderName);
}
