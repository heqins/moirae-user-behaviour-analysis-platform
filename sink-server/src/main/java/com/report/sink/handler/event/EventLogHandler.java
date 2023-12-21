package com.report.sink.handler.event;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.model.dto.sink.EventLogDTO;
import com.report.sink.enums.EventStatusEnum;
import com.report.sink.handler.SinkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;
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

    private ConcurrentLinkedQueue<EventLogDTO> buffers;

    private ScheduledExecutorService scheduledExecutorService;

    private final Integer bufferSize = 100;

    private final Integer jsonLengthLimit = 1024;

    private final Long flushIntervalMillSeconds = 1000L;

    private final DataSource dataSource;

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
        buffers = new ConcurrentLinkedQueue<>();

        run();
    }

    public EventLogDTO transferFromJson(JSONObject jsonObject, Integer status, String errorReason, String errorHandling) {
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

        String dataJson = JSONUtil.toJsonStr(jsonObject);
        eventLog.setDataJson(dataJson.substring(0, Math.min(dataJson.length(), jsonLengthLimit)));
        eventLog.setFields(jsonObject.keySet());
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
        if (this.buffers.size() >= this.bufferSize) {
            this.flush();
        }

        if (eventLog != null) {
            this.buffers.offer(eventLog);
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
            List<EventLogDTO> batch = new CopyOnWriteArrayList<>();
            EventLogDTO data;
            int count = 0;
            while ((data = this.buffers.poll()) != null && count <= this.bufferSize) {
                batch.add(data);
                count++;
            }

            if (!batch.isEmpty()) {
                Connection connection;
                try {
                    connection = dataSource.getConnection();
                }catch (SQLException e) {
                    batch.clear();
                    return;
                }

                try {
                    connection.setAutoCommit(false);

                    try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
                        for (EventLogDTO eventLog : batch) {
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
                    }
                } catch (SQLException e) {
                    logger.error("DorisEventLogHandler insertSql execute error", e);
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {
                        logger.error("DorisEventLogHandler rollback error", e1);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
