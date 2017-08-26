package es.pys.dao;

import java.util.List;

import es.pys.model.Usuario;

public interface IUsuarioDao extends IBaseDao<Usuario> {

	/**
	 * Realiza una búsqueda de usuarios por identificador
	 * 
	 * @param id
	 *            Identificador de l usuario
	 * @return Película encontrada
	 */
	public abstract Usuario findUsuario(Long id);

	/**
	 * Realiza una búsqueda de usuarios por nombre
	 * 
	 * @param nombre
	 *            Nombre del usuario
	 * @return Lista con el usuario encontrado
	 */
	public abstract List<Usuario> findUsuario(String nombre);

}