package com.flink.job.util;

import com.flink.job.config.Config;
import com.flink.job.config.Parameters;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.flink.job.config.Parameters.*;

/**
 * @author heqin
 */
public class PropertyUtils {

    private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class);

    private static final Properties myProperties;

    static {
        myProperties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");

        try {
            myProperties.load(in);
        }catch (IOException ioe) {
            log.error("PropertyUtils load properties error", ioe);
            ioe.printStackTrace();
        }
    }

    public static String getStrValue(String key) {
        if (myProperties.containsKey(key)) {
            return String.valueOf(myProperties.getProperty(key));
        }

        return "";
    }

    public static int getIntValue(String key) {
        if (myProperties.containsKey(key)) {
            return Integer.parseInt(myProperties.getProperty(key));
        }

        return -1;
    }

    public static Properties getKafkaProperty() {
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", PropertyUtils.getStrValue("kafka.bootstrap.servers"));
        properties.setProperty("zookeeper.connect", PropertyUtils.getStrValue("kafka.zookeeper.connect"));

        return properties;
    }


}
