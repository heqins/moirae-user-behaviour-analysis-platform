package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import com.api.common.entity.EventLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DorisEventHandlerTest {

    @Resource
    private EventLogHandler handler;

    @Test
    public void flushTest() {
        addEvent();

        handler.flush();
    }

    @Test
    public void addEvent() {
        EventLog log = new EventLog();
//        log.set("event_name", "test");
//        log.set("event_time", System.currentTimeMillis());
//        log.set("app_id", "popo");
//        log.set("app_version", "3.44");

        handler.addEvent(log);
    }
}
