package com.iskender.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BrowserUtils {

    private static final Logger logger = LogManager.getLogger(BrowserUtils.class);

    public static void waitForPageToLoad(long timeOutInSeconds) {
        logger.info("Waiting for page to load completely");
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        logger.info("Page loaded successfully");
    }

    public static WebElement waitForVisibility(WebElement element, int timeToWaitInSec) {
        logger.info("Waiting for element to be visible");
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(timeToWaitInSec));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickablility(WebElement element, int timeout) {
        logger.info("Waiting for element to be clickable");
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void clickWithJS(WebElement element) {
        logger.info("Clicking element with JavaScript");
        ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].click();", element);
    }

    public static void scrollToElement(WebElement element) {
        logger.info("Scrolling to element");
        ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public static String getScreenshot(String name) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = name + "_" + timestamp;
        
        logger.info("Taking screenshot: " + fileName);
        TakesScreenshot takesScreenshot = (TakesScreenshot) Driver.getDriver();
        File source = takesScreenshot.getScreenshotAs(OutputType.FILE);
        String target = System.getProperty("user.dir") + "/test-output/screenshots/" + fileName + ".png";
        File finalDestination = new File(target);
        
        // Create directory if it doesn't exist
        finalDestination.getParentFile().mkdirs();
        
        FileUtils.copyFile(source, finalDestination);
        logger.info("Screenshot saved to: " + target);
        return target;
    }

    public static String getScreenshotAsBase64(String name) {
        try {
            logger.info("Taking screenshot as base64: " + name);
            TakesScreenshot takesScreenshot = (TakesScreenshot) Driver.getDriver();
            String base64Screenshot = takesScreenshot.getScreenshotAs(OutputType.BASE64);
            logger.info("Base64 screenshot captured successfully");
            return base64Screenshot; // ExtentReports handles data:image/png;base64 prefix itself
        } catch (Exception e) {
            logger.error("Failed to take base64 screenshot", e);
            return null;
        }
    }

    public static void waitFor(int seconds) {
        logger.info("Waiting for " + seconds + " seconds");
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            logger.error("Wait interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static String getCurrentUrl() {
        String url = Driver.getDriver().getCurrentUrl();
        logger.info("Current URL: " + url);
        return url;
    }

    public static String getPageTitle() {
        String title = Driver.getDriver().getTitle();
        logger.info("Page title: " + title);
        return title;
    }

    public static void navigateTo(String url) {
        logger.info("Navigating to: " + url);
        Driver.getDriver().get(url);
    }

    public static boolean isElementDisplayed(WebElement element) {
        try {
            boolean displayed = element.isDisplayed();
            logger.info("Element displayed: " + displayed);
            return displayed;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.warn("Element not found or stale: " + e.getMessage());
            return false;
        }
    }

    public static void switchToNewTab() {
        logger.info("Switching to new tab");
        var windowHandles = Driver.getDriver().getWindowHandles();
        logger.info("Total tabs: " + windowHandles.size());
        
        if (windowHandles.size() > 1) {
            String newTab = windowHandles.toArray()[windowHandles.size() - 1].toString();
            Driver.getDriver().switchTo().window(newTab);
            logger.info("Switched to new tab successfully");
        } else {
            logger.warn("No new tab found to switch to");
        }
    }

    public static void switchToMainTab() {
        logger.info("Switching back to main tab");
        var windowHandles = Driver.getDriver().getWindowHandles();
        if (windowHandles.size() > 0) {
            String mainTab = windowHandles.toArray()[0].toString();
            Driver.getDriver().switchTo().window(mainTab);
            logger.info("Switched to main tab successfully");
        }
    }

    public static void dismissCookiePopupIfPresent() {
        try {
            List<WebElement> cookieButtons = Driver.getDriver().findElements(
                    By.xpath("//a[contains(text(),'Accept')]")
            );

            if (!cookieButtons.isEmpty()) {
                cookieButtons.get(0).click();
                waitFor(1);
            }
        } catch (Exception e) {
            // Ignore if no cookie popup
        }
    }
}