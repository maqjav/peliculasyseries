package storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import es.pys.storage.services.DriveStorage;

public class TestDrive {

	/** Path to the Service Account's Private Key file */
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "/storage/drive-privatekey.p12";
	private static final String SERVICE_ACCOUNT_EMAIL = "823171543558@developer.gserviceaccount.com";
	private static final String APP_NAME = "PYS";
	private static Drive service = null;

	public void getDriveService() throws GeneralSecurityException, IOException, URISyntaxException {
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

		Files.List request = service.files().list();
		FileList folders = request.execute();

		for (com.google.api.services.drive.model.File folder : folders.getItems()) {
			System.out.println("Titulo: " + folder.getTitle() + " identificador: " + folder.getId());
		}
	}
}
