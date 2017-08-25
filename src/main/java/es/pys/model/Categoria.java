package es.pys.model;

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
@Table(name = "categoria")
public class Categoria {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(max = 50)
	private String nombre;

	@NotNull
	private Boolean activo;
	
	@PersistenceContext
	transient EntityManager entityManager;

	public static final EntityManager entityManager() {
		EntityManager em = new Categoria().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	/**
	 * Realiza una búsqueda de una categoría por su identificador
	 * 
	 * @param id
	 *            Identificador de la categoría
	 * @return Categoría encontrada
	 */
	public static Categoria findCategoria(Long id) {
		if (id == null)
			return null;
		return entityManager().find(Categoria.class, id);
	}

	/**
	 * Realiza una búsqueda de todas las categorías
	 * 
	 * @return Listado de categorías encontradas
	 */
	public static List<Categoria> findAllCategorias() {
		return entityManager().createQuery("SELECT o FROM Categoria o", Categoria.class).getResultList();
	}

	/**
	 * Realiza una búsqueda de categorías por nombre
	 * 
	 * @param nombre
	 *            Nombre de la categoría
	 * @return Listado de categorías encontradas
	 */
	public static List<Categoria> findCategoriasByNombre(String nombre) {
		return entityManager()
				.createQuery("SELECT o FROM Categoria o WHERE o.nombre like :nombre", Categoria.class)
				.setParameter("nombre", "%" + nombre + "%").getResultList();
	}

	@Transactional
	public void persist() {
		if (entityManager == null)
			entityManager = entityManager();
		entityManager.persist(this);
	}

	@Transactional
	public void remove() {
		if (entityManager == null)
			entityManager = entityManager();
		if (entityManager.contains(this)) {
			entityManager.remove(this);
		} else {
			Categoria attached = Categoria.findCategoria(id);
			entityManager.remove(attached);
		}
	}

	@Transactional
	public void flush() {
		if (entityManager == null)
			entityManager = entityManager();
		entityManager.flush();
	}

	@Transactional
	public void clear() {
		if (entityManager == null)
			entityManager = entityManager();
		entityManager.clear();
	}

	@Transactional
	public Categoria merge() {
		if (entityManager == null)
			entityManager = entityManager();
		Categoria merged = entityManager.merge(this);
		entityManager.flush();
		return merged;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
