package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.dto.TableFieldDTO;
import com.api.common.entity.EventLog;
import com.report.sink.enums.EventFailReasonEnum;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author heqin
 */
@Component
@Slf4j
public class SinkHandler {

    private static final String EVENT_TABLE_PREFIX = "event_log_detail_";

    @Resource(name = "redisCacheServiceImpl")
    private ICacheService redisCacheService;

    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private AppEventsHandler appEventsHandler;

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

            EventLog isFieldValid = checkEventFieldValidity(jsonObject);
            if (isFieldValid != null) {
                eventLogHandler.addEvent(isFieldValid);
                continue;
            }

            String tableName = generateTableName(jsonObject);
            if (tableName == null) {
                EventLog eventLog = transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject),
                        EventStatusEnum.FAIL.getStatus(), EventFailReasonEnum.TABLE_NAME_ERROR.gerReason(), "保留数据");

                eventLogHandler.addEvent(eventLog);
                continue;
            }

            eventLogHandler.addEvent(null);
        }
    }

    private String generateTableName(JSONObject jsonObject) {
        if (jsonObject == null || !jsonObject.containsKey("app_Id")) {
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

    private EventLog checkEventFieldValidity(JSONObject jsonObject) {
        Map<String, String> fields = redisCacheService.getAppFieldsCache(jsonObject.getStr("app_id"));
        if (fields != null) {
            for (Map.Entry<String, String> entry: fields.entrySet()) {
                if (!jsonObject.containsKey(entry.getKey())) {
                    continue;
                }

                String fieldName = entry.getKey();
                TableFieldDTO tableFieldDTO = null;
                try {
                    tableFieldDTO = JSONUtil.toBean(entry.getValue(), TableFieldDTO.class);
                }catch (Exception e) {
                    log.error("");
                    continue;
                }

                Object obj = jsonObject.get(fieldName);
                if (obj == null) {
                    continue;
                }


            }
        }

        return null;
    }

    private void test() {

    }

    private EventLog transferFromJson(JSONObject jsonObject, String dataJson, Integer status, String errorReason, String errorHandling) {
        if (jsonObject == null) {
            log.error("");
            return null;
        }

        if (status == null || !EventStatusEnum.isStatusValid(status)) {
            log.error("");
            return null;
        }

        EventLog eventLog = new EventLog();
        eventLog.setEventType(jsonObject.getStr("event_type"));
        eventLog.setStatus(status);
        eventLog.setDataJson(dataJson);
        eventLog.setAppId(jsonObject.getStr("app_id"));
        eventLog.setEventTime(jsonObject.getLong("event_time"));
        eventLog.setEventName(jsonObject.getStr("event_name"));
        eventLog.setErrorHandling(errorHandling);
        eventLog.setErrorReason(errorReason);

        return eventLog;
    }

    public static void main(String[] args) {
        Object test = 0;
        System.out.println(test.getClass().getCanonicalName());

    }
}
