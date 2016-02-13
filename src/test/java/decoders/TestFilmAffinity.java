package decoders;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.pys.decoders.factory.Decoder;
import es.pys.decoders.factory.DecoderFactory;
import es.pys.decoders.factory.DecoderType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/META-INF/spring/applicationContext.xml"})
public class TestFilmAffinity {

	private final static String url = "https://www.filmaffinity.com/es/film486156.html";

	@Test
	public void extraerContenido() {
		try {
			Decoder decoder = DecoderFactory.getDecoder(DecoderType.FILMAFFINITY);
			Map<String, String> contenido = decoder.sacarDatos(url);

			for (Entry<String, String> entry : contenido.entrySet())
				System.out.println(entry.getKey() + " : " + entry.getValue());
				
			//Pelicula pelicula = introducirDatosZinema(contenido, "", "", "");
			//System.out.println(pelicula.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
