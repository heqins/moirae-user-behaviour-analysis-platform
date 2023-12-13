package com.report.sink.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperty {
    private String bootstrapServers;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    private Producer producer;

    public static class Producer {
        private int retries;
        private int batchSize;
        private int bufferMemory;
        private String keySerializer;
        private String valueSerializer;
        private Properties properties;
        private int lingerMs;
        private int acks;

        public int getRetries() {
            return retries;
        }

        public void setRetries(int retries) {
            this.retries = retries;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getBufferMemory() {
            return bufferMemory;
        }

        public void setBufferMemory(int bufferMemory) {
            this.bufferMemory = bufferMemory;
        }

        public String getKeySerializer() {
            return keySerializer;
        }

        public void setKeySerializer(String keySerializer) {
            this.keySerializer = keySerializer;
        }

        public String getValueSerializer() {
            return valueSerializer;
        }

        public void setValueSerializer(String valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public int getLingerMs() {
            return lingerMs;
        }

        public void setLingerMs(int lingerMs) {
            this.lingerMs = lingerMs;
        }

        public int getAcks() {
            return acks;
        }

        public void setAcks(int acks) {
            this.acks = acks;
        }
    }

}
