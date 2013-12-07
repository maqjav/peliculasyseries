package es.pys.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.pys.decoders.factory.Decoder;
import es.pys.decoders.factory.DecoderFactory;
import es.pys.decoders.factory.DecoderType;
import es.pys.model.Buscador;
import es.pys.model.Categoria;
import es.pys.model.Favorito;
import es.pys.model.Fichero;
import es.pys.model.Inserccion;
import es.pys.model.Pais;
import es.pys.model.Pelicula;
import es.pys.model.Sesion;
import es.pys.model.Usuario;
import es.pys.storage.Storage;
import es.pys.storage.exceptions.StorageException;
import es.pys.storage.factory.StorageFactory;

@RequestMapping("/")
@Controller
@ComponentScan("es.pys.web")
public class PeliculaController {

	private static Logger log = LogManager.getRootLogger();

	@Autowired
	private ReloadableResourceBundleMessageSource messageSource;

	@Autowired
	private StorageFactory storageFactory;

	@Autowired
	private Sesion sesion;

	private static Integer PAG_SIZE = 20;

	/**
	 * Función de entrada de la aplicación. Obtiene el listado de peliculas
	 * disponibles
	 * 
	 * @param page
	 *            Número de página actual
	 * @param size
	 *            Número de registros por página
	 * @param uiModel
	 *            Modelo
	 * @return Redirección a la página de entrada
	 */
	@RequestMapping(produces = "text/html")
	public String listar(Model uiModel, @RequestParam(value = "page", required = false) Integer pagina) {
		List<Pelicula> peliculas = Pelicula.findAllPeliculas(obtenerPosicion(pagina), PAG_SIZE);

		Long total = Pelicula.countPeliculas();

		// Insertamos los datos necesarios para la portada
		Comunes.cargaDatosListado(uiModel, new Buscador(), peliculas, pagina, total, false);

		log.debug("Listando la pagina: " + pagina);

		return "index";
	}

	/**
	 * Función de entrada desde el buscador de la cabecera
	 * 
	 * @param buscador
	 *            Datos de la búsqueda
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Listado de películas encontradas con la búsqueda dada
	 */
	@RequestMapping(value = "/buscador", method = RequestMethod.POST, produces = "text/html")
	public String buscar(@ModelAttribute("buscador") Buscador buscador, Model uiModel) {
		log.debug("Buscando con los datos: " + buscador.getTexto());

		String opcion = buscador.getOpcionBusqueda();
		List<Pelicula> peliculas = null;
		Long total = new Long(0);
		if (opcion.equals(messageSource.getMessage("opcion_titulo", null, new Locale("es_ES")))) {
			// Buscamos por título
			peliculas = Pelicula.findPeliculasByTitulo(buscador.getTexto(), obtenerPosicion(buscador.getPagina()),
					PAG_SIZE);
			total = Pelicula.countPeliculasByTitulo(buscador.getTexto());
		} else if (opcion.equals(messageSource.getMessage("opcion_director", null, new Locale("es_ES")))) {
			// Buscamos por director
			peliculas = Pelicula.findPeliculasByDirector(buscador.getTexto(), obtenerPosicion(buscador.getPagina()),
					PAG_SIZE);
			total = Pelicula.countPeliculasByDirector(buscador.getTexto());
		} else if (opcion.equals(messageSource.getMessage("opcion_interpretes", null, new Locale("es_ES")))) {
			// Buscamos por intérprete
			peliculas = Pelicula.findPeliculasByInterprete(buscador.getTexto(), obtenerPosicion(buscador.getPagina()),
					PAG_SIZE);
			total = Pelicula.countPeliculasByInterpretes(buscador.getTexto());
		} else if (opcion.equals("")) {
			// Mostramos todo
			peliculas = Pelicula.findAllPeliculas(obtenerPosicion(buscador.getPagina()), PAG_SIZE);
			total = Pelicula.countPeliculas();
		}

		if (peliculas == null || peliculas.isEmpty()) {
			String titulo = messageSource.getMessage("mensaje_no_encontrado_titulo", null, new Locale("es_ES"));
			String mensaje = messageSource.getMessage("mensaje_no_encontrado", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje, buscador);

			log.debug("No se han encontrado datos en la busqueda");
			return "mensaje";
		} else {
			Comunes.cargaDatosListado(uiModel, buscador, peliculas, buscador.getPagina(), total, true);
			log.debug("Se han encontrado registros en la busqueda");
			return "index";
		}
	}

