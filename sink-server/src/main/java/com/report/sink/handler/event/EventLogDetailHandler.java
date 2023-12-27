package com.report.sink.handler.event;

import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.enums.AttributeTypeEnum;
import com.api.common.error.SinkErrorException;
import com.api.common.model.dto.sink.EventLogDTO;
import com.api.common.model.dto.sink.MetaEventAttributeDTO;
import com.api.common.model.dto.sink.TableColumnDTO;
import com.report.sink.enums.EventFailReasonEnum;
import com.report.sink.handler.SinkHandler;
import com.report.sink.handler.meta.MetaEventHandler;
import com.report.sink.helper.DorisHelper;
import com.report.sink.model.bo.MetaEventAttribute;
import com.report.sink.service.ICacheService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Component
public class EventLogDetailHandler implements EventsHandler{
    
    private final Logger logger = LoggerFactory.getLogger(SinkHandler.class);

    private ConcurrentLinkedQueue<EventLogDTO> buffers;

    private final int capacity = 3000;

    private ScheduledExecutorService scheduledExecutorService;

    private final ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("event-log-detail-handler-thread-pool")
                .setUncaughtExceptionHandler((value, ex) -> {logger.error("event log detail handler value:{} error {}", value.getName(), ex.getMessage());})
                .build();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        buffers = new ConcurrentLinkedQueue<>();

