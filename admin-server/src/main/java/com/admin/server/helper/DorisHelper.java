package com.admin.server.helper;

import com.admin.server.model.dto.DbColumnValueDto;
import com.admin.server.properties.DorisProrperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.*;
import java.util.*;

@Component
public class DorisHelper {

    private final Logger logger = LoggerFactory.getLogger(DorisHelper.class);

    @Resource
    private DorisProrperties dorisProrperties;

    public void alterTableColumn(String dbName, String tableName, String columnName, String type) {
        String changeColumnTypeSql = "ALTER TABLE %s.%s MODIFY COLUMN %s %s";
        String sql = String.format(changeColumnTypeSql, dbName, tableName, columnName, type);

        try {
            // 创建数据库连接
            Connection connection = DriverManager.getConnection(dorisProrperties.getUrl(), dorisProrperties.getUsername(), dorisProrperties.getPassword());

            // 创建 Statement 对象
            Statement statement = connection.createStatement();

            // 执行 SQL 语句
            statement.executeUpdate(sql);

            // 关闭 Statement 和连接
            statement.close();
            connection.close();

            logger.info("alterTableColumn 表字段类型更改成功 tableName: {}, columnName: {}, type: {}", tableName, columnName, type);
        } catch (SQLException e) {
            throw new IllegalStateException("表字段更改异常:" + e.getMessage());
        }
    }

    public List<DbColumnValueDto> selectColumnValues(String dbName, String tableName, String columnName) {
        String selectColumnValueSql = "SELECT DISTINCT %s AS value FROM %s.%s WHERE %s IS NOT NULL;";
        String sql = String.format(selectColumnValueSql, columnName, dbName, tableName, columnName);

        List<DbColumnValueDto> results = new ArrayList<>();

        try {
            // 创建数据库连接
            Connection connection = DriverManager.getConnection(dorisProrperties.getUrl(), dorisProrperties.getUsername(), dorisProrperties.getPassword());

            // 创建 Statement 对象
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // 执行 SQL 语句
            ResultSet resultSet = preparedStatement.executeQuery(sql);
            while (resultSet.next()) {
                Object obj = resultSet.getObject("value");
                String value = Objects.isNull(obj) ? "" : String.valueOf(obj);

                DbColumnValueDto dbColumnValueDto = new DbColumnValueDto();
                dbColumnValueDto.setColumnName(columnName);
                dbColumnValueDto.setValue(value);

                results.add(dbColumnValueDto);
            }

            // 关闭 Statement 和连接
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            logger.error("selectColumnValues execute query sql error", e);
        }

        return results;
    }

    public void createApp(String dbName, String tableName) {
        String createTableSql = String.format("CREATE TABLE `%s`.`%s` (\n" +
                "                                          app_id VARCHAR(255),\n" +
                "                                          event_time BIGINT(20),\n" +
                "                                          event_date DATE,\n" +
                "                                          event_name VARCHAR(255),\n" +
                "                                          event_type VARCHAR(32),\n" +
                "                                          status TINYINT\n" +
                ") ENGINE=OLAP\n" +
                "DUPLICATE KEY (app_id, event_time)\n" +
                "PARTITION BY RANGE (event_date) ()\n" +
                "DISTRIBUTED BY HASH(event_name) BUCKETS 32\n" +
                "PROPERTIES(\n" +
                "    \"dynamic_partition.time_unit\" = \"DAY\",\n" +
                "    \"dynamic_partition.start\" = \"-2\",\n" +
                "    \"dynamic_partition.end\" = \"2\",\n" +
                "    \"dynamic_partition.prefix\" = \"p\",\n" +
                "    \"dynamic_partition.buckets\" = \"32\",\n" +
                "    \"replication_num\" = \"1\"\n" +
                ");", dbName, tableName);

        try {
            // 创建数据库连接
            Connection connection = DriverManager.getConnection(dorisProrperties.getUrl(), dorisProrperties.getUsername(), dorisProrperties.getPassword());

            // 创建 Statement 对象
            Statement statement = connection.createStatement();

            // 执行 SQL 语句
            statement.executeUpdate(createTableSql);

            // 关闭 Statement 和连接
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("表创建异常:" + e.getMessage());
        }
    }

    public List<Map<String, Object>> selectEventAnalysis(Pair<String, List<String>> pair) {
        try (Connection connection = DriverManager.getConnection(dorisProrperties.getUrl(), dorisProrperties.getUsername(), dorisProrperties.getPassword())) {
            PreparedStatement statement = connection.prepareStatement(pair.getKey());

            for (int i = 0; i < pair.getValue().size(); i++) {
                statement.setObject(i + 1, pair.getValue().get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> item = new HashMap<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    item.put(columnName, resultSet.getObject(i));
                }

                list.add(item);
            }

            return list;
        } catch (SQLException e) {
            logger.error("selectEventAnalysis execute query sql error", e);
        }

        return new ArrayList<>();
    }

    public static void main(String[] args) {
        DorisHelper dorisHelper = new DorisHelper();
        List<DbColumnValueDto> dbColumnValueDtos = dorisHelper.selectColumnValues("user_behaviour_analysis", "event_log", "");

        System.out.println("test");
    }
}
