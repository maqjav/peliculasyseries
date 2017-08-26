package es.pys.dao;

import java.util.List;

import es.pys.model.Pelicula;

public interface IPeliculaDao extends IBaseDao<Pelicula> {

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @return Número total de películas en la base de datos
	 */
	public abstract long countPeliculas();

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param idCategoria
	 *            Identificador de la categoría a la que pertenecen las
	 *            películas
	 * @return Número total de películas en la base de datos
	 */
	public abstract long countPeliculasByCategoria(Long idCategoria);

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param titulo
	 *            Titulo de la película
	 * @return Número total de películas en la base de datos
	 */
	public abstract long countPeliculasByTitulo(String titulo);

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param direccion
	 *            Director de la película
	 * @return Número total de películas en la base de datos
	 */
	public abstract long countPeliculasByDirector(String direccion);

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param interpretes
	 *            Interpretes de la película
	 * @return Número total de películas en la base de datos
	 */
	public abstract long countPeliculasByInterpretes(String interpretes);

	/**
	 * Realiza una búsqueda de todas las películas
	 * 
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public abstract List<Pelicula> findAllPeliculas(int posicion, int resultados);

	/**
	 * Realiza una búsqueda de peliculas por identificador
	 * 
	 * @param id
	 *            Identificador de la película
	 * @return Película encontrada
	 */
	public abstract Pelicula findPelicula(Long id);

	/**
	 * Realiza una búsqueda de peliculas por id
	 * 
	 * @param id
	 *            Identificador desde el que se quiere obtener los resultados
	 * @return Listado de películas encontrado
	 */
	public abstract List<Pelicula> findPeliculasById(Long id);

	/**
	 * Realiza una búsqueda de peliculas por título
	 * 
	 * @param titulo
	 *            Título de la película
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public abstract List<Pelicula> findPeliculasByTitulo(String titulo,
			int posicion, int resultados);

	/**
	 * Realiza una búsqueda de peliculas por director
	 * 
	 * @param direccion
	 *            Nombre del director
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public abstract List<Pelicula> findPeliculasByDirector(String direccion,
			int posicion, int resultados);

	/**
	 * Realiza una búsqueda de peliculas por intérprete
	 * 
	 * @param interprete
	 *            Nombre del intérprete
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public abstract List<Pelicula> findPeliculasByInterprete(String interprete,
			int posicion, int resultados);

	/**
	 * Realiza una búsqueda de peliculas por categoría
	 * 
	 * @param idCategoria
	 *            Identificador de la categoría
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public abstract List<Pelicula> findPeliculasByCategoria(Long idCategoria,
			int posicion, int resultados);

}