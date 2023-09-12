package com.flink.job.config;

import com.api.common.config.Param;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.table.descriptors.Kafka;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author heqin
 */
public class Parameters {

  private final ParameterTool tool;

  public Parameters(ParameterTool tool) {
    this.tool = tool;
  }

  <T> T getOrDefault(Param<T> param) {
    if (!tool.has(param.getName())) {
      return param.getDefaultValue();
    }

    Object value;
    if (param.getType() == Integer.class) {
      value = tool.getInt(param.getName());
    } else if (param.getType() == Long.class) {
      value = tool.getLong(param.getName());
    } else if (param.getType() == Double.class) {
      value = tool.getDouble(param.getName());
    } else if (param.getType() == Boolean.class) {
      value = tool.getBoolean(param.getName());
    } else {
      value = tool.get(param.getName());
    }
    return param.getType().cast(value);
  }

  public static Parameters fromArgs(String[] args) {
    ParameterTool tool = ParameterTool.fromArgs(args);
    return new Parameters(tool);
  }

  /**
   * Kafka
   */
  public static final Param<String> KAFKA_HOST = Param.string("kafka-host", "localhost");
  public static final Param<Integer> KAFKA_PORT = Param.integer("kafka-port", 9092);

  /**
   * flink-etl-flow: report-log-data -> log-etl-main -> log-sink
   */
  public static final Param<String> LOG_ETL_MAIN_TOPIC = Param.string("log-etl-main-topic", "log-etl-main");

  public static final Param<String> LOG_SINK_TOPIC = Param.string("log-sink-topic", "log-sink");

  public static final Param<String> REPORT_LOG_DATA_TOPIC = Param.string("report-log-data-topic", "report-log-data");

  public static final Param<String> LOG_DATA_INVALID_TOPIC = Param.string("report-log-invalid-data-topic", "report-log-invalid-data");

  public static final Param<String> REPORT_LOG_DATA_GROUP_ID = Param.string("report-log-data-flink-group-id", "reportLogDataDev");

  public static final Param<String> LOG_ETL_MAIN__GROUP_ID = Param.string("log-etl-main-group-id", "logEtlMainDev");

  // Socket
  public static final Param<Integer> SOCKET_PORT = Param.integer("pubsub-rules-export", 9999);

  public static final String LOCAL_MODE_DISABLE_WEB_UI = "-1";

  public static final String LOCAL_WEB_UI_PORT = "50100";

  /**
   * If specified, the port for the web UI or {@link #LOCAL_MODE_DISABLE_WEB_UI} to disable it.
   * Accepts
   *
   * <ul>
   *   <li>a list of ports (“50100,50101”),
   *   <li>ranges (“50100-50200”), or
   *   <li>a combination of both, or
   *   <li>"0" to let the system choose a free port
   * </ul>
   *
   * <p>The chosen port will be shown in the logs in a line like this:
   *
   * <pre>
   *  12:11:04.062 [main] INFO o.a.f.r.d.DispatcherRestEndpoint - Web frontend listening at http://localhost:8081.
   * </pre>
   */
  public static final Param<String> LOCAL_EXECUTION =
      Param.string("local", LOCAL_MODE_DISABLE_WEB_UI);

  public static final Param<String> LOCAL_EXECUTION_WITH_UI =
          Param.string("local", LOCAL_WEB_UI_PORT);

  public static final List<Param<String>> STRING_PARAMS =
      Arrays.asList(
          LOCAL_EXECUTION,
          LOCAL_EXECUTION_WITH_UI,
          KAFKA_HOST,
          LOG_ETL_MAIN_TOPIC,
          LOG_DATA_INVALID_TOPIC,
          LOG_SINK_TOPIC,
          LOG_ETL_MAIN__GROUP_ID,
          REPORT_LOG_DATA_TOPIC,
          REPORT_LOG_DATA_GROUP_ID);

  public static final List<Param<Integer>> INT_PARAMS =
      Arrays.asList(
          KAFKA_PORT,
          SOCKET_PORT);

  public static final List<Param<Boolean>> BOOL_PARAMS = Collections.emptyList();
}
