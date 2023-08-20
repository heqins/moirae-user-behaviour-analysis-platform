package com.flink.job.util;

import com.api.common.constant.ConfigConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author heqin
 */
@Slf4j
public class PropertyUtils {

    private static final Properties myProperties;

    static {
        myProperties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigConstant.CONFIG_FILE_PATH);

        try {
            myProperties.load(in);
        }catch (IOException ioe) {
            log.error("load properties error", ioe);
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
