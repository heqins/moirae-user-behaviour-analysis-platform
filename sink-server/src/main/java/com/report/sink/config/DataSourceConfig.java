package com.report.sink.config;

import com.report.sink.properties.DataSourceProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Resource
    private DataSourceProperty dataSourceProperty;

    @Bean(name = "doris")
    public DataSource dorisDataSource() {
        DataSourceProperty.DorisConfig dorisConfig = dataSourceProperty.getDoris();
        return DataSourceBuilder.create()
                .url(dorisConfig.getUrl())
                .driverClassName(dorisConfig.getDriver())
                .password(dorisConfig.getPassword())
                .username(dorisConfig.getUsername())
                .build();
    }
}
