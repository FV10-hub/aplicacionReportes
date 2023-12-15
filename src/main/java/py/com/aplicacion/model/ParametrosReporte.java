package py.com.aplicacion.model;

/*
* 14 dic. 2023 - Elitebook
*/

import java.util.ArrayList;
import java.util.List;

/*
* 14 dic. 2023 - Elitebook
* esta clase me va servir para formaterar la request para mis parametros desde el front
*/
public class ParametrosReporte {
	private String codModulo;
	private String reporte;
	private String formato;
	private List<String> parametros;
	private List<Object> valores;
	
	public ParametrosReporte() {
		parametros = new ArrayList<String>();
		valores = new ArrayList<Object>();
	}

	public List<String> getParametros() {
		return parametros;
	}

	public void setParametros(List<String> parametros) {
		this.parametros = parametros;
	}

	public List<Object> getValores() {
		return valores;
	}

	public void setValores(List<Object> valores) {
		this.valores = valores;
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

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}
	
}

