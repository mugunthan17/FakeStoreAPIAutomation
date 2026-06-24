package com.fakestoreapi.automation.pages;

import com.fakestoreapi.automation.utils.JsonUtils;
import com.fakestoreapi.automation.utils.LoggerUtil;

import java.util.HashMap;
import java.util.Map;

public class Login extends RestCommonDefs {
    
    public Map<String, String> login(String username, String password) {
        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("username", username);
        loginPayload.put("password", password);
        
        String requestBody = JsonUtils.toJson(loginPayload);
        LoggerUtil.info("Attempting login with username: " + username);
        
        return post(REST_LOGIN, requestBody);
    }
    
    public Map<String, String> loginWithJsonBody(String requestBody) {
        LoggerUtil.info("Performing login with custom JSON body");
        return post(REST_LOGIN, requestBody);
    }
    
    public Map<String, String> loginWithHeaders(String username, String password, Map<String, String> headers) {
        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("username", username);
        loginPayload.put("password", password);
        
        String requestBody = JsonUtils.toJson(loginPayload);
        LoggerUtil.info("Attempting login with username: " + username + " and custom headers");
        
        return post(REST_LOGIN, headers, requestBody);
    }
    
    public String getAuthToken() {
        try {
            String token = extractFromResponse("token");
            if (token != null) {
                LoggerUtil.info("Auth token extracted successfully");
            } else {
                LoggerUtil.error("Token not found in response");
            }
            return token;
        } catch (Exception e) {
            LoggerUtil.error("Failed to extract auth token: " + e.getMessage());
            return null;
        }
    }
    
    public boolean isLoginSuccessful() {
        if (!isResponseSuccess()) {
            LoggerUtil.error("Login failed - HTTP status: " + getResponseCode());
            return false;
        }
        
        String token = getAuthToken();
        boolean isSuccessful = token != null && !token.isEmpty();
        
        if (isSuccessful) {
            LoggerUtil.info("Login successful");
        } else {
            LoggerUtil.error("Login failed - No token received");
        }
        
        return isSuccessful;
    }
    
    public String performLoginAndGetToken(String username, String password) {
        login(username, password);
        return getAuthToken();
    }
    
    public Map<String, String> createUser(Map<String, Object> userData) {
        LoggerUtil.info("Creating new user");
        String requestBody = JsonUtils.toJson(userData);
        return post(REST_POST_CREATE_USER, requestBody);
    }
    
    public Map<String, String> getAllUsers() {
        LoggerUtil.info("Fetching all users");
        return get(REST_GET_ALL_USERS);
    }
    
    public Map<String, String> getUserById(int userId) {
        LoggerUtil.info("Fetching user with ID: " + userId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(userId));
        
        String endpoint = substitutePlaceholders(REST_GET_USER_BY_ID, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> deleteUser(int userId) {
        LoggerUtil.info("Deleting user with ID: " + userId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(userId));
        
        String endpoint = substitutePlaceholders(REST_DELETE_USER, pathParams);
        return delete(endpoint);
    }
}
