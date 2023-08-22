package com.flink.job.window;

import cn.hutool.json.JSONUtil;
import com.api.common.entity.EventLog;
import com.api.common.entity.EventLogPv;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Iterator;

public class LogPvWindow extends ProcessWindowFunction<EventLog, EventLogPv, String, TimeWindow> {

    private ValueState<Long> countState;

    private OutputTag<String> etlOutputTag;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 初始化状态变量
        countState = getRuntimeContext().getState(new ValueStateDescriptor<>("countState", Long.class));
    }

    public LogPvWindow(OutputTag<String> outputTag) {
        this.etlOutputTag = outputTag;
    }

    @Override
    public void process(String s, Context context, Iterable<EventLog> elements, Collector<EventLogPv> out) throws Exception {
        Long count = countState.value();
        if (count == null) {
            count = 0L;
        }

        Iterator<EventLog> iterator = elements.iterator();
        EventLogPv pv = new EventLogPv();
        String appId = null;
        while (iterator.hasNext()) {
            count++;
            EventLog next = iterator.next();
            if (appId == null) {
                appId = next.getAppId();
            }

            context.output(etlOutputTag, JSONUtil.toJsonStr(next));
        }

        pv.setAppName(appId);
        pv.setWindowStart(context.window().getStart());
        pv.setWindowEnd(context.window().getEnd());
        pv.setCount(count);

        count = 0L;
        countState.update(count);

        out.collect(pv);
    }
}
