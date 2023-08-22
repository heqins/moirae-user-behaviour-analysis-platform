package com.report.sink.handler;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.api.common.entity.EventLog;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EventLogHandler {

    private static final String INSERT_SQL = "INSERT INTO event_log (app_id, event_time, event_date, event_name, event_data) VALUES (?, ?, ?, ?, ?)";

    private List<EventLog> buffers;

    private ScheduledExecutorService scheduledExecutorService;

    private final Integer bufferSize = 1000;

    private Long flushIntervalMillSeconds = 1000L;

    private DataSource dataSource;

    private final ReentrantLock lock = new ReentrantLock();

    public EventLogHandler(@Qualifier(value = "doris")DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("report-data-doris")
                .setUncaughtExceptionHandler((value, ex) -> {log.error("");})
                .build();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        buffers = new ArrayList<>(bufferSize);

        this.run();
    }

    public void run() {
        if (this.flushIntervalMillSeconds > 0) {
            scheduledExecutorService.scheduleAtFixedRate(this::flush, 1000, flushIntervalMillSeconds, TimeUnit.MILLISECONDS);
        }
    }

    public void addEvent(EventLog eventLog) {
        if (eventLog != null) {
            this.buffers.add(eventLog);

            if (this.buffers.size() == this.bufferSize) {
                this.flush();
            }
        }
    }

    public void flush() {
        if (this.buffers.isEmpty()) {
            return;
        }

        if (lock.isLocked()) {
            return;
        }

        lock.lock();
        try {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
                    for (EventLog eventLog : this.buffers) {
                        preparedStatement.setString(1, eventLog.getAppId());
                        preparedStatement.setLong(2, eventLog.getEventTime());
                        preparedStatement.setDate(3, new Date(eventLog.getEventTime()));
                        preparedStatement.setString(4, eventLog.getEventName());
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
                    log.error("DorisEventLogHandler insertSql execute error", e);
                }
            } catch (SQLException e) {
                log.error("DorisEventLogHandler connection error", e);
            } finally {
                this.buffers.clear();
            }
        } catch (Exception e) {
            log.error("DorisEventLogHandler lock error", e);
        } finally {
            lock.unlock();
        }
    }
}
