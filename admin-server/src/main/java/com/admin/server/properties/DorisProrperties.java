package com.admin.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.datasource.doris")
@Component
@Data
public class DorisProrperties {

    private String url;

    private String password;

    private String username;

    private String driver;

}
