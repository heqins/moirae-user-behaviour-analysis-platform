package com.report.sink.handler;

import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.dto.sink.LogEventDTO;
import com.api.common.dto.sink.MetaEventAttributeDTO;
import com.api.common.dto.sink.TableColumnDTO;
import com.api.common.dto.sink.EventLogDTO;
import com.report.sink.enums.EventFailReasonEnum;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.helper.DorisHelper;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Component
@Slf4j
public class DorisHandler implements EventsHandler{

    private final ReentrantLock lock = new ReentrantLock();

    private List<LogEventDTO> buffers;

    private final int capacity = 1000;

    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("report-data-doris")
                .setUncaughtExceptionHandler((value, ex) -> {log.error("");})
                .build();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        buffers = new ArrayList<>(capacity);

        runSchedule();
    }

    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private DorisHelper dorisHelper;

    @Resource
    private MetaEventHandler metaEventHandler;

    @Resource(name = "redisCacheService")
    private ICacheService redisCache;

    public void runSchedule() {
        scheduledExecutorService.scheduleAtFixedRate(this::flush, 10, 50, TimeUnit.MILLISECONDS);
    }

    private void  alterTableColumn(JSONObject jsonObject, String dbName, String tableName) {
        String appId = jsonObject.getStr("app_id");
        if (StringUtils.isBlank(appId)) {
            throw new IllegalArgumentException("no appId");
        }

        List<TableColumnDTO> columns = dorisHelper.getTableColumnInfos(dbName, tableName);

        Set<String> jsonFields = jsonObject.keySet();
        Set<String> existFields = new HashSet<>();

        if (!CollectionUtils.isEmpty(columns)) {
            for (TableColumnDTO columnDTO: columns) {
                if (columnDTO == null) {
                    continue;
                }

                if (!jsonObject.containsKey(columnDTO.getColumnName())) {
                    continue;
                }

                String columnName = columnDTO.getColumnName();
                Object objField = jsonObject.get(columnName);
                String objFieldType = objField.getClass().getCanonicalName();

                // 比较表字段类型是否一致
                if (!objFieldType.equals(columnDTO.getColumnType())) {
                    EventLogDTO failLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject),
                            EventStatusEnum.FAIL.getStatus(), EventFailReasonEnum.KEY_FIELDS_MISSING.gerReason(), "保留数据");
                    eventLogHandler.addEvent(failLog);

                    throw new IllegalStateException("字段类型不一致");
                }
            }

            existFields = columns.stream().map(TableColumnDTO::getColumnName).collect(Collectors.toSet());
        }

        // 比较上报数据和已有的字段，如果有新的字段需要更改表结构
        Set<String> newFieldKeys = getNewFieldKey(jsonFields, existFields);
        if (!CollectionUtils.isEmpty(newFieldKeys)) {
            dorisHelper.addTableColumn(dbName, tableName, jsonObject, newFieldKeys);
        }
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
    public void addEvent(JSONObject jsonObject, String dbName, String tableName) {
        if (jsonObject == null) {
            return;
        }

        if (!checkAttributeStatusValid(jsonObject)) {
            return;
        }

        try {
            alterTableColumn(jsonObject, dbName, tableName);
        }catch (IllegalArgumentException | IllegalStateException ia) {
            log.error("reportEventsToDorisHandler addEvent error", ia);
            return;
        }

        insertTableData(jsonObject, dbName, tableName);
    }

    private boolean checkAttributeStatusValid(JSONObject jsonObject) {
        Set<String> attributeSets = jsonObject.keySet();
        if (CollectionUtils.isEmpty(attributeSets)) {
            return false;
        }

        List<MetaEventAttributeDTO> attributeCache = redisCache.multiGetMetaEventAttributeCache(new ArrayList<>(attributeSets));
        if (CollectionUtils.isEmpty(attributeSets)) {
            return true;
        }

        return attributeCache.stream().allMatch(value -> value.getStatus().equals(1));
    }

    private void insertTableData(JSONObject jsonObject, String dbName, String tableName) {
        if (jsonObject != null) {
            LogEventDTO eventDTO = new LogEventDTO();
            eventDTO.setDataObject(jsonObject);
            eventDTO.setDbName(dbName);
            eventDTO.setTableName(tableName);

            this.buffers.add(eventDTO);
        }

        if (this.buffers.size() >= this.capacity) {
            this.flush();
        }
    }

    @Override
    public void flush() {
        if (this.buffers.isEmpty()) {
            return;
        }

        boolean acquireLock = false;
        try {
            acquireLock = lock.tryLock(500, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e) {
            log.error("reportEventsToDorisHandler flush lock error", e);
        }

        if (!acquireLock) {
            return;
        }

        try {
            Map<String, List<LogEventDTO>> tableGroupMap = this.buffers
                    .stream()
                    .collect(Collectors.groupingBy(logEvent -> logEvent.getDbName() + ":" + logEvent.getTableName()));

            for (Map.Entry<String, List<LogEventDTO>> entry: tableGroupMap.entrySet()) {
                String key = entry.getKey();
                String[] split = key.split(":");

                String dbName = split[0];
                String tableName = split[1];

                List<TableColumnDTO> tableColumnInfos = dorisHelper.getTableColumnInfos(dbName, tableName);
                if (CollectionUtils.isEmpty(tableColumnInfos)) {
                    continue;
                }

                List<JSONObject> jsonDataList = entry.getValue().stream().map(LogEventDTO::getDataObject).collect(Collectors.toList());
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

            this.buffers.clear();
        }finally {
            lock.unlock();
        }
    }
}
