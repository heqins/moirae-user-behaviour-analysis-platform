package com.report.sink.helper;

import com.report.sink.model.bo.MetaEvent;
import com.report.sink.model.bo.MetaEventAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class MySqlHelper {

    @Resource(name = "mysqlDataSource")
    private DataSource dataSource;

    public void insertMetaEvent(List<MetaEvent> metaEventList) {
        for (MetaEvent event: metaEventList) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                String metaEventInsertSql = "insert ignore into meta_event(appid, event_name) values (?, ?);";
                try (PreparedStatement statement = connection.prepareStatement(metaEventInsertSql)){
                    statement.setString(1, event.getAppId());
                    statement.setString(2, event.getEventName());

                    statement.execute();
                }catch (SQLException e) {
                    connection.rollback();
                }
            }catch (Exception e) {

            }finally {

            }
        }
    }

    public void insertMetaAttributeEvent(List<MetaEventAttribute> metaAttributeRelationList) {
        for (MetaEventAttribute event: metaAttributeRelationList) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                String metaAttributeInsertSql = "insert ignore into meta_event_attribute(app_id, event_name, event_attribute) values (?, ?, ?);";
                try (PreparedStatement statement = connection.prepareStatement(metaAttributeInsertSql)){
                    statement.setString(1, event.getAppId());
                    statement.setString(2, event.getShowName());
                    statement.setString(3, event.getAttributeName());

                    statement.execute();
                }catch (SQLException e) {
                    connection.rollback();
                }
            }catch (Exception e) {

            }finally {

            }
        }
    }
}
