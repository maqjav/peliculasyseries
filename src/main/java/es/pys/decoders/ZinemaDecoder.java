package es.pys.decoders;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import es.pys.decoders.factory.Decoder;
import es.pys.decoders.factory.DecoderType;

public class ZinemaDecoder extends Decoder {

	private static Logger log = LogManager.getRootLogger();

	private static String atributos[] = { "Año", "País", "Género", "Duración", "Estreno", "T. original", "Dirección",
			"Intérpretes", "Guión", "Fotografía", "Música", "Montaje", "Sinopsis" };

	public ZinemaDecoder() {
		super(DecoderType.ZINEMA);
	}

	@Override
	public Map<String, String> sacarDatos(String url) throws Exception {
		log.info("Analizando una página de www.zinema.com...");

		// Instanciamos HTMLCleaner
		HtmlCleaner cleaner = new HtmlCleaner();

		// Cargamos las propiedades
		CleanerProperties props = cleaner.getProperties();
		// Indica que se deben transoformar los caracteres latinos en su
		// respectiva letra
		props.setTranslateSpecialEntities(true);

		// Paraseamos la URL dada
		TagNode tagNode = new HtmlCleaner(props).clean(new URL(url));

		// Primero es necesario verificar que version de la página se está
		// utilizando.
		// Para ello buscamos un atributo que solo exista en una de las páginas
		// Un ejemplo es el atributo bgcolor="#000000" que tiene el nuevo estilo

		// Si no se encuentra es el modelo viejo
		if (tagNode.getElementListByAttValue("bgcolor", "#000000", true, true).isEmpty())
			return extraerPaginaAntigua(tagNode);
		else
			return extraerPaginaNueva(tagNode);
	}

