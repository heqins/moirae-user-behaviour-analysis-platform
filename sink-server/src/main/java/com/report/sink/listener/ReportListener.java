package com.report.sink.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ReportListener {

    @KafkaListener(topics = "report-main-test")
    public void onMainReportMessage(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        log.info("test");
        try {
            acknowledgment.acknowledge();
        }catch (Exception e) {
            log.error("report-main error", e);
        }
    }
}
