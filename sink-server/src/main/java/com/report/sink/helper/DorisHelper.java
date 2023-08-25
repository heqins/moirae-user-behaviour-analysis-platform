package com.report.sink.helper;

import cn.hutool.json.JSONObject;
import com.api.common.dto.TableColumnDTO;
import com.api.common.entity.EventLog;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author heqin
 */
@Component
@Slf4j
public class DorisHelper {

    private static final String COLUMN_QUERY_SQL = "select column_name, column_type, is_nullable\n" +
            "FROM information_schema.columns\n" +
            "WHERE table_schema = ? AND table_name = ?;";

    private static final String ALTER_ADD_COLUMN_SQL = "ALTER TABLE `%s`.`%s` ADD COLUMN %s %s;";

    @Resource(name = "redisCacheServiceImpl")
    private ICacheService redisCacheService;

    @Resource(name = "localCacheServiceImpl")
    private ICacheService localCacheService;

    @Resource(name = "doris")
    private DataSource dataSource;

    public List<TableColumnDTO> getTableColumnInfos(String dbName, String tableName) {
        if (StringUtils.isBlank(dbName) || StringUtils.isBlank(tableName)) {
            return null;
        }

        List<TableColumnDTO> columns;
        columns = localCacheService.getColumnCache(dbName, tableName);
        if (columns != null) {
            return columns;
        }

        columns = redisCacheService.getColumnCache(dbName, tableName);
        if (columns != null) {
            return columns;
        }

        columns = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(COLUMN_QUERY_SQL)) {
                statement.setString(1, dbName);
                statement.setString(2, tableName);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String columnName = resultSet.getString("column_name");
                    String columnType = resultSet.getString("column_type");
                    String isNullable = resultSet.getString("is_nullable");

                    TableColumnDTO columnDTO = new TableColumnDTO();
                    columnDTO.setColumnName(columnName);
                    columnDTO.setStatus(1);
                    columnDTO.setNullable(Objects.equals(isNullable.toLowerCase(Locale.ROOT), "yes"));
                    columnDTO.setTableName(tableName);
                    columnDTO.setColumnType(columnType);

                    columns.add(columnDTO);
                }
            } catch (SQLException e) {
                connection.rollback();
                log.error("DorisHelper getTableColumnInfos sql error", e);
            }
        }catch (SQLException e) {
            log.error("DorisHelper getTableColumnInfos sql error", e);
        }

        localCacheService.setColumnCache(dbName, tableName, columns);
        redisCacheService.setColumnCache(dbName, tableName, columns);

        return columns;
    }

    public void changeTableSchema(String dbName, String tableName, JSONObject jsonObject, Set<String> jsonFields) {
        List<String> alterQueries = new ArrayList<>(jsonFields.size());
        for (String jsonField: jsonFields) {
            if (!jsonObject.containsKey(jsonField)) {
                log.error("DorisHelper changeTableSchema column not include dbName:{} tableName:{} field:{}", dbName, tableName, jsonField);
                continue;
            }
            String className = jsonObject.get(jsonField).getClass().getCanonicalName().toLowerCase(Locale.ROOT);
            String type = "";
            switch (className) {
                case "java.lang.string":
                    type = "VARCHAR(64)";
                    break;
                case "java.lang.integer":
                    type = "INT";
                default:
                    break;
            }

            if (StringUtils.isBlank(type)) {
                log.error("");
                continue;
            }

            String query = String.format(ALTER_ADD_COLUMN_SQL, dbName, tableName, jsonField, type);
            alterQueries.add(query);
        }

        if (!CollectionUtils.isEmpty(alterQueries)) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                for (String sql: alterQueries) {
                    try (PreparedStatement statement = connection.prepareStatement(sql)){
                        statement.execute();
                    } catch (SQLException e) {
                        connection.rollback();
                        log.error("DorisEventLogHandler changeTableSchema execute error", e);
                    }
                }

                connection.commit();
            }catch (SQLException e) {
                log.error("", e);
            }
        }
    }
}
