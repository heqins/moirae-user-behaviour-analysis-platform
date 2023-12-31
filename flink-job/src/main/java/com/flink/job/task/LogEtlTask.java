package com.flink.job.task;

import cn.hutool.json.JSONUtil;
import com.flink.job.config.Config;
import com.flink.job.model.entity.EventLogPv;
import com.flink.job.sink.KafkaSink;
import com.flink.job.sink.LogPvMysqlSink;
import com.flink.job.source.KafkaSource;
import com.flink.job.window.LogPvWindow;
import com.flink.job.window.watermark.LogPvWatermarkAssigner;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

import java.util.concurrent.TimeUnit;

import static com.flink.job.config.Parameters.*;
import static org.apache.flink.configuration.RestOptions.BIND_PORT;

/**
 * @author heqin
 */
public class LogEtlTask {

    private Config config;

    public LogEtlTask(Config config) {
        this.config = config;
    }

    public void run() throws Exception {
        justRedirectStream();
//        StreamExecutionEnvironment environment = configureStreamExecutionEnvironment();
//        environment.setParallelism(1);
//
//        DataStreamSource<String> sourceStream = environment.addSource(KafkaSource
//                .getFlinkKafkaSource(config.get(LOG_ETL_MAIN_TOPIC), config.get(LOG_ETL_MAIN__GROUP_ID)));
//
//        OutputTag<String> etlOutputTag = new OutputTag<>("etl-output-tag"){};
//
//        SingleOutputStreamOperator<EventLogPv> countStream = sourceStream.map(JSONUtil::parseObj)
//                .assignTimestampsAndWatermarks(new LogPvWatermarkAssigner())
//                .filter(log -> StringUtils.isNotBlank(log.getStr("app_id")))
//                .keyBy(log -> log.getStr("app_id"))
//                .timeWindow(Time.seconds(5))
//                .process(new LogPvWindow(etlOutputTag));
//
//        //countStream.print();
//
//        DataStream<String> sideOutput = countStream.getSideOutput(etlOutputTag);
//
//        sideOutput.addSink(KafkaSink.createSink(config.get(LOG_SINK_TOPIC)));
//
//        countStream.addSink(new LogPvMysqlSink());
//        environment.execute("logEtlTask");
    }

    private void justRedirectStream() throws Exception {
        StreamExecutionEnvironment environment = configureStreamExecutionEnvironment();
        environment.setParallelism(1);

        DataStreamSource<String> sourceStream = environment.addSource(KafkaSource
                .getFlinkKafkaSource(config.get(LOG_ETL_MAIN_TOPIC), config.get(LOG_ETL_MAIN__GROUP_ID)));

        sourceStream.addSink(KafkaSink.createSink(config.get(LOG_SINK_TOPIC)));

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
}
