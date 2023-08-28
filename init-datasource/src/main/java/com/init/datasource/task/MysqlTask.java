package com.init.datasource.task;

import com.api.common.util.JdbcUtil;
import com.api.common.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author heqin
 */
@Slf4j
public class MysqlTask {

    public static void main(String[] args) {
        String driverName = PropertyUtil.getStrValue("mysql.driver");
        String url = PropertyUtil.getStrValue("mysql.url");
        String username = PropertyUtil.getStrValue("mysql.username");
        String password = PropertyUtil.getStrValue("mysql.password");
        String filePath = PropertyUtil.getStrValue("mysql.sql-file");

        Properties properties = JdbcUtil.generateJdbcConnectionProperties(url, username, password, filePath, driverName);
        JdbcUtil.executeSqlFile(properties);
    }
}
