package com.api.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    private static final Properties PROPERTIES;

    private static final String CONFIG_FILE_PATH = "config.properties";

    static {
        PROPERTIES = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_PATH);

        try {
            PROPERTIES.load(in);
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static String getStrValue(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static int getIntValue(String key) {
        return Integer.parseInt(PROPERTIES.getProperty(key));
    }
}
