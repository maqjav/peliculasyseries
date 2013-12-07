package es.pys.storage.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import es.pys.storage.Storage;
import es.pys.storage.exceptions.StorageException;

/**
 * Almacen de ficheros alojados en el servidor
 * 
 * @author javimarcos
 * 
 */
@Configuration
@ComponentScan("es.pys.storage.services")
public class LocalStorage extends BaseStorage implements Storage {

	@Override
	public List<String> saveFile(File file) throws StorageException {
		return super.saveFile(file);
	}

	@Override
	public List<String> saveFile(InputStream in) throws StorageException {
		return super.saveFile(in);
	}

	@Override
	public byte[] loadFile(String fileName, String folderName) throws StorageException {
		return super.loadFile(fileName, folderName);
	}
}
