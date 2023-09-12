package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.constant.SinkConstants;
import com.api.common.enums.AppStatusEnum;
import com.api.common.enums.MetaEventStatusEnum;
import com.api.common.model.dto.admin.AppDTO;
import com.api.common.model.dto.sink.EventLogDTO;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.model.bo.MetaEvent;
import com.report.sink.properties.DataSourceProperty;
import com.report.sink.service.IAppService;
import com.report.sink.service.IMetaEventService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author heqin
 */
@Component
public class SinkHandler {

    private final Logger logger = LoggerFactory.getLogger(SinkHandler.class);
    
    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private EventLogDetailHandler eventLogDetailHandler;

    @Resource
    private DataSourceProperty dataSourceProperty;

    @Resource
    private IAppService appService;

    @Resource
    private MetaEventHandler metaEventHandler;

    @Resource
    private IMetaEventService metaEventService;

    public void run(List<ConsumerRecord<String, String>> logRecords) {
        if (CollectionUtils.isEmpty(logRecords)) {
            return;
        }

        DataSourceProperty.DorisConfig dorisConfig = dataSourceProperty.getDoris();

        for (ConsumerRecord<String, String> record: logRecords) {
            JSONObject jsonObject = parseJson(record.value());
            if (jsonObject == null) {
                logger.warn("SinkHandler jsonObject null");
                continue;
            }

            if (!jsonObject.containsKey("app_id")) {
                logger.warn("SinkHandler jsonObject not found appId:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            String appId = jsonObject.getStr("app_id");
            AppDTO appDTO = appService.getAppInfo(appId);
            // todo: 0 -> 1
            if (appDTO == null || !Objects.equals(appDTO.getStatus(), AppStatusEnum.ENABLE.getStatus())) {
                logger.warn("SinkHandler appId not found:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            String eventName = jsonObject.getStr("event_name");
            if (eventName == null) {
                logger.warn("SinkHandler jsonObject not found eventName:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            if (!checkIfEventEnabled(appId, eventName)) {
                continue;
            }

            String tableName = SinkConstants.generateTableName(appId);

            EventLogDTO eventLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject), EventStatusEnum.SUCCESS.getStatus(), null, null);
            eventLogHandler.addEvent(eventLog);

            MetaEvent metaEvent = getMetaEvent(appId, eventName);
            metaEventHandler.addMetaEvent(metaEvent);

            eventLogDetailHandler.addEvent(jsonObject, dorisConfig != null ? dorisConfig.getDbName() : "", tableName);
        }
    }

    private MetaEvent getMetaEvent(String appId, String eventName) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(eventName)) {
            return null;
        }

        MetaEvent metaEvent = new MetaEvent();
        metaEvent.setEventName(eventName);
        metaEvent.setAppId(appId);

        return metaEvent;
    }
    private Boolean checkIfEventEnabled(String appId, String eventName) {
        MetaEvent metaEvent = metaEventService.getMetaEvent(appId, eventName);
        if (metaEvent != null && MetaEventStatusEnum.DISABLE.getStatus().equals(metaEvent.getStatus())) {
            return false;
        }

        return true;
    }

    private JSONObject parseJson(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj(json);
        }catch (Exception e) {
            logger.error("parseJson parse error", e);
        }

        return jsonObject;
    }
}
