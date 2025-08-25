package com.iskender.tests;

import com.iskender.utils.ConfigurationReader;
import com.iskender.utils.BrowserUtils;
import com.iskender.utils.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iskender.utils.ReportManager;
import com.iskender.pages.BasePage;
import org.testng.Assert;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.IOException;
import java.lang.reflect.Method;

public class TestBase {

    private static final Logger logger = LogManager.getLogger(TestBase.class);

    @BeforeSuite
    public void setUpSuite() {
        logger.info("=== TEST SUITE STARTED ===");
        ReportManager.initializeReport();

        // Create directories if they don't exist
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("test-output/screenshots"));
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("test-output/reports"));
        } catch (IOException e) {
            logger.error("Failed to create directories", e);
        }
    }

    @Parameters({"browser"})
    @BeforeClass
    public void setUpClass(@Optional String browser) {
        String className = this.getClass().getSimpleName();
        logger.info("=== STARTING TEST CLASS: " + className + " ===");

        // Set browser parameter if provided
        if (browser != null && !browser.trim().isEmpty()) {
            System.setProperty("browser", browser);
            logger.info("Browser parameter set to: " + browser);
        }

        ReportManager.logInfo("Starting test class: " + className);
    }

    @BeforeMethod
    public void setUp(Method method) {
        String testName = method.getName();
        logger.info("========================================");
        logger.info("STARTING TEST: " + testName);
        logger.info("========================================");

        // Create test in report
        Test testAnnotation = method.getAnnotation(Test.class);
        String description = testAnnotation != null ? testAnnotation.description() : "No description provided";
        ReportManager.createTest(testName, description);

        logger.info("Test setup completed for: " + testName);
        ReportManager.logInfo("Test setup completed");

    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        try {
            // Take screenshot if test failed and it's a UI test
            if (result.getStatus() == ITestResult.FAILURE && isUITest()) {
                try {
                    // Take screenshot and save as file
                    String screenshotPath = BrowserUtils.getScreenshot(testName + "_failed");
                    ReportManager.addScreenshot(screenshotPath);

                    // Also add base64 as backup for HTML report
                    ReportManager.addScreenshotBase64(testName + "_failed");

                    ReportManager.logFail("Test failed - Screenshot captured: " + result.getThrowable().getMessage());
                } catch (IOException screenshotException) {
                    // Fallback to base64 only if file screenshot fails
                    logger.warn("File screenshot failed, using base64 only", screenshotException);
                    ReportManager.addScreenshotBase64(testName + "_failed");
                    ReportManager.logFail("Test failed - Screenshot captured (base64): " + result.getThrowable().getMessage());
                }
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                ReportManager.logPass("Test completed successfully");
            } else if (result.getStatus() == ITestResult.SKIP) {
                ReportManager.logSkip("Test was skipped");
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            ReportManager.logFail("Failed to capture screenshot: " + e.getMessage());
        }

        String status = result.getStatus() == ITestResult.SUCCESS ? "PASSED" :
                result.getStatus() == ITestResult.FAILURE ? "FAILED" : "SKIPPED";

        logger.info("========================================");
        logger.info("TEST COMPLETED: " + testName + " - " + status);
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Failure reason: " + result.getThrowable().getMessage());
        }
        logger.info("========================================");
        logger.info("Test teardown completed for: " + testName);
    }

    @AfterClass
    public void tearDownClass() {
        String className = this.getClass().getSimpleName();

        // Close driver only for UI tests
        if (isUITest()) {
            Driver.closeDriver();
            logger.info("Driver closed for UI test class: " + className);
        }

        logger.info("=== COMPLETED TEST CLASS: " + className + " ===");
        ReportManager.logInfo("Completed test class: " + className);
    }

    @AfterSuite
    public void tearDownSuite() {
        ReportManager.flushReports();
        logger.info("=== TEST SUITE COMPLETED ===");
    }

    // Helper methods
    private boolean isUITest() {
        // Check if current test class is in UI package
        return this.getClass().getPackage().getName().contains(".ui");
    }

    // Utility methods for test classes
    protected void logStep(String step) {
        logger.info("STEP: " + step);
        ReportManager.logInfo("STEP: " + step);
    }

    protected void logAssertion(String assertion, boolean result) {
        if (result) {
            logger.info("ASSERTION PASSED: " + assertion);
        } else {
            logger.error("ASSERTION FAILED: " + assertion);
        }
        if (result) {
            ReportManager.logPass("ASSERTION: " + assertion);
        } else {
            ReportManager.logFail("ASSERTION FAILED: " + assertion);
        }
    }

    protected void logInfo(String message) {
        logger.info(message);
        ReportManager.logInfo(message);
    }

    protected void logWarning(String message) {
        logger.warn(message);
        ReportManager.logWarning(message);
    }

    // Common assertion methods with logging
    protected void verifyTrue(boolean condition, String message) {
        logAssertion(message, condition);
        Assert.assertTrue(condition, message);
    }

    protected void verifyFalse(boolean condition, String message) {
        logAssertion("NOT " + message, !condition);
        Assert.assertFalse(condition, message);
    }

    protected void verifyEquals(Object actual, Object expected, String message) {
        boolean result = (actual != null && actual.equals(expected)) ||
                (actual == null && expected == null);
        logAssertion(message + " [Expected: " + expected + ", Actual: " + actual + "]", result);
        Assert.assertEquals(actual, expected, message);
    }

    protected void verifyNotNull(Object object, String message) {
        boolean result = object != null;
        logAssertion(message, result);
        Assert.assertNotNull(object, message);
    }

    protected void verifyContains(String actual, String expected, String message) {
        boolean result = actual != null && actual.contains(expected);
        logAssertion(message + " [Expected to contain: " + expected + ", Actual: " + actual + "]", result);
        Assert.assertTrue(result, message);
    }

    // Common browser interaction methods
    protected void navigateToUrl(String url) {
        logStep("Navigating to URL: " + url);
        BrowserUtils.navigateTo(url);
    }

    protected void navigateToHomePage() {
        String baseUrl = ConfigurationReader.getAppUrl();
        logStep("Navigating to home page: " + baseUrl);
        BrowserUtils.navigateTo(baseUrl);
    }

    protected void waitForPageLoad() {
        logStep("Waiting for page to load");
        BrowserUtils.waitForPageToLoad(10);
    }

    protected void waitForSeconds(int seconds) {
        logStep("Waiting for " + seconds + " seconds");
        BrowserUtils.waitFor(seconds);
    }

    protected String getCurrentPageUrl() {
        String url = BrowserUtils.getCurrentUrl();
        logInfo("Current page URL: " + url);
        return url;
    }

    protected void switchToNewTabAndVerifyUrl(String expectedUrl, String description) {
        logStep("Switching to new tab and verifying URL contains: " + expectedUrl);
        BrowserUtils.switchToNewTab();
        waitForSeconds(2);
        String currentUrl = getCurrentPageUrl();
        verifyContains(currentUrl, expectedUrl, description);
    }

    protected String getCurrentPageTitle() {
        String title = BrowserUtils.getPageTitle();
        logInfo("Current page title: " + title);
        return title;
    }

}