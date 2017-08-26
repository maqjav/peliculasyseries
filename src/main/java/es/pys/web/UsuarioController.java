package es.pys.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import es.pys.dao.IAvatarDao;
import es.pys.dao.IFicheroDao;
import es.pys.dao.IPeliculaDao;
import es.pys.dao.IUsuarioDao;
import es.pys.model.Avatar;
import es.pys.model.Buscador;
import es.pys.model.Favorito;
import es.pys.model.Inserccion;
import es.pys.model.Pelicula;
import es.pys.model.Registro;
import es.pys.model.Sesion;
import es.pys.model.Usuario;

@RequestMapping("/usuarios")
@Controller
public class UsuarioController extends BaseController {

	@Autowired
	private ReloadableResourceBundleMessageSource messageSource;

	@Autowired
	private Sesion sesion;
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Autowired
	private IFicheroDao ficheroDao;
	
	@Autowired
	private IPeliculaDao peliculaDao;
	
	@Autowired
	private IAvatarDao avatarDao;

	/**
	 * Función para realizar login
	 * 
	 * @param usuario
	 *            Datos del usuario que intenta hacer login
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Mensajes de error o redirección a index conectado
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult, Model uiModel,
			HttpServletRequest httpServletRequest) {
		String titulo = messageSource.getMessage("mensaje_error_usuario_titulo", null, new Locale("es_ES"));
		String mensaje;

		if (bindingResult.hasErrors())
			mensaje = messageSource.getMessage("mensaje_error_usuario", null, new Locale("es_ES"));

		else {
			// Verificamos que se han rellenado todos los campos
			if (usuario.getNombre() == null || usuario.getNombre().equals(""))
				mensaje = messageSource.getMessage("mensaje_error_nombre_vacio", null, new Locale("es_ES"));
			else if (usuario.getContrasenia() == null || usuario.getContrasenia().equals(""))
				mensaje = messageSource.getMessage("mensaje_error_contrasenia_vacio", null, new Locale("es_ES"));
			else {
				// Obtenemos el usuario de la base de datos
				List<Usuario> usuarioBd = usuarioDao.findUsuario(usuario.getNombre());

				// Verificamos que existe
				if (usuarioBd == null || usuarioBd.isEmpty())
					mensaje = messageSource.getMessage("mensaje_error_usuario_datos", null, new Locale("es_ES"));
				// Verificamos si la contraseña coincide
				else if (!usuarioBd.get(0).getContrasenia().equals(usuario.getContrasenia()))
					mensaje = messageSource.getMessage("mensaje_error_usuario_datos", null, new Locale("es_ES"));
				// Accede al portal
				else {
					sesion.setId(usuarioBd.get(0).getId());
					sesion.setPermisos(usuarioBd.get(0).getPermisos());
					sesion.setNombre(usuarioBd.get(0).getNombre());

					return "redirect:/";
				}
			}
		}

		cargaDatosMensaje(uiModel, titulo, mensaje);
		return "mensaje";
	}

	/**
	 * Función para mostrar el formulario de registro
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Redirección al formulario de registro
	 */
	@RequestMapping(value = "/registro", method = RequestMethod.GET)
	public String registro(Model uiModel) {
		// Insertamos el objeto registro
		uiModel.addAttribute("registro", new Registro());

		// Insertamos el objeto buscador
		uiModel.addAttribute("buscador", new Buscador());

		// Insertamos el listado de categorías
		uiModel.addAttribute("categorias", categoriaDao.findAllCategorias());

		return "registro";
	}

