package com.flink.job.util;

import com.api.common.entity.ReportLogPv;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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

//    public static boolean putData(Object object) throws Exception {
//        String sql = "";
//        if (object instanceof ReportLogPv) {
//            ReportLogPv pv = (ReportLogPv) object;
//            sql = String.format("insert into report_log_pv(window_start, window_end, count) values (%d, '%d', '%d')",
//                    pv.getWindowStart(), pv.getWindowEnd(), pv.getCount());
//            System.out.println(sql);
//            stmt.p
//        }
//
//        return !stmt.execute(sql);
//    }

    public static String format(String str) {
        str.replaceAll("\"", "\\\"");
        if (str.startsWith("\"")) {
            str = "\\\"" + str.substring(1, str.length() - 1) + "\\\"";
        }
        return str;
    }

}
