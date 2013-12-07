package es.pys.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * 
 * @author javiermarcos
 *
 * Clase para el registro.
 * No se introduce directamente el fichero en la clase usuario para no mantener
 * tanta información en memoria cuando el usuario se da de alta.
 * 
 */
public class Registro extends Usuario {

	@NotNull
	@Size(max = 8)
	private String contraseniaVerifica;
	
	// Fichero
	private CommonsMultipartFile fichero;
	
	public String getContraseniaVerifica() {
		return contraseniaVerifica;
	}

	public void setContraseniaVerifica(String contraseniaVerifica) {
		this.contraseniaVerifica = contraseniaVerifica;
	}

	public CommonsMultipartFile getFichero() {
		return fichero;
	}

	public void setFichero(CommonsMultipartFile fichero) {
		this.fichero = fichero;
	}
}
