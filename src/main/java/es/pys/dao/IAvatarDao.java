package es.pys.dao;

import es.pys.model.Avatar;

public interface IAvatarDao extends IBaseDao<Avatar>{

	/**
	 * Realiza una búsqueda de usuarios por identificador
	 * 
	 * @param id
	 *            Identificador de l usuario
	 * @return Avatar encontrado
	 */
	public abstract Avatar findAvatar(Long id);

}