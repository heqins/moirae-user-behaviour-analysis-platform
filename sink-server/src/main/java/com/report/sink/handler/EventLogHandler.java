package com.report.sink.handler;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.json.JSONObject;
import com.api.common.model.dto.sink.EventLogDTO;
import com.report.sink.enums.EventStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author heqin
 * 用于将event_log的数据作为日志记录到doris中，用以后台查看，数据以json的形式存在data字段
 */
@Component
public class EventLogHandler implements EventsHandler{

    private final Logger logger = LoggerFactory.getLogger(SinkHandler.class);

    private static final String INSERT_SQL = "INSERT INTO event_log (app_id, event_time, event_date, event_name," +
            " event_data, event_type, error_reason, error_handling, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private List<EventLogDTO> buffers;

    private ScheduledExecutorService scheduledExecutorService;

    private final Integer bufferSize = 1000;

    private final Integer jsonLengthLimit = 1024;

    private Long flushIntervalMillSeconds = 1000L;

    private DataSource dataSource;

    private final ReentrantLock lock = new ReentrantLock();

    public EventLogHandler(@Qualifier(value = "dorisDataSource")DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("report-data-doris")
                .setUncaughtExceptionHandler((value, ex) -> {logger.error("");})
                .build();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        buffers = new ArrayList<>(bufferSize);

        run();
    }

    public EventLogDTO transferFromJson(JSONObject jsonObject, String dataJson, Integer status, String errorReason, String errorHandling) {
        if (jsonObject == null) {
            logger.error("EventLogHandler");
            return null;
        }

        if (status == null || !EventStatusEnum.isStatusValid(status)) {
            logger.error("EventLogHandler");
            return null;
        }

        EventLogDTO eventLog = new EventLogDTO();
        eventLog.setEventType(jsonObject.getStr("event_type"));
        eventLog.setStatus(status);
        if (dataJson != null) {
            eventLog.setDataJson(dataJson.substring(0, Math.min(dataJson.length(), jsonLengthLimit)));
        }
        eventLog.setAppId(jsonObject.getStr("app_id"));
        eventLog.setEventTime(jsonObject.getLong("event_time"));
        eventLog.setEventName(jsonObject.getStr("event_name"));
        eventLog.setErrorHandling(errorHandling);
        eventLog.setErrorReason(errorReason);

        return eventLog;
    }

    public void run() {
        if (this.flushIntervalMillSeconds > 0) {
            scheduledExecutorService.scheduleAtFixedRate(this::flush, 1000, flushIntervalMillSeconds, TimeUnit.MILLISECONDS);
        }
    }

    public void addEvent(EventLogDTO eventLog) {
        if (eventLog != null) {
            this.buffers.add(eventLog);
        }

        if (this.buffers.size() >= this.bufferSize) {
            this.flush();
        }
    }

    @Override
    public void flush() {
        if (this.buffers.isEmpty()) {
            return;
        }

        boolean acquireLock = false;
        try {
            acquireLock = lock.tryLock(300, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e) {
            logger.error("EventLogHandler tryLock error", e);
        }

        if (!acquireLock) {
            return;
        }

        try {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
                    for (EventLogDTO eventLog : this.buffers) {
                        preparedStatement.setString(1, eventLog.getAppId());
                        preparedStatement.setLong(2, eventLog.getEventTime());
                        preparedStatement.setDate(3, new Date(eventLog.getEventTime()));
                        preparedStatement.setString(4, eventLog.getEventName());
                        preparedStatement.setString(6, eventLog.getEventType());
                        preparedStatement.setString(7, eventLog.getErrorReason());
                        preparedStatement.setString(8, eventLog.getErrorHandling());
                        preparedStatement.setInt(9, eventLog.getStatus());

                        if (eventLog.getDataJson() != null) {
                            String json = eventLog.getDataJson().substring(0, Math.min(eventLog.getDataJson().length(), 1000));
                            preparedStatement.setString(5, json);
                        }
                        preparedStatement.addBatch();
                    }

                    preparedStatement.executeBatch();
                    connection.commit();
                } catch (SQLException e) {
                    connection.rollback();
                    logger.error("DorisEventLogHandler insertSql execute error", e);
                }
            } catch (SQLException e) {
                logger.error("DorisEventLogHandler connection error", e);
            } finally {
                this.buffers.clear();
            }
        } catch (Exception e) {
            logger.error("DorisEventLogHandler lock error", e);
        } finally {
            lock.unlock();
        }
    }
}
