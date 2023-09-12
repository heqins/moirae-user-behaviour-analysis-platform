package com.report.sink.listener;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
        String topic = "my-topic";
        int partition = 0;
        long offset = 12345L;
        String key = "my-key";

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("event_name", "测试");
        jsonObject.set("event_type", "测试");
        jsonObject.set("event_time", System.currentTimeMillis());
        jsonObject.set("app_id", "2crdwf5q");
        jsonObject.set("app_version", "3.14.0");
        jsonObject.set("app_beta_flag4", 3.14);
        jsonObject.set("unique_id", MD5.create().digestHex("test"));

        String value = JSONUtil.toJsonStr(jsonObject);
        ConsumerRecord<String, String> record = new ConsumerRecord<>(topic, partition, offset, key, value);

        logRecords.add(record);

        sinkHandler.run(logRecords);
    }
}
