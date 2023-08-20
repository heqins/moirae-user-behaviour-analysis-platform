package com.flink.job.window;

import com.api.common.entity.ReportLog;
import com.api.common.entity.ReportLogPv;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class LogPvWindow extends ProcessWindowFunction<ReportLog, ReportLogPv, String, TimeWindow> {

    private ValueState<Long> countState;

    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
        // 初始化状态变量
        countState = getRuntimeContext().getState(new ValueStateDescriptor<>("countState", Long.class));
    }

    @Override
    public void process(String s, Context context, Iterable<ReportLog> elements, Collector<ReportLogPv> out) throws Exception {
        Long count = countState.value();
        if (count == null) {
            count = 0L;
        }

        Iterator<ReportLog> iterator = elements.iterator();
        ReportLogPv pv = new ReportLogPv();
        String appName = null;
        while (iterator.hasNext()) {
            count++;
            ReportLog next = iterator.next();
            if (appName == null) {
                appName = next.getAppName();
            }
        }

        pv.setAppName(appName);
        pv.setWindowStart(context.window().getStart());
        pv.setWindowEnd(context.window().getEnd());
        pv.setCount(count);

        count = 0L;
        countState.update(count);

        out.collect(pv);
    }
}
