package es.pys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "pais")
public class Pais {

	@Id
	@Size(max = 2)
	@Column(name = "iso")
	private String iso;

	@NotNull
	@Size(max = 80)
	@Column(name = "nombre_imprimible")
	private String nombreImprimible;

	@NotNull
	@Size(max = 3)
	@Column(name = "iso3")
	private String iso3;

	@NotNull
	@Column(name = "numero")
	private Integer numero;

	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getNombreImprimible() {
		return nombreImprimible;
	}

	public void setNombreImprimible(String nombreImprimible) {
		this.nombreImprimible = nombreImprimible;
	}

	public String getIso3() {
		return iso3;
	}

	public void setIso3(String iso3) {
		this.iso3 = iso3;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}
}
