package es.pys.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import es.pys.dao.ICategoriaDao;
import es.pys.model.Categoria;

@Repository
public class CategoriaDaoImpl extends BaseDao<Categoria> implements ICategoriaDao {

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.ICategoriaDao#findCategoria(java.lang.Long)
	 */
	@Override
	public Categoria findCategoria(Long id) {
		if (id == null)
			return null;
		return entityManager.find(Categoria.class, id);
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.ICategoriaDao#findAllCategorias()
	 */
	@Override
	public List<Categoria> findAllCategorias() {
		return entityManager.createQuery("SELECT o FROM Categoria o", Categoria.class).getResultList();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.ICategoriaDao#findCategoriasByNombre(java.lang.String)
	 */
	@Override
	public List<Categoria> findCategoriasByNombre(String nombre) {
		return entityManager
				.createQuery("SELECT o FROM Categoria o WHERE o.nombre like :nombre", Categoria.class)
				.setParameter("nombre", "%" + nombre + "%").getResultList();
	}
}
