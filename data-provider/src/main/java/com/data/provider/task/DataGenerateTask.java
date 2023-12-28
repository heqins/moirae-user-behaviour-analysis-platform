package com.data.provider.task;

import com.data.provider.generator.DefaultKafkaGenerator;

public class DataGenerateTask {

    public static void main(String[] args) {
        DefaultKafkaGenerator kafkaGenerator = new DefaultKafkaGenerator();
        kafkaGenerator.generateData();
    }
}
