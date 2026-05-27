package com.fakestoreapi.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
    
    public static void info(String message) {
        logger.info(message);
    }
    
    public static void debug(String message) {
        logger.debug(message);
    }
    
    public static void warn(String message) {
        logger.warn(message);
    }
    
    public static void error(String message) {
        logger.error(message);
    }
    
    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
    
    public static void logRequest(String method, String endpoint) {
        info(String.format("API Request: %s %s", method, endpoint));
    }
    
    public static void logResponse(int statusCode, long responseTime) {
        info(String.format("API Response: Status Code = %d, Response Time = %d ms", statusCode, responseTime));
    }
}
