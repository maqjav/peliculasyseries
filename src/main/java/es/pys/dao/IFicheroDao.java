package es.pys.dao;

import java.util.List;

import es.pys.model.Fichero;

public interface IFicheroDao extends IBaseDao<Fichero> {

	/**
	 * Realiza una búsqueda de usuarios por identificador
	 * 
	 * @param id
	 *            Identificador de l usuario
	 * @return Fichero encontrado
	 */
	public abstract Fichero findFichero(Long id);

	/**
	 * Realiza una búsqueda de todos los ficheros
	 * 
	 * @return Listado de ficheros encontrado
	 */
	public abstract List<Fichero> findAllFicheros();

	/**
	 * Realiza una búsqueda de un fichero dado su nombre
	 * 
	 * @param nombreFichero
	 * @return
	 */
	public abstract Fichero findFicheroByNombre(String nombreFichero);

	/**
	 * Obtiene un listado de los nombres de ficheros disponibles
	 * 
	 * @return Lista con los ficheros disponibles
	 */
	public abstract List<String> getFicherosDisponibles();

	/**
	 * Añade un listado de ficheros y obtiene el listado de nombres una vez
	 * realizado el cambio
	 */
	public abstract void setFicheros(List<String> nombresFicheros);

}