package com.report.sink.helper;

import com.api.common.bo.MetaEvent;
import com.api.common.bo.MetaAttributeRelation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MySqlHelper {

    @Resource(name = "mysql")
    private DataSource dataSource;

    public void insertMetaEvent(List<MetaEvent> metaEventList) {
        for (MetaEvent event: metaEventList) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                String metaEventInsertSql = "insert into meta_event(appid, event_name) values (?, ?);";
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

    public void insertMetaAttributeEvent(List<MetaAttributeRelation> metaAttributeRelationList) {
        for (MetaAttributeRelation event: metaAttributeRelationList) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                String metaAttributeInsertSql = "insert into meta_attr_relation(app_id, event_name, event_attr) values (?, ?, ?);";
                try (PreparedStatement statement = connection.prepareStatement(metaAttributeInsertSql)){
                    statement.setString(1, event.getAppId());
                    statement.setString(2, event.getEventName());
                    statement.setString(3, event.getEventAttribute());

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
