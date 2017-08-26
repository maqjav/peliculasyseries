package es.pys.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import es.pys.dao.IFicheroDao;
import es.pys.model.Fichero;

@Repository
public class FicheroDaoImpl extends BaseDao<Fichero> implements IFicheroDao {

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IFicheroDao#findFichero(java.lang.Long)
	 */
	@Override
	public Fichero findFichero(Long id) {
		if (id == null)
			return null;
		return entityManager.find(Fichero.class, id);
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IFicheroDao#findAllFicheros()
	 */
	@Override
	public List<Fichero> findAllFicheros() {
		return entityManager.createQuery("SELECT o FROM Fichero o ORDER BY id DESC", Fichero.class).getResultList();
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IFicheroDao#findFicheroByNombre(java.lang.String)
	 */
	@Override
	public Fichero findFicheroByNombre(String nombreFichero) {
		List<Fichero> ficheros = entityManager
				.createQuery("SELECT o FROM Fichero o WHERE o.nombreFichero = :nombreFichero", Fichero.class)
				.setParameter("nombreFichero", nombreFichero).getResultList();
		if (!ficheros.isEmpty())
			return ficheros.get(0);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IFicheroDao#getFicherosDisponibles()
	 */
	@Override
	public List<String> getFicherosDisponibles() {
		// Obtenemos el listado de ficheros nuevos
		List<String> ficheros = new ArrayList<String>();
		for (Fichero fichero : findFicheroByNoInsertado()) {
			ficheros.add(fichero.getNombreFichero());
		}

		return ficheros;
	}

	/* (non-Javadoc)
	 * @see es.pys.dao.impl.IFicheroDao#setFicheros(java.util.List)
	 */
	@Override
	public void setFicheros(List<String> nombresFicheros) {
		// Obtenemos el listado de ficheros nuevos
		for (String nombreFichero : nombresFicheros) {
			// Localizamos si ya existe
			Fichero fichero = findFicheroByNombre(nombreFichero);
			if (fichero == null)
				persist(new Fichero(nombreFichero));
		}
	}
	
	/**
	 * Busca un listado de ficheros en estado sin insertar
	 * 
	 * @return Listado de ficheros encontrado
	 */
	private List<Fichero> findFicheroByNoInsertado() {
		return entityManager
				.createQuery("SELECT o FROM Fichero o WHERE o.insertado = false", Fichero.class).getResultList();
	}
}
