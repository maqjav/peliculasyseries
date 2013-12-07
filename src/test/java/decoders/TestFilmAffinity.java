package decoders;

import java.util.Map;

import org.junit.Test;

import es.pys.decoders.factory.Decoder;
import es.pys.decoders.factory.DecoderFactory;
import es.pys.decoders.factory.DecoderType;
import es.pys.model.Pelicula;
import es.pys.web.PeliculaController;

public class TestFilmAffinity extends PeliculaController {

	private final static String url = "http://www.filmaffinity.com/es/film698708.html";

	@Test
	public void extraerContenido() {
		try {
			Decoder decoder = DecoderFactory.getDecoder(DecoderType.FILMAFFINITY);
			Map<String, String> contenido = decoder.sacarDatos(url);

			Pelicula pelicula = introducirDatosZinema(contenido, "", "", "");
			System.out.println(pelicula.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
