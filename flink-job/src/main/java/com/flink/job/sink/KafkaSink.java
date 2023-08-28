package com.flink.job.sink;

import com.flink.job.util.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

@Slf4j
public class KafkaSink {

    public static SinkFunction<String> createSink(String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("topic is null");
        }

        Properties kafkaProps = PropertyUtils.getKafkaProperty();
        // 启用自动创建主题
        kafkaProps.setProperty("auto.create.topics.enable", "false");
        return new FlinkKafkaProducer<String>(topic, new SimpleStringSchema(), kafkaProps);
    }
}
