package py.com.aplicacion.config;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;


/*
* 13 dic. 2023 - Elitebook
*/

@Configuration
public class DatabaseDataSource implements JRDataSource {

    @Autowired
    private DataSourceConfiguration dataSourceConfiguration;

    public DatabaseDataSource(DataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    public DataSourceConfiguration getDatabaseConfiguration() {
        return dataSourceConfiguration;
    }

    public void setDatabaseConfiguration(DataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Override
    public boolean next() throws JRException {
        return false; // Implementa esta lógica para iterar sobre los resultados de tu consulta
    }

    @Override
    public Object getFieldValue(net.sf.jasperreports.engine.JRField jrField) throws JRException {
        return null; // Implementa esta lógica para obtener los valores de los campos en cada fila
    }

    public Connection getConnection() throws SQLException {
        return DriverManager
                .getConnection(
                        dataSourceConfiguration.getUrl(),
                        dataSourceConfiguration.getUsername(),
                        dataSourceConfiguration.getPassword()
                );
    }


}

