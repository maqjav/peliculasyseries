package es.pys.decoders;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;

import es.pys.decoders.factory.Decoder;
import es.pys.decoders.factory.DecoderType;

public class FilmaffinityDecoder extends Decoder {

	private static Logger log = LogManager.getRootLogger();

	// Listado con los campos que se van a extraer
	// Si se quieren añadir mas es necesario modificar tambien el listado de
	// parámetros en el MAP
	private static String atributos[] = { "TÍTULO ORIGINAL", "AÑO", "DURACIÓN", "PAÍS", "DIRECTOR", "GUIÓN", "MÚSICA",
			"FOTOGRAFÍA", "REPARTO", "GÉNERO", "SINOPSIS" };

	public FilmaffinityDecoder() {
		super(DecoderType.FILMAFFINITY);
	}

	@Override
	public Map<String, String> sacarDatos(String url) throws Exception {
		log.info("Analizando una pagina de www.filmaffinity.com...");

		// Generamos el map con los datos
		Map<String, String> datos = new HashMap<String, String>();

		// Instanciamos HTMLCleaner
		HtmlCleaner cleaner = new HtmlCleaner();

		// Cargamos las propiedades
		CleanerProperties props = cleaner.getProperties();
		// Indica que se deben transoformar los caracteres latinos en su
		// respectiva letra
		props.setTranslateSpecialEntities(true);

		// Paraseamos la URL dada
		TagNode tagNode = new HtmlCleaner(props).clean(new URL(url), "ISO-8859-1");

		log.debug("Buscando el titulo...");

		// Obtenemos el titulo en español
		// A diferencia del resto de datos, el titulo viene en un DIV separado
		String titulo = "";
		TagNode[] tagTitulo = tagNode.getElementsByAttValue("id", "main-title", true, false);
		if (tagTitulo == null || tagTitulo.length < 1)
			log.debug("No se ha podido obtener el titulo");
		else {
			TagNode ultimo = accederUltimoTag(tagTitulo[0]);
			titulo = extraerContenido(ultimo);
			datos.put("titulo", eliminarBasura(titulo.trim()));
			log.debug("Localizado el atributo: TÍTULO: " + eliminarBasura(titulo.trim()));
		}

		// Obtenemos el resto de datos
		// Buscamos la tabla con identificador mcardtable que es la que contiene
		// todos los datos
		TagNode[] table = tagNode.getElementsByAttValue("class", "movie-info", true, true);

		// Verificamos que se ha encontrado dicha tabla
		if (table == null)
			System.out
					.println("No se han podido extraer los datos. No se ha encontrado la tabla que contiene los datos.");
		else {
			log.debug("Buscando el resto de datos...");

			String atributoLocalizado = "";

			// Analizamos cada una de las filas
			for (int i = 0; i < table[0].getChildren().size(); i++) {
				// Extraemos el tag hijo
				HtmlNode nodo = (HtmlNode) table[0].getChildren().get(i);
				if (nodo instanceof TagNode) {
					TagNode tr = (TagNode) nodo;

					// Si todavía no hemos encontrado un atributo y la fila
					// donde nos situamos tiene etiqueta DT (atributo)
					if (atributoLocalizado.equals("") && tr.toString().equals("dt")) {
						// Localizamos el atributo
						for (String atributo : atributos) {
							// Obtenemos el valor de esta columna
							TagNode tag = accederUltimoTag(tr);

							// Extraemos el contenido
							String contenido = extraerContenido(tag);

							if (contenido.equalsIgnoreCase(atributo)) {
								log.debug("Localizado el atributo: " + atributo);
								atributoLocalizado = dameKey(atributo);
								break;
							}
						}
					}
					// En caso de no encontrarnos en una etiqueta de tipo DT o
					// de tener un atributo encontrado
					// estaremos intentando obtener el valor
					else if (!atributoLocalizado.equals("")) {
						String valor = "";

						// Verificamos que no se trate de un listado de enlaces
						// En caso de ser un listado de enlaces se obtendrá el
						// listado de tags con cada enlace
						List<TagNode> tags = checkEnlaces(tr);
						if (tags != null) {
							for (TagNode a : tags) {
								String contenido = extraerContenido(a);
								if (valor.equals(""))
									valor += contenido;
								else
									valor += ", " + contenido;
							}
						} else {
							// Nos desplazamos al último tag de esta columna
							TagNode tag = accederUltimoTag(tr);
							valor = extraerContenido(tag);
						}

						log.debug("Valor: " + valor);
						datos.put(atributoLocalizado, valor);

						// Reseteamos el atributo localizado
						atributoLocalizado = "";
					}
				}
			}
		}

		// Indicamos el nombre de la pagina
		datos.put("pagina", "affinity");

		return datos;
	}

