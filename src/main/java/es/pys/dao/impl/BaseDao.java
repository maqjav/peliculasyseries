package es.pys.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import es.pys.dao.IBaseDao;

public abstract class BaseDao<T> implements IBaseDao<T> {

	@PersistenceContext
	protected EntityManager entityManager;
	
	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IBaseDao#persist(T)
	 */
	@Override
	@Transactional
	public void persist(T instancia) {
		entityManager.persist(instancia);
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IBaseDao#remove(T)
	 */
	@Override
	@Transactional
	public void remove(T instancia) {
		entityManager.remove(instancia);
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IBaseDao#flush()
	 */
	@Override
	@Transactional
	public void flush() {
		entityManager.flush();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IBaseDao#clear()
	 */
	@Override
	@Transactional
	public void clear() {
		entityManager.clear();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IBaseDao#merge(T)
	 */
	@Override
	@Transactional
	public T merge(T instancia) {
		T merged = entityManager.merge(instancia);
		entityManager.flush();
		return merged;
	}
}
