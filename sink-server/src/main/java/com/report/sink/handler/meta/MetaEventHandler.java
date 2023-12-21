package com.report.sink.handler.meta;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.report.sink.handler.SinkHandler;
import com.report.sink.handler.event.EventsHandler;
import com.report.sink.helper.MySqlHelper;
import com.report.sink.model.bo.MetaEvent;
import com.report.sink.model.bo.MetaEventAttribute;
import com.report.sink.service.ICacheService;
import org.jboss.netty.util.internal.ExecutorUtil;
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
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Component
public class MetaEventHandler implements EventsHandler {
    
    private final Logger logger = LoggerFactory.getLogger(SinkHandler.class);

    private final ReentrantLock lock = new ReentrantLock();

    private ConcurrentLinkedQueue<MetaEvent> metaEventsBuffers;

    private ConcurrentLinkedQueue<MetaEventAttribute> metaEventAttributeBuffers;

    private ScheduledExecutorService scheduledExecutorService;

    private final int capacity = 100;

    private final Long flushIntervalMillSeconds = 1000L;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = ThreadFactoryBuilder
                .create()
                .setNamePrefix("meta-event-handler-thread-pool")
                .setUncaughtExceptionHandler((value, ex) -> {logger.error("meta event handler value:{} error {}", value.getName(), ex.getMessage());})
                .build();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);

        metaEventsBuffers = new ConcurrentLinkedQueue<>();
        metaEventAttributeBuffers = new ConcurrentLinkedQueue<>();

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

        if (metaEventsBuffers.size() >= this.capacity) {
            flush();
        }

        metaEventsBuffers.add(metaEvent);
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
        scheduledExecutorService.scheduleAtFixedRate(this::flush, 1000, flushIntervalMillSeconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public void flush() {
//        lock.lock();
//        try {
//            if (!CollectionUtils.isEmpty(metaEventsBuffers)) {
//                mySqlHelper.insertMetaEvent(metaEventsBuffers);
//
//                metaEventsBuffers.clear();
//            }
//
//            if (!CollectionUtils.isEmpty(metaEventAttributeBuffers)) {
//                mySqlHelper.insertMetaAttributeEvent(metaEventAttributeBuffers);
//
//                metaEventAttributeBuffers.clear();
//            }
//        }finally {
//            lock.unlock();
//        }
    }
}
