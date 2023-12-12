package py.com.aplicacion.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

/*
* 12 dic. 2023 - Elitebook
* WebService que contendra los metodos configurados como servicios del WS.
*/
@Component
@Path("/reportesWS")
public class ReportesWs {
	
	@GET
	@Path("/op")
    public String index() {
        return "HOLAAA";
    }

}