	/**
	 * Función de entrada desde el menú de categorías
	 * 
	 * @param id
	 *            Identificador de la categoría por la que se va a filtrar
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Listado de películas encontradas con la búsqueda dada
	 */
	@RequestMapping(value = "/buscador/{id}", produces = "text/html")
	public String buscar(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer pagina,
			Model uiModel) {
		log.debug("Buscando un registro por ID: " + id);

		List<Pelicula> peliculas = null;
		Long total = new Long(0);
		if (id != null) {
			// Buscamos por categoría
			peliculas = Pelicula.findPeliculasByCategoria(id, obtenerPosicion(pagina), PAG_SIZE);
			total = Pelicula.countPeliculasByCategoria(id);
		} else {
			// Mostramos todo
			peliculas = Pelicula.findAllPeliculas(obtenerPosicion(pagina), PAG_SIZE);
			total = Pelicula.countPeliculas();
		}

		if (peliculas == null || peliculas.isEmpty()) {
			String titulo = messageSource.getMessage("mensaje_no_encontrado_titulo", null, new Locale("es_ES"));
			String mensaje = messageSource.getMessage("mensaje_no_encontrado", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());

			log.debug("No se han encontrado datos en la busqueda");
			return "mensaje";
		} else {
			Comunes.cargaDatosListado(uiModel, new Buscador(), peliculas, pagina, total, false);
			log.debug("Se han encontrado registros en la busqueda");
			return "index";
		}
	}

	/**
	 * Función para cargar los datos de edición de un registro de una película
	 * desde el formulario de insercción donde tenemos la película
	 * 
	 * @param pelicula
	 *            Datos de la película
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Ventana de edición de películas
	 */
	@RequestMapping(value = "/subirImagenes", method = RequestMethod.POST, produces = "multipart/form-data")
	public String subirImagenes(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("inserccion") Inserccion inserccion, Model uiModel) {
		log.debug("Subiendo imagenes. El tamaño del fichero adjunto es: " + inserccion.getPaquete().getSize());

		try { // Extraemos los ficheros y los almacenamos
			Storage storageService = storageFactory.getStorage();
			List<String> listado = storageService.saveFile(inserccion.getPaquete().getInputStream());
			inserccion.setFicheros(listado);
			// Mensaje de confirmacion
			uiModel.addAttribute("subida_imagenes_ok", true);
		}
		catch (IOException e) {
			log.error("Se ha producido un error al subir las imagenes", e);
			// Mensaje de confirmacion
			uiModel.addAttribute("subida_imagenes_ok", false);
		}
		catch (StorageException e) {
			log.error("Se ha producido un error al subir las imagenes", e);
			// Mensaje de confirmacion
			uiModel.addAttribute("subida_imagenes_ok", false);
		}

		// Insertamos el listado de ficheros disponibles
		inserccion.setFicheros(Fichero.getFicherosDisponibles());

		// Cargamos el archivador predeterminado
		inserccion.setArchivador(messageSource.getMessage("archivador", null, new Locale("es_ES")));

		// Insertamos el objeto buscador
		uiModel.addAttribute("buscador", new Buscador());

		// Insertamos el listado de categorías
		uiModel.addAttribute("categorias", Categoria.findAllCategorias());

		// Insertamos el objeto inserccion
		uiModel.addAttribute("inserccion", inserccion);

		return "administracion";
	}

