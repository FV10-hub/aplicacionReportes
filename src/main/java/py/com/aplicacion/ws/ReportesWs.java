package py.com.aplicacion.ws;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@POST
	@Path("/downloadReport")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response downloadReport(ParametrosReporte params) throws ClassNotFoundException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String nombreArchivo = params.getReporte() + sdf.format(System.currentTimeMillis());
			// Generar el informe y construir la respuesta
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] response = reportGenerator.generateReport(outputStream, params);
            
			// Configurar los encabezados según una condición
            Response.ResponseBuilder responseBuilder = Response.ok(response);
            if ("PDF".equalsIgnoreCase(params.getFormato())) {
                responseBuilder.header("Content-Disposition", "attachment; filename="+nombreArchivo+".pdf");
                responseBuilder.header("Content-Type", "application/pdf");
            } else if ("XLS".equalsIgnoreCase(params.getFormato())) {
                responseBuilder.header("Content-Disposition", "attachment; filename="+nombreArchivo+".xlsx");
                responseBuilder.header("Content-Type", "application/vnd.ms-excel");
                // Agregar otros encabezados específicos para Excel si es necesario
            }
         

            return responseBuilder.entity(response).build();
		} catch (IOException | JRException | SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrio un error").build();
		}
	}

}
