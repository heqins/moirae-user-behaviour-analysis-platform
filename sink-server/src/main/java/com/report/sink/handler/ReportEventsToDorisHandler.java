package com.report.sink.handler;

import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.dto.LogEventDTO;
import com.api.common.dto.TableColumnDTO;
import com.api.common.entity.EventLog;
import com.report.sink.enums.EventFailReasonEnum;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.helper.DorisHelper;
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
public class ReportEventsToDorisHandler implements EventsHandler{

    private final ReentrantLock lock = new ReentrantLock();

    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private DorisHelper dorisHelper;

    private List<LogEventDTO> buffers;

    private int capacity;

    private ScheduledExecutorService scheduledExecutorService;

    public ReportEventsToDorisHandler() {}

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("report-data-doris")
                .setUncaughtExceptionHandler((value, ex) -> {log.error("");})
                .build();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        buffers = new ArrayList<>(1000);

        this.runSchedule();
    }

    public void runSchedule() {
        scheduledExecutorService.scheduleAtFixedRate(this::flush, 10, 50, TimeUnit.MILLISECONDS);
    }

    private void alterTableColumn(JSONObject jsonObject, String dbName, String tableName) {
        String appId = jsonObject.getStr("app_id");
        if (StringUtils.isBlank(appId)) {
            EventLog failLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject),
                    EventStatusEnum.FAIL.getStatus(), EventFailReasonEnum.KEY_FIELDS_MISSING.gerReason(),
                    "保留数据");
            eventLogHandler.addEvent(failLog);
            return;
        }
        List<TableColumnDTO> columns = new ArrayList<>();
        try {
            columns = dorisHelper.getTableColumnInfos(dbName, tableName);
        }catch (IllegalStateException e) {
            log.error("reportEventsToDorisHandler error", e);
            return;
        }

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
                    EventLog failLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject),
                            EventStatusEnum.FAIL.getStatus(), EventFailReasonEnum.KEY_FIELDS_MISSING.gerReason(), "保留数据");
                    eventLogHandler.addEvent(failLog);
                }
            }

            existFields = columns.stream().map(TableColumnDTO::getColumnName).collect(Collectors.toSet());
        }

        // 比较上报数据和已有的字段，如果有新的字段需要更改表结构
        Set<String> newFieldKeys = getNewFieldKey(jsonFields, existFields);
        if (!CollectionUtils.isEmpty(newFieldKeys)) {
            dorisHelper.changeTableSchema(dbName, tableName, jsonObject, newFieldKeys);
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

        alterTableColumn(jsonObject, dbName, tableName);

        insertTableData(jsonObject, dbName, tableName);
    }

    private void insertTableData(JSONObject jsonObject, String dbName, String tableName) {
        lock.lock();
        try {
            if (jsonObject != null) {
                LogEventDTO eventDTO = new LogEventDTO();
                eventDTO.setDataObject(jsonObject);
                eventDTO.setDbName(dbName);
                eventDTO.setTableName(tableName);

                this.buffers.add(eventDTO);
            }
        }finally {
            lock.unlock();
        }

        if (this.buffers.size() == this.capacity) {
            this.flush();
        }
    }

    @Override
    public void flush() {
        if (this.buffers.isEmpty()) {
            return;
        }

        if (lock.isLocked()) {
            return;
        }

        lock.lock();
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
                columnNames.forEach(column -> strJoiner.append(column));
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