	/**
	 * Función para cargar los datos de edición de un registro de una película
	 * desde el formulario de insercción donde tenemos la película
	 * 
	 * @param pelicula
	 *            Datos de la película
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Ventana de edición de películas
	 */
	@RequestMapping(value = "/editar", method = RequestMethod.POST, produces = "text/html")
	public String editar(@ModelAttribute("pelicula") Pelicula pelicula, Model uiModel) {
		log.debug("Se procede a editar la pelicula: " + pelicula.getTitulo());

		// Recargamos la pelicula desde la base de datos
		pelicula = Pelicula.findPelicula(pelicula.getId());

		// Recargamos el listado de paises
		uiModel.addAttribute("paises", Pais.findAllPaises());

		// Añadimos la imagen de la pelicula a editar
		List<String> imagenes = Fichero.getFicherosDisponibles();

		if (!imagenes.contains(pelicula.getImg()))
			imagenes.add(0, pelicula.getImg());
		uiModel.addAttribute("ficheros", imagenes);

		// Datos de la película
		cargaDatosFicha(uiModel, new Buscador(), pelicula);

		return "editar";
	}

	/**
	 * Función para cargar los datos de edición de un registro de una película
	 * desde el formulario de edición
	 * 
	 * @param pelicula
	 *            Datos de la película
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Ventana de edición de películas
	 */
	@RequestMapping(value = "/editarFicha", method = RequestMethod.POST, produces = "text/html")
	public String editarFicha(@ModelAttribute("buscador") Buscador buscador, Model uiModel) {
		log.debug("Se procede a editar la pelicula: " + buscador.getTexto());

		// Recargamos la pelicula desde la base de datos
		Pelicula pelicula = Pelicula.findPelicula(new Long(buscador.getTexto()));

		// Recargamos el listado de paises
		uiModel.addAttribute("paises", Pais.findAllPaises());

		// Recargamos el listado de imagenes disponibles
		List<String> imagenes = Fichero.getFicherosDisponibles();

		// Añadimos la imagen de la pelicula a editar
		if (!imagenes.contains(pelicula.getImg()))
			imagenes.add(0, pelicula.getImg());
		uiModel.addAttribute("ficheros", imagenes);

		// Datos de la película
		cargaDatosFicha(uiModel, new Buscador(), pelicula);

		return "editar";
	}

	/**
	 * Función para generar un script con los datos de la base de datos desde un
	 * ID en particular
	 * 
	 * @param pelicula
	 *            Objeto pelicula que incluye el ID desde el que se va a generar
	 *            el script
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Archivo a descargar
	 */
	@RequestMapping(value = "/generar", method = RequestMethod.POST, produces = "text/html")
	public void generar(@ModelAttribute("buscador") Buscador buscador, Model uiModel, HttpServletResponse response) {
		log.debug("Generando backup de la base de datos");

		// Recuperamos el identificador
		Long id = new Long(buscador.getTexto());

		// Obtenemos todos los resultados desde el ID dado
		List<Pelicula> peliculas = Pelicula.findPeliculasById(id);

		String datos = "";

		for (Pelicula p : peliculas) {
			// Montamos la sentencia
			datos += "(" + p.getId() + ", '" + arreglarCadena(p.getTitulo()) + "', '" + p.getNacionalidad().getIso()
					+ "', '" + p.getFecha() + "', " + p.getCategoria().getId() + ", '" + p.getDuracion() + "', '"
					+ arreglarCadena(p.getTituloOriginal()) + "', '" + arreglarCadena(p.getDireccion()) + "', '"
					+ arreglarCadena(p.getInterpretes()) + "', '" + arreglarCadena(p.getGuion()) + "', '"
					+ arreglarCadena(p.getFotografia()) + "', '" + arreglarCadena(p.getMusica()) + "', '"
					+ arreglarCadena(p.getMontaje()) + "', '" + arreglarCadena(p.getSinopsis()) + "', '"
					+ arreglarCadena(p.getImg()) + "', '" + p.getDisco() + "', " + p.getArchivador() + "),\n";
		}

		// Corregimos los NULL
		datos = datos.replace("'NULL'", "NULL");

		// Enviamos fichero por pantalla
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(datos.getBytes(Charset.forName("UTF-8")));

			// Asignamos el nombre del fichero
			Format formatter = new SimpleDateFormat("MM_dd_yy-hh_mm");
			String s = formatter.format(new Date());

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/octet-stream");
			response.setContentLength(datos.getBytes().length);
			response.setHeader("Content-Disposition", "attachment; filename=backup_" + s + ".sql");

			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Corrige caracteres clave en un script SQL
	 * 
	 * @param cadena
	 *            Cadena a corregir
	 * @return Cadena corregida
	 */
	private String arreglarCadena(String cadena) {
		// Sustituimos datos en blanco por NULL
		if (cadena == null || cadena.equals(""))
			return "NULL";
		else {
			cadena = cadena.replace("'", "''");
			cadena = cadena.replace("\n", "");
		}
		return cadena;
	}

	/**
	 * Función para editar los datos de un registro de una película
	 * 
	 * @param id
	 *            Identificador de la película a editar
	 * @param pelicula
	 *            Datos de la película
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Ventana de edición de películas
	 */
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.POST, produces = "text/html")
	public String editar(@PathVariable("id") Long id, @ModelAttribute("pelicula") Pelicula pelicula, Model uiModel) {
		log.debug("Guardando los datos de edición de la pelicula con ID: " + id);

		// Insertamos el ID
		pelicula.setId(id);

		// Actualizamos
		pelicula.merge();

		// Insertamos el mensaje de notificación
		uiModel.addAttribute("editado", true);

		// Datos de la película
		cargaDatosFicha(uiModel, new Buscador(), pelicula);

		return "editar";
	}

