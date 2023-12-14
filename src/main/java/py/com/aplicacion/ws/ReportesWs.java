package py.com.aplicacion.ws;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import py.com.aplicacion.config.DatabaseDataSource;
import py.com.aplicacion.model.ParametrosReporte;
import py.com.aplicacion.services.JaperReportGenerator;

/*
* 12 dic. 2023 - Elitebook
* WebService que contendra los metodos configurados como servicios del WS.
*/
@Component
@Path("/reportesWS")
public class ReportesWs {

	@Autowired
	private JaperReportGenerator reportGenerator;

	@Autowired
	private DatabaseDataSource databaseDataSource;

	@POST
	@Path("/downloadReport/{reportName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response downloadReport(@PathParam("reportName") String reportName, Map<String, Object> parameters) {
		Map<String, Object> response = new HashMap<>();
		try {
			response = reportGenerator.generateReport(reportName, parameters);
			// Obtener el arreglo de bytes y los encabezados desde el mapa
			byte[] reportBytes = (byte[]) response.get("reporte");
			org.springframework.http.HttpHeaders springHeaders = (org.springframework.http.HttpHeaders) response
					.get("cabecera");

			Response.ResponseBuilder responseBuilder = Response.ok(reportBytes);

			// Convertir los encabezados de Spring a Map<String, List<String>> y añadirlos a
			// la respuesta
			springHeaders.forEach((key, values) -> {
				values.forEach(value -> responseBuilder.header(key, value));
			});

			// return responseBuilder.build();

			response.put("mensaje", "HOLA MARICON");
			response.put("error", "OP");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("OOOPPP").build();
		} catch (IOException | JRException e) {
			// Manejar errores si es necesario
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

	@POST
	@Path("/download/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response downloadFile(@PathParam("filename") String filename) {
		// Se obtiene la imagen del archivo del classpath del proyecto.
		InputStream imageInputStream = this.getClass().getClassLoader().getResourceAsStream("images/devpredator.png");

		return Response.ok(imageInputStream, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename = export.xlsx").build();
	}

	@POST
	@Path("/prueba/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response fer(@PathParam("filename") String filename, Map<String, Object> parameters) {
		byte[] byteArray = new byte[1902];// tu arreglo de bytes
		return Response.ok(byteArray, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=export.xlsx").build();
	}

	@POST
	@Path("/generarPdf")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response geraPDF(ParametrosReporte params) {
		Map<String, Object> response = new HashMap<>();
		try {			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] bytes = generateJasperReportPDF("", "", outputStream, params);
			String nomeRelatorio = "puto.pdf";
			return Response.ok(bytes).type("application/pdf")
					.header("Content-Disposition", "filename=\"" + nomeRelatorio + "\"").build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public byte[] generateJasperReportPDF(String codModulo, String jasperReportName, ByteArrayOutputStream outputStream,
			ParametrosReporte param) {

		Map<String, Object> parametros = new HashMap<String, Object>();

		JRPdfExporter exporter = new JRPdfExporter();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			String reportLocation = "D:\\reportes\\StoArticulos.jasper";
			InputStream inputStream = new FileInputStream(reportLocation);
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, databaseDataSource.getConnection());																								// Service

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			exporter.exportReport();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in generate Report..." + e);
		} finally {
		}
		return outputStream.toByteArray();
	}

}
