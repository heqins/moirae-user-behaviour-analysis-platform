package com.report.sink.helper;

import com.api.common.dto.TableColumnDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.report.sink.constants.CacheConstants;
import com.report.sink.properties.DataSourceProperty;
import com.report.sink.service.ICacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author heqin
 */
@Component
public class DorisHelper {

    private static final String COLUMN_QUERY_SQL = "select column_name, column_type, is_nullable\n" +
            "FROM information_schema.columns\n" +
            "WHERE table_schema = '%s' AND table_name = '%s';";

    @Resource
    private DataSourceProperty dataSourceProperty;

    @Resource(name = "redisCacheServiceImpl")
    private ICacheService redisCacheService;

    @Resource(name = "localCacheServiceImpl")
    private ICacheService localCacheService;

    @Resource(name = "doris")
    private DataSource dataSource;

    public List<TableColumnDTO> getTableColumnInfos(String dbName, String tableName) {
        if (StringUtils.isBlank(dbName) || StringUtils.isNotBlank(tableName)) {
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

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(COLUMN_QUERY_SQL)) {
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
            }
        }catch (SQLException e) {

        }

        localCacheService.setColumnCache(dbName, tableName, columns);
        redisCacheService.setColumnCache(dbName, tableName, columns);

        return columns;
    }
}
