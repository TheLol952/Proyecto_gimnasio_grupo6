package com.sv.gimnasio.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configura el DataSource y el pool Tomcat JDBC utilizado por todos los DAO.
 *
 * Las credenciales se resuelven desde {@code spring.datasource} y los ajustes
 * del pool se enlazan desde {@code spring.datasource.tomcat}. Esta separacion
 * permite cambiar valores por ambiente sin recompilar ni guardar secretos.
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Bean(destroyMethod = "close")
    @ConfigurationProperties("spring.datasource.tomcat")
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(DataSource.class)
                .build();
    }
}
