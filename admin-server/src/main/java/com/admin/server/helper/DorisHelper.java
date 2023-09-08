package com.admin.server.helper;

import com.admin.server.properties.DorisProrperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DorisHelper {

    private final String CHANGE_COLUMN_TYPE_SQL = "ALTER TABLE %s.%s MODIFY COLUMN %s %s";

    private final Logger logger = LoggerFactory.getLogger(DorisHelper.class);

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

            logger.info("alterTableColumn 表字段类型更改成功 tableName: {}, columnName: {}, type: {}", tableName, columnName, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
