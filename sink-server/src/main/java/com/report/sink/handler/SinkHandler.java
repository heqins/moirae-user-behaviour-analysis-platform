package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.entity.ReportLog;
import com.report.sink.helper.RedisHelper;
import com.report.sink.service.ICacheService;
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
    @Resource(name = "redisCacheServiceImpl")
    private ICacheService redisCacheService;

    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private FailEventLogHandler failEventLogHandler;

    @Resource
    private AppEventsHandler appEventsHandler;

    public void run(List<ConsumerRecord<String, String>> logRecords) {
        if (CollectionUtils.isEmpty(logRecords)) {
            return;
        }

        for (ConsumerRecord<String, String> record: logRecords) {
            JSONObject jsonObject = parseJson(record.value());
            if (jsonObject == null) {
                log.error("");
                continue;
            }

            eventLogHandler.addEvent(jsonObject);

            // todo: 生成表名
            String tableName = "";
            boolean isFieldValid = checkEventFields(jsonObject);

            if (!isFieldValid) {
                failEventLogHandler.addEvent(jsonObject);
            }


        }
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

    private boolean checkEventFields(JSONObject jsonObject) {
        List<String> fields = redisCacheService.getDimCache(jsonObject.getStr("app_id"));
        for (String field: fields) {

        }

        return true;
    }
}
