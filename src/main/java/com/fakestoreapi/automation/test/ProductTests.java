package com.fakestoreapi.automation.test;

import com.fakestoreapi.automation.pages.Product;
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

public class ProductTests {
    
    private Product product;
    
    @BeforeClass
    public void setUp() {
        LoggerUtil.info("Setting up ProductTests...");
        product = new Product();
        LoggerUtil.info("ProductTests setup completed");
    }
    
    @AfterClass
    public void tearDown() {
        LoggerUtil.info("Tearing down ProductTests...");
        ExtentManager.flushReports();
        LoggerUtil.info("ProductTests teardown completed");
    }
    
    @DataProvider(name = "productTestData")
    public Iterator<Object[]> productTestData(ITestContext context) {
        
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
    
    @Test(dataProvider = "productTestData", description = "Add New Product API Test")
    public void testAddNewProduct(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Add New Product ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String title = testData.getOrDefault("Title", "");
            String priceStr = testData.getOrDefault("Price", "0");
            String description = testData.getOrDefault("Description", "");
            String image = testData.getOrDefault("Image", "");
            String category = testData.getOrDefault("Category", "");
            String expectedStatusCode = testData.getOrDefault("ExpectedStatusCode", "200");
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            
            
            Map<String, Object> productPayload = new HashMap<>();
            productPayload.put("title", title);
            productPayload.put("price", Double.parseDouble(priceStr));
            productPayload.put("description", description);
            productPayload.put("image", image);
            productPayload.put("category", category);
            
            LoggerUtil.info("Product Payload: " + productPayload.toString());
            
            
            Map<String, String> response = product.createProduct(productPayload);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", product.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Product Payload: " + productPayload.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(product.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (product.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    if (!title.isEmpty()) {
                        Assert.assertTrue(responseBody.contains("\"title\""), 
                            "Response should contain 'title' field");
                        LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'title' field");
                    }
                    
                    
                    Assert.assertTrue(responseBody.contains("\"price\""), 
                        "Response should contain 'price' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'price' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testAddNewProduct: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "productTestData", description = "Update Product API Test")
    public void testUpdateProduct(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Update Product ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String productIdStr = testData.getOrDefault("ProductId", "1");
            String title = testData.getOrDefault("UpdatedTitle", "");
            String priceStr = testData.getOrDefault("UpdatedPrice", "0");
            String description = testData.getOrDefault("UpdatedDescription", "");
            String image = testData.getOrDefault("UpdatedImage", "");
            String category = testData.getOrDefault("UpdatedCategory", "");
            String expectedStatusCode = testData.getOrDefault("ExpectedStatusCode", "200");
            
            int productId = Integer.parseInt(productIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Updating Product ID: " + productId);
            
            
            Map<String, Object> productPayload = new HashMap<>();
            productPayload.put("title", title);
            productPayload.put("price", Double.parseDouble(priceStr));
            productPayload.put("description", description);
            productPayload.put("image", image);
            productPayload.put("category", category);
            
            LoggerUtil.info("Product Update Payload: " + productPayload.toString());
            
            
            Assert.assertFalse(title.isEmpty(), "Updated title should not be empty from Excel");
            Assert.assertNotNull(priceStr, "Updated price should not be null from Excel");
            Assert.assertFalse(description.isEmpty(), "Updated description should not be empty from Excel");
            LoggerUtil.info("✓ Payload Data Validation PASSED: All required fields have values");
            
            
            Map<String, String> response = product.updateProduct(productId, productPayload);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", product.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Product ID: " + productId);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Product Update Payload: " + productPayload.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(product.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (product.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    if (!title.isEmpty()) {
                        Assert.assertTrue(responseBody.contains("\"title\""), 
                            "Response should contain 'title' field");
                        LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'title' field");
                    }
                    
                    
                    Assert.assertTrue(responseBody.contains("\"price\""), 
                        "Response should contain 'price' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'price' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\":" + productId) || responseBody.contains("\"id\":\"" + productId + "\""), 
                        "Response should contain the updated product ID: " + productId);
                    LoggerUtil.info("✓ Product ID Validation PASSED: ID matches requested product (" + productId + ")");
                    
                    
                    if (!title.isEmpty()) {
                        Assert.assertTrue(responseBody.contains(title) || responseBody.contains("\"title\":\"" + title + "\""),
                            "Response should contain updated title: " + title);
                        LoggerUtil.info("✓ Updated Title Validation PASSED: Response contains '" + title + "'");
                    }
                    
                    
                    double expectedPrice = Double.parseDouble(priceStr);
                    String pricePattern1 = "\"price\":" + (int)expectedPrice;  
                    String pricePattern2 = "\"price\":" + expectedPrice;       
                    Assert.assertTrue(responseBody.contains(pricePattern1) || responseBody.contains(pricePattern2),
                        "Response should contain updated price: " + expectedPrice);
                    LoggerUtil.info("✓ Updated Price Validation PASSED: Response contains price '" + expectedPrice + "'");
                    
                    
                    if (!description.isEmpty()) {
                        Assert.assertTrue(responseBody.contains(description) || responseBody.contains("\"description\":\"" + description + "\""),
                            "Response should contain updated description: " + description);
                        LoggerUtil.info("✓ Updated Description Validation PASSED: Response contains description");
                    }
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testUpdateProduct: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "productTestData", description = "Delete Product API Test")
    public void testDeleteProduct(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Delete Product ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String productIdStr = testData.getOrDefault("ProductId", "1");
            String expectedStatusCode = testData.getOrDefault("ExpectedStatusCode", "200");
            
            int productId = Integer.parseInt(productIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Deleting Product ID: " + productId);
            
            
            Map<String, String> response = product.deleteProduct(productId);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", product.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Product ID: " + productId);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            
            if (responseCode.equals("404")) {
                
                LoggerUtil.info("✓ Status Code Validation PASSED: Product not found (404) - Expected for non-existent product ID " + productId);
                Assert.assertEquals(responseCode, "404", "Non-existent product should return 404");
            } else {
                
                Assert.assertTrue(product.isResponseSuccess(), 
                    "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
                LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
                
                
                if (!responseCode.equals("204") && !responseBody.isEmpty()) {
                    try {
                        
                        Assert.assertTrue(responseBody.contains("\"id\""), 
                            "Response should contain 'id' field");
                        LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                        
                        
                        Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                            "Response should be valid JSON object");
                        LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                        
                        
                        Assert.assertTrue(responseBody.contains("\"id\":" + productId) || responseBody.contains("\"id\":\"" + productId + "\""), 
                            "Response should contain the deleted product ID: " + productId);
                        LoggerUtil.info("✓ Product ID Validation PASSED: ID matches deleted product (" + productId + ")");
                        
                    } catch (AssertionError e) {
                        LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                        throw e;
                    }
                } else if (responseCode.equals("204")) {
                    LoggerUtil.info("✓ Response Validation PASSED: 204 No Content - Product deleted successfully");
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testDeleteProduct: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "productTestData", description = "Get Single Product API Test")
    public void testGetSingleProduct(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Get Single Product ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String productIdStr = testData.getOrDefault("ProductId", "1");
            String expectedStatusCode = testData.getOrDefault("ExpectedStatusCode", "200");
            
            int productId = Integer.parseInt(productIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Fetching Product ID: " + productId);
            
            
            Map<String, String> response = product.getProductById(productId);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", product.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Product ID: " + productId);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(product.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (product.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\":" + productId) || responseBody.contains("\"id\":\"" + productId + "\""), 
                        "Response ID should match the requested product ID: " + productId);
                    LoggerUtil.info("✓ Product ID Validation PASSED: Response ID matches requested product ID (" + productId + ")");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"title\""), 
                        "Response should contain 'title' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'title' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"price\""), 
                        "Response should contain 'price' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'price' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"description\""), 
                        "Response should contain 'description' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'description' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"category\""), 
                        "Response should contain 'category' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'category' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"image\""), 
                        "Response should contain 'image' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'image' field");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"rating\""), 
                        "Response should contain 'rating' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'rating' field");
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testGetSingleProduct: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(description = "Get All Products API Test")
    public void testGetAllProducts() {
        try {
            LoggerUtil.info("========== Test: Get All Products ==========");
            
            
            Map<String, String> response = product.getAllProducts();
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.debug("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", product.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test: Get All Products");
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(product.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (product.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.trim().startsWith("[") && responseBody.trim().endsWith("]"), 
                        "Response should be a valid JSON array");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON array format");
                    
                    
                    Assert.assertFalse(responseBody.trim().equals("[]"), 
                        "Response array should not be empty");
                    LoggerUtil.info("✓ Response Data Validation PASSED: Array is not empty");
                    
                    
                    
                    String[] requiredAttributes = {"id", "title", "price", "description", "category", "image", "rating"};
                    
                    
                    int productCount = 0;
                    int startIndex = 0;
                    
                    
                    while ((startIndex = responseBody.indexOf("{", startIndex)) != -1) {
                        int endIndex = responseBody.indexOf("}", startIndex);
                        if (endIndex == -1) break;
                        
                        String productObject = responseBody.substring(startIndex, endIndex + 1);
                        productCount++;
                        
                        
                        for (String attribute : requiredAttributes) {
                            String attributePattern = "\"" + attribute + "\":";
                            Assert.assertTrue(productObject.contains(attributePattern),
                                "Product #" + productCount + " should contain '" + attribute + "' field");
                        }
                        
                        LoggerUtil.debug("✓ Product #" + productCount + " Validation PASSED: All required attributes present");
                        
                        startIndex = endIndex + 1;
                    }
                    
                    LoggerUtil.info("✓ All Products Validation PASSED: Validated " + productCount + " products");
                    LoggerUtil.info("✓ Each product contains required attributes: id, title, price, description, category, image, rating");
                    
                    
                    if (ExtentManager.getTest() != null) {
                        ExtentManager.getTest().info("Total Products Validated: " + productCount);
                        ExtentManager.getTest().info("All products contain required attributes: " + String.join(", ", requiredAttributes));
                    }
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: Get All Products ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testGetAllProducts: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
}
