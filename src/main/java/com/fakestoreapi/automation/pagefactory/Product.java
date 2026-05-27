package com.fakestoreapi.automation.pagefactory;

import com.fakestoreapi.automation.utils.JsonUtils;
import com.fakestoreapi.automation.utils.LoggerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product extends RestCommonDefs {
    
    public Map<String, String> getAllProducts() {
        LoggerUtil.info("Fetching all products");
        return get(REST_GET_ALL_PRODUCTS);
    }
    
    public Map<String, String> getProductById(int productId) {
        LoggerUtil.info("Fetching product with ID: " + productId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(productId));
        
        String endpoint = substitutePlaceholders(REST_GET_PRODUCT_BY_ID, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> getLimitedProducts(int limit) {
        LoggerUtil.info("Fetching " + limit + " products");
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("limit", String.valueOf(limit));
        
        String endpoint = substitutePlaceholders(REST_GET_LIMITED_PRODUCTS, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> getSortedProducts(String sortOrder) {
        LoggerUtil.info("Fetching products sorted in " + sortOrder + " order");
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("sort", sortOrder);
        
        String endpoint = substitutePlaceholders(REST_GET_SORTED_PRODUCTS, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> getAllCategories() {
        LoggerUtil.info("Fetching all product categories");
        return get(REST_GET_ALL_CATEGORIES);
    }
    
    public Map<String, String> getProductsByCategory(String category) {
        LoggerUtil.info("Fetching products in category: " + category);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("category", category);
        
        String endpoint = substitutePlaceholders(REST_GET_PRODUCTS_BY_CATEGORY, pathParams);
        return get(endpoint);
    }
    
    public Map<String, String> createProduct(Map<String, Object> productData) {
        LoggerUtil.info("Creating new product");
        String requestBody = JsonUtils.toJson(productData);
        return post(REST_POST_CREATE_PRODUCT, requestBody);
    }
    
    public Map<String, String> updateProduct(int productId, Map<String, Object> productData) {
        LoggerUtil.info("Updating product with ID: " + productId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(productId));
        
        String endpoint = substitutePlaceholders(REST_PUT_UPDATE_PRODUCT, pathParams);
        String requestBody = JsonUtils.toJson(productData);
        return put(endpoint, requestBody);
    }
    
    public Map<String, String> patchProduct(int productId, Map<String, Object> productData) {
        LoggerUtil.info("Partially updating product with ID: " + productId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(productId));
        
        String endpoint = substitutePlaceholders(REST_PATCH_PRODUCT, pathParams);
        String requestBody = JsonUtils.toJson(productData);
        return patch(endpoint, requestBody);
    }
    
    public Map<String, String> deleteProduct(int productId) {
        LoggerUtil.info("Deleting product with ID: " + productId);
        
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", String.valueOf(productId));
        
        String endpoint = substitutePlaceholders(REST_DELETE_PRODUCT, pathParams);
        return delete(endpoint);
    }
    
    public int getProductCount() {
        getAllProducts();
        List<Object> products = extractFromResponse("$");
        int count = products != null ? products.size() : 0;
        LoggerUtil.info("Total products count: " + count);
        return count;
    }
    
    public String getProductTitle(int productId) {
        getProductById(productId);
        String title = extractFromResponse("title");
        LoggerUtil.info("Product title: " + title);
        return title;
    }
    
    public Double getProductPrice(int productId) {
        getProductById(productId);
        Double price = extractFromResponse("price");
        LoggerUtil.info("Product price: " + price);
        return price;
    }
    
    public boolean isProductExists(int productId) {
        Map<String, String> response = getProductById(productId);
        boolean exists = "200".equals(response.get("RESPONSE_CODE"));
        LoggerUtil.info("Product ID " + productId + " exists: " + exists);
        return exists;
    }
}
