package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.constant.SinkConstants;
import com.api.common.enums.AppStatusEnum;
import com.api.common.enums.MetaEventStatusEnum;
import com.api.common.error.SinkErrorException;
import com.api.common.model.dto.admin.AppDTO;
import com.api.common.model.dto.sink.EventLogDTO;
import com.report.sink.enums.EventFailReasonEnum;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.handler.event.EventLogDetailHandler;
import com.report.sink.handler.event.EventLogHandler;
import com.report.sink.handler.meta.MetaEventHandler;
import com.report.sink.model.bo.MetaEvent;
import com.report.sink.properties.DataSourceProperty;
import com.report.sink.service.IAppService;
import com.report.sink.service.IMetaEventService;
import com.report.sink.util.JsonUtil;
import io.swagger.v3.core.util.Json;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
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
    private DataSourceProperty dataSourceProperty;

    @Resource
    private IAppService appService;

    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private EventLogDetailHandler eventLogDetailHandler;

    @Resource
    private MetaEventHandler metaEventHandler;

    @Resource
    private IMetaEventService metaEventService;

    @PostConstruct
    public void init() {
        if (dataSourceProperty == null || dataSourceProperty.getDoris() == null) {
            throw new IllegalArgumentException("SinkHandler Doris Config is null!");
        }
    }

    public void run(List<ConsumerRecord<String, String>> logRecords) {
        if (CollectionUtils.isEmpty(logRecords)) {
            return;
        }

        for (ConsumerRecord<String, String> record: logRecords) {
            JSONObject jsonObject = parseJson(record.value());
            if (jsonObject == null) {
                logger.warn("SinkHandler jsonObject null");
                continue;
            }

            boolean isAppIdExist = JsonUtil.checkIfJsonFieldExist(jsonObject, "app_id");
            if (!isAppIdExist) {
                logger.warn("SinkHandler jsonObject not contains appId data:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            String appId = (String) JsonUtil.getNestedFieldValueRecursive(jsonObject, "app_id");
            AppDTO appDTO = appService.getAppInfo(appId);

            if (appDTO == null || !Objects.equals(appDTO.getStatus(), AppStatusEnum.ENABLE.getStatus())) {
                logger.warn("SinkHandler appId not found data:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            Object eventNameObj = JsonUtil.getNestedFieldValueRecursive(jsonObject, "event_name");
            if (eventNameObj == null) {
                logger.warn("SinkHandler jsonObject not found data:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            String eventName = (String) eventNameObj;
            if (!checkIfEventEnabled(appId, eventName)) {
                logger.warn("SinkHandler event not enabled data:{}", JSONUtil.toJsonStr(jsonObject));
                continue;
            }

            String tableName = SinkConstants.generateTableName(appId);

            EventLogDTO eventLog = eventLogHandler.transferFromJson(jsonObject, EventStatusEnum.SUCCESS.getStatus(), null, null);
            eventLog.setDbName(dataSourceProperty.getDoris().getDbName());
            eventLog.setTableName(tableName);
            eventLog.setEventName(eventName);
            eventLog.setAppId(appId);

            try {
                MetaEvent metaEvent = getMetaEvent(appId, eventName);
                metaEventHandler.addMetaEvent(metaEvent);

                eventLogDetailHandler.addEvent(eventLog);
            }catch (SinkErrorException se) {
                eventLog.setErrorHandling(se.getErrorHandling());
                eventLog.setErrorReason(se.getErrorMessage());
                eventLog.setStatus(EventStatusEnum.FAIL.getStatus());
            }catch (Exception e) {
                eventLog.setErrorReason(EventFailReasonEnum.UNKNOWN_ERROR.gerReason());
                eventLog.setErrorHandling(e.getMessage());
                eventLog.setStatus(EventStatusEnum.FAIL.getStatus());
            }

            eventLogHandler.addEvent(eventLog);
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