	/**
	 * Función para extraer datos de una página de www.zinema.com con el estilo
	 * nuevo
	 * 
	 * @param tagNode
	 *            Tag principal con todo el HTML
	 * @return Map con los datos
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> extraerPaginaNueva(TagNode tagNode) {
		// Generamos el map con los datos
		Map<String, String> datos = new HashMap<String, String>();
		datos.put("pagina", "zinema");

		log.debug("Buscando el titulo...");

		// Obtenemos el titulo en español
		// A diferencia del resto de datos, el titulo viene en un TD con
		// background negro
		List<TagNode> tds = tagNode.getElementListHavingAttribute("bgcolor", true);
		for (TagNode td : tds) {
			// Localizamos el TD con el background en negro
			if (td.getAttributeByName("bgcolor").equals("#000000")) {
				// Accedemos al ultimo tag
				TagNode tag = accederUltimoTag(td);

				// Extraemos el valor
				String titulo = extraerContenido(tag);

				// Como el título viene todo en mayúsculas es necesario
				// decorarlo
				titulo = titulo.charAt(0) + titulo.substring(1, titulo.length()).toLowerCase();

				log.debug("Localizado el atributo: Título: ");
				log.debug("Valor: " + titulo.toString());
				datos.put("titulo", titulo.toString());
				break;
			}
		}

		// Obtenemos el resto de datos
		// Buscamos un parrafo que contenga los datos
		TagNode[] ps = tagNode.getElementsByName("p", true);
		TagNode parrafo = null;

		// Entre todos los párrafos buscamos uno que contenga la etiqueta Año
		for (TagNode p : ps) {
			if (corregirTextos(p.getText().toString()).contains("Año")) {
				parrafo = p;
				break;
			}
		}

		// Verificamos que se ha encontrado el párrafo
		if (parrafo == null)
			System.out
					.println("No se han podido extraer los datos. No se ha encontrado el párrafo que contiene los datos.");
		else {
			TagNode contenedor = null;

			// No podemos encontrar con un parrafo donde los datos se contienen
			// en un hijo
			// o con un parrafo donde se incluyen directamente los datos
			// Para dar con el contenedor que tiene los datos directamente,
			// probamos a buscar el primer atributo
			for (int i = 0; i < parrafo.getChildren().size(); i++) {

				String contenido = "";
				// Buscamos por un hijo con los datos
				try {
					TagNode child = (TagNode) parrafo.getChildren().get(i);
					TagNode tag = accederUltimoTag(child);

					// Extraemos el contenido
					contenido = extraerContenido(tag);

					if (contenido.contains(atributos[0])) {
						if (child.getChildren().size() < 10)
							contenedor = child.getParent();
						else
							contenedor = child;
						break;
					}
					// El padre contiene los datos
				}
				catch (ClassCastException e) {

				}
			}
			if (contenedor == null)
				contenedor = parrafo;

			// Una vez localizado el contenedor de los datos, vamos extrayendo
			// la información
			String atributoLocalizado = "";
			for (int i = 0; i < contenedor.getChildren().size(); i++) {
				// Cogemos el primer elemento
				try {
					if (atributoLocalizado.equals("")) {
						TagNode child = (TagNode) contenedor.getChildren().get(i);

						// Obtenemos el valor de este elemento
						TagNode tag = accederUltimoTag(child);

						// Extraemos el contenido
						String contenido = extraerContenido(tag);

						if (!contenido.equals(""))
							for (String atributo : atributos) {
								if (contenido.contains(atributo)) {
									log.debug("Localizado el atributo: " + atributo);
									atributoLocalizado = dameKey(atributo);
									break;
								}
							}
					} else {
						try {
							TagNode child = (TagNode) contenedor.getChildren().get(i);

							// Nos desplazamos al último tag de esta columna
							TagNode tag = accederUltimoTag(child);

							// Extraemos el contenido del tag
							if (tag != null) {
								String valor = extraerContenido(tag);

								log.debug("Valor: " + valor);
								datos.put(atributoLocalizado, valor);

								// Reseteamos el atributo localizado
								atributoLocalizado = "";
							}
						}
						catch (ClassCastException e) {
							// En este caso el dato no se encuentra dentro de un
							// tag
							TagNode contenido = (TagNode) contenedor.getChildren().get(i);

							String valor = corregirTextos(contenido.toString());
							if (!valor.equals("")) {
								log.debug("Valor: " + valor);
								datos.put(atributoLocalizado, valor);

								// Reseteamos el atributo localizado
								atributoLocalizado = "";
							}
						}
					}
				}
				catch (ClassCastException e) {

				}
			}
		}

		// Por ultimo es necesario extraer la sinopsis que viene en un apartado
		// diferente
		// Recuperamos todos los elemetos TR
		List<TagNode> trs = tagNode.getElementListByName("tr", true);

		// Buscamos por una fila con el texto Sinopsis
		for (int i = 0; i < trs.size(); i++) {
			// Nos desplazamos al ultimo tag de esta columna
			TagNode tag = accederUltimoTag(trs.get(i));

			// Extraemos el contenido del tag
			String valor = extraerContenido(tag);

			// Si la fila contiene la etiqueta, la siguiente contiene el valor
			if (valor.contains("Sinopsis")) {
				log.debug("Localizado el atributo: Sinopsis");

				// Nos desplazamos al último tag de esta columna
				tag = accederUltimoTag(trs.get(i + 1));

				// Extraemos el contenido del tag
				valor = extraerContenido(tag);
				log.debug("Valor: " + valor + ".");
				datos.put("sinopsis", valor + ".");
			}
		}

		return datos;
	}

	/**
	 * Función para extraer datos de una página de www.zinema.com con el estilo
	 * antiguo
	 * 
	 * @param tagNode
	 *            Tag principal con todo el HTML
	 * @return Map con los datos
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> extraerPaginaAntigua(TagNode tagNode) {
		// Generamos el map con los datos
		Map<String, String> datos = new HashMap<String, String>();
		datos.put("pagina", "zinema");

		log.debug("Buscando el título...");

		// Obtenemos el titulo en español
		// A diferencia del resto de datos, el titulo viene en un P separado y
		// alineado a la derecha
		List<TagNode> ps = tagNode.getElementListByName("p", true);
		for (TagNode nodo : ps) {
			// Localizamos el párrafo con el estilo "align=right" que es el que
			// contiene el título
			if (nodo.getAttributeByName("align").equals("right")) {
				// Accedemos al ultimo tag
				TagNode tag = accederUltimoTag(nodo);

				// Extraemos el valor
				String titulo = extraerContenido(tag);

				log.debug("Localizado el atributo: Título: ");
				log.debug("Valor: " + titulo.toString());
				datos.put("titulo", titulo.toString());
				break;
			}
		}

		// Obtenemos el resto de datos
		// Buscamos una tabla, ya que la página contiene solo una y esta
		// contiene los datos
		TagNode[] tables = tagNode.getElementsByName("table", true);
		TagNode table = null;

		// Verificamos que solo se ha obtenido una tabla, en caso contrario
		// buscamos por los
		// atributos que tiene la tabla donde se encuentran los datos
		if (tables == null)
			System.out
					.println("No se han podido extraer los datos. No se ha encontrado la tabla que contiene los datos.");
		else if (tables.length > 1) {
			for (TagNode tag : tables) {
				Map<String, String> atributes = tag.getAttributes();
				if (atributes.get("border") != null && atributes.get("border").equals("0")
						&& atributes.get("width") != null && atributes.get("width").equals("100%")) {
					table = tag;
					break;
				}
			}

			if (table == null)
				System.out
						.println("No se han podido extraer los datos. No se ha encontrado la tabla que contiene los datos.");
		}

		// Comprobamos que hemos filtrado una tabla de un conjunto de tablas y
		// en caso contrario nos
		// quedamos con la única existente
		if (table == null)
			table = tables[0];

		// Obtenemos el hijo tbody
		TagNode tbody = (TagNode) table.getChildren().get(0);

		// Extraemos todas las filas de la tabla
		List<TagNode> trs = tbody.getChildTagList();

		log.debug("Buscando el resto de datos...");

		// Analizamos cada una de las filas
		boolean sinopsis = false;
		String atributoAnterior = "";
		boolean noEncontrado = false;
		for (TagNode tr : trs) {

			// Obtenemos todas las columnas de la fila
			List<TagNode> tds = tr.getChildTagList();

			String atributoLocalizado = "";
			// Analizamos cada una de las columnas de las filas
			for (TagNode td : tds) {
				// Si no hemos encontrado un atributo ya lo intentamos localizar
				if (!sinopsis && atributoLocalizado.equals("")) {
					// Obtenemos el valor de esta columna
					TagNode tag = accederUltimoTag(td);

					// Extraemos el contenido
					String contenido = extraerContenido(tag);

					for (String atributo : atributos) {
						if (contenido.equals(atributo)) {
							log.debug("Localizado el atributo: " + atributo);
							atributoLocalizado = dameKey(atributo);
							noEncontrado = true;
							break;
						}
					}
				}
				// Si ya lo hemos localizado estaremos buscando su valor
				else {
					// En el caso de encontrarnos en la sinopsis, los datos se
					// encuentran
					// en la siguiente fila, no en la misma columna, de modo que
					// tenemos que saltar hasta la siguiente fila
					if (atributoLocalizado.equals("sinopsis")) {
						sinopsis = true;
						break;
					} else
						sinopsis = false;

					// Nos desplazamos al último tag de esta columna
					TagNode tag = accederUltimoTag(td);

					// Extraemos el contenido del tag
					String valor = extraerContenido(tag);

					log.debug("Valor: " + valor);
					datos.put(atributoLocalizado, valor);

					// Guardamos el atributo anterior
					atributoAnterior = new String(atributoLocalizado);

					// Reseteamos el atributo localizado
					atributoLocalizado = "";
				}
			}

			// Si llegado a este punto no hemos encontrado un atributo, podemos
			// estar en el caso
			// de que la fila actual contenga datos del atributo anterior
			// (listado de interpretes por ejemplo)
			if (noEncontrado)
				noEncontrado = false;
			else if (!atributoAnterior.equals("")) {
				// Comprobamos si existen datos en el último TD del listado
				TagNode td = tds.get(tds.size() - 1);

				// Accedemos al último tag
				TagNode tag = accederUltimoTag(td);

				// Extraemos el contenido del tag
				String valor = extraerContenido(tag);

				if (!valor.equals("")) {
					String anterior = datos.get(atributoAnterior);
					anterior += ", " + valor;
					datos.put(atributoAnterior, anterior);
					log.debug("Nuevo valor: " + anterior);
				}

			}
		}

		return datos;
	}

	/**
	 * Función para obtener el contenido de un tag dado
	 * 
	 * @param tag
	 *            Tag del que se quiere obtener el contenido
	 * @return Contenido en texto de este tag
	 */
	private String extraerContenido(TagNode tag) {
		String texto = "";
		// Verificamos si se trata de un texto con lineas destacadas
		if (tag.hasChildren() && !tag.getElementListByName("em", true).isEmpty()) {
			for (int i = 0; i < tag.getChildren().size(); i++) {
				try {
					// Si se trata de un nodo EM
					TagNode nodo = (TagNode) tag.getChildren().get(i);

					TagNode contenido = (TagNode) nodo.getChildren().get(0);
					texto += contenido.toString();
				}
				catch (ClassCastException e) {
					// Si se trata de un contenido
					try {
						TagNode contenido = (TagNode) tag.getChildren().get(i);
						texto += contenido.toString();
					}
					catch (ClassCastException e2) {
						return "";
					}
				}
			}
		}
		// En caso contrario extraemos el texto sin mas
		else if (tag.hasChildren()) {
			// Extramos el texto
			TagNode nodo = (TagNode) tag.getChildren().get(0);

			// Sacamos el texto
			texto = nodo.toString();
		} else
			return "";

		return corregirTextos(texto);
	}

