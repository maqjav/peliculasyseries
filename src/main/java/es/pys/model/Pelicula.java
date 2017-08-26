package es.pys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	@JoinColumn(name = "nacionalidad")
	private Pais nacionalidad;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "categoria")
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
