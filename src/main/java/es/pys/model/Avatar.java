package es.pys.model;

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
@Table(name = "avatar")
public class Avatar {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(max = 100)
	private String contentType;

	@NotNull
	private Long contentLength;

	@NotNull
	private byte[] fichero;

	@PersistenceContext
	transient EntityManager entityManager;

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
	 * @return Avatar encontrado
	 */
	public static Avatar findAvatar(Long id) {
		if (id == null)
			return null;
		return entityManager().find(Avatar.class, id);
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
	public Avatar merge() {
		if (entityManager == null)
			entityManager = entityManager();
		Avatar merged = entityManager.merge(this);
		entityManager.flush();
		return merged;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getContentLength() {
		return contentLength;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public byte[] getFichero() {
		return fichero;
	}

	public void setFichero(byte[] fichero) {
		this.fichero = fichero;
	}
}