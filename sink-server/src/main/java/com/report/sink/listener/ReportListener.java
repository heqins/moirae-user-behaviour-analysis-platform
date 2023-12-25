package com.report.sink.listener;

import cn.hutool.core.date.StopWatch;
import com.report.sink.handler.SinkHandler;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Component
public class ReportListener {

    private final Logger logger = LoggerFactory.getLogger(ReportListener.class);

    private final Counter kafkaConsumerCounter = Metrics.counter("kafka.consumer.sink.records.count");

    @Resource
    private SinkHandler sinkHandler;

    private final StopWatch stopWatch = new StopWatch();

    @KafkaListener(topics = "${kafka.topics.online-log}", containerFactory = "batchManualFactory")
    public void onlineReportMessage(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        stopWatch.start();
        try {
            sinkHandler.run(records);

            kafkaConsumerCounter.increment(records.size());
        }catch (Exception e) {
            logger.error("onlineReportMessage error", e);
        }
        stopWatch.stop();
        logger.info("消费执行效率 个数:{} 处理时间:{}ms", records.size(), stopWatch.getLastTaskTimeMillis());

        acknowledgment.acknowledge();
    }
}
