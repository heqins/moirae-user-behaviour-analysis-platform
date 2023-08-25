package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.entity.EventLog;
import com.report.sink.enums.EventFailReasonEnum;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private static final String EVENT_TABLE_PREFIX = "event_log_detail_";

    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private ReportEventsToDorisHandler reportEventsToDorisHandler;

    public void run(List<ConsumerRecord<String, String>> logRecords) {
        if (CollectionUtils.isEmpty(logRecords)) {
            return;
        }

        for (ConsumerRecord<String, String> record: logRecords) {
            JSONObject jsonObject = parseJson(record.value());
            if (jsonObject == null) {
                log.error("SinkHandler jsonObject null");
                continue;
            }

            String tableName = generateTableName(jsonObject);
            if (tableName == null) {
                EventLog eventLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject), EventStatusEnum.FAIL.getStatus(), EventFailReasonEnum.TABLE_NAME_ERROR.gerReason(), "保留数据");
                eventLogHandler.addEvent(eventLog);
                continue;
            }

            EventLog eventLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject), EventStatusEnum.SUCCESS.getStatus(), null, null);
            eventLogHandler.addEvent(eventLog);

            reportEventsToDorisHandler.addEvent(jsonObject, tableName);
        }
    }

    private String generateTableName(JSONObject jsonObject) {
        if (jsonObject == null || !jsonObject.containsKey("app_id") || StringUtils.isBlank(jsonObject.getStr("app_id"))) {
            return null;
        }

        return EVENT_TABLE_PREFIX + jsonObject.getStr("app_id");
    }

    private JSONObject parseJson(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj(json);
        }catch (Exception e) {
            log.error("parseJson parse error", e);
        }

        return jsonObject;
    }

    public static void main(String[] args) {
        Object test = 0;
        System.out.println(test.getClass().getCanonicalName());

    }
}
