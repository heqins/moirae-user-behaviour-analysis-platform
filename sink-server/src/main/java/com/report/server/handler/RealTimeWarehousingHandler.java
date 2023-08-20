//package com.report.server.handler;
//
//import java.sql.PreparedStatement;
//import java.util.ArrayList;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
//public class RealTimeWarehousingHandler {
//
//    private final List<RealTimeWarehousingData> buffer;
//    private final Object bufferLock;
//    private final int batchSize;
//    private final int flushInterval;
//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RealTimeWarehousing.class);
//    private final ScheduledExecutorService executorService;
//
//    public RealTimeWarehousing(BatchConfig config) {
//        Logger.info("NewRealTimeWarehousing", "batchSize", config.getBufferSize(), "flushInterval", config.getFlushInterval());
//        buffer = new ArrayList<>(config.getBufferSize());
//        bufferLock = new Object();
//        batchSize = config.getBufferSize();
//        flushInterval = config.getFlushInterval();
//
//        if (config.getFlushInterval() > 0) {
//            executorService = Executors.newScheduledThreadPool(1);
//            executorService.scheduleAtFixedRate(this::flush, flushInterval, flushInterval, TimeUnit.SECONDS);
//        } else {
//            executorService = null;
//        }
//    }
//
//    public void flush() {
//        synchronized (bufferLock) {
//            if (!buffer.isEmpty()) {
//                long startNow = System.currentTimeMillis();
//
//                try {
//                    ClickHouseSqlx.beginTransaction();
//
//                    PreparedStatement stmt = ClickHouseSqlx.prepareStatement("INSERT INTO xwl_real_time_warehousing (table_id, event_name, create_time, report_data) VALUES (?, ?, ?, ?)");
//
//                    for (RealTimeWarehousingData data : buffer) {
//                        stmt.setLong(1, data.getAppid());
//                        stmt.setString(2, data.getEventName());
//                        stmt.setString(3, data.getCreateTime());
//                        stmt.setString(4, BytesUtil.bytes2str(data.getData()));
//
//                        stmt.addBatch();
//                    }
//
//                    stmt.executeBatch();
//                    stmt.close();
//
//                    ClickHouseSqlx.commitTransaction();
//
//                    long elapsedTime = System.currentTimeMillis() - startNow;
//                    int len = buffer.size();
//                    if (len > 0) {
//                        Logger.info("入库实时数据成功", "所花时间", elapsedTime + "ms", "数据长度为", len);
//                    }
//                } catch (Exception e) {
//                    ClickHouseSqlx.rollbackTransaction();
//                    Logger.error("入库数据状态出现错误", e);
//                }
//
//                buffer.clear();
//            }
//        }
//    }
//
//    public void add(RealTimeWarehousingData data) {
//        synchronized (bufferLock) {
//            buffer.add(data);
//            if (buffer.size() >= batchSize) {
//                flush();
//            }
//        }
//    }
//
//    public void flushAll() {
//        synchronized (bufferLock) {
//            while (!buffer.isEmpty()) {
//                flush();
//            }
//        }
//    }
//
//    public void stop() {
//        if (executorService != null) {
//            executorService.shutdown();
//        }
//    }
//}
