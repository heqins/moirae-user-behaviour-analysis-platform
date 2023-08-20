package com.init.datasource.task;

import com.api.common.util.JdbcUtil;
import com.api.common.util.MyProperties;

import java.util.Properties;

/**
 * @author heqin
 */
public class DorisTask {

    public static void main(String[] args) {
        insert();
    }

    private static void insert() {
        String driverName = MyProperties.getStrValue("doris.driver");
        String url = MyProperties.getStrValue("doris.url");
        String username = MyProperties.getStrValue("doris.username");
        String password = MyProperties.getStrValue("doris.password");
        String filePath = MyProperties.getStrValue("doris.sql-file");

        Properties properties = JdbcUtil.generateJdbcConnectionProperties(url, username, password, filePath, driverName);
        JdbcUtil.executeSqlFile(properties);
    }
}
