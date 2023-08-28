package com.api.common.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author heqin
 */
public class JdbcUtil {

    public static void executeSqlFile(Properties jdbcProperties) {
        Connection connection = null;
        Statement statement = null;

        try {
            // Load the JDBC driver
            String driverName = jdbcProperties.getProperty("driver");
            String url = jdbcProperties.getProperty("url");
            String username = jdbcProperties.getProperty("username");
            String password = jdbcProperties.getProperty("password");
            String filePath = jdbcProperties.getProperty("sql-file");

            Class.forName(driverName);

            // Create a connection to the database
            connection = DriverManager.getConnection(url, username, password);

            // Read the SQL file
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder sqlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                sqlContent.append(line);
                if (!line.endsWith(";")) {
                    sqlContent.append("\n");
                }
            }

            reader.close();

            // Split SQL commands
            String[] sqlCommands = sqlContent.toString().split(";");

            // Execute SQL commands
            statement = connection.createStatement();
            for (String sqlCommand : sqlCommands) {
                statement.execute(sqlCommand);
            }

            // Close the resources
            statement.close();
            connection.close();

            System.out.println("执行sql语句成功！");
        }catch (Exception e) {
            if (statement != null) {
                try {
                    statement.close();
                }catch (SQLException se) {
                    se.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                }catch (SQLException se) {
                    se.printStackTrace();
                }
            }

            System.out.println("执行sql语句失败！");
            e.printStackTrace();
        }
    }

    public static Properties generateJdbcConnectionProperties(String url,
                                                     String username,
                                                     String password,
                                                     String filePath,
                                                     String driverName) {
        Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("password", password);
        properties.setProperty("driver", driverName);
        properties.setProperty("username", username);
        properties.setProperty("sql-file", filePath);

        return properties;
    }
}
