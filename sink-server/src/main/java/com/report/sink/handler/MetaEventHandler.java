package com.report.sink.handler;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.api.common.entity.MetaEvent;
import com.api.common.entity.MetaEventAttribute;
import com.report.sink.dao.MetaEventDao;
import com.report.sink.helper.MySqlHelper;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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

        runSchedule();
    }

    @Resource(name = "redisCacheServiceImpl")
    private ICacheService redisCache;

    @Resource
    private MySqlHelper mySqlHelper;

    @Resource
    private MetaEventDao metaEventDao;

    public void addMetaEvents(List<MetaEvent> metaEvents) {
        if (!CollectionUtils.isEmpty(metaEvents)) {
            Map<String, List<MetaEvent>> appMetaEvents = metaEvents.stream().collect(Collectors.groupingBy(MetaEvent::getAppId));
            for (Map.Entry<String, List<MetaEvent>> entry: appMetaEvents.entrySet()) {
                String appId = entry.getKey();
                List<MetaEvent> metaEventCache = redisCache.getMetaEventCache(appId);
                if (!CollectionUtils.isEmpty(metaEventCache)) {
                    Set<String> enabledMetaEvents = getEnabledMetaEvents(metaEventCache);

                    metaEventsBuffers.addAll(entry.getValue().stream().filter(value -> enabledMetaEvents.contains(value.getEventName())).collect(Collectors.toList()));
                    continue;
                }

                List<MetaEvent> metaEventsLists = new ArrayList<>();

            }

            if (metaEventsBuffers.size() >= 1000) {
                flush();
            }
        }
    }

    private Set<String> getEnabledMetaEvents(List<MetaEvent> metaEvents) {
        if (CollectionUtils.isEmpty(metaEvents)) {
            return Collections.emptySet();
        }

        return metaEvents.stream().map(MetaEvent::getEventName).collect(Collectors.toSet());
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
