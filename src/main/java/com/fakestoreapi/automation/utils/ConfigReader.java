package com.fakestoreapi.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/main/resources/config/config.properties";
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fis);
            fis.close();
            LoggerUtil.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            LoggerUtil.error("Failed to load configuration properties: " + e.getMessage());
            throw new RuntimeException("Configuration file not found at: " + CONFIG_FILE_PATH);
        }
    }
    
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            LoggerUtil.warn("Property not found: " + key);
        }
        return value;
    }
    
    public static String getBaseUrl() {
        return getProperty("base.url");
    }
    
    public static String getLoginEndpoint() {
        return getProperty("api.login");
    }
    
    public static String getProductsEndpoint() {
        return getProperty("api.products");
    }
    
    public static String getCartsEndpoint() {
        return getProperty("api.carts");
    }
    
    public static String getUsersEndpoint() {
        return getProperty("api.users");
    }
    
    public static int getRequestTimeout() {
        return Integer.parseInt(getProperty("request.timeout"));
    }
    
    public static int getRetryCount() {
        return Integer.parseInt(getProperty("retry.count"));
    }
    
    public static String getReportPath() {
        return getProperty("report.path");
    }
    
    public static String getReportName() {
        return getProperty("report.name");
    }
    
    public static String getTestDataPath() {
        return getProperty("testdata.path");
    }
}
