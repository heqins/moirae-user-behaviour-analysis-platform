package com.report.sink.handler.event;

import cn.hutool.core.util.RandomUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Component
public class FailEventsHandler {

    private final Logger logger = LoggerFactory.getLogger(FailEventsHandler.class);

    @Value("${kafka.topics.fail}")
    private String failEventTopic;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendToKafka(List<String> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }

        List<ProducerRecord<String, String>> records = messages.stream()
                .map(message -> {
                    String randomKey = RandomUtil.randomString(8);
                    return new ProducerRecord<>(failEventTopic, randomKey, message);
                })
                .collect(Collectors.toList());

        // 使用 KafkaTemplate 批量发送消息
        for (ProducerRecord<String, String> record : records) {
            ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(record);

            result.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onFailure(Throwable throwable) {
                    logger.error("send message to topic {} failed, error: {}", failEventTopic, throwable.getMessage());
                }

                @Override
                public void onSuccess(SendResult<String, String> stringStringSendResult) {
                }
            });
        }
    }
}
