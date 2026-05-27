package com.fakestoreapi.automation.pagefactory;

import com.fakestoreapi.automation.utils.ConfigReader;
import com.fakestoreapi.automation.utils.ExtentManager;
import com.fakestoreapi.automation.utils.LoggerUtil;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestCommonDefs {
    
    
    protected static String baseUrl;
    protected Response response;
    protected Map<String, String> lastHttpResponse;
    
    
    public static final String HTTP_GET_METHOD = "GET";
    public static final String HTTP_POST_METHOD = "POST";
    public static final String HTTP_PUT_METHOD = "PUT";
    public static final String HTTP_DELETE_METHOD = "DELETE";
    public static final String HTTP_PATCH_METHOD = "PATCH";
    
    
    public static final List<String> SUCCESS_RESPONSE_CODE = Arrays.asList("200", "201", "202", "204");
    public static final List<String> FAILURE_RESPONSE_CODE = Arrays.asList("400", "403", "404", "500");
    public static final List<String> SERVER_RESPONSE_CODE = Arrays.asList("500", "501", "502", "503", "504");
    
    
    public static final int MAX_TIMEOUT_FOR_RESTCALL = 5; 
    public static final int MAX_RESPONSE_TIME = 15; 
    
    
    public static final String REST_LOGIN = "/auth/login";
    
    
    public static final String REST_GET_ALL_PRODUCTS = "/products";
    public static final String REST_GET_PRODUCT_BY_ID = "/products/$id";
    public static final String REST_GET_LIMITED_PRODUCTS = "/products?limit=$limit";
    public static final String REST_GET_SORTED_PRODUCTS = "/products?sort=$sort";
    public static final String REST_GET_ALL_CATEGORIES = "/products/categories";
    public static final String REST_GET_PRODUCTS_BY_CATEGORY = "/products/category/$category";
    public static final String REST_POST_CREATE_PRODUCT = "/products";
    public static final String REST_PUT_UPDATE_PRODUCT = "/products/$id";
    public static final String REST_PATCH_PRODUCT = "/products/$id";
    public static final String REST_DELETE_PRODUCT = "/products/$id";
    
    
    public static final String REST_GET_ALL_CARTS = "/carts";
    public static final String REST_GET_CART_BY_ID = "/carts/$id";
    public static final String REST_GET_LIMITED_CARTS = "/carts?limit=$limit";
    public static final String REST_GET_SORTED_CARTS = "/carts?sort=$sort";
    public static final String REST_GET_CARTS_DATE_RANGE = "/carts?startdate=$startdate&enddate=$enddate";
    public static final String REST_GET_USER_CARTS = "/carts/user/$userId";
    public static final String REST_POST_CREATE_CART = "/carts";
    public static final String REST_PUT_UPDATE_CART = "/carts/$id";
    public static final String REST_PATCH_CART = "/carts/$id";
    public static final String REST_DELETE_CART = "/carts/$id";
    
    
    public static final String REST_GET_ALL_USERS = "/users";
    public static final String REST_GET_USER_BY_ID = "/users/$id";
    public static final String REST_POST_CREATE_USER = "/users";
    public static final String REST_PUT_UPDATE_USER = "/users/$id";
    public static final String REST_PATCH_USER = "/users/$id";
    public static final String REST_DELETE_USER = "/users/$id";
    
    static {
        baseUrl = ConfigReader.getBaseUrl();
        RestAssured.baseURI = baseUrl;
    }
    
    public RestCommonDefs() {
        lastHttpResponse = new HashMap<>();
    }
    
    public Map<String, String> sendHttpRequest(String url, String method, Map<String, String> headers, String... options) {
        Map<String, String> httpResponse = new HashMap<>();
        
        try {
            Response response = null;
            int timeoutMinutes = MAX_TIMEOUT_FOR_RESTCALL;
            String requestBody = "";
            
            
            if (options.length >= 1) {
                requestBody = options[0];
            }
            if (options.length >= 2) {
                try {
                    timeoutMinutes = Integer.parseInt(options[1]);
                } catch (NumberFormatException e) {
                    LoggerUtil.warn("Invalid timeout value, using default: " + MAX_TIMEOUT_FOR_RESTCALL);
                }
            }
            
            LoggerUtil.info("Sending HTTP Request --> " + method + " " + url);
            if (!method.equalsIgnoreCase(HTTP_GET_METHOD) && !requestBody.isEmpty()) {
                LoggerUtil.debug("Request Payload: " + requestBody);
            }
            
            
            int maxTimeoutMs = timeoutMinutes * 60 * 1000;
            RestAssuredConfig config = RestAssured.config()
                    .redirect(RedirectConfig.redirectConfig().followRedirects(false))
                    .httpClient(HttpClientConfig.httpClientConfig()
                            .setParam("http.connection.timeout", maxTimeoutMs)
                            .setParam("http.socket.timeout", maxTimeoutMs)
                            .setParam("http.connection-manager.timeout", maxTimeoutMs));
            
            LoggerUtil.debug("REST call timeout configured: " + timeoutMinutes + " minutes");
            LoggerUtil.info("Headers: " + headers.toString());
            
            long startTime = System.currentTimeMillis();
            
            
            if (method.equalsIgnoreCase(HTTP_GET_METHOD)) {
                response = RestAssured.given()
                        .config(config)
                        .headers(headers)
                        .get(url);
                        
            } else if (method.equalsIgnoreCase(HTTP_POST_METHOD)) {
                response = RestAssured.given()
                        .config(config)
                        .headers(headers)
                        .body(requestBody)
                        .post(url);
                        
            } else if (method.equalsIgnoreCase(HTTP_PUT_METHOD)) {
                response = RestAssured.given()
                        .config(config)
                        .headers(headers)
                        .body(requestBody)
                        .put(url);
                        
            } else if (method.equalsIgnoreCase(HTTP_DELETE_METHOD)) {
                response = RestAssured.given()
                        .config(config)
                        .headers(headers)
                        .body(requestBody)
                        .delete(url);
                        
            } else if (method.equalsIgnoreCase(HTTP_PATCH_METHOD)) {
                response = RestAssured.given()
                        .config(config)
                        .headers(headers)
                        .body(requestBody)
                        .patch(url);
                        
            } else {
                LoggerUtil.error("Unsupported HTTP method: " + method);
                httpResponse.put("RESPONSE_CODE", "999");
                httpResponse.put("RESPONSE_BODY", "ERROR: Unsupported HTTP method");
                return httpResponse;
            }
            
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            
            
            if (response != null) {
                this.response = response;
                String responseCode = String.valueOf(response.getStatusCode());
                String responseBody = response.getBody().asString();
                
                httpResponse.put("RESPONSE_CODE", responseCode);
                httpResponse.put("RESPONSE_BODY", responseBody);
                
                LoggerUtil.info("Response Code: " + responseCode);
                LoggerUtil.info("Response Time: " + elapsedTime + " seconds");
                
                
                if (ExtentManager.getTest() != null) {
                    ExtentManager.getTest().info(method + " Request: " + url);
                    if (!requestBody.isEmpty()) {
                        ExtentManager.getTest().info("Request Body: " + requestBody);
                    }
                    ExtentManager.getTest().info("Response Status: " + responseCode);
                    ExtentManager.getTest().info("Response Time: " + elapsedTime + " seconds");
                }
                
                
                if (elapsedTime >= MAX_RESPONSE_TIME) {
                    LoggerUtil.warn("Response time exceeded threshold: " + elapsedTime + "s (Max: " + MAX_RESPONSE_TIME + "s)");
                }
                
            } else {
                LoggerUtil.error("Response is null");
                httpResponse.put("RESPONSE_CODE", "999");
                httpResponse.put("RESPONSE_BODY", "ERROR: No response received");
            }
            
        } catch (Exception ex) {
            LoggerUtil.error("Exception in sendHttpRequest: " + ex.getMessage(), ex);
            httpResponse.put("RESPONSE_CODE", "999");
            httpResponse.put("RESPONSE_BODY", "ERROR: " + ex.getMessage());
        }
        
        lastHttpResponse = httpResponse;
        return httpResponse;
    }
    
    public String substitutePlaceholders(String url, Map<String, String> params) {
        String processedUrl = url;
        
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String placeholder = "$" + entry.getKey();
                processedUrl = processedUrl.replace(placeholder, entry.getValue());
            }
        }
        
        LoggerUtil.debug("URL after placeholder substitution: " + processedUrl);
        return processedUrl;
    }
    
    public String buildFullUrl(String endpoint) {
        return baseUrl + endpoint;
    }
    
    public Map<String, String> createDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }
    
    public Map<String, String> get(String endpoint, Map<String, String> headers) {
        String fullUrl = buildFullUrl(endpoint);
        return sendHttpRequest(fullUrl, HTTP_GET_METHOD, headers);
    }
    
    public Map<String, String> get(String endpoint) {
        return get(endpoint, createDefaultHeaders());
    }
    
    public Map<String, String> post(String endpoint, Map<String, String> headers, String requestBody) {
        String fullUrl = buildFullUrl(endpoint);
        return sendHttpRequest(fullUrl, HTTP_POST_METHOD, headers, requestBody);
    }
    
    public Map<String, String> post(String endpoint, String requestBody) {
        return post(endpoint, createDefaultHeaders(), requestBody);
    }
    
    public Map<String, String> put(String endpoint, Map<String, String> headers, String requestBody) {
        String fullUrl = buildFullUrl(endpoint);
        return sendHttpRequest(fullUrl, HTTP_PUT_METHOD, headers, requestBody);
    }
    
    public Map<String, String> put(String endpoint, String requestBody) {
        return put(endpoint, createDefaultHeaders(), requestBody);
    }
    
    public Map<String, String> patch(String endpoint, Map<String, String> headers, String requestBody) {
        String fullUrl = buildFullUrl(endpoint);
        return sendHttpRequest(fullUrl, HTTP_PATCH_METHOD, headers, requestBody);
    }
    
    public Map<String, String> patch(String endpoint, String requestBody) {
        return patch(endpoint, createDefaultHeaders(), requestBody);
    }
    
    public Map<String, String> delete(String endpoint, Map<String, String> headers) {
        String fullUrl = buildFullUrl(endpoint);
        return sendHttpRequest(fullUrl, HTTP_DELETE_METHOD, headers, "");
    }
    
    public Map<String, String> delete(String endpoint) {
        return delete(endpoint, createDefaultHeaders());
    }
    
    public Map<String, String> getLastHttpResponse() {
        return lastHttpResponse;
    }
    
    public String getResponseCode() {
        return lastHttpResponse.getOrDefault("RESPONSE_CODE", "999");
    }
    
    public String getResponseBody() {
        return lastHttpResponse.getOrDefault("RESPONSE_BODY", "");
    }
    
    public Response getResponse() {
        return response;
    }
    
    public int getStatusCode() {
        try {
            return Integer.parseInt(getResponseCode());
        } catch (NumberFormatException e) {
            return 999;
        }
    }
    
    public long getResponseTime() {
        return response != null ? response.getTime() : 0;
    }
    
    public void printResponse() {
        if (response != null) {
            LoggerUtil.info("Response Body: " + response.getBody().asPrettyString());
        } else {
            LoggerUtil.info("Response Body: " + getResponseBody());
        }
    }
    
    public boolean validateStatusCode(int expectedStatusCode) {
        int actualStatusCode = getStatusCode();
        boolean isValid = actualStatusCode == expectedStatusCode;
        
        if (isValid) {
            LoggerUtil.info("Status code validated: " + actualStatusCode);
        } else {
            LoggerUtil.error("Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
        }
        
        return isValid;
    }
    
    public boolean isResponseSuccess() {
        String responseCode = getResponseCode();
        return SUCCESS_RESPONSE_CODE.contains(responseCode);
    }
    
    public boolean isResponseFailure() {
        String responseCode = getResponseCode();
        return FAILURE_RESPONSE_CODE.contains(responseCode);
    }
    
    public <T> T extractFromResponse(String jsonPath) {
        if (response != null) {
            return response.jsonPath().get(jsonPath);
        }
        return null;
    }
    
    public String getStatusMessage(String statusCode) {
        switch (statusCode) {
            case "200":
            case "201":
            case "202":
            case "204":
                return "Success - Operation completed successfully";
            case "400":
                return "Bad Request - Invalid data";
            case "401":
                return "Unauthorized - Authentication required";
            case "404":
                return "Not Found - Resource not found";
            case "500":
                return "Internal Server Error - Server error occurred";
            default:
                return "Response Code: " + statusCode;
        }
    }
}
