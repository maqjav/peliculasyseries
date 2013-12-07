package es.pys.web;

import java.util.List;

import org.springframework.ui.Model;

import es.pys.model.Buscador;
import es.pys.model.Categoria;
import es.pys.model.Pelicula;

public class Comunes {

	/**
	 * Carga los datos necesarios para mostrar las listas
	 * 
	 * @param uiModel
	 *            Modelo de la vista
	 * @param buscador
	 *            Objeto para el buscador
	 * @param peliculas
	 *            Listado de peliculas
	 * @param pagina
	 *            Pagina actual en el listado
	 * @param total
	 *            Total de resultados para el listado en concreto
	 * @param flag
	 *            Especifica si venimos desde el buscador de la cabecera o desde
	 *            otro listado
	 */
	public static void cargaDatosListado(Model uiModel, Buscador buscador, List<Pelicula> peliculas, Integer pagina,
			Long total, boolean flag) {
		// Insertamos el objeto buscador
		uiModel.addAttribute("buscador", buscador);

		// Insertamos el listado de categorías
		uiModel.addAttribute("categorias", Categoria.findAllCategorias());

		// Insertamos el listado de películas
		uiModel.addAttribute("peliculas", peliculas);

		// Insertamos el número de resultados total
		uiModel.addAttribute("total", total);

		// Insertamos el flag del buscador
		uiModel.addAttribute("flag", flag);

		// Insertamos la página actual
		if (pagina == null)
			pagina = new Integer(0);

		uiModel.addAttribute("pagina", pagina);

		// Calculamos el número de páginas
		Integer paginas = new Long(total / 20).intValue();
		if (total % 20 != 0)
			paginas++;
		uiModel.addAttribute("paginas", paginas);

		// Comprobamos si se debe mostrar el botón siguiente o no
		if (((pagina + 1) * 20) >= total)
			uiModel.addAttribute("siguiente", false);
		else
			uiModel.addAttribute("siguiente", true);
	}
}
