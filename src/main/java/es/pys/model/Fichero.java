package es.pys.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@Table(name = "fichero")
public class Fichero {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(max = 250)
	@Column(name = "nombre_fichero")
	private String nombreFichero;

	// 4Sync listado
	@Column(name = "_4sync_idl")
	private Long _4syncIdL;

	// 4Sync ficha
	@Column(name = "_4sync_idf")
	private Long _4syncIdF;

	// Google Drive listado
	@Column(name = "google_drive_idl")
	private String googleDriveIdL;

	// Google Drive ficha
	@Column(name = "google_drive_idf")
	private String googleDriveIdF;

	@NotNull
	private Boolean insertado;

	@PersistenceContext
	transient EntityManager entityManager;

	public Fichero() {
		// No se utiliza
	}

	public Fichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
		insertado = false;
	}

	public Fichero(String nombreFichero, Long _4syncIdF, Long _4syncIdL) {
		this.nombreFichero = nombreFichero;
		this._4syncIdF = _4syncIdF;
		this._4syncIdL = _4syncIdL;
		insertado = false;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new Avatar().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	/**
	 * Realiza una búsqueda de usuarios por identificador
	 * 
	 * @param id
	 *            Identificador de l usuario
	 * @return Fichero encontrado
	 */
	public static Fichero findFichero(Long id) {
		if (id == null)
			return null;
		return entityManager().find(Fichero.class, id);
	}

	/**
	 * Realiza una búsqueda de todos los ficheros
	 * 
	 * @return Listado de ficheros encontrado
	 */
	public static List<Fichero> findAllFicheros() {
		return entityManager().createQuery("SELECT o FROM Fichero o ORDER BY id DESC", Fichero.class).getResultList();
	}

	/**
	 * Realiza una búsqueda de un fichero dado su nombre
	 * 
	 * @param nombreFichero
	 * @return
	 */
	public static Fichero findFicheroByNombre(String nombreFichero) {
		List<Fichero> ficheros = entityManager()
				.createQuery("SELECT o FROM Fichero o WHERE o.nombreFichero = :nombreFichero", Fichero.class)
				.setParameter("nombreFichero", nombreFichero).getResultList();
		if (!ficheros.isEmpty())
			return ficheros.get(0);
		else
			return null;
	}

	/**
	 * Busca un listado de ficheros en estado sin insertar
	 * 
	 * @return Listado de ficheros encontrado
	 */
	private static List<Fichero> findFicheroByNoInsertado() {
		return entityManager()
				.createQuery("SELECT o FROM Fichero o WHERE o.insertado = false", Fichero.class).getResultList();
	}

	/**
	 * Elimina la instancia de la base de datos
	 */
	@Transactional
	public void remove() {
		if (entityManager == null)
			entityManager = entityManager();
		if (entityManager.contains(this)) {
			entityManager.remove(this);
		} else {
			Fichero attached = Fichero.findFichero(id);
			entityManager.remove(attached);
		}
	}

	/**
	 * Fuerza la transacción
	 */
	@Transactional
	public void flush() {
		if (entityManager == null)
			entityManager = entityManager();
		entityManager.flush();
	}

	/**
	 * Limpia la cache de transacciones
	 */
	@Transactional
	public void clear() {
		if (entityManager == null)
			entityManager = entityManager();
		entityManager.clear();
	}

	/**
	 * Actualiza la instancia en la base de datos
	 * 
	 * @return Instancia actualizada
	 */
	@Transactional
	public Fichero merge() {
		if (entityManager == null)
			entityManager = entityManager();
		Fichero merged = entityManager.merge(this);
		entityManager.flush();
		return merged;
	}

	/**
	 * Inserta la instancia en la base de datos
	 */
	@Transactional
	public void persist() {
		if (entityManager == null)
			entityManager = entityManager();
		entityManager.persist(this);
	}

	/**
	 * Obtiene un listado de los nombres de ficheros disponibles
	 * 
	 * @return Lista con los ficheros disponibles
	 */
	public static List<String> getFicherosDisponibles() {
		// Obtenemos el listado de ficheros nuevos
		List<String> ficheros = new ArrayList<String>();
		for (Fichero fichero : Fichero.findFicheroByNoInsertado()) {
			ficheros.add(fichero.getNombreFichero());
		}

		return ficheros;
	}

	/**
	 * Añade un listado de ficheros y obtiene el listado de nombres una vez
	 * realizado el cambio
	 */
	public static void setFicheros(List<String> nombresFicheros) {
		// Obtenemos el listado de ficheros nuevos
		for (String nombreFichero : nombresFicheros) {
			// Localizamos si ya existe
			Fichero fichero = Fichero.findFicheroByNombre(nombreFichero);
			if (fichero == null)
				new Fichero(nombreFichero).persist();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreFichero() {
		return nombreFichero;
	}

	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}

	public Long get_4syncIdL() {
		return _4syncIdL;
	}

	public void set_4syncIdL(Long _4syncIdL) {
		this._4syncIdL = _4syncIdL;
	}

	public Long get_4syncIdF() {
		return _4syncIdF;
	}

	public void set_4syncIdF(Long _4syncIdF) {
		this._4syncIdF = _4syncIdF;
	}

	public String getGoogleDriveIdL() {
		return googleDriveIdL;
	}

	public void setGoogleDriveIdL(String googleDriveIdL) {
		this.googleDriveIdL = googleDriveIdL;
	}

	public String getGoogleDriveIdF() {
		return googleDriveIdF;
	}

	public void setGoogleDriveIdF(String googleDriveIdF) {
		this.googleDriveIdF = googleDriveIdF;
	}

	public Boolean getInsertado() {
		return insertado;
	}

	public void setInsertado(Boolean insertado) {
		this.insertado = insertado;
	}
}