	/**
	 * Función para extraer el último tag de un nodo dado
	 * 
	 * @param nodo
	 *            Nodo del que se quiere obtener el último hijo. <br/>
	 *            Siempre se va a acceder al primer nodo de cada hijo.
	 * @return Último nodo encontrado accediendo siempre al primero
	 */
	private TagNode accederUltimoTag(TagNode nodo) {
		if (!nodo.hasChildren())
			return nodo;
		else {
			// Vamos accediendo a los siguientes tag por orden
			for (int i = 0; i < nodo.getChildren().size(); i++) {
				try {
					TagNode child = (TagNode) nodo.getChildren().get(i);

					TagNode siguiente = accederUltimoTag(child);
					if (siguiente != null)
						return siguiente;
				}
				catch (ClassCastException e) {
					// Si el siguiente tag es un elemento final, verificamos que
					// contenga información
					// en caso contrario lo eliminamos y continuamos
					TagNode contenido = (TagNode) nodo.getChildren().get(i);
					if (contenido.toString().trim().equals("")) {
						nodo.removeChild(contenido);
						i--;
					}
					// Si tiene un valor devolvemos el nodo ya que es el último
					// tag
					else
						return nodo;
				}
			}

			// Si hemos salido del bucle quiere decir que no habrá hijos útiles
			// y que por lo
			// tanto estamos en el último tag con un valor blanco
			return null;
		}
	}

	@Override
	protected String dameKey(String atributo) {
		if (atributo.equals("T. original"))
			return "tituloOriginal";
		else if (atributo.equals("Año"))
			return "anyo";
		else if (atributo.equals("Duración"))
			return "duracion";
		else if (atributo.equals("País"))
			return "pais";
		else if (atributo.equals("Dirección"))
			return "director";
		else if (atributo.equals("Estreno"))
			return "estreno";
		else if (atributo.equals("Guión"))
			return "guion";
		else if (atributo.equals("Música"))
			return "musica";
		else if (atributo.equals("Fotografía"))
			return "fotografia";
		else if (atributo.equals("Intérpretes"))
			return "reparto";
		else if (atributo.equals("Género"))
			return "genero";
		else if (atributo.equals("Montaje"))
			return "montaje";
		else
			// if (atributo.equals("SINOPSIS"))
			return "sinopsis";
	}

}
