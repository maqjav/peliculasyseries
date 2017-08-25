package es.pys.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
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

	@PersistenceContext
	transient EntityManager entityManager;

	public static final EntityManager entityManager() {
		EntityManager em = new Pais().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	/**
	 * Realiza una búsqueda de paises por identificador
	 * 
	 * @param id
	 *            Identificador del pais
	 * @return Pais encontrada
	 */
	public static Pais findPais(String id) {
		if (id == null)
			return null;
		return entityManager().find(Pais.class, id);
	}

	/**
	 * Realiza una búsqueda de paises por nombre
	 * 
	 * @param nombre
	 *            Nombre del pais
	 * @return Pais encontrado
	 */
	public static Pais findPaisByNombre(String nombre) {
		return entityManager()
				.createQuery("SELECT o FROM Pais o WHERE o.nombreImprimible like :nombreImprimible", Pais.class)
				.setParameter("nombreImprimible", "%" + nombre + "%").getSingleResult();
	}

	/**
	 * Realiza una búsqueda de todos los paises
	 * 
	 * @return Listado de paises encontrado
	 */
	public static List<Pais> findAllPaises() {
		return entityManager().createQuery("SELECT o FROM Pais o", Pais.class).getResultList();
	}

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
