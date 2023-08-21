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

    @Data
    public static class DorisConfig {
        private String url;

        private String username;

        private String password;

        private String driver;
    }
}
