package com.flink.job.task;

import com.flink.job.constants.ConfigConstant;
import com.flink.job.source.KafkaSource;
import com.flink.job.util.MysqlUtils;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class LogEtlMainTask {
    public static void main(String[] args) {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> sourceStream = environment.addSource(KafkaSource
                .getFlinkKafkaSource(ConfigConstant.Topics.LOG_ETL_MAIN_TOPIC, ConfigConstant.GroupIds.LOG_ETL_MAIN__GROUP_ID));



        String pvSql = "insert into report_log_pv(window_start, window_end, count) values (?, ?, ?)";
        sourceStream.addSink(JdbcSink.sink(
                pvSql,
                (ps, row) -> {
                    ps.setLong(0, row);
                },
                MysqlUtils.getJdbcConnectionOptions()));
    }
}
