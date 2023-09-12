package com.report.sink.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author heqin
 */
@Component
@ConfigurationProperties(prefix = "data-source")
public class DataSourceProperty {

    private DorisConfig doris;

    private MysqlConfig mysql;

    public DorisConfig getDoris() {
        return doris;
    }

    public void setDoris(DorisConfig doris) {
        this.doris = doris;
    }

    public MysqlConfig getMysql() {
        return mysql;
    }

    public void setMysql(MysqlConfig mysql) {
        this.mysql = mysql;
    }

    public static class MysqlConfig {
        private String url;

        private String username;

        private String password;

        private String driver;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }
    }

    public static class DorisConfig {
        private String url;

        private String username;

        private String password;

        private String driver;

        private String dbName;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }
    }
}
