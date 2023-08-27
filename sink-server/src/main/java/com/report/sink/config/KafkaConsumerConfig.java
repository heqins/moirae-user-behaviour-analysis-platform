package com.report.sink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

/**
 * @author heqin
 */
@Configuration
public class KafkaConsumerConfig {

    @Bean(value = "batchManualFactory")
    public KafkaListenerContainerFactory<?> batchManualFactory(ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        ContainerProperties props = factory.getContainerProperties();
        props.setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        factory.setBatchListener(true);

        return factory;
    }

}
