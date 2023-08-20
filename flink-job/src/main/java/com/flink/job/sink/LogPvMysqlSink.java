package com.flink.job.sink;

import com.api.common.entity.ReportLogPv;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class LogPvMysqlSink extends RichSinkFunction<ReportLogPv> {
    // 声明连接和预编译语句
    Connection connection = null;
    PreparedStatement insertStmt = null;
    PreparedStatement updateStmt = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_behaviour_analysis?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai", "root", "5323105");
        insertStmt = connection.prepareStatement("insert into report_log_pv(window_start, window_end, count, app_name) values (?, ?, ?, ?)");
    }

    @Override
    public void close() throws Exception {
        super.close();
        insertStmt.close();
        connection.close();
    }

    @Override
    public void invoke(ReportLogPv value, Context context) throws Exception {
        super.invoke(value, context);
        insertStmt.setLong(1, value.getWindowStart());
        insertStmt.setLong(2, value.getWindowEnd());
        insertStmt.setLong(3, value.getCount());
        insertStmt.setString(4, value.getAppName());
        insertStmt.execute();
    }
}
