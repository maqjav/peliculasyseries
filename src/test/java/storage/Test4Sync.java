package storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.pmstation.shared.soap.api.AccountItemArray;
import com.pmstation.shared.soap.api.ApiException;
import com.pmstation.shared.soap.api.DesktopAppJax2;
import com.pmstation.shared.soap.api.DesktopAppJax2Service;


public class Test4Sync {

	private final String usuario = "maqjav";
	private final String password = "03031984";

	private static final String TEST_ZIP_FILE = "test.zip";

	public void login() {
		DesktopAppJax2 da = new DesktopAppJax2Service().getDesktopAppJax2Port();
		try {
			String res = da.login(usuario, password);

			if (StringUtils.isEmpty(res)) {
				System.out.println("Se ha realizado el login correctamente");
			} else {
				System.out.println("No se ha podido realizar el login: " + res);
			}

			boolean res1 = da.isAccountActive(usuario, password);
			System.out.println("El resultado es: " + res1);
		}
		catch (ApiException e) {
			System.out.println("Se ha producido un error al hacer el login: " + e.getMessage());
		}
	}

	public void uploadFile() {
		login();
		File testFile = FileUtils.toFile(this.getClass().getResource(TEST_ZIP_FILE));

		AccountItemArray folders = getFolders();
		// try {
		uploadFile(testFile);
		// } catch (ZipException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private AccountItemArray getFolders() {
		DesktopAppJax2 da = new DesktopAppJax2Service().getDesktopAppJax2Port();
		try {
			return da.getAllFolders(usuario, password);
		}
		catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private boolean uploadFile(final File file) {
		DesktopAppJax2 da = new DesktopAppJax2Service().getDesktopAppJax2Port();

		// Verificamos si se pueden subir ficheros
		if (!da.hasRightUpload()) {
			// log.error("Actualmente no se pueden subir ficheros");
			return false;
		}

		try {
			long nuevoId = da.uploadStartFile(usuario, password, 0, file.getName(), file.length());
			if (nuevoId < 0) {
				// log.error("No se ha podido crear el identificador para el fichero.");
				return false;
			}

			int dataCenterId = (int) da.getNewFileDataCenter(usuario, password);
			String sesionKey = da.createUploadSessionKey(usuario, password, -1);
			String upload = da.getUploadFormUrl(dataCenterId, sesionKey);

			InputStream in = new FileInputStream(file);

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(upload);
			MultipartEntity me = new MultipartEntity();
			StringBody rfid = new StringBody("" + nuevoId);
			StringBody rfb = new StringBody("" + 0);
			InputStreamBody isb = new InputStreamBody(in, file.getName()) {

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
			HttpEntity resEnt = resp.getEntity();

			String res = da.uploadFinishFile(usuario, password, nuevoId, getMd5Digest(file));
			if (StringUtils.isEmpty(res)) {
				System.out.println("File uploaded");
				return true;
			} else {
				System.out.println("Failed: " + res);
				return false;
			}
		}
		catch (ApiException e) {
			// log.error("Se ha producido un error al intentar crear el nuevo identificador para un fichero dado. ",e);
		}
		catch (FileNotFoundException e) {
			// log.error("Error al convertir el fichero en un inputstream",e);
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private String getMd5Digest(File file) throws IOException {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			return null;
		}

		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file));
		int theByte = 0;
		while ((theByte = bi.read()) != -1) {
			md5.update((byte) theByte);
		}
		bi.close();

		BigInteger bigInt = new BigInteger(1, md5.digest());
		return bigInt.toString(16);
	}
}
