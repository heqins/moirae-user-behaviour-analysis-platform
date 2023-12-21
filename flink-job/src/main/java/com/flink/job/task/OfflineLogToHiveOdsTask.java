package com.flink.job.task;

import com.flink.job.config.Config;
import com.flink.job.config.Parameters;
import com.flink.job.source.KafkaSource;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

import static com.flink.job.config.Parameters.*;

/**
 * @author heqin
 */
public class OfflineLogToHiveOdsTask {

    private Config config;

    public OfflineLogToHiveOdsTask(Config config) {
        this.config = config;
    }

    public void run() throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(environment, settings);
        Configuration conf = tableEnv.getConfig().getConfiguration();

        DataStreamSource<String> sourceStream = environment.addSource(KafkaSource.getFlinkKafkaSource(config.get(OFFLINE_LOG_TOPIC), config.get(OFFLINE_LOG_GROUP_ID)));

        conf.setString("table.exec.hive.fallback-mapred-reader", "true");
        tableEnv.getConfig().addConfiguration(conf);
        tableEnv.getConfig().setSqlDialect(SqlDialect.HIVE);
        String name = "hive";
        String defaultDatabase = "user_behaviour_analysis_dev";
        ///home/hadoop/bigdata/hive/
        String hiveConfDir = "hdfs://ns1/user/hive/conf";

        HiveCatalog hiveCatalog = new HiveCatalog(name, defaultDatabase, hiveConfDir,"3.1.2");

        // 注册 Kafka 数据流为 Flink 表
        tableEnv.createTemporaryView("kafka_source", sourceStream, "id, name, value");

        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS user_behaviour_analysis_dev");
        tableEnv.useDatabase("user_behaviour_analysis_dev");

        // 创建 Hive 表（带分区）
        tableEnv.executeSql("CREATE TABLE IF NOT EXISTS behaviour_log_ods (id INT, name STRING, value DOUBLE) " +
                        "PARTITIONED BY (date STRING) " +
                        "STORED AS PARQUET " +
                        "TBLPROPERTIES ('parquet.compress'='GZIP')");

        // 编写 Flink SQL 查询，将 Kafka 数据写入 Hive 分区表
        String flinkSql = "INSERT INTO ods_table_partitioned PARTITION (date='20220101') SELECT id, name, value FROM kafka_source";
        tableEnv.executeSql(flinkSql);

        // 执行 Flink 应用程序
        environment.execute("OfflineLogToHiveOdsTask");
    }

    public static void main(String[] args) throws Exception {
        ParameterTool tool = ParameterTool.fromArgs(args);
        Parameters inputParams = new Parameters(tool);
        Config config = new Config(inputParams, STRING_PARAMS, INT_PARAMS, BOOL_PARAMS);

        OfflineLogToHiveOdsTask task = new OfflineLogToHiveOdsTask(config);
        task.run();
    }
}
