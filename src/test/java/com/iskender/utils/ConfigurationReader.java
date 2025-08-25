package com.iskender.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationReader {

    private static final Properties properties;

    static {
        try {
            properties = new Properties();
            InputStream input = ConfigurationReader.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                throw new RuntimeException("Configuration file 'config.properties' not found in resources");
            }
            properties.load(input);
            input.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    public static String getProperty(String key) {
        // Check system properties first, then config file
        String systemValue = System.getProperty(key);
        return systemValue != null ? systemValue : properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    // Specific getters
    public static String getAppUrl() {
        return getProperty("app.url", "https://useinsider.com/");
    }

    public static String getApiBaseUrl() {
        return getProperty("api.base.url", "https://petstore.swagger.io/v2");
    }

    public static boolean isBrowserHeadless() {
        return getBooleanProperty("browser.headless", false);
    }

    public static int getTimeout() {
        return getIntProperty("timeout", 10);
    }
}