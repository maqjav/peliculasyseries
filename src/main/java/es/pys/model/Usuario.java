package es.pys.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "usuario")
public class Usuario {

	public Usuario() {
		// Predeterminado
	}

	public Usuario(Long id, String nombre, String permisos) {
		this.id = id;
		this.nombre = nombre;
		this.permisos = permisos;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "avatar")
	private Avatar avatar;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_pelicula", joinColumns = {
			@JoinColumn(name = "USUARIO", nullable = false, updatable = false) },
			inverseJoinColumns = { @JoinColumn(name = "PELICULA", nullable = false, updatable = false) })
	private Set<Pelicula> pelicula = new HashSet<Pelicula>();

	@NotNull
	@Size(max = 8)
	@Column(name = "nombre")
	private String nombre;

	@NotNull
	@Size(max = 8)
	@Column(name = "contrasenia")
	private String contrasenia;

	@NotNull
	@Size(max = 1)
	@Column(name = "permisos")
	private String permisos;

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