package com.report.sink.handler;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.json.JSONUtil;
import com.api.common.entity.EventLog;
import com.api.common.entity.ReportLog;
import com.report.sink.properties.DataSourceProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author heqin
 * 用于将event_log的数据作为日志记录到doris中，用以后台查看，数据以json的形式存在data字段
 */
@Component
@Slf4j
public class DorisEventLogHandler {

    private List<EventLog> buffers;

    private ScheduledExecutorService scheduledExecutorService;

    private final Integer bufferSize = 1000;

    private Long flushIntervalMillSeconds = 500L;

    private DataSourceProperty dataSourceProperty;

    public DorisEventLogHandler(DataSourceProperty dataSourceProperty) {
        this.dataSourceProperty = dataSourceProperty;
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
            scheduledExecutorService.scheduleAtFixedRate(this::flush, 200, flushIntervalMillSeconds, TimeUnit.MILLISECONDS);
        }
    }

    public void addEvent(List<ReportLog> reportLogs) {
        if (!CollectionUtils.isEmpty(reportLogs)) {
            reportLogs.forEach(report -> {
                if (report != null) {
                    EventLog eventLog = new EventLog();
                    eventLog.setAppId(report.getAppName());
                    eventLog.setEventName(report.getEventName());
                    eventLog.setEventTime(report.getEventTime());
                    eventLog.setDataJson(JSONUtil.toJsonStr(report));

                    this.buffers.add(eventLog);
                }
            });
        }
    }

    public void flush() {
        if (buffers.size() > 0) {
            long currentSystemTime = System.currentTimeMillis();

            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                DataSourceProperty.DorisConfig dorisConfig = dataSourceProperty.getDoris();
                Class.forName(dorisConfig.getDriver());

                // Create a connection to the database
                connection = DriverManager.getConnection(dorisConfig.getUrl(), dorisConfig.getUsername(), dorisConfig.getPassword());
                // 关闭自动提交
                connection.setAutoCommit(false);

                String insertSql = "INSERT INTO real_time_event_log (app_id, event_time, event_date, event_name, event_data) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(insertSql);

                for (EventLog eventLog : buffers) {
                    preparedStatement.setString(1, eventLog.getAppId());
                    preparedStatement.setLong(2, eventLog.getEventTime());
                    preparedStatement.setDate(3, new Date(eventLog.getEventTime()));
                    preparedStatement.setString(4, eventLog.getEventName());
                    if (eventLog.getDataJson() != null) {
                        String json = eventLog.getDataJson().substring(0, Math.min(eventLog.getDataJson().length(), 1000));
                        preparedStatement.setString(5, json);
                    }
                    // 添加到批处理
                    preparedStatement.addBatch();
                }

                // 执行批处理
                int[] batchResults = preparedStatement.executeBatch();

                log.info("insert {} rows into real_time_event_log sql{} costTime:{} ms",
                        batchResults.length, System.currentTimeMillis() - currentSystemTime);

                // 提交事务
                connection.commit();
            }catch (Exception e) {
                try {
                    if (connection != null) {
                        connection.rollback();
                    }
                }catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }

                e.printStackTrace();
            }finally {
                this.buffers.clear();

                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    }catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    }catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            }
        }
    }
}
