package com.fakestoreapi.automation.test;

import com.fakestoreapi.automation.pages.Cart;
import com.fakestoreapi.automation.utils.ExcelUtils;
import com.fakestoreapi.automation.utils.ExtentManager;
import com.fakestoreapi.automation.utils.LoggerUtil;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartTests extends BaseTest{
    
    private Cart cart;
    
    @BeforeClass
    public void setUp() {
        LoggerUtil.info("Setting up CartTests...");
        cart = new Cart();
        LoggerUtil.info("CartTests setup completed");
    }
    
    @AfterClass
    public void tearDown() {
        LoggerUtil.info("Tearing down CartTests...");
        LoggerUtil.info("CartTests teardown completed");
    }
    
    @DataProvider(name = "cartTestData")
    public Iterator<Object[]> cartTestData(ITestContext context) {
        
        String testData = context.getCurrentXmlTest().getParameter("testData");
        String testDataSets = context.getCurrentXmlTest().getParameter("testDataSets");
        
        LoggerUtil.info("Loading test data from: " + testData);
        LoggerUtil.info("Test data sets filter: " + testDataSets);
        
        if (testData == null || !testData.contains(":")) {
            LoggerUtil.error("Invalid testData parameter format. Expected: 'filepath:sheetname'");
            throw new RuntimeException("Invalid testData parameter format. Expected: 'filepath:sheetname'");
        }
        
        
        String[] parts = testData.split(":");
        String filePath = parts[0];
        String sheetName = parts[1];
        
        LoggerUtil.info("Excel File: " + filePath);
        LoggerUtil.info("Sheet Name: " + sheetName);
        
        
        ExcelUtils excelUtils = new ExcelUtils(filePath);
        excelUtils.setSheet(sheetName);
        
        
        List<Map<String, String>> testDataList = excelUtils.getAllData();
        
        
        if (testDataSets != null && !"ALL".equalsIgnoreCase(testDataSets.trim())) {
            testDataList = excelUtils.filterTestData(testDataList, testDataSets);
        }
        
        LoggerUtil.info("Total test data rows loaded: " + testDataList.size());
        
        
        List<Object[]> dataProviderList = new ArrayList<>();
        for (Map<String, String> data : testDataList) {
            dataProviderList.add(new Object[]{data});
        }
        
        excelUtils.close();
        
        return dataProviderList.iterator();
    }
    
    @Test(dataProvider = "cartTestData", description = "Add New Cart API Test")
    public void testAddNewCart(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Add New Cart ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String userIdStr = testData.getOrDefault("UserId", "1");
            String date = testData.getOrDefault("Date", "");
            
            int userId = Integer.parseInt(userIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("User ID: " + userId);
            LoggerUtil.info("Date: " + date);
            
            
            List<Map<String, Object>> products = new ArrayList<>();
            
            
            String productId1Str = testData.getOrDefault("ProductId1", "");
            String quantity1Str = testData.getOrDefault("Quantity1", "");
            
            if (!productId1Str.isEmpty() && !quantity1Str.isEmpty()) {
                Map<String, Object> product1 = new HashMap<>();
                product1.put("productId", Integer.parseInt(productId1Str));
                product1.put("quantity", Integer.parseInt(quantity1Str));
                products.add(product1);
                LoggerUtil.info("Added Product 1: ID=" + productId1Str + ", Quantity=" + quantity1Str);
            }
            
            
            String productId2Str = testData.getOrDefault("ProductId2", "");
            String quantity2Str = testData.getOrDefault("Quantity2", "");
            
            if (!productId2Str.isEmpty() && !quantity2Str.isEmpty()) {
                Map<String, Object> product2 = new HashMap<>();
                product2.put("productId", Integer.parseInt(productId2Str));
                product2.put("quantity", Integer.parseInt(quantity2Str));
                products.add(product2);
                LoggerUtil.info("Added Product 2: ID=" + productId2Str + ", Quantity=" + quantity2Str);
            }
            
            
            Map<String, Object> cartPayload = new HashMap<>();
            cartPayload.put("userId", userId);
            cartPayload.put("date", date);
            cartPayload.put("products", products);
            
            LoggerUtil.info("Cart Payload: " + cartPayload.toString());
            
            
            Assert.assertFalse(products.isEmpty(), "Products list should not be empty");
            Assert.assertTrue(userId > 0, "User ID should be greater than 0");
            LoggerUtil.info("✓ Payload Data Validation PASSED: All required fields have values");
            
            
            Map<String, String> response = cart.createCart(cartPayload);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", cart.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("User ID: " + userId);
                ExtentManager.getTest().info("Date: " + date);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Cart Payload: " + cartPayload.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(cart.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (cart.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"userId\""), 
                        "Response should contain 'userId' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'userId' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"products\""), 
                        "Response should contain 'products' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'products' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"userId\":" + userId) || responseBody.contains("\"userId\":\"" + userId + "\""), 
                        "Response should contain the cart userId: " + userId);
                    LoggerUtil.info("✓ User ID Validation PASSED: ID matches requested user (" + userId + ")");
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testAddNewCart: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "cartTestData", description = "Update Cart API Test")
    public void testUpdateCart(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Update Cart ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String cartIdStr = testData.getOrDefault("CartId", "1");
            String userIdStr = testData.getOrDefault("UserId", "1");
            String date = testData.getOrDefault("Date", "");
            
            int cartId = Integer.parseInt(cartIdStr);
            int userId = Integer.parseInt(userIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Cart ID: " + cartId);
            LoggerUtil.info("User ID: " + userId);
            LoggerUtil.info("Date: " + date);
            
            
            List<Map<String, Object>> products = new ArrayList<>();
            
            
            String productsWithQuantities = testData.getOrDefault("ProductsWithQuantities", "");
            LoggerUtil.info("ProductsWithQuantities raw value: '" + productsWithQuantities + "'");
            
            if (!productsWithQuantities.isEmpty()) {
                
                String[] productPairs = productsWithQuantities.split(",");
                LoggerUtil.info("Number of product pairs after split: " + productPairs.length);
                
                for (int i = 0; i < productPairs.length; i++) {
                    String productPair = productPairs[i].trim();
                    LoggerUtil.info("Processing product pair [" + i + "]: '" + productPair + "'");
                    
                    if (!productPair.isEmpty()) {
                        
                        String[] parts = productPair.split(":");
                        LoggerUtil.info("Parts array length: " + parts.length + ", Parts: " + java.util.Arrays.toString(parts));
                        
                        if (parts.length == 2) {
                            String productIdStr = parts[0].trim();
                            String quantityStr = parts[1].trim();
                            LoggerUtil.info("ProductId: '" + productIdStr + "', Quantity: '" + quantityStr + "'");
                            
                            if (!productIdStr.isEmpty() && !quantityStr.isEmpty()) {
                                Map<String, Object> product = new HashMap<>();
                                product.put("productId", Integer.parseInt(productIdStr));
                                product.put("quantity", Integer.parseInt(quantityStr));
                                products.add(product);
                                LoggerUtil.info("Added Product " + (i + 1) + ": ID=" + productIdStr + ", Quantity=" + quantityStr);
                            } else {
                                LoggerUtil.warn("Skipped product pair due to empty productId or quantity");
                            }
                        } else {
                            LoggerUtil.warn("Skipped product pair - expected 2 parts after split by ':', got: " + parts.length);
                        }
                    }
                }
            } else {
                LoggerUtil.warn("ProductsWithQuantities is empty or not found in test data");
            }
            
            LoggerUtil.info("Total products built: " + products.size());
            
            
            Map<String, Object> cartPayload = new HashMap<>();
            cartPayload.put("userId", userId);
            cartPayload.put("date", date);
            cartPayload.put("products", products);
            
            LoggerUtil.info("Cart Payload: " + cartPayload.toString());
            
            
            Assert.assertFalse(products.isEmpty(), "Products list should not be empty");
            Assert.assertTrue(userId > 0, "User ID should be greater than 0");
            Assert.assertTrue(cartId > 0, "Cart ID should be greater than 0");
            LoggerUtil.info("✓ Payload Data Validation PASSED: All required fields have values");
            
            
            Map<String, String> response = cart.updateCart(cartId, cartPayload);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", cart.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Cart ID: " + cartId);
                ExtentManager.getTest().info("User ID: " + userId);
                ExtentManager.getTest().info("Date: " + date);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Cart Payload: " + cartPayload.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(cart.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (cart.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"userId\""), 
                        "Response should contain 'userId' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'userId' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"products\""), 
                        "Response should contain 'products' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'products' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"userId\":" + userId) || responseBody.contains("\"userId\":\"" + userId + "\""), 
                        "Response should contain the cart userId: " + userId);
                    LoggerUtil.info("✓ User ID Validation PASSED: ID matches requested user (" + userId + ")");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\":" + cartId) || responseBody.contains("\"id\":\"" + cartId + "\""), 
                        "Response should contain the cart ID: " + cartId);
                    LoggerUtil.info("✓ Cart ID Validation PASSED: ID matches requested cart (" + cartId + ")");
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testUpdateCart: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "cartTestData", description = "Delete Cart API Test")
    public void testDeleteCart(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Delete Cart ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String cartIdStr = testData.getOrDefault("CartId", "0");
            
            int cartId = Integer.parseInt(cartIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Cart ID: " + cartId);
            
            
            Map<String, String> response = cart.deleteCart(cartId);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", cart.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Cart ID: " + cartId);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(cart.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (cart.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\":" + cartId) || responseBody.contains("\"id\":\"" + cartId + "\""), 
                        "Response should contain the deleted cart ID: " + cartId);
                    LoggerUtil.info("✓ Cart ID Validation PASSED: ID matches deleted cart (" + cartId + ")");
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testDeleteCart: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "cartTestData", description = "Get Single Cart API Test")
    public void testGetSingleCart(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Get Single Cart ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String cartIdStr = testData.getOrDefault("CartId", "1");
            String expectedStatusCode = testData.getOrDefault("ExpectedStatusCode", "200");
            
            int cartId = Integer.parseInt(cartIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Fetching Cart ID: " + cartId);
            
            
            Map<String, String> response = cart.getCartById(cartId);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", cart.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Cart ID: " + cartId);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(cart.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (cart.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.trim().startsWith("{") && responseBody.trim().endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertFalse(responseBody.trim().equals("{}"), 
                        "Response should not be an empty object");
                    LoggerUtil.info("✓ Response Data Validation PASSED: Response contains data");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    
                    boolean idMatches = responseBody.contains("\"id\":" + cartId) || 
                                       responseBody.contains("\"id\":\"" + cartId + "\"");
                    
                    if (idMatches) {
                        LoggerUtil.info("✓ PASS - Cart ID Validation: Response ID (" + cartId + ") matches requested cart ID");
                        if (ExtentManager.getTest() != null) {
                            ExtentManager.getTest().pass("Cart ID Validation PASSED: ID " + cartId + " matches");
                        }
                    } else {
                        LoggerUtil.error("✗ FAIL - Cart ID Validation: Response ID does NOT match requested cart ID (" + cartId + ")");
                        if (ExtentManager.getTest() != null) {
                            ExtentManager.getTest().fail("Cart ID Validation FAILED: ID mismatch for cart " + cartId);
                        }
                    }
                    
                    Assert.assertTrue(idMatches, 
                        "FAIL: Response ID should match the requested cart ID: " + cartId);
                    
                    
                    Assert.assertTrue(responseBody.contains("\"userId\""), 
                        "Response should contain 'userId' field");
                    LoggerUtil.info("✓ Response Attribute Validation PASSED: Contains 'userId' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"userId\":") && 
                                    !responseBody.contains("\"userId\":null"), 
                        "userId should be a valid number, not null");
                    LoggerUtil.info("✓ userId Validation PASSED: userId is a valid number");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"date\""), 
                        "Response should contain 'date' field");
                    LoggerUtil.info("✓ Response Attribute Validation PASSED: Contains 'date' field");
                    
                    
                    if (responseBody.contains("\"date\":\"") && responseBody.contains("T") && responseBody.contains("Z")) {
                        LoggerUtil.info("✓ Date Format Validation PASSED: Date is in ISO 8601 format");
                    } else {
                        LoggerUtil.warn("⚠ Date Format Warning: Date might not be in expected ISO 8601 format");
                    }
                    
                    
                    Assert.assertTrue(responseBody.contains("\"products\""), 
                        "Response should contain 'products' field");
                    LoggerUtil.info("✓ Response Attribute Validation PASSED: Contains 'products' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"products\":["), 
                        "Products should be an array");
                    LoggerUtil.info("✓ Products Structure Validation PASSED: Products is an array");
                    
                    
                    if (responseBody.contains("\"products\":[]")) {
                        LoggerUtil.warn("⚠ Edge Case Detected: Products array is empty");
                        if (ExtentManager.getTest() != null) {
                            ExtentManager.getTest().info("Edge Case: Cart has empty products array");
                        }
                    } else {
                        LoggerUtil.info("✓ Products Array Validation PASSED: Products array contains items");
                        
                        
                        if (responseBody.contains("\"productId\"") && responseBody.contains("\"quantity\"")) {
                            LoggerUtil.info("✓ Product Structure Validation PASSED: Products have 'productId' and 'quantity' fields");
                        } else {
                            LoggerUtil.warn("⚠ Product Structure Warning: Products might not have expected structure");
                        }
                    }
                    
                    
                    if (responseBody.contains("\"__v\"")) {
                        LoggerUtil.info("✓ Optional Field Detected: Response contains '__v' field (MongoDB version)");
                    }
                    
                    
                    String[] requiredAttributes = {"id", "userId", "date", "products"};
                    int foundAttributes = 0;
                    StringBuilder missingAttributes = new StringBuilder();
                    
                    for (String attribute : requiredAttributes) {
                        if (responseBody.contains("\"" + attribute + "\"")) {
                            foundAttributes++;
                        } else {
                            missingAttributes.append(attribute).append(", ");
                        }
                    }
                    
                    Assert.assertEquals(foundAttributes, requiredAttributes.length, 
                        "All required attributes should be present. Missing: " + missingAttributes.toString());
                    LoggerUtil.info("✓ Complete Attribute Validation PASSED: All " + foundAttributes + " required attributes found");
                    
                    
                    if (ExtentManager.getTest() != null) {
                        ExtentManager.getTest().info("Validation Summary:");
                        ExtentManager.getTest().info("- Cart ID Match: PASS (" + cartId + ")");
                        ExtentManager.getTest().info("- Required Attributes: " + foundAttributes + "/" + requiredAttributes.length);
                        ExtentManager.getTest().info("- Attributes Validated: id, userId, date, products");
                    }
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    if (ExtentManager.getTest() != null) {
                        ExtentManager.getTest().fail("Validation Failed: " + e.getMessage());
                    }
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testGetSingleCart: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(description = "Get All Carts API Test")
    public void testGetAllCarts() {
        try {
            LoggerUtil.info("========== Test: Get All Carts ==========");
            
            
            Map<String, String> response = cart.getAllCarts();
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.debug("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", cart.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test: Get All Carts");
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(cart.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (cart.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.trim().startsWith("[") && responseBody.trim().endsWith("]"), 
                        "Response should be a valid JSON array");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON array format");
                    
                    
                    Assert.assertFalse(responseBody.trim().equals("[]"), 
                        "Response array should not be empty");
                    LoggerUtil.info("✓ Response Data Validation PASSED: Array is not empty");
                    
                    
                    
                    String[] requiredAttributes = {"id", "userId", "date", "products"};
                    
                    
                    int cartCount = 0;
                    int startIndex = 0;
                    int braceDepth = 0;
                    boolean inString = false;
                    char prevChar = '\0';
                    
                    
                    int searchStart = 1;
                    
                    
                    for (int i = searchStart; i < responseBody.length(); i++) {
                        char currentChar = responseBody.charAt(i);
                        
                        
                        if (currentChar == '"' && prevChar != '\\') {
                            inString = !inString;
                        }
                        
                        
                        if (!inString) {
                            if (currentChar == '{') {
                                if (braceDepth == 0) {
                                    startIndex = i;
                                }
                                braceDepth++;
                            } else if (currentChar == '}') {
                                braceDepth--;
                                
                                
                                if (braceDepth == 0) {
                                    cartCount++;
                                    String cartObject = responseBody.substring(startIndex, i + 1);
                                    
                                    
                                    for (String attribute : requiredAttributes) {
                                        String attributePattern = "\"" + attribute + "\":";
                                        Assert.assertTrue(cartObject.contains(attributePattern),
                                            "Cart #" + cartCount + " should contain '" + attribute + "' field");
                                    }
                                    
                                    LoggerUtil.debug("✓ Cart #" + cartCount + " Validation PASSED: All required attributes present");
                                }
                            }
                        }
                        
                        prevChar = currentChar;
                    }
                    
                    LoggerUtil.info("✓ All Carts Validation PASSED: Validated " + cartCount + " carts");
                    LoggerUtil.info("✓ Each cart contains required attributes: id, userId, date, products");
                    
                    
                    if (ExtentManager.getTest() != null) {
                        ExtentManager.getTest().info("Total Carts Validated: " + cartCount);
                        ExtentManager.getTest().info("All carts contain required attributes: " + String.join(", ", requiredAttributes));
                    }
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: Get All Carts ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testGetAllCarts: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
}
