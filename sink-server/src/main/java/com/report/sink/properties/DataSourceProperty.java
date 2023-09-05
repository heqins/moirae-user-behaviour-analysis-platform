package com.report.sink.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author heqin
 */
@Component
@ConfigurationProperties(prefix = "data-source")
@Data
public class DataSourceProperty {

    private DorisConfig doris;

    private MysqlConfig mysql;

    @Data
    public static class MysqlConfig {
        private String url;

        private String username;

        private String password;

        private String driver;
    }

    @Data
    public static class DorisConfig {
        private String url;

        private String username;

        private String password;

        private String driver;

        private String dbName;
    }
}