	/**
	 * Función para insertar un nuevo registro de una película
	 * 
	 * @param inserccion
	 *            Datos del nuevo registro
	 * @param uiModel
	 *            Modelo de la vista
	 * @param req
	 *            HttpServletRequest del controlador
	 * @return Ventana de confirmación o de error
	 */
	@RequestMapping(value = "/insertar", method = RequestMethod.POST, produces = "text/html")
	public String insertar(@ModelAttribute("inserccion") Inserccion inserccion, Model uiModel, HttpServletRequest req) {
		log.debug("Se procede a realizar una inserccion de la siguiente URL: " + inserccion.getUrl());

		String titulo = messageSource.getMessage("error_insertar_titulo", null, new Locale("es_ES"));
		String mensaje = "";
		String respuesta = "";

		// Validamos la URL
		if (inserccion.getUrl() == null || inserccion.getUrl().equals("")) {
			mensaje = messageSource.getMessage("error_insertar_url", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());
			respuesta = "mensaje";
		}
		// Validamos el disco
		else if (inserccion.getDisco() == null || inserccion.getDisco().equals("")) {
			mensaje = messageSource.getMessage("error_insertar_disco", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());
			respuesta = "mensaje";
		}
		// Validamos el archivador
		else if (inserccion.getArchivador() == null || inserccion.getArchivador().equals("")) {
			mensaje = messageSource.getMessage("error_insertar_archivador", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());
			respuesta = "mensaje";
		}
		// Validamos que el archivador sea un número
		else if (inserccion.getArchivador() != null) {
			try {
				new Integer(inserccion.getArchivador());
				respuesta = insertarPelicula(inserccion, uiModel, titulo);
			}
			catch (Exception e) {
				mensaje = messageSource.getMessage("error_insertar_archivador_num", null, new Locale("es_ES"));
				cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());
				respuesta = "mensaje";
			}
		}
		// Insertamos la película
		else {
			respuesta = insertarPelicula(inserccion, uiModel, titulo);
		}

		// Recargamos el listado de ficheros disponibles para el combo
		// No se guardan entre vistas
		inserccion.setFicheros(Fichero.getFicherosDisponibles());

