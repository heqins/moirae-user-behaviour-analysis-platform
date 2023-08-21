package com.report.sink.listener;

import com.report.sink.handler.SinkHandler;
import com.report.sink.properties.DataSourceProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Slf4j
@Component
public class ReportListener {

    @Resource
    private SinkHandler sinkHandler;

    @KafkaListener(topics = "report-data-main")
    public void onMainReportMessage(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        try {
            sinkHandler.run(records);

            acknowledgment.acknowledge();
        }catch (Exception e) {
            log.error("report-main error", e);
        }
    }
}
