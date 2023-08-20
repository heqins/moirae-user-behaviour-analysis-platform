package com.api.common.util;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
                sqlContent.append(line);
            }
            reader.close();

            // Split SQL commands
            String[] sqlCommands = sqlContent.toString().split(";");

            // Execute SQL commands
            statement = connection.createStatement();
            for (String sqlCommand : sqlCommands) {
                boolean execute = statement.execute(sqlCommand);
            }
            // Close the resources
            statement.close();
            connection.close();
        }catch (Exception e) {
            if (statement != null) {
                try {
                    statement.close();
                }catch (SQLException se) {
                    log.error("close statement error", se);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                }catch (SQLException se) {
                    log.error("close connection error", se);
                }
            }
        }
    }
}
