package py.com.aplicacion.model;

import java.util.List;

/*
* 14 dic. 2023 - Elitebook
*/
public class ParametrosReporte {
	private String codModulo;
	private String reporte;
	private List<String> parametros;
	private List<Object> valor;
	
	public ParametrosReporte() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<String> getParametros() {
		return parametros;
	}

	public void setParametros(List<String> parametros) {
		this.parametros = parametros;
	}

	public List<Object> getValor() {
		return valor;
	}

	public void setValor(List<Object> valor) {
		this.valor = valor;
	}

	public String getCodModulo() {
		return codModulo;
	}

	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	public String getReporte() {
		return reporte;
	}

	public void setReporte(String reporte) {
		this.reporte = reporte;
	}

	
}
