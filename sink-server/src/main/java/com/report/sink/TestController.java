package com.report.sink;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.report.sink.handler.SinkHandler;
import com.report.sink.listener.ReportListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/stress-test")
public class TestController {

    @Resource
    private SinkHandler sinkHandler;

    @GetMapping(value = "/hello")
    public void hello() {
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
