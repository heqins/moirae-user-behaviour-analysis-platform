package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.dto.TableColumnDTO;
import com.api.common.entity.EventLog;
import com.report.sink.enums.EventFailReasonEnum;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.helper.DorisHelper;
import com.report.sink.properties.DataSourceProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.expression.Sets;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Component
@Slf4j
public class ReportEventsToDorisHandler {

    private final ReentrantLock lock = new ReentrantLock();

    @Resource
    private EventLogHandler eventLogHandler;

    @Resource
    private DorisHelper dorisHelper;

    @Resource
    private DataSourceProperty dataSourceProperty;

    private List<JSONObject> buffers;

    private int capacity;

    public ReportEventsToDorisHandler() {}

    @PostConstruct
    public void init() {
        buffers = new ArrayList<>(1000);
    }

    private void alterTableColumn(JSONObject jsonObject, String tableName) {
        String appId = jsonObject.getStr("app_id");
        if (StringUtils.isBlank(appId)) {
            EventLog failLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject),
                    EventStatusEnum.FAIL.getStatus(), EventFailReasonEnum.KEY_FIELDS_MISSING.gerReason(),
                    "保留数据");
            eventLogHandler.addEvent(failLog);
            return;
        }

        DataSourceProperty.DorisConfig dorisConfig = dataSourceProperty.getDoris();
        if (dorisConfig == null) {
            return;
        }

        List<TableColumnDTO> columns = dorisHelper.getTableColumnInfos(dorisConfig.getDbName(), tableName);

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
                String objFieldType = objField.getClass().getTypeName();

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
            dorisHelper.changeTableSchema(dorisConfig.getDbName(), tableName, jsonObject, newFieldKeys);
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
    public void addEvent(JSONObject jsonObject, String tableName) {
        if (jsonObject == null) {
            return;
        }

        alterTableColumn(jsonObject, tableName);

        insertTableData(jsonObject);
    }

    private void insertTableData(JSONObject jsonObject) {
        lock.lock();
        try {
            if (jsonObject != null) {
                this.buffers.add(jsonObject);
            }
        }finally {
            lock.unlock();
        }

        if (this.buffers.size() == this.capacity) {
            this.flush();
        }
    }

    private void flush() {
        if (this.buffers.isEmpty()) {
            return;
        }

        if (lock.isLocked()) {
            return;
        }

        lock.lock();
        try {

        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        String test = "test";
        String typeName = test.getClass().getTypeName();
        System.out.println(typeName);
    }
}