	/**
	 * Función para acceder al panel del usuario
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Redirección al panel del usuario (para un usuario normal) o al
	 *         panel de administración (para un administrador)
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String perfil(@PathVariable("id") Long id, Model uiModel,
			@RequestParam(value = "page", required = false) Integer pagina) {
		// Verificamos que el usuario en sesión es el mismo que el que intenta
		// acceder
		if (sesion.getId() != null && sesion.getId().equals(id)) {
			if (sesion.getPermisos().equals("A")) {
				// Insertamos el objeto de inserccion
				Inserccion inserccion = new Inserccion();
				inserccion.setFicheros(ficheroDao.getFicherosDisponibles());

				// Cargamos el archivador predeterminado
				inserccion.setArchivador(messageSource.getMessage("archivador", null, new Locale("es_ES")));

				// Insertamos el objeto buscador
				uiModel.addAttribute("buscador", new Buscador());

				// Insertamos el listado de categorías
				uiModel.addAttribute("categorias", categoriaDao.findAllCategorias());

				// Insertamos el objeto inserccion
				uiModel.addAttribute("inserccion", inserccion);
				return "administracion";
			} else {
				// Recuperamos el usuario completo
				Usuario usuario = usuarioDao.findUsuario(sesion.getId());

				// Mostramos el listado de favoritos
				List<Pelicula> peliculas = new ArrayList<Pelicula>();
				for (Pelicula pelicula : usuario.getPelicula()) {
					peliculas.add(pelicula);
				}

				// Mostramos el listado
				if (peliculas.isEmpty()) {
					String titulo = messageSource.getMessage("mensaje_no_encontrado_titulo", null, new Locale("es_ES"));
					String mensaje = messageSource.getMessage("mensaje_no_favoritos", null, new Locale("es_ES"));
					cargaDatosMensaje(uiModel, titulo, mensaje);
					return "mensaje";
				} else {
					Long total = new Long(peliculas.size());

					// Insertamos los datos necesarios para la portada
					cargaDatosListado(uiModel, new Buscador(), peliculas, pagina, total, false);

					// Parámetro para controlar la paginación en función de si
					// es un listado normal
					// o un listado de usuario
					uiModel.addAttribute("privado", true);

					return "index";
				}
			}
		} else {
			String titulo = messageSource.getMessage("error_perfil_titulo", null, new Locale("es_ES"));
			String mensaje = messageSource.getMessage("error_perfil", null, new Locale("es_ES"));
			cargaDatosMensaje(uiModel, titulo, mensaje);
			return "mensaje";
		}
	}

	/**
	 * Función para realizar el registro
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Redirección al formulario de registro
	 */
	@RequestMapping(value = "/registro", method = RequestMethod.POST)
	public String registro(@ModelAttribute("registro") Registro registro, BindingResult bindingResult, Model uiModel) {
		String titulo = messageSource.getMessage("mensaje_error_registro_titulo", null, new Locale("es_ES"));
		String mensaje;

		// Verificamos que se ha introducido un nombre
		if (registro.getNombre() == null || registro.getNombre().equals(""))
			mensaje = messageSource.getMessage("mensaje_error_registro_nombre", null, new Locale("es_ES"));
		// Verificamos que el nombre tiene al menos 6 letras
		else if (registro.getNombre().length() < 6)
			mensaje = messageSource.getMessage("mensaje_error_registro_nombre_longitud", null, new Locale("es_ES"));
		// Verificamos que el nombre esta libre
		else if (!usuarioDao.findUsuario(registro.getNombre()).isEmpty())
			mensaje = messageSource.getMessage("mensaje_error_registro_nombre_repetido", null, new Locale("es_ES"));
		// Verificamos que se ha introducido la contraseña
		else if (registro.getContrasenia() == null || registro.getContrasenia().equals(""))
			mensaje = messageSource.getMessage("mensaje_error_registro_contrasenia", null, new Locale("es_ES"));
		// Verificamos que se ha introducido la contraseña de verificacion
		else if (registro.getContraseniaVerifica() == null || registro.getContraseniaVerifica().equals(""))
			mensaje = messageSource.getMessage("mensaje_error_registro_contrasenia_v", null, new Locale("es_ES"));
		// Verificamos que las contraseñas son iguales
		else if (!registro.getContraseniaVerifica().equals(registro.getContrasenia()))
			mensaje = messageSource.getMessage("mensaje_error_registro_contrasenias", null, new Locale("es_ES"));
		// Verificamos si se ha adjuntado un archivo y cumple con las
		// condiciones
		else {
			mensaje = validarTipoFichero(registro.getFichero());

			// Si todo es correcto no se devuelve un mensaje con lo que se puede
			// realizar el registro
			if (mensaje == null || mensaje.equals("")) {
				// Insertamos el usuario
				Usuario usuario = rellenarUsuario(registro);
				uiModel.asMap().remove("registro");
				usuarioDao.persist(usuario);

				titulo = messageSource.getMessage("mensaje_registro_correcto_titulo", null, new Locale("es_ES"));
				mensaje = messageSource.getMessage("mensaje_registro_correcto", null, new Locale("es_ES"));
			}
		}

		cargaDatosMensaje(uiModel, titulo, mensaje);
		return "mensaje";
	}

