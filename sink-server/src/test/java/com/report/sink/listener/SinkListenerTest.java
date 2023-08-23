package com.report.sink.listener;

import com.report.sink.handler.SinkHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SinkListenerTest {

    @Resource
    private SinkHandler sinkHandler;

    @Test
    public void testListener() {
        List<ConsumerRecord<String, String>> logRecords = new ArrayList<>();

    }
}
