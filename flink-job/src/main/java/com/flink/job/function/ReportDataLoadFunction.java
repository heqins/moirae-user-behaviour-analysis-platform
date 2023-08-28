package com.flink.job.function;

import cn.hutool.json.JSONUtil;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * @author heqin
 */
public class ReportDataLoadFunction extends ProcessFunction<String, String> {
    private OutputTag<String> invalidOutput;

    public ReportDataLoadFunction(OutputTag<String> outputTag) {
        this.invalidOutput = outputTag;
    }

    @Override
    public void processElement(String value, ProcessFunction<String, String>.Context ctx, Collector<String> out) throws Exception {
        if (value == null || !JSONUtil.isTypeJSON(value)) {
            ctx.output(invalidOutput, value);
            return;
        }

        out.collect(value);
    }
}
