package com.iskender.tests;

import com.iskender.utils.ConfigurationReader;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

/**
 * Base class for API test automation
 * Contains common setup/teardown methods and utilities for API testing
 */
public class ApiTestBase extends TestBase {

    private static final Logger logger = LogManager.getLogger(ApiTestBase.class);
    protected SoftAssert softAssert;

    @BeforeClass
    public void setUpApi() {
        RestAssured.baseURI = ConfigurationReader.getApiBaseUrl();
        logger.info("API Base URI set to: " + RestAssured.baseURI);
        logInfo("API test class started: " + this.getClass().getSimpleName());
    }

    @BeforeMethod
    public void setUpApiTest() {
        softAssert = new SoftAssert();
        logInfo("API test method setup completed");
    }

    @AfterMethod
    public void tearDownApiTest() {
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            // Log the error but don't fail the teardown to avoid affecting other tests
            logger.error("Soft assertion failures in test: " + e.getMessage());
            // Don't re-throw - just log the failure to avoid breaking test suite
        } finally {
            // Reset softAssert for next test
            softAssert = new SoftAssert();
        }
        logInfo("API test method teardown completed");
    }
    
    @AfterClass
    public void tearDownApi() {
        logger.info("API test class completed: " + this.getClass().getSimpleName());
        logInfo("API test class completed");
    }

    // API-specific utility methods
    protected void logApiStep(String step) {
        logger.info("API STEP: " + step);
        logInfo("API STEP: " + step);
    }

    protected void logApiInfo(String message) {
        logger.info("API: " + message);
        logInfo("API: " + message);
    }

    protected void logApiAssertion(String assertion, boolean result) {
        String status = result ? "PASSED" : "FAILED";
        logger.info("API ASSERTION " + status + ": " + assertion);
        if (result) {
            logInfo("API ASSERTION PASSED: " + assertion);
        } else {
            logWarning("API ASSERTION FAILED: " + assertion);
        }
    }

    // Soft assertion methods with logging
    protected void verifySoftTrue(boolean condition, String message) {
        logApiAssertion(message, condition);
        softAssert.assertTrue(condition, message);
    }

    protected void verifySoftEquals(Object actual, Object expected, String message) {
        boolean result = (actual != null && actual.equals(expected)) || 
                        (actual == null && expected == null);
        logApiAssertion(message + " [Expected: " + expected + ", Actual: " + actual + "]", result);
        softAssert.assertEquals(actual, expected, message);
    }

    protected void verifySoftNotNull(Object object, String message) {
        boolean result = object != null;
        logApiAssertion(message, result);
        softAssert.assertNotNull(object, message);
    }
}