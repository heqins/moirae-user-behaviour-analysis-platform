package com.init.datasource.task;

import com.api.common.util.MyProperties;

import java.sql.*;

/**
 * @author heqin
 */
public class DorisTask {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private static final String DB_URL_PATTERN = "jdbc:mysql://%s:%d/%s?rewriteBatchedStatements=true";

    private static final String HOST = "127.0.0.1"; // Leader Node host

    private static final int PORT = 9030;   // query_port of Leader Node

    private static final String DB = "user_behaviour_analysis";

    private static final String TBL = "test_1";

    private static final String USER = "admin";

    private static final String PASSWD = "my_pass";

    private static final int INSERT_BATCH_SIZE = 10000;

    public static void main(String[] args) {
        insert();
    }

    private static void insert() {
        Connection connection = null;
        Statement statement = null;

        try {
            // Load the JDBC driver
            String driverName = MyProperties.getStrValue("mysql.driver");
            String url = MyProperties.getStrValue("mysql.url");
            String username = MyProperties.getStrValue("mysql.username");
            String password = MyProperties.getStrValue("mysql.password");
            String filePath = MyProperties.getStrValue("mysql.sql-file");

            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.prepareStatement(password);

            for (int i =0; i < INSERT_BATCH_SIZE; i++) {
                stmt.setInt(1, i);
                stmt.setInt(2, i * 100);
                stmt.addBatch();
            }

            int[] res = statement.executeBatch();
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
