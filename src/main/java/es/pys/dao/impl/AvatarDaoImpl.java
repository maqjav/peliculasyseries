package es.pys.dao.impl;

import org.springframework.stereotype.Repository;

import es.pys.dao.IAvatarDao;
import es.pys.model.Avatar;

@Repository
public class AvatarDaoImpl extends BaseDao<Avatar> implements IAvatarDao {

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IAvatarDao#findAvatar(java.lang.Long)
	 */
	@Override
	public Avatar findAvatar(Long id) {
		return entityManager.find(Avatar.class, id);
	}
}
