package com.flink.job.window;

import cn.hutool.json.JSONUtil;
import com.api.common.entity.ReportLog;
import com.api.common.entity.ReportLogPv;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Iterator;

public class LogPvWindow extends ProcessWindowFunction<ReportLog, ReportLogPv, String, TimeWindow> {

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

            context.output(etlOutputTag, JSONUtil.toJsonStr(next));
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