	/**
	 * Función para obtener el contenido de un tag dado
	 * 
	 * @param tag
	 *            Tag del que se quiere obtener el contenido
	 * @return Contenido en texto de este tag
	 */
	private static String extraerContenido(TagNode tag) {
		// Comprobamos si el tag se trata de una imagen
		if (tag.toString().equals("img")) {
			// Extraemos la ruta de la imagen
			String ruta = tag.getAttributeByName("src");
			return ruta.replace("/imgs/countries/", "");
		}

		// En caso contrario buscamos un texto
		if (tag.hasChildren()) {
			// Extramos el texto
			for (int i = 0; i < tag.getChildren().size(); i++) {
				try {
					HtmlNode nodo = (HtmlNode) tag.getChildren().get(i);

					// Sacamos el texto
					String texto = ((ContentNode) nodo).toString();

					// Corregimos errores en el texto
					texto = corregirTextos(texto);

					if (!texto.equals(""))
						return texto;
				}
				catch (ClassCastException e) {

				}
			}

			return "";
		} else
			return "";
	}

	@Override
	protected String dameKey(String atributo) {
		if (atributo.equals("TÍTULO ORIGINAL"))
			return "tituloOriginal";
		else if (atributo.equals("AÑO"))
			return "anyo";
		else if (atributo.equals("DURACIÓN"))
			return "duracion";
		else if (atributo.equals("PAÍS"))
			return "pais";
		else if (atributo.equals("DIRECTOR"))
			return "director";
		else if (atributo.equals("GUIÓN"))
			return "guion";
		else if (atributo.equals("MÚSICA"))
			return "musica";
		else if (atributo.equals("FOTOGRAFÍA"))
			return "fotografia";
		else if (atributo.equals("REPARTO"))
			return "reparto";
		else if (atributo.equals("GÉNERO"))
			return "genero";
		else
			// if (atributo.equals("SINOPSIS"))
			return "sinopsis";
	}

	/**
	 * Función para verificar si se trata de un listado de enlaces, en tal caso
	 * se devuelve el listado de tags con cada uno de los enlaces. SI no se
	 * obtiene null
	 * 
	 * @param tag
	 *            Tag con el que se va a realizar la verificación
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<TagNode> checkEnlaces(TagNode tag) {
		// Verificamos el número de hijos que contiene el tag
		if (tag.getChildren().size() > 1) {
			// Comprobamos que no se trata de una columna con una imagen (PAIS)
			// en
			// tal caso no se trata de un listado
			if (!tag.getElementListByName("img", true).isEmpty())
				return null;
			else
				return tag.getElementListByName("a", true);
		} else
			return null;
	}

	/**
	 * Función para extraer el ultimo tag de un nodo dado
	 * 
	 * @param nodo
	 *            Nodo del que se quiere obtener el último hijo. <br/>
	 *            Siempre se va a acceder al primer nodo de cada hijo.
	 * @return Último nodo encontrado accediendo siempre al primero
	 */
	private static TagNode accederUltimoTag(TagNode nodo) {
		if (!nodo.hasChildren())
			return nodo;
		else {
			// Si el hijo es un TAG lo analizamos
			if (nodo.getChildren().get(0) instanceof TagNode) {
				TagNode child = (TagNode) nodo.getChildren().get(0);
				return accederUltimoTag(child);
			}
			// Si el hijo no es un TAG si no que es un contenido devolvemos el
			// TAG
			// pues ya hemos llegado al final
			else
				return nodo;
		}
	}
}
