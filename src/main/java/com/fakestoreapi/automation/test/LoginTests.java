package com.fakestoreapi.automation.test;

import com.fakestoreapi.automation.pages.Login;
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

public class LoginTests {
    
    private Login login;
    
    @BeforeClass
    public void setUp() {
        LoggerUtil.info("Setting up LoginTests...");
        login = new Login();
        LoggerUtil.info("LoginTests setup completed");
    }
    
    @AfterClass
    public void tearDown() {
        LoggerUtil.info("Tearing down LoginTests...");
        LoggerUtil.info("LoginTests teardown completed");
        
    }
    
    @DataProvider(name = "loginTestData")
    public Iterator<Object[]> loginTestData(ITestContext context) {
        
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
    
    
    
    @Test(dataProvider = "loginTestData", description = "Add New User API Test")
    public void testAddNewUser(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Add New User ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String email = testData.getOrDefault("Email", "");
            String username = testData.getOrDefault("Username", "");
            String password = testData.getOrDefault("Password", "");
            String firstName = testData.getOrDefault("FirstName", "");
            String lastName = testData.getOrDefault("LastName", "");
            String city = testData.getOrDefault("City", "");
            String street = testData.getOrDefault("Street", "");
            String number = testData.getOrDefault("Number", "");
            String zipcode = testData.getOrDefault("Zipcode", "");
            String latitude = testData.getOrDefault("Latitude", "");
            String longitude = testData.getOrDefault("Longitude", "");
            String phone = testData.getOrDefault("Phone", "");
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Email: " + email);
            LoggerUtil.info("Username: " + username);
            LoggerUtil.info("Name: " + firstName + " " + lastName);
            
            
            Map<String, Object> name = new HashMap<>();
            name.put("firstname", firstName);
            name.put("lastname", lastName);
            
            
            Map<String, Object> geolocation = new HashMap<>();
            geolocation.put("lat", latitude);
            geolocation.put("long", longitude);
            
            
            Map<String, Object> address = new HashMap<>();
            address.put("city", city);
            address.put("street", street);
            address.put("number", Integer.parseInt(number));
            address.put("zipcode", zipcode);
            address.put("geolocation", geolocation);
            
            
            Map<String, Object> userPayload = new HashMap<>();
            userPayload.put("email", email);
            userPayload.put("username", username);
            userPayload.put("password", password);
            userPayload.put("name", name);
            userPayload.put("address", address);
            userPayload.put("phone", phone);
            
            LoggerUtil.info("User Payload: " + userPayload.toString());
            
            
            Assert.assertFalse(email.isEmpty(), "Email should not be empty");
            Assert.assertFalse(username.isEmpty(), "Username should not be empty");
            Assert.assertFalse(password.isEmpty(), "Password should not be empty");
            LoggerUtil.info("✓ Payload Data Validation PASSED: All required fields have values");
            
            
            Map<String, String> response = login.createUser(userPayload);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", login.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Email: " + email);
                ExtentManager.getTest().info("Username: " + username);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("User Payload: " + userPayload.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(login.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            
            if (login.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    String idPattern = "\"id\":(\\d+)";
                    if (responseBody.matches(".*" + idPattern + ".*")) {
                        LoggerUtil.info("✓ User ID Validation PASSED: Valid user ID found in response");
                    }
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testAddNewUser: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "loginTestData", description = "Delete User API Test")
    public void testDeleteUser(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Delete User ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String userIdStr = testData.getOrDefault("UserId", "0");
            
            int userId = Integer.parseInt(userIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("User ID: " + userId);
            
            
            Map<String, String> response = login.deleteUser(userId);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", login.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("User ID: " + userId);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(login.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (login.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'id' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\":" + userId) || responseBody.contains("\"id\":\"" + userId + "\""), 
                        "Response should contain the deleted user ID: " + userId);
                    LoggerUtil.info("✓ User ID Validation PASSED: ID matches deleted user (" + userId + ")");
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testDeleteUser: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "loginTestData", description = "User Login API Test")
    public void testLogin(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: User Login ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.getOrDefault("TestCase", "");
            String username = testData.getOrDefault("Username", "");
            String password = testData.getOrDefault("Password", "");
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Username: " + username);
            
            
            Assert.assertFalse(username.isEmpty(), "Username should not be empty");
            Assert.assertFalse(password.isEmpty(), "Password should not be empty");
            LoggerUtil.info("✓ Input Data Validation PASSED: Username and password have values");
            
            
            Map<String, String> response = login.login(username, password);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", login.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("Username: " + username);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(login.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (login.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.contains("\"token\""), 
                        "Response should contain 'token' field");
                    LoggerUtil.info("✓ Response Body Validation PASSED: Contains 'token' field");
                    
                    
                    Assert.assertTrue(responseBody.startsWith("{") && responseBody.endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    String token = login.getAuthToken();
                    Assert.assertNotNull(token, "Token should not be null");
                    Assert.assertFalse(token.isEmpty(), "Token should not be empty");
                    LoggerUtil.info("✓ Token Validation PASSED: Valid authentication token received");
                    LoggerUtil.info("Authentication Token: " + token);
                    
                    
                    Assert.assertTrue(login.isLoginSuccessful(), 
                        "Login should be successful with valid credentials");
                    LoggerUtil.info("✓ Login Success Validation PASSED: User authenticated successfully");
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: " + testCase + " ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testLogin: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "loginTestData", description = "Get Single User API Test")
    public void testGetSingleUser(Map<String, String> testData) {
        try {
            LoggerUtil.info("========== Test: Get Single User ==========");
            LoggerUtil.info("Test Data: " + testData.toString());
            
            
            String testCase = testData.get("TestCase");
            String userIdStr = testData.get("UserId");
            String expectedStatusCode = testData.getOrDefault("ExpectedStatusCode", "200");
            
            int userId = Integer.parseInt(userIdStr);
            
            LoggerUtil.info("Executing Test Case: " + testCase);
            LoggerUtil.info("Fetching User ID: " + userId);
            
            
            Map<String, String> response = login.getUserById(userId);
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.info("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", login.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test Case: " + testCase);
                ExtentManager.getTest().info("User ID: " + userId);
                ExtentManager.getTest().info("Test Data: " + testData.toString());
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Response Body: " + responseBody);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(login.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (login.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.trim().startsWith("{") && responseBody.trim().endsWith("}"), 
                        "Response should be valid JSON object");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON format");
                    
                    
                    Assert.assertFalse(responseBody.trim().equals("{}"), 
                        "Response should not be an empty object");
                    LoggerUtil.info("✓ Response Data Validation PASSED: Response contains data");
                    
                    
                    Assert.assertTrue(responseBody.contains("\"id\""), 
                        "Response should contain 'id' field");
                    
                    boolean idMatches = responseBody.contains("\"id\":" + userId) || 
                                       responseBody.contains("\"id\":\"" + userId + "\"");
                    
                    if (idMatches) {
                        LoggerUtil.info("✓ PASS - User ID Validation: Response ID (" + userId + ") matches requested user ID");
                        if (ExtentManager.getTest() != null) {
                            ExtentManager.getTest().pass("User ID Validation PASSED: ID " + userId + " matches");
                        }
                    } else {
                        LoggerUtil.error("✗ FAIL - User ID Validation: Response ID does NOT match requested user ID (" + userId + ")");
                        if (ExtentManager.getTest() != null) {
                            ExtentManager.getTest().fail("User ID Validation FAILED: ID mismatch for user " + userId);
                        }
                    }
                    
                    Assert.assertTrue(idMatches, 
                        "FAIL: Response ID should match the requested user ID: " + userId);
                    
                    
                    String[] requiredAttributes = {"address", "id", "email", "username", "password", "name", "phone"};
                    int foundAttributes = 0;
                    StringBuilder missingAttributes = new StringBuilder();
                    
                    for (String attribute : requiredAttributes) {
                        if (responseBody.contains("\"" + attribute + "\"")) {
                            foundAttributes++;
                            LoggerUtil.info("✓ Response Attribute Validation PASSED: Contains '" + attribute + "' field");
                        } else {
                            missingAttributes.append(attribute).append(", ");
                        }
                    }
                    
                    
                    Assert.assertEquals(foundAttributes, requiredAttributes.length, 
                        "All required attributes should be present. Missing: " + missingAttributes.toString());
                    LoggerUtil.info("✓ Complete Attribute Validation PASSED: All " + foundAttributes + " required attributes found");
                    
                    
                    if (responseBody.contains("\"address\"")) {
                        Assert.assertTrue(responseBody.contains("\"geolocation\""), 
                            "Address should contain 'geolocation' field");
                        Assert.assertTrue(responseBody.contains("\"city\""), 
                            "Address should contain 'city' field");
                        Assert.assertTrue(responseBody.contains("\"street\""), 
                            "Address should contain 'street' field");
                        LoggerUtil.info("✓ Address Object Validation PASSED: Contains nested fields (geolocation, city, street)");
                    }
                    
                    if (responseBody.contains("\"name\"")) {
                        Assert.assertTrue(responseBody.contains("\"firstname\"") || responseBody.contains("\"lastName\""), 
                            "Name should contain 'firstname' or 'lastname' field");
                        LoggerUtil.info("✓ Name Object Validation PASSED: Contains nested name fields");
                    }
                    
                    
                    if (ExtentManager.getTest() != null) {
                        ExtentManager.getTest().info("Validation Summary:");
                        ExtentManager.getTest().info("- User ID Match: PASS (" + userId + ")");
                        ExtentManager.getTest().info("- Required Attributes: " + foundAttributes + "/" + requiredAttributes.length);
                        ExtentManager.getTest().info("- Attributes Validated: " + String.join(", ", requiredAttributes));
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
            LoggerUtil.error("Exception in testGetSingleUser: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test(description = "Get All Users API Test")
    public void testGetAllUsers() {
        try {
            LoggerUtil.info("========== Test: Get All Users ==========");
            
            
            Map<String, String> response = login.getAllUsers();
            
            
            String responseCode = response.get("RESPONSE_CODE");
            String responseBody = response.get("RESPONSE_BODY");
            
            LoggerUtil.info("Response Code: " + responseCode);
            LoggerUtil.debug("Response Body: " + responseBody);
            
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", responseCode);
            result.put("responseBody", responseBody);
            result.put("message", login.getStatusMessage(responseCode));
            result.put("response", response);
            
            
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().info("Test: Get All Users");
                ExtentManager.getTest().info("Response Code: " + responseCode);
                ExtentManager.getTest().info("Result: " + result.toString());
            }
            
            
            Assert.assertTrue(login.isResponseSuccess(), 
                "Response should be successful (200, 201, 202, or 204). Actual: " + responseCode);
            LoggerUtil.info("✓ Status Code Validation PASSED: Response code " + responseCode + " is a success code");
            
            
            if (login.isResponseSuccess() && !responseBody.isEmpty()) {
                try {
                    
                    Assert.assertTrue(responseBody.trim().startsWith("[") && responseBody.trim().endsWith("]"), 
                        "Response should be a valid JSON array");
                    LoggerUtil.info("✓ Response Structure Validation PASSED: Valid JSON array format");
                    
                    
                    Assert.assertFalse(responseBody.trim().equals("[]"), 
                        "Response array should not be empty");
                    LoggerUtil.info("✓ Response Data Validation PASSED: Array is not empty");
                    
                    
                    
                    String[] requiredAttributes = {"id", "email", "username", "password", "name", "phone"};
                    
                    
                    int userCount = 0;
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
                                    userCount++;
                                    String userObject = responseBody.substring(startIndex, i + 1);
                                    
                                    
                                    for (String attribute : requiredAttributes) {
                                        String attributePattern = "\"" + attribute + "\":";
                                        Assert.assertTrue(userObject.contains(attributePattern),
                                            "User #" + userCount + " should contain '" + attribute + "' field");
                                    }
                                    
                                    LoggerUtil.debug("✓ User #" + userCount + " Validation PASSED: All required attributes present");
                                }
                            }
                        }
                        
                        prevChar = currentChar;
                    }
                    
                    LoggerUtil.info("✓ All Users Validation PASSED: Validated " + userCount + " users");
                    LoggerUtil.info("✓ Each user contains required attributes: id, email, username, password, name, phone");
                    
                    
                    if (ExtentManager.getTest() != null) {
                        ExtentManager.getTest().info("Total Users Validated: " + userCount);
                        ExtentManager.getTest().info("All users contain required attributes: " + String.join(", ", requiredAttributes));
                    }
                    
                } catch (AssertionError e) {
                    LoggerUtil.error("✗ Response Body Validation FAILED: " + e.getMessage());
                    throw e;
                }
            }
            
            LoggerUtil.info("========== Test Completed: Get All Users ==========");
            
        } catch (Exception e) {
            LoggerUtil.error("Exception in testGetAllUsers: " + e.getMessage(), e);
            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail("Test failed with exception: " + e.getMessage());
            }
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
}
