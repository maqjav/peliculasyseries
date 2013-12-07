package es.pys.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@Table(name = "usuario")
public class Usuario {

	public Usuario() {
		// TODO Auto-generated constructor stub
	}

	public Usuario(Long id, String nombre, String permisos) {
		this.id = id;
		this.nombre = nombre;
		this.permisos = permisos;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	private Avatar avatar;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Pelicula> pelicula = new HashSet<Pelicula>();

	@NotNull
	@Size(max = 8)
	private String nombre;

	@NotNull
	@Size(max = 8)
	private String contrasenia;

	@NotNull
	@Size(max = 1)
	private String permisos;

	@PersistenceContext
	transient EntityManager entityManager;

	public static final EntityManager entityManager() {
		EntityManager em = new Usuario().entityManager;
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
	 * @return Película encontrada
	 */
	public static Usuario findUsuario(Long id) {
		if (id == null)
			return null;
		return entityManager().find(Usuario.class, id);
	}

	/**
	 * Realiza una búsqueda de usuarios por nombre
	 * 
	 * @param nombre
	 *            Nombre del usuario
	 * @return Lista con el usuario encontrado
	 */
	public static List<Usuario> findUsuario(String nombre) {
		return entityManager()
				.createQuery("SELECT o FROM Usuario o WHERE o.nombre = :nombre", Usuario.class)
				.setParameter("nombre", nombre).getResultList();
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
			Pelicula attached = Pelicula.findPelicula(id);
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
	public Usuario merge() {
		if (entityManager == null)
			entityManager = entityManager();
		Usuario merged = entityManager.merge(this);
		entityManager.flush();
		return merged;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Avatar getAvatar() {
		return avatar;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public Set<Pelicula> getPelicula() {
		return pelicula;
	}

	public void setPelicula(Set<Pelicula> pelicula) {
		this.pelicula = pelicula;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	public String getPermisos() {
		return permisos;
	}

	public void setPermisos(String permisos) {
		this.permisos = permisos;
	}
}