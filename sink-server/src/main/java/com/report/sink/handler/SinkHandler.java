package com.report.sink.handler;

import com.report.sink.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Component
@Slf4j
public class SinkHandler {

    @Resource
    private RedisHelper redisHelper;

    public void run(List<ConsumerRecord<String, String>> logRecords) {
        if (CollectionUtils.isEmpty(logRecords)) {
            return;
        }


    }
}
