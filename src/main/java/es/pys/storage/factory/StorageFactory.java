package es.pys.storage.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.pys.storage.IStorage;

@Service
public class StorageFactory {

	@Autowired
	private IStorage driveStorage;

	@Autowired
	private IStorage localStorage;

	@Value("${api.system.storage.variable}")
	private String SYSTEM_STORAGE_VARIABLE;

	/**
	 * Obtiene el Storage en funcion del tipo
	 * 
	 * @param storageType
	 * @return
	 */
	public IStorage getStorage(StorageType storageType) {
		if (storageType.equals(StorageType.DRIVE))
			return driveStorage;
		else
			return localStorage;
	}

	/**
	 * Obtiene el Storage en funcion de la variable del sistema
	 * storage.preference
	 * 
	 * @param systemStoragePreference
	 *            Variable que indica el tipo de Storage utilizado:
	 *            <ul>
	 *            <li>drive: Google Drive.</li>
	 *            <li>_4sync: 4Sync.</li>
	 *            <li>local: Local file storage.</li>
	 *            </ul>
	 * @return
	 */
	public IStorage getStorage(String systemStoragePreference) {
		if (systemStoragePreference == null || systemStoragePreference.equals("local"))
			return getStorage(StorageType.LOCAL);
		else if (systemStoragePreference.equals("drive"))
			return getStorage(StorageType.DRIVE);
		else if (systemStoragePreference.equals("4sync"))
			return getStorage(StorageType._4SYNC);

		return null;
	}

	/**
	 * Obtiene el Storage en funcion de la configuracion del sistema
	 * 
	 * @return
	 */
	public IStorage getStorage() {
		String systemVar = System.getenv(SYSTEM_STORAGE_VARIABLE);
		return getStorage(systemVar);
	}
}