		return respuesta;
	}

	/**
	 * Inserta una película en la base de datos
	 * 
	 * @param inserccion
	 *            Objeto Inserccion con los datos introducidos
	 * @param uiModel
	 *            Modelo de la vista
	 * @param titulo
	 *            Titulo del mensaje a mostrar
	 * @return Llamada a la siguiente vista
	 */
	private String insertarPelicula(Inserccion inserccion, Model uiModel, String titulo) {
		String mensaje;
		try {
			String url = inserccion.getUrl();
			Map<String, String> contenido = new HashMap<String, String>();
			Decoder decoder = null;
			if (url.startsWith("http://www.zinema.com")) {
				decoder = DecoderFactory.getDecoder(DecoderType.ZINEMA);
			} else if (url.startsWith("http://www.filmaffinity.com")) {
				decoder = DecoderFactory.getDecoder(DecoderType.FILMAFFINITY);
			}

			contenido = decoder.sacarDatos(url);

			// Datos de affinity
			Pelicula pelicula;
			try {
				if (contenido.get("pagina").equals("affinity"))
					pelicula = introducirDatosFilmAffinity(contenido, inserccion.getFichero(), inserccion.getDisco(),
							inserccion.getArchivador());
				// Datos de zinema
				else
					pelicula = introducirDatosZinema(contenido, inserccion.getFichero(), inserccion.getDisco(),
							inserccion.getArchivador());
			}
			catch (Exception e) {
				mensaje = messageSource.getMessage("error_insertar_grave", new String[] { e.getMessage() }, new Locale(
						"es_ES"));
				cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());
				return "mensaje";
			}

			// Guardamos la película para mostrar los resultados
			uiModel.addAttribute("pelicula", pelicula);

			// Volvemos a la página de insercción
			// Insertamos el objeto buscador
			uiModel.addAttribute("buscador", new Buscador());

			// Insertamos el listado de categorías
			uiModel.addAttribute("categorias", Categoria.findAllCategorias());

			return "administracion";
		}
		catch (Exception e) {
			e.printStackTrace();

			mensaje = messageSource.getMessage("error_insertar", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());
			return "mensaje";
		}
	}

	/**
	 * Inserta los datos de una película de affinity
	 * 
	 * @param contenido
	 *            Array con todos los datos
	 * @param fichero
	 *            Nombre de la imagen
	 * @param disco
	 *            Número del disco
	 * @param archivador
	 *            Número del archivador
	 * @return Pelicula insertada
	 * @throws Exception
	 *             Se ha producido un error al intentar insertar una película de
	 *             zinema
	 */
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
				Pais pais = Pais.findPaisByNombre(contenido.get("pais"));
				pelicula.setNacionalidad(pais);
			}

			// Buscamos la categoría con las primeras 4 letras obtenidas
			List<Categoria> categoria = null;
			String nombre = contenido.get("genero").substring(0, 1).toUpperCase()
					+ contenido.get("genero").substring(1, 4).toLowerCase();
			categoria = Categoria.findCategoriasByNombre(nombre);

			pelicula.setCategoria(categoria.get(0));
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
			pelicula.persist();

			// Actualizamos el fichero para que deje de aparecer en el combo
			ficheroInsertado(fichero);

			return pelicula;
		}
		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Actualiza el registro del fichero de la imagen indicando que esta
	 * insertado
	 * 
	 * @param nombreFichero
	 */
	private static void ficheroInsertado(String nombreFichero) {
		Fichero imagen = Fichero.findFicheroByNombre(nombreFichero);
		if (imagen != null) {
			imagen.setInsertado(true);
			imagen.merge();
		}
	}

	/**
	 * Inserta los datos de una película de affinity
	 * 
	 * @param contenido
	 *            Array con todos los datos
	 * @param fichero
	 *            Nombre de la imagen
	 * @param disco
	 *            Número del disco
	 * @param archivador
	 *            Número del archivador
	 * @return Pelicula insertada
	 * @throws Exception
	 *             Se ha producido un error al intentar insertar una película de
	 *             affinity
	 */
	private Pelicula introducirDatosFilmAffinity(Map<String, String> contenido, String fichero, String disco,
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

			// Buscamos el pais
			String idPais = contenido.get("pais").replace(".jpg", "").toUpperCase();
			if (idPais.equals("GB"))
				idPais = "UK";
			Pais pais = Pais.findPais(idPais);

			pelicula.setNacionalidad(pais);
			pelicula.setDireccion(contenido.get("director"));
			pelicula.setGuion(contenido.get("guion"));
			pelicula.setMusica(contenido.get("musica"));
			pelicula.setFotografia(contenido.get("fotografia"));
			pelicula.setInterpretes(contenido.get("reparto"));
			pelicula.setDuracion(contenido.get("duracion"));

			// Cogemos la primera categoría
			String[] generos = contenido.get("genero").split(", ");

			// Buscamos la categoría con las primeras 4 letras obtenidas
			List<Categoria> categoria = null;
			for (int i = 0; i < generos.length; i++) {
				String nombre = generos[i].substring(0, 1).toUpperCase() + generos[i].substring(1, 4).toLowerCase();
				categoria = Categoria.findCategoriasByNombre(nombre);
				if (!categoria.isEmpty())
					break;
			}

			pelicula.setCategoria(categoria.get(0));
			pelicula.setSinopsis(contenido.get("sinopsis"));
			pelicula.setMontaje("");
			pelicula.setTituloOriginal(contenido.get("tituloOriginal"));
			pelicula.setImg(fichero);
			pelicula.setDisco(disco);
			pelicula.setArchivador(new Integer(archivador));
			pelicula.persist();

			// Actualizamos el fichero para que deje de aparecer en el combo
			ficheroInsertado(fichero);

			return pelicula;
		}
		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función para obtener el listado de ficheros que se quieren mostrar para
	 * ayudar con las insercciones de películas
	 * 
	 * @param id
	 *            Identificador de la película
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Listado de películas encontradas con la búsqueda dada
	 */
	@RequestMapping(value = "/ficha/{id}", produces = "text/html")
	public String verFicha(@PathVariable("id") Long id, Model uiModel) {
		log.debug("Se procede a visualizar la ficha con ID: " + id);

		// Recuperamos el usuario completo
		Usuario usuario = Usuario.findUsuario(sesion.getId());

		Pelicula pelicula = null;
		if (id != null) {
			// Obtenemos los datos de la película
			pelicula = Pelicula.findPelicula(id);
			cargaDatosFicha(uiModel, new Buscador(), pelicula);

			// Verificamos si la pelicula es una favorita del usuario conectado
			if (usuario != null) {
				boolean favorito = false;
				for (Pelicula peliculaUsuario : usuario.getPelicula())
					if (peliculaUsuario.getId().equals(id)) {
						favorito = true;
						break;
					}
				Favorito fav = new Favorito();
				fav.setFavorito(favorito);
				fav.setId(id);
				uiModel.addAttribute("favorito", fav);
			}

			return "ficha";
		} else {
			String titulo = messageSource.getMessage("mensaje_error_ficha_titulo", null, new Locale("es_ES"));
			String mensaje = messageSource.getMessage("mensaje_error_ficha", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje, new Buscador());
			return "mensaje";
		}
	}

	/**
	 * Obtiene la posición actual con respecto a la página
	 * 
	 * @param pagina
	 *            Número de la página
	 * @return
	 */
	private int obtenerPosicion(Integer pagina) {
		if (pagina == null)
			pagina = new Integer(0);
		return pagina * PAG_SIZE;
	}

	/**
	 * Carga los datos necesarios para mostrar las vistas
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @param buscador
	 *            Objeto para el buscador
	 * @param peliculas
	 *            Listado de peliculas
	 * @param buscador
	 *            Objeto para el buscador
	 */
	private void cargaDatosMensaje(Model uiModel, String titulo, String mensaje, Buscador buscador) {
		// Insertamos el objeto buscador
		uiModel.addAttribute("buscador", buscador);

		// Insertamos el listado de categorías
		uiModel.addAttribute("categorias", Categoria.findAllCategorias());

		// Insertamos los textos del mensaje
		uiModel.addAttribute("titulo", titulo);
		uiModel.addAttribute("mensaje", mensaje);
	}

	/**
	 * Carga los datos de una pelicula en concreto
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @param buscador
	 *            Objeto para el buscador
	 * @param pelicula
	 *            Datos de la película
	 */
	private void cargaDatosFicha(Model uiModel, Buscador buscador, Pelicula pelicula) {
		// Insertamos el objeto buscador
		uiModel.addAttribute("buscador", new Buscador());

		// Insertamos el listado de categorías
		uiModel.addAttribute("categorias", Categoria.findAllCategorias());

		// Insertamos los textos del mensaje
		uiModel.addAttribute("pelicula", pelicula);
	}
}
