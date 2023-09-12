package com.flink.job.task.runner;

import com.flink.job.config.Config;
import com.flink.job.config.Parameters;
import com.flink.job.task.ReportDataTask;
import org.apache.flink.api.java.utils.ParameterTool;

import static com.flink.job.config.Parameters.*;

public class ReportLogRunner {

    public static void main(String[] args) throws Exception {
        ParameterTool tool = ParameterTool.fromArgs(args);
        Parameters inputParams = new Parameters(tool);
        Config config = new Config(inputParams, STRING_PARAMS, INT_PARAMS, BOOL_PARAMS);

        ReportDataTask task = new ReportDataTask(config);
        task.run();
    }
}
