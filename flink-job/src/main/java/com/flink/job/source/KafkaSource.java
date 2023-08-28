package com.flink.job.source;

import com.flink.job.util.PropertyUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class KafkaSource {

    public static SourceFunction<String> getFlinkKafkaSource(String topic, String groupId) {
        Properties kafkaProperty = PropertyUtils.getKafkaProperty();
        kafkaProperty.setProperty("group.id", groupId);

        return new FlinkKafkaConsumer<String>(topic, new SimpleStringSchema(), kafkaProperty);
    }
}