        runSchedule();
    }

    @Resource
    private DorisHelper dorisHelper;

    @Resource
    private MetaEventHandler metaEventHandler;

    @Resource(name = "redisCacheService")
    private ICacheService redisCache;

    public void runSchedule() {
        scheduledExecutorService.scheduleAtFixedRate(this::flush, 1000, 100, TimeUnit.MILLISECONDS);
    }

    private void  alterTableColumn(EventLogDTO eventLogDTO) {
        String appId = eventLogDTO.getAppId();
        if (StringUtils.isBlank(appId)) {
            throw new SinkErrorException(EventFailReasonEnum.KEY_FIELDS_MISSING.gerReason(), "app_id为空");
        }

        List<TableColumnDTO> columns = dorisHelper.getTableColumnInfos(eventLogDTO.getDbName(), eventLogDTO.getTableName());

        Set<String> jsonFields = eventLogDTO.getFieldValueMap().keySet();
        Set<String> existFields = new HashSet<>();

        if (!CollectionUtils.isEmpty(columns)) {
            for (TableColumnDTO columnDTO: columns) {
                if (columnDTO == null) {
                    continue;
                }

                if (!eventLogDTO.getFieldValueMap().containsKey(columnDTO.getColumnName())) {
                    continue;
                }

                String columnName = columnDTO.getColumnName();
                Object objField = eventLogDTO.getFieldValueMap().get(columnName);
                if (objField == null) {
                    continue;
                }

                String objFieldType = objField.getClass().getCanonicalName();

                // 比较表字段类型是否一致
                if (!objFieldType.equals(columnDTO.getColumnType())) {
                    throw new SinkErrorException(EventFailReasonEnum.KEY_FIELDS_MISSING.gerReason(), "保留数据");
                }
            }

            existFields = columns.stream().map(TableColumnDTO::getColumnName).collect(Collectors.toSet());
        }
        // 比较上报数据和已有的字段，如果有新的字段需要更改表结构
        Set<String> newFieldKeys = getNewFieldKey(jsonFields, existFields);
        if (!CollectionUtils.isEmpty(newFieldKeys)) {
            dorisHelper.addTableColumn(eventLogDTO, newFieldKeys);

            newFieldKeys.forEach(fieldKey -> {
                MetaEventAttribute metaEventAttribute = getMetaEventAttributeFromJsonObj(eventLogDTO, fieldKey);
                if (metaEventAttribute == null) {
                    return;
                }

                metaEventHandler.addMetaAttributeEvent(metaEventAttribute);
            });
        }
    }

    private MetaEventAttribute getMetaEventAttributeFromJsonObj(EventLogDTO eventLogDTO, String fieldKey) {
        if (eventLogDTO == null || StringUtils.isBlank(fieldKey)) {
            return null;
        }

        Object fieldObj = eventLogDTO.getFieldValueMap().get(fieldKey);
        if (fieldObj == null) {
            return null;
        }

        String className = fieldObj.getClass().getCanonicalName();
        String dataType = AttributeDataTypeEnum.getDefaultDataTypeByClass(className);
        if (dataType == null) {
            return null;
        }

        MetaEventAttribute metaEventAttribute = new MetaEventAttribute();
        String appId = (String) eventLogDTO.getFieldValueMap().get("app_id");
        String eventName = (String) eventLogDTO.getFieldValueMap().get("event_name");

        metaEventAttribute.setAppId(appId);
        metaEventAttribute.setEventName(eventName);
        metaEventAttribute.setAttributeName(fieldKey);
        metaEventAttribute.setDataType(dataType);
        metaEventAttribute.setAttributeType(AttributeTypeEnum.USER_CUSTOM.getStatus());

        return metaEventAttribute;
    }

    private Set<String> getNewFieldKey(Set<String> jsonFields, Set<String> existFields) {
        Set<String> res = new HashSet<>();
        for (String jsonField: jsonFields) {
            if (!existFields.contains(jsonField)) {
                res.add(jsonField);
            }
        }

        return res;
    }
    public void addEvent(EventLogDTO eventLogDTO) {
        if (eventLogDTO == null) {
            return;
        }

        if (!checkAttributeStatusValid(eventLogDTO)) {
            throw new SinkErrorException(EventFailReasonEnum.RULE_CONTROL_ERROR.gerReason(), "字段管控");
        }

        alterTableColumn(eventLogDTO);
        insertTableData(eventLogDTO);
    }

    private boolean checkAttributeStatusValid(EventLogDTO eventLogDTO) {
        Set<String> attributeSets = eventLogDTO.getFieldValueMap().keySet();
        if (CollectionUtils.isEmpty(attributeSets)) {
            return false;
        }

        List<MetaEventAttributeDTO> attributeCache = redisCache.multiGetMetaEventAttributeCache(new ArrayList<>(attributeSets));
        if (CollectionUtils.isEmpty(attributeCache)) {
            return true;
        }

        return attributeCache.stream().allMatch(value -> value.getStatus().equals(1));
    }

    private void insertTableData(EventLogDTO eventLogDTO) {
//        if (this.buffers.size() >= this.capacity) {
//            this.flush();
//        }

        if (eventLogDTO != null && eventLogDTO.getJsonObject() != null) {
            this.buffers.add(eventLogDTO);
        }
    }

    @Override
    public void flush() {
        if (this.buffers.isEmpty()) {
            return;
        }

        boolean acquireLock = false;
        try {
            acquireLock = lock.tryLock(300, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("reportEventsToDorisHandler flush lock error", e);
        }

        if (!acquireLock) {
            return;
        }

        try {
            Map<String, List<EventLogDTO>> eventMap = new ConcurrentHashMap<>();
            EventLogDTO data;
            int count = 0;
            while ((data = this.buffers.poll()) != null && count <= this.capacity) {
                String key = data.getDbName() + ":" + data.getTableName();
                List<EventLogDTO> eventList = eventMap.getOrDefault(key, new CopyOnWriteArrayList<>());
                eventList.add(data);

                eventMap.putIfAbsent(key, eventList);
                count++;
            }

            try {
                for (Map.Entry<String, List<EventLogDTO>> entry : eventMap.entrySet()) {
                    String key = entry.getKey();
                    String[] split = key.split(":");

                    String dbName = split[0];
                    String tableName = split[1];

                    List<TableColumnDTO> tableColumnInfos = dorisHelper.getTableColumnInfos(dbName, tableName);
                    if (CollectionUtils.isEmpty(tableColumnInfos)) {
                        continue;
                    }

                    List<Map<String, Object>> jsonDataList = entry.getValue().stream().map(EventLogDTO::getFieldValueMap).collect(Collectors.toList());
                    List<String> columnNames = tableColumnInfos.stream().map(TableColumnDTO::getColumnName).collect(Collectors.toList());


                    StrJoiner strJoiner = new StrJoiner(", ");
                    StrJoiner paramJoiner = new StrJoiner(", ");

                    StringBuilder sb = new StringBuilder();
                    sb.append("INSERT INTO ");
                    sb.append(tableName);
                    sb.append(" (");
                    columnNames.forEach(strJoiner::append);
                    sb.append(strJoiner.toString());
                    sb.append(") ");
                    sb.append("VALUES (");
                    columnNames.forEach(column -> paramJoiner.append("?"));
                    sb.append(paramJoiner.toString());
                    sb.append(")");

                    String insertSql = sb.toString();

                    dorisHelper.tableInsertData(insertSql, tableColumnInfos, jsonDataList);
                }
            } catch (Exception e) {
                logger.error("EventLogDetailHandler flush error", e);
                eventMap.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws Exception {
        String jsonString = "{\"common\":{\"eventName\":\"PpsK4fRP\",\"os\":\"win\",\"uniqueId\":\"Relxgv4tDBlF\",\"appId\":\"2crdwf5q\",\"appVersion\":\"9.8.1\"},\"action\":{\"actionId\":\"CKLHt0a72g\",\"item\":\"582\",\"itemType\":\"DPrafvrx\"},\"errorData\":{\"errorCode\":\"200\",\"msg\":\"正常\"},\"ts\":1703425918105}";
    }
}
