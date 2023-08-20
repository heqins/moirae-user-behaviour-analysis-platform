package com.init.datasource.task;

import com.api.common.util.JdbcUtil;
import com.api.common.util.MyProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author heqin
 */
@Slf4j
public class MysqlTask {

    public static void main(String[] args) {
        String driverName = MyProperties.getStrValue("mysql.driver");
        String url = MyProperties.getStrValue("mysql.url");
        String username = MyProperties.getStrValue("mysql.username");
        String password = MyProperties.getStrValue("mysql.password");
        String filePath = MyProperties.getStrValue("mysql.sql-file");

        Properties properties = JdbcUtil.generateJdbcConnectionProperties(url, username, password, filePath, driverName);
        JdbcUtil.executeSqlFile(properties);
    }
}
