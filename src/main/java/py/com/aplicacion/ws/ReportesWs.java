package py.com.aplicacion.ws;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
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
    @Path("/downloadReport/{reportName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downloadReport(
            @PathParam("reportName") String reportName,
            Map<String, Object> parameters
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
        	response = reportGenerator.generateReport(reportName, parameters);
        	// Obtener el arreglo de bytes y los encabezados desde el mapa
            byte[] reportBytes = (byte[]) response.get("reporte");
            org.springframework.http.HttpHeaders springHeaders = (org.springframework.http.HttpHeaders) response.get("cabecera");
            
            Response.ResponseBuilder responseBuilder = Response.ok(reportBytes);
            
         // Convertir los encabezados de Spring a Map<String, List<String>> y añadirlos a la respuesta
            springHeaders.forEach((key, values) -> {
                values.forEach(value -> responseBuilder.header(key, value));
            });
            
            return responseBuilder.build();
        } catch (IOException | JRException e) {
            // Manejar errores si es necesario
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

}