	/**
	 * Función para guardar una película como favorito
	 * 
	 * @param id
	 *            Identificador de la película
	 * @param uiModel
	 *            Modelo de la vista
	 */
	@RequestMapping(value = "/favorito", method = RequestMethod.POST)
	public String favorito(@ModelAttribute("favorito") Favorito favorito, Model uiModel) {
		// Recuperamos el usuario completo
		Usuario usuario = usuarioDao.findUsuario(sesion.getId());

		// Recuperamos la película
		Pelicula pelicula = peliculaDao.findPelicula(favorito.getId());

		// Creamos un Favorito
		if (favorito.getFavorito()) {
			usuario.getPelicula().add(pelicula);
			usuarioDao.merge(usuario);
		}
		// Eliminamos un favorito
		else {
			for (Pelicula peli : usuario.getPelicula()) {
				if (peli.getId().equals(favorito.getId())) {
					usuario.getPelicula().remove(peli);
					break;
				}
			}

			usuarioDao.merge(usuario);
		}

		// Volvemos a mostrar la ficha
		return "redirect:/ficha/" + favorito.getId();
	}

	/**
	 * Función para generar un fichero TXT con el listado de películas favoritas
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Archivo a descargar
	 */
	@RequestMapping(value = "/exportar", produces = "text/html")
	public void generar(Model uiModel, HttpServletResponse response) {
		// Recuperamos el usuario completo
		Usuario usuario = usuarioDao.findUsuario(sesion.getId());

		String datos = "Fichero generado automáticamente \n\n";
		for (Pelicula p : usuario.getPelicula()) {
			// Montamos la sentencia
			datos += "-- " + p.getTitulo() + "\n";
		}

		// Enviamos fichero por pantalla
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(datos.getBytes(Charset.forName("UTF-8")));

			// Asignamos el nombre del fichero
			Format formatter = new SimpleDateFormat("MM_dd_yy");
			String s = formatter.format(new Date());

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/octet-stream");
			response.setContentLength(datos.getBytes().length);
			response.setHeader("Content-Disposition", "attachment; filename=" + usuario.getNombre() + "_lista_" + s
					+ ".txt");

			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Valida que el fichero es de tipo imagen
	 * 
	 * @param fichero
	 *            Fichero a validar
	 * @return Mensaje de respuesta
	 */
	private String validarTipoFichero(CommonsMultipartFile fichero) {
		try {
			// Si no se ha subido fichero se devuelve OK
			if (fichero.getSize() == 0)
				return "";

			BufferedImage imagen = ImageIO.read(fichero.getInputStream());
			// En el caso de no ser una imagen
			if (imagen == null) {
				return messageSource.getMessage("mensaje_error_registro_tipo_fichero", null, new Locale("es_ES"));
			}
			// En el caso de no medir 80x80px
			else if (imagen.getWidth() < 80 || imagen.getWidth() > 80 || imagen.getHeight() < 80
					|| imagen.getHeight() > 80) {
				return messageSource.getMessage("mensaje_error_registro_tipo_tamano", null, new Locale("es_ES"));
			}
			// Todo OK
			else
				return "";
		}
		catch (IOException e) {
			// Ignoramos el fichero en caso de error
			return "";
		}
	}

	/**
	 * Completa el objeto usuario con los datos del registro
	 * 
	 * @param registro
	 *            Datos del registro
	 * @return Usuario con todos los datos
	 */
	private Usuario rellenarUsuario(Registro registro) {
		// Creamos el usuario
		Usuario usuarioTmp = new Usuario();
		usuarioTmp.setNombre(registro.getNombre());
		usuarioTmp.setPermisos("N");
		usuarioTmp.setContrasenia(registro.getContrasenia());

		// Creamos el avatar
		if (registro.getFichero() != null) {
			Avatar avatar = new Avatar();
			avatar.setContentLength(registro.getFichero().getSize());
			avatar.setContentType(registro.getFichero().getContentType());
			try {
				avatar.setFichero(IOUtils.toByteArray(registro.getFichero().getInputStream()));
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			avatarDao.persist(avatar);
			usuarioTmp.setAvatar(avatar);
		}

		return usuarioTmp;
	}

	/**
	 * Función para invalidar la sesión
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @return Redirección a index desconectado
	 */
	@RequestMapping(value = "/desconectar", method = RequestMethod.GET)
	public String desconectar(Model uiModel, HttpSession session, SessionStatus status) {
		// Invalidamos la sesión
		session.invalidate();

		// Reseteamos el usuario en memoria
		status.setComplete();

		return "redirect:/";
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
	 */
	private void cargaDatosMensaje(Model uiModel, String titulo, String mensaje) {
		// Insertamos el objeto buscador
		uiModel.addAttribute("buscador", new Buscador());

		// Insertamos el listado de categorías
		uiModel.addAttribute("categorias", categoriaDao.findAllCategorias());

		// Insertamos los textos del mensaje
		uiModel.addAttribute("titulo", titulo);
		uiModel.addAttribute("mensaje", mensaje);
	}
}
