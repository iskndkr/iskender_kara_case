package com.iskender.pages;

import com.iskender.utils.BrowserUtils;
import com.iskender.utils.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

public abstract class BasePage {

    protected static final Logger logger = LogManager.getLogger(BasePage.class);

    public BasePage() {
        PageFactory.initElements(Driver.getDriver(), this);
        logger.info("Initialized page: " + this.getClass().getSimpleName());
    }

    // Common wait methods
    protected WebElement waitForVisibility(WebElement element, int timeout) {
        return BrowserUtils.waitForVisibility(element, timeout);
    }

    protected WebElement waitForClickability(WebElement element, int timeout) {
        return BrowserUtils.waitForClickablility(element, timeout);
    }

    // Common click methods
    protected void click(WebElement element) {
        waitForClickability(element, 3);
        element.click();
        logger.info("Clicked on element: " + getElementInfo(element));
        BrowserUtils.dismissCookiePopupIfPresent();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            boolean displayed = element.isDisplayed();
            logger.info("Element displayed: " + displayed + " - " + getElementInfo(element));
            return displayed;
        } catch (Exception e) {
            logger.warn("Element not found or not displayed: " + e.getMessage());
            return false;
        }
    }

    // Text methods
    protected String getText(WebElement element) {
        String text = waitForVisibility(element, 10).getText();
        logger.info("Got text '" + text + "' from element: " + getElementInfo(element));
        return text;
    }


    // Hover methods
    protected void hover(WebElement element) {
        Actions actions = new Actions(Driver.getDriver());
        waitForVisibility(element, 10);
        actions.moveToElement(element).perform();
        logger.info("Hovered over element: " + getElementInfo(element));
    }

    // Scroll methods
    protected void scrollToElement(WebElement element) {
        BrowserUtils.scrollToElement(element);
        logger.info("Scrolled to element: " + getElementInfo(element));
    }

    // Utility methods
    protected void waitFor(int seconds) {
        BrowserUtils.waitFor(seconds);
    }

    protected void waitForPageLoad() {
        BrowserUtils.waitForPageToLoad(10);
    }

    // Helper method to get element info for logging
    private String getElementInfo(WebElement element) {
        try {
            String tagName = element.getTagName();
            String id = element.getAttribute("id");
            String className = element.getAttribute("class");

            StringBuilder info = new StringBuilder(tagName);
            if (id != null && !id.isEmpty()) {
                info.append("#").append(id);
            }
            if (className != null && !className.isEmpty()) {
                info.append(".").append(className.replace(" ", "."));
            }
            return info.toString();
        } catch (Exception e) {
            return "unknown element";
        }

    }
}