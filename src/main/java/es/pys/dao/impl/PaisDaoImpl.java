package es.pys.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import es.pys.dao.IPaisDao;
import es.pys.model.Pais;

@Repository
public class PaisDaoImpl extends BaseDao<Pais> implements IPaisDao {

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPaisDao#findPais(java.lang.String)
	 */
	@Override
	public Pais findPais(String id) {
		if (id == null)
			return null;
		return entityManager.find(Pais.class, id);
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPaisDao#findPaisByNombre(java.lang.String)
	 */
	@Override
	public Pais findPaisByNombre(String nombre) {
		return entityManager
				.createQuery("SELECT o FROM Pais o WHERE o.nombreImprimible like :nombreImprimible", Pais.class)
				.setParameter("nombreImprimible", "%" + nombre + "%").getSingleResult();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPaisDao#findAllPaises()
	 */
	@Override
	public List<Pais> findAllPaises() {
		return entityManager.createQuery("SELECT o FROM Pais o", Pais.class).getResultList();
	}
}
