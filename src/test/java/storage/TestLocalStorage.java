package storage;

import java.io.File;

import org.apache.commons.io.FileUtils;

import es.pys.storage.exceptions.StorageException;
import es.pys.storage.services.LocalStorage;

public class TestLocalStorage extends LocalStorage {

	private static final String TEST_ZIP_FILE = "test.zip";

	public void saveFile() {
		try {
			File testFile = FileUtils.toFile(this.getClass().getResource(TEST_ZIP_FILE));
			saveFile(testFile);
		}
		catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
