package es.pys.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import es.pys.dao.IPeliculaDao;
import es.pys.model.Pelicula;

@Repository
public class PeliculaDaoImpl extends BaseDao<Pelicula> implements IPeliculaDao {

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#countPeliculas()
	 */
	@Override
	public long countPeliculas() {
		return entityManager.createQuery("SELECT COUNT(o) FROM Pelicula o", Long.class).getSingleResult();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#countPeliculasByCategoria(java.lang.Long)
	 */
	@Override
	public long countPeliculasByCategoria(Long idCategoria) {
		return entityManager
				.createQuery("SELECT COUNT(o) FROM Pelicula o WHERE o.categoria.id = :idCategoria", Long.class)
				.setParameter("idCategoria", idCategoria).getSingleResult();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#countPeliculasByTitulo(java.lang.String)
	 */
	@Override
	public long countPeliculasByTitulo(String titulo) {
		return entityManager
				.createQuery(
						"SELECT COUNT(o) FROM Pelicula o WHERE o.titulo like :titulo OR o.tituloOriginal like :titulo",
						Long.class).setParameter("titulo", "%" + titulo + "%").getSingleResult();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#countPeliculasByDirector(java.lang.String)
	 */
	@Override
	public long countPeliculasByDirector(String direccion) {
		return entityManager
				.createQuery("SELECT COUNT(o) FROM Pelicula o WHERE o.direccion like :direccion", Long.class)
				.setParameter("direccion", "%" + direccion + "%").getSingleResult();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#countPeliculasByInterpretes(java.lang.String)
	 */
	@Override
	public long countPeliculasByInterpretes(String interpretes) {
		return entityManager
				.createQuery("SELECT COUNT(o) FROM Pelicula o WHERE o.interpretes like :interpretes", Long.class)
				.setParameter("interpretes", "%" + interpretes + "%").getSingleResult();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#findAllPeliculas(int, int)
	 */
	@Override
	public List<Pelicula> findAllPeliculas(int posicion, int resultados) {
		return entityManager
				.createQuery("SELECT o FROM Pelicula o ORDER BY id DESC", Pelicula.class).setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#findPelicula(java.lang.Long)
	 */
	@Override
	public Pelicula findPelicula(Long id) {
		if (id == null)
			return null;
		return entityManager.find(Pelicula.class, id);
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#findPeliculasById(java.lang.Long)
	 */
	@Override
	public List<Pelicula> findPeliculasById(Long id) {
		return entityManager
				.createQuery("SELECT o FROM Pelicula o WHERE o.id > :id ORDER BY id ASC", Pelicula.class)
				.setParameter("id", id).getResultList();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#findPeliculasByTitulo(java.lang.String, int, int)
	 */
	@Override
	public List<Pelicula> findPeliculasByTitulo(String titulo, int posicion, int resultados) {
		return entityManager
				.createQuery(
						"SELECT o FROM Pelicula o WHERE o.titulo like :titulo OR o.tituloOriginal like :titulo ORDER BY id DESC",
						Pelicula.class).setParameter("titulo", "%" + titulo + "%").setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#findPeliculasByDirector(java.lang.String, int, int)
	 */
	@Override
	public List<Pelicula> findPeliculasByDirector(String direccion, int posicion, int resultados) {
		return entityManager
				.createQuery("SELECT o FROM Pelicula o WHERE o.direccion like :direccion ORDER BY id DESC",
						Pelicula.class).setParameter("direccion", "%" + direccion + "%").setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#findPeliculasByInterprete(java.lang.String, int, int)
	 */
	@Override
	public List<Pelicula> findPeliculasByInterprete(String interprete, int posicion, int resultados) {
		return entityManager
				.createQuery("SELECT o FROM Pelicula o WHERE o.interpretes like :interpretes ORDER BY id DESC",
						Pelicula.class).setParameter("interpretes", "%" + interprete + "%").setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IPeliculaDao#findPeliculasByCategoria(java.lang.Long, int, int)
	 */
	@Override
	public List<Pelicula> findPeliculasByCategoria(Long idCategoria, int posicion, int resultados) {
		return entityManager
				.createQuery("SELECT o FROM Pelicula o WHERE o.categoria.id = :idCategoria ORDER BY id DESC",
						Pelicula.class).setParameter("idCategoria", idCategoria).setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}
}
