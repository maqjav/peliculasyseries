package decoders;

import java.util.Map;

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

	private final static String url = "http://www.filmaffinity.com/es/film376816.html";

	@Test
	public void extraerContenido() {
		try {
			Decoder decoder = DecoderFactory.getDecoder(DecoderType.FILMAFFINITY);
			Map<String, String> contenido = decoder.sacarDatos(url);

			//Pelicula pelicula = introducirDatosZinema(contenido, "", "", "");
			//System.out.println(pelicula.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
