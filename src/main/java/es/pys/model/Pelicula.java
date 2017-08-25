package es.pys.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@Table(name = "pelicula")
public class Pelicula {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(max = 200)
	@Column(name = "titulo")
	private String titulo;

	@NotNull
	@Size(max = 200)
	@Column(name = "titulo_original")
	private String tituloOriginal;

	@NotNull
	@Column(name = "fecha")
	private Integer fecha;

	@ManyToOne
	@NotNull
	@Column(name = "nacionalidad")
	private Pais nacionalidad;

	@ManyToOne
	@NotNull
	@Column(name = "categoria")
	private Categoria categoria;

	@NotNull
	@Size(max = 200)
	@Column(name = "direccion")
	private String direccion;

	@Size(max = 20)
	@Column(name = "duracion")
	private String duracion;

	@Size(max = 500)
	@Column(name = "interpretes")
	private String interpretes;

	@Size(max = 200)
	@Column(name = "guion")
	private String guion;

	@Size(max = 300)
	@Column(name = "fotografia")
	private String fotografia;

	@Size(max = 200)
	@Column(name = "musica")
	private String musica;

	@Size(max = 200)
	@Column(name = "montaje")
	private String montaje;

	@Size(max = 20)
	@Column(name = "disco")
	private String disco;

	@Column(name = "archivador")
	private Integer archivador;

	@NotNull
	@Column(name = "sinopsis", columnDefinition = "TEXT")
	private String sinopsis;

	@NotNull
	@Size(max = 200)
	@Column(name = "img")
	private String img;

	@PersistenceContext
	transient EntityManager entityManager;

