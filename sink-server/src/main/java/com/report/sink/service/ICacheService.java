package com.report.sink.service;

import com.api.common.dto.TableColumnDTO;

import java.util.List;

/**
 * @author heqin
 */
public interface ICacheService {

    List<TableColumnDTO> getColumnCache(String dbName, String tableName);

    void setColumnCache(String dbName, String tableName, List<TableColumnDTO> columns);

    void removeColumnCache(String dbName, String tableName);


}
