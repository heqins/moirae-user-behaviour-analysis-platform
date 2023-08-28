package com.flink.job.task;

import com.flink.job.config.Config;
import com.flink.job.config.Parameters;
import org.apache.flink.api.java.utils.ParameterTool;

import static com.flink.job.config.Parameters.*;

public class EtlTaskRunner {
    public static void main(String[] args) throws Exception {
        ParameterTool tool = ParameterTool.fromArgs(args);
        Parameters inputParams = new Parameters(tool);
        Config config = new Config(inputParams, STRING_PARAMS, INT_PARAMS, BOOL_PARAMS);

        LogEtlTask logEtlTask = new LogEtlTask(config);
        logEtlTask.run();
    }
}
