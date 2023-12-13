package py.com.aplicacion.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

/*
* 13 dic. 2023 - Elitebook
*/
@Component
public class JaperReportGenerator {

    private DatabaseDataSource databaseDataSource;

    @Autowired
    public JaperReportGenerator(DatabaseDataSource databaseDataSource) {
        this.databaseDataSource = databaseDataSource;
    }

    public Map<String, Object> generateReport(String reportName,
                                                 Map<String, Object> params)
    throws JRException, IOException {
        String format = this.getFormatFromParams(params);
        Map<String, Object> objectForReturn= new HashMap<String, Object>();
        try (InputStream inputStream = new FileInputStream(this.getAbsolutPath(reportName))) {
            // Cargar el archivo Jasper
            //JasperReport jasperReport = JasperCompileManager.compileReport(new File(reportPath).getAbsolutePath());//(JasperReport) JRLoader.loadObject(new InputStream(reportPath));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);

            // Rellenar el informe con los parámetros proporcionados
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, this.getParamsFormated(params), databaseDataSource.getConnection());
            //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametersToReport, new JREmptyDataSource());

            // Exportar el informe a PDF o XLS
            byte[] reportBytes;
            String contentType;
            String fileExtension;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            // Configurar las cabeceras de la respuesta
            HttpHeaders headers = new HttpHeaders();
            if (format.equalsIgnoreCase("PDF")) {
                reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
                contentType = MediaType.APPLICATION_PDF_VALUE;
                fileExtension = "pdf";
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setContentDispositionFormData(reportName, reportName + sdf.format(System.currentTimeMillis()) + "." + fileExtension);
            } else if (format.equalsIgnoreCase("XLS")) {
                JRXlsxExporter exporter = new JRXlsxExporter();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
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
                contentType = "application/vnd.ms-excel";
                fileExtension = "xlsx";
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setContentDispositionFormData(reportName, reportName+ sdf.format(System.currentTimeMillis()) + "." + fileExtension);

            } else {
                throw new IllegalArgumentException("Formato de informe no válido. Debe ser PDF o XLS.");
            }
            objectForReturn.put("reporte", reportBytes);
            objectForReturn.put("cabecera", headers);
            // Enviar el archivo adjunto como respuesta
            return objectForReturn;
        } catch (IOException e) {
            e.printStackTrace();
            objectForReturn.put("errorn", e.getCause().getMessage());
            return objectForReturn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFormatFromParams(Map<String, Object> params) {
        String result = "";
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!"format".equalsIgnoreCase(key)) continue;
            result = (String) value;

        }
        return result;
    }

    private String getAbsolutPath(String reportName){
        String separator = File.separator;
        String path = Constantes.CARPETA_REPORTES_WINDOWS;
        String reportPath = path + separator + reportName + ".jasper";
        return reportPath;
    }

    private Map<String, Object> getParamsFormated(Map<String, Object> params){
        Map<String, Object> parametersToReport = new HashMap<>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (entry.getValue() instanceof Date)
                parametersToReport.put(key, (Date) entry.getValue());
            if (entry.getValue() instanceof Integer)
                parametersToReport.put(key, (Integer) entry.getValue());
            if (entry.getValue() instanceof Double)
                parametersToReport.put(key, (Double) entry.getValue());
            if (entry.getValue() instanceof Long)
                parametersToReport.put(key, (Long) entry.getValue());
            if (entry.getValue() instanceof BigDecimal)
                parametersToReport.put(key, (BigDecimal) entry.getValue());
            if (entry.getValue() instanceof String)
                parametersToReport.put(key, (String) entry.getValue());
        }
        return parametersToReport;
    }
}
