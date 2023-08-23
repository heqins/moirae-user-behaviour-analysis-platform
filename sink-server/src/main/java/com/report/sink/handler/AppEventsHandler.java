package com.report.sink.handler;

import com.api.common.entity.EventLog;
import org.springframework.stereotype.Component;

/**
 * @author heqin
 */
@Component
public class AppEventsHandler {

    private void alterTableColumn(EventLog eventLog) {

    }

    public void addEvent(EventLog eventLog) {
        if (eventLog == null) {
            return;
        }

        alterTableColumn(eventLog);


    }
}
