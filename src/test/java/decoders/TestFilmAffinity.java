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
import es.pys.model.Pais;
import es.pys.model.Pelicula;

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
				
			Pelicula pelicula = introducirDatosZinema(contenido, "1", "fichero", "1");
			System.out.println(pelicula.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static Pelicula introducirDatosZinema(Map<String, String> contenido, String fichero, String disco,
			String archivador) throws Exception {
		try {
			Pelicula pelicula = new Pelicula();

			pelicula.setTitulo(contenido.get("titulo"));
			try {
				pelicula.setFecha(new Integer(contenido.get("anyo")));
			}
			catch (Exception e) {
				pelicula.setFecha(new Integer(0));
			}

			// Nos pueden llegar varias nacionalidades además de escritas con su
			// nombre entero
			if (contenido.get("pais").contains("-")) {
				String[] nacionalidades = contenido.get("pais").split("-");
				if (nacionalidades[0].equals("Español"))
					nacionalidades[0] = "España";
				else if (nacionalidades[0].equals("USA"))
					nacionalidades[0] = "Estados Unidos";

				// Buscamos el pais
				Pais pais = Pais.findPaisByNombre(nacionalidades[0]);
				pelicula.setNacionalidad(pais);
			} else if (contenido.get("pais").contains(",")) {
				String[] nacionalidades = contenido.get("pais").split(", ");

				if (nacionalidades[0].equals("Español"))
					nacionalidades[0] = "España";

				// Buscamos el pais
				Pais pais = Pais.findPaisByNombre(nacionalidades[0]);
				pelicula.setNacionalidad(pais);
			} else {
				if (contenido.get("pais").equals("Español"))
					contenido.put("pais", "España");

				// Buscamos el pais
				System.out.println(contenido.get("pais"));
			}

			// Si existen multiples categorias va buscando de una en una hasta dar con una existente
			for (String genero : contenido.get("genero").split(", ")) {
				// Buscamos la categoría con las primeras 4 letras obtenidas
				String nombreCategoria = genero.substring(0, 1).toUpperCase() + genero.substring(1, 4).toLowerCase();
				System.out.println(nombreCategoria);
				break;
			}

			pelicula.setDuracion(contenido.get("duracion"));
			pelicula.setTituloOriginal(contenido.get("tituloOriginal"));
			pelicula.setDireccion(contenido.get("director"));
			if (contenido.get("reparto") == null)
				pelicula.setInterpretes("");
			else
				pelicula.setInterpretes(contenido.get("reparto"));
			if (contenido.get("fotografia") == null)
				pelicula.setFotografia("");
			else
				pelicula.setFotografia(contenido.get("fotografia"));
			pelicula.setGuion(contenido.get("guion"));
			pelicula.setMusica(contenido.get("musica"));
			pelicula.setMontaje(contenido.get("montaje"));
			pelicula.setSinopsis(contenido.get("sinopsis"));
			pelicula.setImg(fichero);
			pelicula.setDisco(disco);
			pelicula.setArchivador(new Integer(archivador));
			
			return pelicula;
		}
		catch (Exception e) {
			throw e;
		}
	}
	}
