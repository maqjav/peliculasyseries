package es.pys.model;

import javax.validation.constraints.NotNull;

public class Buscador {

	@NotNull
	private String texto;
	
	@NotNull
	private String opcionBusqueda;
	
	@NotNull
	private Integer pagina;
	
	public String getTexto() {
		return texto;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public String getOpcionBusqueda() {
		return opcionBusqueda;
	}
	
	public void setOpcionBusqueda(String opcionBusqueda) {
		this.opcionBusqueda = opcionBusqueda;
	}

	public Integer getPagina() {
		return pagina;
	}

	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}
}
