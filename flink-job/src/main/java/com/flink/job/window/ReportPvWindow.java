package com.flink.job.window;

import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import scala.Tuple2;
import scala.collection.Iterable;

public class ReportPvWindow extends ProcessWindowFunction<Tuple2<String, String>, Tuple2<String, Integer>, String, TimeWindow> {

    @Override
    public void process(String s, Context context, Iterable<Tuple2<String, String>> elements, Collector<Tuple2<String, Integer>> out) throws Exception {

    }
}
