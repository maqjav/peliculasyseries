package es.pys.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class Inserccion {

	private String fichero;
	private List<String> ficheros;
	private String url;
	private String disco;
	private String archivador;

	private MultipartFile paquete;

	public String getFichero() {
		return fichero;
	}

	public void setFichero(String fichero) {
		this.fichero = fichero;
	}

	public List<String> getFicheros() {
		return ficheros;
	}

	public void setFicheros(List<String> ficheros) {
		this.ficheros = ficheros;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDisco() {
		return disco;
	}

	public void setDisco(String disco) {
		this.disco = disco;
	}

	public String getArchivador() {
		return archivador;
	}

	public void setArchivador(String archivador) {
		this.archivador = archivador;
	}

	public MultipartFile getPaquete() {
		return paquete;
	}

	public void setPaquete(MultipartFile paquete) {
		this.paquete = paquete;
	}
}
