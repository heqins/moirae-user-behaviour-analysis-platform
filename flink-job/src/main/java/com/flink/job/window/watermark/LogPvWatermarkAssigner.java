package com.flink.job.window.watermark;

import com.api.common.entity.EventLog;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

public class LogPvWatermarkAssigner implements AssignerWithPeriodicWatermarks<EventLog> {

    private static final long serialVersionUID = 1L;

    @Override
    public Watermark getCurrentWatermark() {
        // 返回当前水印（Watermark），这里假设事件时间是单调递增的，直接使用系统时间作为水印
        return new Watermark(System.currentTimeMillis());
    }

    @Override
    public long extractTimestamp(EventLog element, long previousElementTimestamp) {
        // 从元素中提取事件时间
        return element.getEventTime();
    }
}