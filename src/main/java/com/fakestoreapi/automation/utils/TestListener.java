package com.fakestoreapi.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    
    private static ExtentReports extentReports;
    
    @Override
    public void onStart(ITestContext context) {
        
        String loggerFileName = context.getCurrentXmlTest().getParameter("loggerFileName");
        if (loggerFileName != null && !loggerFileName.isEmpty()) {
            MDC.put("logFileName", loggerFileName);
            LoggerUtil.info("Log file set to: " + loggerFileName);
        } else {
            MDC.put("logFileName", "application.log");
        }
        
        LoggerUtil.info("Test Suite started: " + context.getName());
        extentReports = ExtentManager.createInstance();
    }
    
    @Override
    public void onFinish(ITestContext context) {
        LoggerUtil.info("Test Suite finished: " + context.getName());
        ExtentManager.flushReports();
        
        
        MDC.remove("logFileName");
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        LoggerUtil.info("Test started: " + result.getMethod().getMethodName());
        ExtentTest test = extentReports.createTest(result.getMethod().getMethodName());
        test.assignCategory(result.getTestClass().getRealClass().getSimpleName());
        
        if (result.getMethod().getDescription() != null) {
            test.info(result.getMethod().getDescription());
        }
        
        ExtentManager.setTest(test);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        LoggerUtil.info("Test passed: " + result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.PASS, 
                MarkupHelper.createLabel("Test Passed: " + result.getMethod().getMethodName(), ExtentColor.GREEN));
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        LoggerUtil.error("Test failed: " + result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, 
                MarkupHelper.createLabel("Test Failed: " + result.getMethod().getMethodName(), ExtentColor.RED));
            test.fail(result.getThrowable());
        } else {
            LoggerUtil.error("ExtentTest instance is null. Test may have failed during DataProvider phase.");
            LoggerUtil.error("Failure details: " + result.getThrowable());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        LoggerUtil.warn("Test skipped: " + result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, 
                MarkupHelper.createLabel("Test Skipped: " + result.getMethod().getMethodName(), ExtentColor.YELLOW));
            test.skip(result.getThrowable());
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        LoggerUtil.warn("Test failed but within success percentage: " + result.getMethod().getMethodName());
    }
}
