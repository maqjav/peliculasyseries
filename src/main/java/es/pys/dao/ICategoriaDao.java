package es.pys.dao;

import java.util.List;

import es.pys.model.Categoria;

public interface ICategoriaDao extends IBaseDao<Categoria> {

	/**
	 * Realiza una búsqueda de una categoría por su identificador
	 * 
	 * @param id
	 *            Identificador de la categoría
	 * @return Categoría encontrada
	 */
	public abstract Categoria findCategoria(Long id);

	/**
	 * Realiza una búsqueda de todas las categorías
	 * 
	 * @return Listado de categorías encontradas
	 */
	public abstract List<Categoria> findAllCategorias();

	/**
	 * Realiza una búsqueda de categorías por nombre
	 * 
	 * @param nombre
	 *            Nombre de la categoría
	 * @return Listado de categorías encontradas
	 */
	public abstract List<Categoria> findCategoriasByNombre(String nombre);

}