package com.flink.job.task;

import com.flink.job.constants.ConfigConstant;
import com.flink.job.function.ReportDataLoadFunction;
import com.flink.job.sink.KafkaSink;
import com.flink.job.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;

/**
 * @author heqin
 */
public class ReportDataLoadTask {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> sourceStream = environment.addSource(KafkaSource
                .getFlinkKafkaSource(ConfigConstant.Topics.REPORT_LOG_DATA_TOPIC, ConfigConstant.GroupIds.REPORT_MAIN_GROUP_ID));

        OutputTag<String> invalidReportDataTag = new OutputTag<String>("invalid-report-data-tag"){};

        SingleOutputStreamOperator<String> jsonMapOutputStream = sourceStream
                .process(new ReportDataLoadFunction(invalidReportDataTag));

        jsonMapOutputStream.print();

        DataStream<String> sideOutput = jsonMapOutputStream.getSideOutput(invalidReportDataTag);

        jsonMapOutputStream.addSink(KafkaSink.createSink(ConfigConstant.Topics.LOG_ETL_MAIN_TOPIC));
        sideOutput.addSink(KafkaSink.createSink(ConfigConstant.Topics.LOG_DATA_INVALID_TOPIC));

        environment.execute("ReportMainTask");
    }
}
