package com.flink.job.util;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;

public class MysqlUtils {
    private static String url = PropertyUtils.getStrValue("mysql.url");
    private static String username = PropertyUtils.getStrValue("mysql.username");
    private static String password = PropertyUtils.getStrValue("mysql.password");
    private static JdbcConnectionOptions jdbcConnectionOptions;

    static {
         jdbcConnectionOptions= new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(url)
                .withDriverName("com.mysql.cj.jdbc.Driver")
                .withUsername(username)
                .withPassword(password)
                .build();
    }

    public static JdbcConnectionOptions getJdbcConnectionOptions() {
        return jdbcConnectionOptions;
    }

    public static String format(String str) {
        str.replaceAll("\"", "\\\"");
        if (str.startsWith("\"")) {
            str = "\\\"" + str.substring(1, str.length() - 1) + "\\\"";
        }
        return str;
    }

}
