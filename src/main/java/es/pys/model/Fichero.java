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

@Entity
@Table(name = "fichero")
public class Fichero {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
