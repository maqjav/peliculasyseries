package storage;

import java.io.File;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;

import org.junit.Test;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

public class TestMimetype {

	@Test
	public void testMime() {
		ConfigurableMimeFileTypeMap mimeTypesMap = new ConfigurableMimeFileTypeMap();
		URL url = getClass().getResource("/storage/image.jpg");
		File f = new File(url.getPath());
		System.out.println(mimeTypesMap.getContentType(f));

		MimetypesFileTypeMap mimeTypesMap2 = new MimetypesFileTypeMap();
		System.out.println(mimeTypesMap2.getContentType(f));
	}
}
