package com.fakestoreapi.automation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.response.Response;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LoggerUtil.error("Failed to convert object to JSON: " + e.getMessage());
            return null;
        }
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LoggerUtil.error("Failed to convert JSON to object: " + e.getMessage());
            return null;
        }
    }
    
    public static <T> T readJsonFromFile(String filePath, Class<T> clazz) {
        try {
            return objectMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            LoggerUtil.error("Failed to read JSON from file: " + e.getMessage());
            return null;
        }
    }
    
    public static void writeJsonToFile(Object object, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), object);
            LoggerUtil.info("JSON written to file: " + filePath);
        } catch (IOException e) {
            LoggerUtil.error("Failed to write JSON to file: " + e.getMessage());
        }
    }
    
    public static JsonNode getJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            LoggerUtil.error("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }
    
    public static String getValueFromJson(String json, String path) {
        try {
            JsonNode node = objectMapper.readTree(json);
            String[] keys = path.split("\\.");
            for (String key : keys) {
                node = node.get(key);
            }
            return node != null ? node.asText() : null;
        } catch (Exception e) {
            LoggerUtil.error("Failed to extract value from JSON: " + e.getMessage());
            return null;
        }
    }
    
    public static Map<String, Object> responseToMap(Response response) {
        try {
            return objectMapper.readValue(response.asString(), Map.class);
        } catch (JsonProcessingException e) {
            LoggerUtil.error("Failed to convert response to map: " + e.getMessage());
            return null;
        }
    }
    
    public static String prettyPrintJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            LoggerUtil.error("Failed to pretty print JSON: " + e.getMessage());
            return json;
        }
    }
}
