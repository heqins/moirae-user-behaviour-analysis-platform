package com.flink.job.task;

import cn.hutool.json.JSONUtil;
import com.api.common.entity.ReportLog;
import com.api.common.entity.ReportLogPv;
import com.flink.job.config.Config;
import com.flink.job.config.Parameters;
import com.flink.job.constants.ConfigConstant;
import com.flink.job.source.KafkaSource;
import com.flink.job.util.MysqlUtils;
import com.flink.job.window.ReportPvWindow;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

import static com.flink.job.config.Parameters.*;
import static org.apache.flink.configuration.RestOptions.BIND_PORT;

/**
 * @author heqin
 */
public class LogETLTask {

    private Config config;

    public LogETLTask(Config config) {
        this.config = config;
    }

    public static void main(String[] args) throws Exception {
        ParameterTool tool = ParameterTool.fromArgs(args);
        Parameters inputParams = new Parameters(tool);
        Config config = new Config(inputParams, STRING_PARAMS, INT_PARAMS, BOOL_PARAMS);
        LogETLTask logETLTask = new LogETLTask(config);
        logETLTask.run();
    }

    public void run() throws Exception {
        StreamExecutionEnvironment environment = configureStreamExecutionEnvironment();
        environment.setParallelism(1);

        DataStreamSource<String> sourceStream = environment.addSource(KafkaSource
                .getFlinkKafkaSource(ConfigConstant.Topics.LOG_ETL_MAIN_TOPIC, ConfigConstant.GroupIds.LOG_ETL_MAIN__GROUP_ID));

        SingleOutputStreamOperator<ReportLogPv> countStream = sourceStream.map(json -> JSONUtil.toBean(json, ReportLog.class))
                .assignTimestampsAndWatermarks(new MyWatermarkAssigner())
                .filter(log -> StringUtils.isNotBlank(log.getAppName()))
                .keyBy(v -> v.getAppName())
                .timeWindow(Time.seconds(5))
                .process(new ReportPvWindow());

        countStream.print();

        countStream.addSink(new CustomSink());
        environment.execute("logEtlTask");
    }

    private StreamExecutionEnvironment configureStreamExecutionEnvironment() {
        final String localMode = config.get(LOCAL_EXECUTION_WITH_UI);

        StreamExecutionEnvironment env;
        if (localMode.isEmpty() || localMode.equals(LOCAL_MODE_DISABLE_WEB_UI)) {
            // cluster mode or disabled web UI
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        } else {
            Configuration flinkConfig = new Configuration();
            flinkConfig.set(BIND_PORT, localMode);
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(flinkConfig);
        }

        if (!localMode.isEmpty()) {
            // slower restarts inside the IDE and other local runs
            env.setRestartStrategy(
                    RestartStrategies.fixedDelayRestart(
                            10, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        }

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//        env.getCheckpointConfig().setCheckpointInterval(config.get(CHECKPOINT_INTERVAL));
//        env.getCheckpointConfig()
//                .setMinPauseBetweenCheckpoints(config.get(MIN_PAUSE_BETWEEN_CHECKPOINTS));

        return env;
    }

    public static class CustomSink extends RichSinkFunction<ReportLogPv> {
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

    public static class MyWatermarkAssigner implements AssignerWithPeriodicWatermarks<ReportLog> {

        private static final long serialVersionUID = 1L;

        @Override
        public Watermark getCurrentWatermark() {
            // 返回当前水印（Watermark），这里假设事件时间是单调递增的，直接使用系统时间作为水印
            return new Watermark(System.currentTimeMillis());
        }

        @Override
        public long extractTimestamp(ReportLog element, long previousElementTimestamp) {
            // 从元素中提取事件时间
            return element.getEventTime();
        }
    }
}
