package com.fakestoreapi.automation.test;

import java.sql.DriverManager;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.fakestoreapi.automation.utils.ExtentManager;
import com.fakestoreapi.automation.utils.LoggerUtil;

public class BaseTest {
     @BeforeSuite
    public void setupSuite(){

        ExtentManager.getInstance();

    }
}
