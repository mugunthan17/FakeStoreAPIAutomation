package com.fakestoreapi.automation.pages;

import com.fakestoreapi.automation.utils.JsonUtils;
import com.fakestoreapi.automation.utils.LoggerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart extends RestCommonDefs {
    
    public Map<String, String> getAllCarts() {
        LoggerUtil.info("Fetching all carts");
        return get(REST_GET_ALL_CARTS);
    }
    
    public Map<String, String> getCartById(int cartId) {
        LoggerUtil.info("Fetching cart with ID: " + cartId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(cartId));
        
        String endpoint = substitutePlaceholders(REST_GET_CART_BY_ID, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> getLimitedCarts(int limit) {
        LoggerUtil.info("Fetching " + limit + " carts");
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("limit", String.valueOf(limit));
        
        String endpoint = substitutePlaceholders(REST_GET_LIMITED_CARTS, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> getSortedCarts(String sortOrder) {
        LoggerUtil.info("Fetching carts sorted in " + sortOrder + " order");
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("sort", sortOrder);
        
        String endpoint = substitutePlaceholders(REST_GET_SORTED_CARTS, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> getCartsInDateRange(String startDate, String endDate) {
        LoggerUtil.info("Fetching carts from " + startDate + " to " + endDate);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("startdate", startDate);
        pathParams.put("enddate", endDate);
        
        String endpoint = substitutePlaceholders(REST_GET_CARTS_DATE_RANGE, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> getUserCarts(int userId) {
        LoggerUtil.info("Fetching carts for user ID: " + userId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", String.valueOf(userId));
        
        String endpoint = substitutePlaceholders(REST_GET_USER_CARTS, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> createCart(Map<String, Object> cartData) {
        LoggerUtil.info("Creating new cart");
        String requestBody = JsonUtils.toJson(cartData);
        return post(REST_POST_CREATE_CART, requestBody);
    }
    
    public Map<String, String> updateCart(int cartId, Map<String, Object> cartData) {
        LoggerUtil.info("Updating cart with ID: " + cartId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(cartId));
        
        String endpoint = substitutePlaceholders(REST_PUT_UPDATE_CART, pathParams);
        String requestBody = JsonUtils.toJson(cartData);
        return put(endpoint, requestBody);
    }
    
    public Map<String, String> patchCart(int cartId, Map<String, Object> cartData) {
        LoggerUtil.info("Partially updating cart with ID: " + cartId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(cartId));
        
        String endpoint = substitutePlaceholders(REST_PATCH_CART, pathParams);
        String requestBody = JsonUtils.toJson(cartData);
        return patch(endpoint, requestBody);
    }
    
    public Map<String, String> deleteCart(int cartId) {
        LoggerUtil.info("Deleting cart with ID: " + cartId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(cartId));
        
        String endpoint = substitutePlaceholders(REST_DELETE_CART, pathParams);
        return delete(endpoint);
    }
    
    public int getCartCount() {
        getAllCarts();
        List<Object> carts = extractFromResponse("$");
        int count = carts != null ? carts.size() : 0;
        LoggerUtil.info("Total carts count: " + count);
        return count;
    }
    
    public List<Map<String, Object>> getProductsInCart(int cartId) {
        getCartById(cartId);
        List<Map<String, Object>> products = extractFromResponse("products");
        int productCount = products != null ? products.size() : 0;
        LoggerUtil.info("Cart contains " + productCount + " products");
        return products;
    }
    
    public Integer getCartUserId(int cartId) {
        getCartById(cartId);
        Integer userId = extractFromResponse("userId");
        LoggerUtil.info("Cart belongs to user ID: " + userId);
        return userId;
    }
    
    public boolean isCartExists(int cartId) {
        Map<String, String> response = getCartById(cartId);
        boolean exists = "200".equals(response.get("RESPONSE_CODE"));
        LoggerUtil.info("Cart ID " + cartId + " exists: " + exists);
        return exists;
    }
    
    public int getTotalProductsInCart(int cartId) {
        List<Map<String, Object>> products = getProductsInCart(cartId);
        return products != null ? products.size() : 0;
    }
}
