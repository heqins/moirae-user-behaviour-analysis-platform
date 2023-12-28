package com.report.sink.listener;

import cn.hutool.core.date.StopWatch;
import com.report.sink.handler.SinkHandler;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author heqin
 */
@Component
public class ReportListener {

    private final Logger logger = LoggerFactory.getLogger(ReportListener.class);

    private final Counter kafkaConsumerCounter = Metrics.counter("kafka.consumer.sink.records.count");

    @Resource
    private SinkHandler sinkHandler;

    private final StopWatch stopWatch = new StopWatch();

    // 创建一个 ForkJoinPool
    private ForkJoinPool forkJoinPool = new ForkJoinPool();

    @KafkaListener(topics = "${kafka.topics.online-log}", containerFactory = "batchManualFactory")
    public void onlineReportMessage(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        stopWatch.start();
        try {
            // 创建一个 ProcessListTask 任务
            ProcessListTask processListTask = new ProcessListTask(sinkHandler, records, 0, records.size());
            // 提交任务给 ForkJoinPool 并获取结果
            boolean result = forkJoinPool.invoke(processListTask);
        }catch (Exception e) {
            logger.error("onlineReportMessage error", e);
        }

        kafkaConsumerCounter.increment(records.size());

        stopWatch.stop();
        logger.info("消费执行效率 个数:{} 处理时间:{}ms", records.size(), stopWatch.getLastTaskTimeMillis());

        acknowledgment.acknowledge();
    }

    // 定义一个任务，继承自 RecursiveTask
    static class ProcessListTask extends RecursiveTask<Boolean>
    {

        private static final int THRESHOLD = 100; // 阈值，决定何时进行并行计算
        private List<ConsumerRecord<String, String>> list;
        private int start;
        private int end;

        private SinkHandler sinkHandler;

        // 构造方法，接收列表、起始索引和结束索引
        public ProcessListTask(SinkHandler sinkHandler, List<ConsumerRecord<String, String>> list, int start, int end) {
            this.list = list;
            this.start = start;
            this.end = end;
            this.sinkHandler = sinkHandler;
        }

        @Override
        protected Boolean compute() {
            // 如果任务足够小，直接计算结果
            if (end - start <= THRESHOLD) {
                int last = list.size();
                List<ConsumerRecord<String, String>> consumerRecords = list.subList(start, Math.min(end + 1, last));
                sinkHandler.run(consumerRecords);

                return true;
            } else {
                // 否则，拆分成两个子任务并行计算
                int mid = (start + end) >>> 1;
                ProcessListTask leftTask = new ProcessListTask(sinkHandler, list, start, mid);
                ProcessListTask rightTask = new ProcessListTask(sinkHandler, list, mid, end);

                // 并行执行子任务
                leftTask.fork();
                rightTask.fork();

                // 合并子任务的结果
                boolean leftResult = leftTask.join();
                boolean rightResult = rightTask.join();

                // 合并并返回最终结果
                return leftResult & rightResult;
            }
        }
    }
}
