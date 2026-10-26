package com.sv.gimnasio.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class))
            .withUserConfiguration(DataSourceConfig.class);

    @Test
    void enlazaLosParametrosSolicitadosDelPool() {
        contextRunner.run(context -> {
            assertNull(context.getStartupFailure());
            DataSource dataSource = context.getBean(DataSource.class);

            assertEquals("SELECT 1", dataSource.getValidationQuery());
            assertEquals(3, dataSource.getInitialSize());
            assertEquals(3, dataSource.getMinIdle());
            assertEquals(8, dataSource.getMaxIdle());
            assertEquals(15, dataSource.getMaxActive());
            assertEquals(10_000, dataSource.getMaxWait());
            assertTrue(dataSource.isTestOnBorrow());
            assertFalse(dataSource.isTestOnReturn());
            assertTrue(dataSource.isTestWhileIdle());
            assertTrue(dataSource.isTestOnConnect());
            assertEquals(30_000, dataSource.getTimeBetweenEvictionRunsMillis());
            assertEquals(60_000, dataSource.getMinEvictableIdleTimeMillis());
            assertTrue(dataSource.isRemoveAbandoned());
            assertEquals(60, dataSource.getRemoveAbandonedTimeout());
            assertEquals(30_000, dataSource.getValidationInterval());
        });
    }
}
