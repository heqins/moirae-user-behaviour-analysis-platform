package com.flink.job.task;

import com.flink.job.config.Config;
import com.flink.job.config.Parameters;
import com.flink.job.function.ReportDataLoadFunction;
import com.flink.job.sink.KafkaSink;
import com.flink.job.source.KafkaSource;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;

import static com.flink.job.config.Parameters.*;

/**
 * @author heqin
 */
public class ReportDataTask {

    private Config config;

    public ReportDataTask(Config config) {
        this.config = config;
    }

    public void run() throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> sourceStream = environment.addSource(KafkaSource
                .getFlinkKafkaSource(config.get(REPORT_LOG_DATA_TOPIC), config.get(REPORT_LOG_DATA_GROUP_ID)));

        OutputTag<String> invalidReportDataTag = new OutputTag<String>("invalid-report-data-tag"){};

        SingleOutputStreamOperator<String> jsonMapOutputStream = sourceStream
                .process(new ReportDataLoadFunction(invalidReportDataTag));

        DataStream<String> sideOutput = jsonMapOutputStream.getSideOutput(invalidReportDataTag);

        jsonMapOutputStream.addSink(KafkaSink.createSink(config.get(LOG_ETL_MAIN_TOPIC)));
        sideOutput.addSink(KafkaSink.createSink(config.get(LOG_DATA_INVALID_TOPIC)));

        environment.execute("ReportMainTask");
    }
}
