package com.admin.server.helper;

import com.admin.server.model.dto.DbColumnValueDto;
import com.admin.server.properties.DorisProrperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            e.printStackTrace();
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

    public static void main(String[] args) {
        DorisHelper dorisHelper = new DorisHelper();
        List<DbColumnValueDto> dbColumnValueDtos = dorisHelper.selectColumnValues("user_behaviour_analysis", "event_log", "");

        System.out.println("test");
    }
}
