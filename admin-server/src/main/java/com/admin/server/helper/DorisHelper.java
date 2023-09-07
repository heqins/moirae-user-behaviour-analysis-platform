package com.admin.server.helper;

import com.admin.server.properties.DorisProrperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Slf4j
public class DorisHelper {

    private final String CHANGE_COLUMN_TYPE_SQL = "ALTER TABLE %s.%s MODIFY COLUMN %s %s";

    @Resource
    private DorisProrperties dorisProrperties;

    public void alterTableColumn(String dbName, String tableName, String columnName, String type) {
        String sql = String.format(CHANGE_COLUMN_TYPE_SQL, dbName, tableName, columnName, type);

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

            System.out.println("表字段类型已更改成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
