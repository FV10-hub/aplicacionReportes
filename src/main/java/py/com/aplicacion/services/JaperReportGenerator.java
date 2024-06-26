package py.com.aplicacion.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import py.com.aplicacion.config.Constantes;
import py.com.aplicacion.config.DatabaseDataSource;
import py.com.aplicacion.model.ParametrosReporte;

/*
* 13 dic. 2023 - Elitebook
*/
@Component
public class JaperReportGenerator {

	@Value("${spring.datasource.driverClassName}")
	String driver;

	@Value("${spring.datasource.url}")
	String url;

	@Value("${spring.datasource.username}")
	String user;

	@Value("${spring.datasource.password}")
	String password;

	public byte[] generateReport(ByteArrayOutputStream outputStream, ParametrosReporte param)
			throws JRException, IOException, SQLException, ClassNotFoundException {

		Map<String, Object> parametros = new HashMap<String, Object>();
		for (int pos = 0; pos < param.getParametros().size(); pos++) {
			if (param.getValores().get(pos) instanceof Date)
				parametros.put(param.getParametros().get(pos), (Date) param.getValores().get(pos));

			if (param.getValores().get(pos) instanceof Long) {
				Long valor = (Long) param.getValores().get(pos);
				// Si valor es null, pasamos null, de lo contrario, pasamos el valor Long
				parametros.put(param.getParametros().get(pos), (valor != null) ? valor : null);
			}
			
			if (param.getValores().get(pos) instanceof Integer) {
				//TODO: comentado por que en alguna parte el servicio hace un cast automatico
				//parametros.put(param.getParametros().get(pos), (Integer) param.getValores().get(pos));
				Integer valor = (Integer) param.getValores().get(pos);
				// Si valor es null, pasamos null, de lo contrario, pasamos el valor Long
				parametros.put(param.getParametros().get(pos), (valor != null) ? valor.longValue() : null);
			}	
			
			if (param.getValores().get(pos) instanceof Double)
				parametros.put(param.getParametros().get(pos), (Double) param.getValores().get(pos));

			if (param.getValores().get(pos) instanceof BigDecimal)
				parametros.put(param.getParametros().get(pos), (BigDecimal) param.getValores().get(pos));

			if (param.getValores().get(pos) instanceof String)
				parametros.put(param.getParametros().get(pos), (String) param.getValores().get(pos));
		}

		String format = param.getFormato();

		try (InputStream inputStream = new FileInputStream(
				this.getAbsolutPath(param.getReporte(), param.getCodModulo()))) {
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);

			// Se asignan los parametros de conexion para el archivo de jasper.
			Class.forName(this.driver);
			Connection connection = DriverManager.getConnection(this.url, this.user, this.password);

			// Rellenar el informe con los parámetros proporcionados
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param.getParametrosMapa(), connection);

			byte[] reportBytes;
			if (format.equalsIgnoreCase("PDF")) {
				reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
			} else if (format.equalsIgnoreCase("XLS")) {
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				configuration.setOnePagePerSheet(true);
				configuration.setRemoveEmptySpaceBetweenColumns(true);
				configuration.setDetectCellType(true);
				configuration.setRemoveEmptySpaceBetweenRows(true);
				exporter.setConfiguration(configuration);

				exporter.exportReport();
				reportBytes = outputStream.toByteArray();

			} else {
				throw new IllegalArgumentException("Formato de informe no válido. Debe ser PDF o XLS.");
			}
			return reportBytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getAbsolutPath(String reportName, String modulo) {
		String separator = File.separator;
		String reportPath = Constantes.CARPETA_REPORTES_WINDOWS + separator + modulo + separator + reportName
				+ ".jasper";
		System.out.println("PATH QUE CONSULTO :::: "+reportPath);
		return reportPath;
	}
}
