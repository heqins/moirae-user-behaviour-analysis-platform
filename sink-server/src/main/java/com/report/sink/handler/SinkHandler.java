package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.dto.admin.AppDTO;
import com.api.common.dto.sink.EventLogDTO;
import com.api.common.enums.MetaEventStatusEnum;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.properties.DataSourceProperty;
import com.report.sink.service.IAppService;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private DataSourceProperty dataSourceProperty;

    @Resource
    private IAppService appService;

    @Resource(name = "redisCacheService")
    private ICacheService redisCache;

    public void run(List<ConsumerRecord<String, String>> logRecords) {
        if (CollectionUtils.isEmpty(logRecords)) {
            return;
        }

        DataSourceProperty.DorisConfig dorisConfig = dataSourceProperty.getDoris();

        for (ConsumerRecord<String, String> record: logRecords) {
            JSONObject jsonObject = parseJson(record.value());
            if (jsonObject == null) {
                log.warn("SinkHandler jsonObject null");
                continue;
            }

            if (!jsonObject.containsKey("app_id")) {
                log.warn("SinkHandler jsonObject not found appId:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            String appId = jsonObject.getStr("app_id");
            AppDTO appDTO = appService.getAppInfo(appId);
            if (appDTO == null || appDTO.getClosed()) {
                log.warn("SinkHandler appId not found:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            String eventName = jsonObject.getStr("event_name");
            if (eventName == null) {
                log.warn("SinkHandler jsonObject not found eventName:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            if (!checkIfEventEnabled(appId, eventName)) {
                continue;
            }

            String tableName = generateTableName(jsonObject, appId);

            EventLogDTO eventLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject), EventStatusEnum.SUCCESS.getStatus(), null, null);
            eventLogHandler.addEvent(eventLog);

            reportEventsToDorisHandler.addEvent(jsonObject, dorisConfig != null ? dorisConfig.getDbName() : "", tableName);
        }
    }

    private Boolean checkIfEventEnabled(String appId, String eventName) {
        Integer eventStatus = redisCache.getMetaEventStatusCache(appId, eventName);
        return Objects.equals(MetaEventStatusEnum.ENABLE.getStatus(), eventStatus);
    }

    private String generateTableName(JSONObject jsonObject, String appId) {
        if (jsonObject == null) {
            return null;
        }

        return EVENT_TABLE_PREFIX + appId;
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
}
