package com.iskender.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class Driver {
    
    private static final Logger logger = LogManager.getLogger(Driver.class);
    private static final ThreadLocal<WebDriver> driverPool = new ThreadLocal<>();

    private Driver() {}

    public static WebDriver getDriver() {

        if (driverPool.get() == null) {

            String browserType = System.getProperty("browser");
            if (browserType == null || browserType.trim().isEmpty()) {
                try {
                    // Try new property first, then fallback to old property for compatibility
                    browserType = ConfigurationReader.getProperty("browser.type");
                    if (browserType == null || browserType.trim().isEmpty()) {
                        browserType = ConfigurationReader.getProperty("browser", "chrome");
                    }
                } catch (Exception e) {
                    logger.warn("Failed to read configuration, using default browser: chrome", e);
                    browserType = "chrome";
                }
            }
            
            // Final fallback
            if (browserType == null || browserType.trim().isEmpty()) {
                browserType = "chrome";
            }

            logger.info("Initializing browser: '" + browserType + "'");

            switch (browserType.toLowerCase()) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                    chromeOptions.addArguments("--disable-extensions");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--no-first-run");
                    chromeOptions.addArguments("--no-default-browser-check");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    chromeOptions.addArguments("--user-data-dir=/tmp/test-profile");
                    // Only disable notifications, keep images and popups normal
                    chromeOptions.setExperimentalOption("prefs", java.util.Map.of(
                        "profile.default_content_setting_values.notifications", 2
                    ));
                    
                    if (ConfigurationReader.isBrowserHeadless()) {
                        chromeOptions.addArguments("--headless=new");
                    }
                    driverPool.set(new ChromeDriver(chromeOptions));
                    break;
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addPreference("dom.webdriver.enabled", false);
                    firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                    firefoxOptions.addPreference("dom.push.enabled", false);
                    firefoxOptions.addPreference("dom.disable_open_during_load", false);
                    firefoxOptions.addPreference("privacy.trackingprotection.enabled", true);
                    driverPool.set(new FirefoxDriver(firefoxOptions));
                    break;
                case "edge":
                    driverPool.set(new EdgeDriver());
                    break;
                case "headless-chrome":
                    ChromeOptions headlessOptions = new ChromeOptions();
                    headlessOptions.addArguments("--headless=new");
                    headlessOptions.addArguments("--disable-blink-features=AutomationControlled");
                    headlessOptions.addArguments("--disable-extensions");
                    headlessOptions.addArguments("--disable-notifications");
                    headlessOptions.addArguments("--no-first-run");
                    headlessOptions.addArguments("--no-default-browser-check");
                    
                    headlessOptions.setExperimentalOption("prefs", java.util.Map.of(
                        "profile.default_content_setting_values.notifications", 2
                    ));
                    driverPool.set(new ChromeDriver(headlessOptions));
                    break;
                case "remote-allow-origins":
                    ChromeOptions remoteOptions = new ChromeOptions();
                    remoteOptions.addArguments("--remote-allow-origins=*");
                    driverPool.set(new ChromeDriver(remoteOptions));
                    break;
                default:
                    logger.error("Browser not supported: " + browserType);
                    throw new IllegalArgumentException("Browser not supported: " + browserType);
            }
            
            // Configure driver settings
            if (ConfigurationReader.getBooleanProperty("browser.maximize", true)) {
                driverPool.get().manage().window().maximize();
            }
            int timeout = ConfigurationReader.getTimeout();
            driverPool.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
            logger.info("Browser initialized successfully: " + browserType);
        }

        return driverPool.get();
    }

    public static void closeDriver() {
        if (driverPool.get() != null) {
            logger.info("Closing browser");
            driverPool.get().quit();
            driverPool.remove();
            logger.info("Browser closed successfully");
        }
    }
}