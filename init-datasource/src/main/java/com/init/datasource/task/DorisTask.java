package com.init.datasource.task;

import com.api.common.util.JdbcUtil;
import com.api.common.util.PropertyUtil;

import java.util.Properties;

/**
 * @author heqin
 */
public class DorisTask {

    public static void main(String[] args) {
        insert();
    }

    private static void insert() {
        String driverName = PropertyUtil.getStrValue("doris.driver");
        String url = PropertyUtil.getStrValue("doris.url");
        String username = PropertyUtil.getStrValue("doris.username");
        String password = PropertyUtil.getStrValue("doris.password");
        String filePath = PropertyUtil.getStrValue("doris.sql-file");

        Properties properties = JdbcUtil.generateJdbcConnectionProperties(url, username, password, filePath, driverName);
        JdbcUtil.executeSqlFile(properties);
    }
}
