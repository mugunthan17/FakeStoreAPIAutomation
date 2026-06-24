package com.fakestoreapi.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    
    private static ExtentReports extentReports;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static String reportPath;
    
    public synchronized static ExtentReports createInstance() {
        if (extentReports == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportDir = ConfigReader.getReportPath();
            
            
            File directory = new File(reportDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            reportPath = reportDir + ConfigReader.getReportName() + "_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("FakeStore API Test Report");
            sparkReporter.config().setReportName("API Automation Test Results");
            sparkReporter.config().setEncoding("utf-8");
            
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Application", "FakeStore API");
            extentReports.setSystemInfo("Environment", "QA");
            extentReports.setSystemInfo("Base URL", ConfigReader.getBaseUrl());
            extentReports.setSystemInfo("Tester", System.getProperty("user.name"));
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            
            LoggerUtil.info("Extent Report initialized at: " + reportPath);
        }
        return extentReports;
    }
    
    public synchronized static ExtentReports getInstance() {
        if (extentReports == null) {
            createInstance();
        }
        return extentReports;
    }
    
    public static void setTest(ExtentTest test) {
        extentTest.set(test);
    }
    
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    public static void removeTest() {
        extentTest.remove();
    }
    
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            LoggerUtil.info("Extent Report flushed successfully");
        }
    }
    
    public static String getReportPath() {
        return reportPath;
    }
}
