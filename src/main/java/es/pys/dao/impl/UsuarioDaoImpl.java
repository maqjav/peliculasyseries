package es.pys.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import es.pys.dao.IUsuarioDao;
import es.pys.model.Usuario;

@Repository
public class UsuarioDaoImpl extends BaseDao<Usuario> implements IUsuarioDao {

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IUsuarioDao#findUsuario(java.lang.Long)
	 */
	@Override
	public Usuario findUsuario(Long id) {
		if (id == null)
			return null;
		return entityManager.find(Usuario.class, id);
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IUsuarioDao#findUsuario(java.lang.String)
	 */
	@Override
	public List<Usuario> findUsuario(String nombre) {
		return entityManager
				.createQuery("SELECT o FROM Usuario o WHERE o.nombre = :nombre", Usuario.class)
				.setParameter("nombre", nombre).getResultList();
	}
}
