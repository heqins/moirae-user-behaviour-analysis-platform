package com.report.sink.handler;

import com.api.common.entity.ReportLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DorisEventHandlerTest {

    @Resource
    private EventLogHandler handler;

    @Test
    public void flushTest() {
        addEvents();

        handler.flush();
    }

    @Test
    public void addEvents() {
        List<ReportLog> logList = new ArrayList<>();
        ReportLog log = new ReportLog();
        log.setEventName("test");
        log.setEventTime(System.currentTimeMillis());
        log.setAppName("popo");
        log.setAppVersion("3.44");

        logList.add(log);

        handler.addEvent(logList);
    }
}
