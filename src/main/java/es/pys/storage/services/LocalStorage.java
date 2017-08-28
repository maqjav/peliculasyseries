package es.pys.storage.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import es.pys.storage.IStorage;
import es.pys.storage.exceptions.StorageException;

/**
 * Almacen de ficheros alojados en el servidor
 * 
 * @author javimarcos
 * 
 */
@Service
public class LocalStorage extends BaseStorage implements IStorage {

	@Override
	@Transactional
	public List<String> saveFile(File file) throws StorageException {
		return super.saveFile(file);
	}

	@Override
	@Transactional
	public List<String> saveFile(InputStream in) throws StorageException {
		return super.saveFile(in);
	}

	@Override
	public byte[] loadFile(String fileName, String folderName) throws StorageException {
		return super.loadFile(fileName, folderName);
	}
}
