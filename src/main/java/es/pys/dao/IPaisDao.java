package es.pys.dao;

import java.util.List;

import es.pys.model.Pais;

public interface IPaisDao extends IBaseDao<Pais> {

	/**
	 * Realiza una búsqueda de paises por identificador
	 * 
	 * @param id
	 *            Identificador del pais
	 * @return Pais encontrada
	 */
	public abstract Pais findPais(String id);

	/**
	 * Realiza una búsqueda de paises por nombre
	 * 
	 * @param nombre
	 *            Nombre del pais
	 * @return Pais encontrado
	 */
	public abstract Pais findPaisByNombre(String nombre);

	/**
	 * Realiza una búsqueda de todos los paises
	 * 
	 * @return Listado de paises encontrado
	 */
	public abstract List<Pais> findAllPaises();

}