package com.report.sink.handler;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.report.sink.helper.MySqlHelper;
import com.report.sink.model.bo.MetaEvent;
import com.report.sink.model.bo.MetaEventAttribute;
import com.report.sink.service.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
public class MetaEventHandler implements EventsHandler{
    
    private final Logger logger = LoggerFactory.getLogger(SinkHandler.class);

    private final ReentrantLock lock = new ReentrantLock();

    private List<MetaEvent> metaEventsBuffers;

    private List<MetaEventAttribute> metaEventAttributeBuffers;

    private ScheduledExecutorService scheduledExecutorService;

    private final int capacity = 1000;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("report-data-doris")
                .setUncaughtExceptionHandler((value, ex) -> {logger.error("");})
                .build();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);

        metaEventsBuffers = new ArrayList<>(capacity);
        metaEventAttributeBuffers = new ArrayList<>(capacity);

        runSchedule();
    }

    @Resource(name = "redisCacheService")
    private ICacheService redisCache;

    @Resource
    private MySqlHelper mySqlHelper;

    public void addMetaEvent(MetaEvent metaEvent) {
        if (metaEvent == null) {
            return;
        }

        metaEventsBuffers.add(metaEvent);
        if (metaEventsBuffers.size() >= 1000) {
            flush();
        }
    }

    private Set<String> getEnabledMetaEvents(List<MetaEvent> metaEvents) {
        if (CollectionUtils.isEmpty(metaEvents)) {
            return Collections.emptySet();
        }

        return metaEvents.stream().map(MetaEvent::getEventName).collect(Collectors.toSet());
    }

    public void addMetaAttributeEvent(MetaEventAttribute metaAttributeEvent) {
        if (metaAttributeEvent == null) {
            return;
        }

        metaEventAttributeBuffers.add(metaAttributeEvent);
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
