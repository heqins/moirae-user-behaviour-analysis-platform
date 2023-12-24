package com.flink.job.task;

import com.flink.job.config.Config;
import com.flink.job.config.Parameters;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

import static com.flink.job.config.Parameters.*;

/**
 * @author heqin
 */
public class OfflineLogToMysqlOdsTask {

    private Config config;

    public OfflineLogToMysqlOdsTask(Config config) {
        this.config = config;
    }

    public void run() throws Exception {
        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        TableEnvironment tableEnv = TableEnvironment.create(environmentSettings);

        String csvFilePath = "file:///Users/heqin/workspace/java/user-behaviour-analysis/flink-job/src/main/resources/data/mobile-recommend/tianchi_mobile_recommend_train_user.csv";

        // 创建CSV TableSource
//        TableSource<Row> csvTableSource = CsvTableSource.builder()
//                .path(csvFilePath)
//                .field("user_id", Types.LONG())
//                .field("item_id", Types.LONG())
//                .field("behavior_type", Types.INT())
//                .field("user_geohash", Types.STRING())
//                .field("item_category", Types.INT())
//                .field("current_time", Types.STRING())  // Assuming 'current_time' is a timestamp as a string
//                .ignoreFirstLine()
//                .build();

//        String s = csvTableSource.explainSource();

        String csvSourceSql = "CREATE TABLE csv_table (\n" +
                "  user_id BIGINT,\n" +
                "  item_id BIGINT,\n" +
                "  behavior_type INT," +
                "  user_geohash VARCHAR(64) NULL," +
                "  item_category INT," +
                "  update_time VARCHAR(64)" +
                ") WITH (\n" +
                "  'connector'='filesystem',\n" +
                "  'path'='/Users/heqin/workspace/java/user-behaviour-analysis/flink-job/src/main/resources/data/mobile-recommend/tianchi_mobile_recommend_train_user.csv',\n" +
                "  'format'='csv'\n" +
                ")";

        tableEnv.executeSql(csvSourceSql);

        String mysqlSinkSql = "CREATE TABLE MyUserTable (\n" +
                "  user_id BIGINT,\n" +
                "  item_id BIGINT,\n" +
                "  behavior_type INT," +
                "  user_geohash VARCHAR(64) NULL," +
                "  item_category INT," +
                "  update_time VARCHAR(64)" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://localhost:3306/user_behaviour_analysis_offline_ods_dev',\n" +
                "   'username' = 'root',\n" +
                "   'password' = '5323105',\n" +
                "   'table-name' = 'MyUserTable'\n" +
                ")";

        TableResult tableResult = tableEnv.executeSql(mysqlSinkSql);

        // 注册TableSource
        TableResult tableResult1 = tableEnv.executeSql("insert into MyUserTable select * from csv_table");

        // 执行 Flink 应用程序
    }

    public static void main(String[] args) throws Exception {
        ParameterTool tool = ParameterTool.fromArgs(args);
        Parameters inputParams = new Parameters(tool);
        Config config = new Config(inputParams, STRING_PARAMS, INT_PARAMS, BOOL_PARAMS);

        OfflineLogToMysqlOdsTask task = new OfflineLogToMysqlOdsTask(config);
        task.run();
    }
}
