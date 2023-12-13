package com.report.sink.config;

import com.report.sink.properties.KafkaProperty;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Resource
    private KafkaProperty kafkaProperty;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        KafkaProperty.Producer producer = kafkaProperty.getProducer();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperty.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producer.getKeySerializer());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producer.getValueSerializer());
        configProps.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producer.getBufferMemory());
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
        configProps.put(ProducerConfig.ACKS_CONFIG, producer.getAcks());

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