	public static final EntityManager entityManager() {
		EntityManager em = new Pelicula().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @return Número total de películas en la base de datos
	 */
	public static long countPeliculas() {
		return entityManager().createQuery("SELECT COUNT(o) FROM Pelicula o", Long.class).getSingleResult();
	}

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param idCategoria
	 *            Identificador de la categoría a la que pertenecen las
	 *            películas
	 * @return Número total de películas en la base de datos
	 */
	public static long countPeliculasByCategoria(Long idCategoria) {
		return entityManager()
				.createQuery("SELECT COUNT(o) FROM Pelicula o WHERE o.categoria.id = :idCategoria", Long.class)
				.setParameter("idCategoria", idCategoria).getSingleResult();
	}

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param titulo
	 *            Titulo de la película
	 * @return Número total de películas en la base de datos
	 */
	public static long countPeliculasByTitulo(String titulo) {
		return entityManager()
				.createQuery(
						"SELECT COUNT(o) FROM Pelicula o WHERE o.titulo like :titulo OR o.tituloOriginal like :titulo",
						Long.class).setParameter("titulo", "%" + titulo + "%").getSingleResult();
	}

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param direccion
	 *            Director de la película
	 * @return Número total de películas en la base de datos
	 */
	public static long countPeliculasByDirector(String direccion) {
		return entityManager()
				.createQuery("SELECT COUNT(o) FROM Pelicula o WHERE o.direccion like :direccion", Long.class)
				.setParameter("direccion", "%" + direccion + "%").getSingleResult();
	}

	/**
	 * Realiza una consulta para obtener el número total de películas en la base
	 * de datos
	 * 
	 * @param interpretes
	 *            Interpretes de la película
	 * @return Número total de películas en la base de datos
	 */
	public static long countPeliculasByInterpretes(String interpretes) {
		return entityManager()
				.createQuery("SELECT COUNT(o) FROM Pelicula o WHERE o.interpretes like :interpretes", Long.class)
				.setParameter("interpretes", "%" + interpretes + "%").getSingleResult();
	}

	/**
	 * Realiza una búsqueda de todas las películas
	 * 
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public static List<Pelicula> findAllPeliculas(int posicion, int resultados) {
		return entityManager()
				.createQuery("SELECT o FROM Pelicula o ORDER BY id DESC", Pelicula.class).setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/**
	 * Realiza una búsqueda de peliculas por identificador
	 * 
	 * @param id
	 *            Identificador de la película
	 * @return Película encontrada
	 */
	public static Pelicula findPelicula(Long id) {
		if (id == null)
			return null;
		return entityManager().find(Pelicula.class, id);
	}

	/**
	 * Realiza una búsqueda de peliculas por id
	 * 
	 * @param id
	 *            Identificador desde el que se quiere obtener los resultados
	 * @return Listado de películas encontrado
	 */
	public static List<Pelicula> findPeliculasById(Long id) {
		return entityManager()
				.createQuery("SELECT o FROM Pelicula o WHERE o.id > :id ORDER BY id ASC", Pelicula.class)
				.setParameter("id", id).getResultList();
	}

	/**
	 * Realiza una búsqueda de peliculas por título
	 * 
	 * @param titulo
	 *            Título de la película
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public static List<Pelicula> findPeliculasByTitulo(String titulo, int posicion, int resultados) {
		return entityManager()
				.createQuery(
						"SELECT o FROM Pelicula o WHERE o.titulo like :titulo OR o.tituloOriginal like :titulo ORDER BY id DESC",
						Pelicula.class).setParameter("titulo", "%" + titulo + "%").setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/**
	 * Realiza una búsqueda de peliculas por director
	 * 
	 * @param direccion
	 *            Nombre del director
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public static List<Pelicula> findPeliculasByDirector(String direccion, int posicion, int resultados) {
		return entityManager()
				.createQuery("SELECT o FROM Pelicula o WHERE o.direccion like :direccion ORDER BY id DESC",
						Pelicula.class).setParameter("direccion", "%" + direccion + "%").setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/**
	 * Realiza una búsqueda de peliculas por intérprete
	 * 
	 * @param interprete
	 *            Nombre del intérprete
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public static List<Pelicula> findPeliculasByInterprete(String interprete, int posicion, int resultados) {
		return entityManager()
				.createQuery("SELECT o FROM Pelicula o WHERE o.interpretes like :interpretes ORDER BY id DESC",
						Pelicula.class).setParameter("interpretes", "%" + interprete + "%").setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
	}

	/**
	 * Realiza una búsqueda de peliculas por categoría
	 * 
	 * @param idCategoria
	 *            Identificador de la categoría
	 * @param posicion
	 *            Índice que indica la posición del total
	 * @param resultados
	 *            Número de resultados a mostrar
	 * @return Listado de películas encontrado
	 */
	public static List<Pelicula> findPeliculasByCategoria(Long idCategoria, int posicion, int resultados) {
		return entityManager()
				.createQuery("SELECT o FROM Pelicula o WHERE o.categoria.id = :idCategoria ORDER BY id DESC",
						Pelicula.class).setParameter("idCategoria", idCategoria).setFirstResult(posicion)
				.setMaxResults(resultados).getResultList();
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
	public Pelicula merge() {
		if (entityManager == null)
			entityManager = entityManager();
		Pelicula merged = entityManager.merge(this);
		entityManager.flush();
		return merged;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTituloOriginal() {
		return tituloOriginal;
	}

	public void setTituloOriginal(String tituloOriginal) {
		this.tituloOriginal = tituloOriginal;
	}

	public Integer getFecha() {
		return fecha;
	}

	public void setFecha(Integer fecha) {
		this.fecha = fecha;
	}

	public Pais getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(Pais nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getDuracion() {
		return duracion;
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	public String getInterpretes() {
		return interpretes;
	}

	public void setInterpretes(String interpretes) {
		this.interpretes = interpretes;
	}

	public String getGuion() {
		return guion;
	}

	public void setGuion(String guion) {
		this.guion = guion;
	}

	public String getFotografia() {
		return fotografia;
	}

	public void setFotografia(String fotografia) {
		this.fotografia = fotografia;
	}

	public String getMusica() {
		return musica;
	}

	public void setMusica(String musica) {
		this.musica = musica;
	}

	public String getMontaje() {
		return montaje;
	}

	public void setMontaje(String montaje) {
		this.montaje = montaje;
	}

	public String getDisco() {
		return disco;
	}

	public void setDisco(String disco) {
		this.disco = disco;
	}

	public Integer getArchivador() {
		return archivador;
	}

	public void setArchivador(Integer archivador) {
		this.archivador = archivador;
	}

	public String getSinopsis() {
		return sinopsis;
	}

	public void setSinopsis(String sinopsis) {
		this.sinopsis = sinopsis;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	@Override
	public String toString() {
		String datos = "";
		datos += "\n ID: " + id;
		datos += "\n TITULO: " + titulo;
		datos += "\n TITULO ORIGINAL: " + tituloOriginal;
		datos += "\n FECHA: " + fecha;
		datos += "\n NACIONALIDAD: " + (nacionalidad != null ? nacionalidad.getNombreImprimible() : "");
		datos += "\n CATEGORIA: " + (categoria != null ? categoria.getNombre() : "");
		datos += "\n DIRECCION: " + direccion;
		datos += "\n DURACCION: " + duracion;
		datos += "\n INTERPRETES: " + interpretes;
		datos += "\n GUION: " + guion;
		datos += "\n FOTOGRAFIA: " + fotografia;
		datos += "\n MUSICA: " + musica;
		datos += "\n DISCO: " + disco;
		datos += "\n ARCHIVADOR: " + archivador;
		datos += "\n SINOPSIS: " + sinopsis;
		datos += "\n IMG: " + img;
		return datos;
	}
}
