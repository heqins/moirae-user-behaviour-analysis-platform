package com.report.sink.handler;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.api.common.entity.MetaEvent;
import com.api.common.entity.MetaEventAttribute;
import com.report.sink.helper.MySqlHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class MetaEventHandler implements EventsHandler{
    private final ReentrantLock lock = new ReentrantLock();

    private List<MetaEvent> metaEventsBuffers;

    private List<MetaEventAttribute> metaEventAttributeBuffers;

    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("report-data-doris")
                .setUncaughtExceptionHandler((value, ex) -> {log.error("");})
                .build();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);

        metaEventsBuffers = new ArrayList<>(1000);
        metaEventAttributeBuffers = new ArrayList<>(1000);
    }

    @Resource
    private MySqlHelper mySqlHelper;

    public void addMetaEvents(List<MetaEvent> metaEvents) {
        if (!CollectionUtils.isEmpty(metaEvents)) {
            metaEventsBuffers.addAll(metaEvents);
        }
    }

    public void addMetaAttributeEvents(List<MetaEventAttribute> metaAttributeEvents) {
        if (!CollectionUtils.isEmpty(metaAttributeEvents)) {
            metaEventAttributeBuffers.addAll(metaAttributeEvents);
        }
    }

    public void runSchedule() {
        scheduledExecutorService.scheduleAtFixedRate(this::flush, 10, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void flush() {
        lock.lock();
        try {
            if (!CollectionUtils.isEmpty(metaEventsBuffers)) {
                mySqlHelper.insertMetaEvent(metaEventsBuffers);

                metaEventsBuffers.clear();
            }

            if (!CollectionUtils.isEmpty(metaEventAttributeBuffers)) {
                mySqlHelper.insertMetaAttributeEvent(metaEventAttributeBuffers);

                metaEventAttributeBuffers.clear();
            }
        }finally {
            lock.unlock();
        }
    }
}
