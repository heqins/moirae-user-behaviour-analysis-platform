package com.report.sink.handler.event;

import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;

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

    public void sendToKafka(String message) {
        if (StringUtils.isEmpty(message)) {
            return;
        }

        String randomKey = RandomUtil.randomString(8);
        ProducerRecord<String, String> record = new ProducerRecord<>(failEventTopic, randomKey, message);

        // 使用 KafkaTemplate 批量发送消息
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
