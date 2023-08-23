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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

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

        if (!CollectionUtils.isEmpty(columns)) {
            for (TableColumnDTO columnDTO: columns) {
                if (columnDTO == null) {
                    continue;
                }

                if (!jsonObject.containsKey(columnDTO.getColumnName())) {
                    continue;
                }

                String columnName = columnDTO.getColumnName();

                // 比较表字段类型是否一致

                if (!fieldValue.getClass().equals(tableColumnDTO.getFieldType())) {
                    EventLog failLog = eventLogHandler.transferFromJson(jsonObject, JSONUtil.toJsonStr(jsonObject), EventStatusEnum.FAIL.getStatus(), EventFailReasonEnum.DATA_ERROR.gerReason(), "保留数据"));
                    eventLogHandler.addEvent(failLog);
                    return;
                }
            }
        }

        Set<String> dataFields = jsonObject.keySet();
        Set<String> cacheFields = fields.keySet();
        dataFields.removeAll(cacheFields);

        if (!CollectionUtils.isEmpty(dataFields)) {
            for (String field: dataFields) {
                TableColumnDTO tableColumnDTO = new TableColumnDTO();
                Object obj = jsonObject.get(tableColumnDTO);
                tableColumnDTO.setFieldName(field);
                tableColumnDTO.setFieldType(obj.getClass());
                tableColumnDTO.setStatus(1);

                redisCacheService.setAppFieldCache(appId, tableColumnDTO);
            }
        }
    }

    private void changeTableSchema() {

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

    }
}
