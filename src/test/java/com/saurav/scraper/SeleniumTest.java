package com.saurav.scraper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URL;
import java.util.HashMap;

public class SeleniumTest {
    public WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        // Use environment variables or fallback to hardcoded credentials
        String username = System.getenv("BROWSERSTACK_USERNAME");
        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");

        if (username == null) username = "sauravrai_QyXufU";
        if (accessKey == null) accessKey = "po7AyP546bi1DyivqJQz";

        // Top-level W3C capability
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "chrome");
        caps.setCapability("browserVersion", "latest");

        // All other BrowserStack-specific values go inside bstack:options
        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "10");
        bstackOptions.put("projectName", "BrowserStack Sample");
        bstackOptions.put("buildName", "browserstack-build-1");
        bstackOptions.put("sessionName", "Parallel Test");
        bstackOptions.put("userName", username);
        bstackOptions.put("accessKey", accessKey);

        // Attach bstack:options
        caps.setCapability("bstack:options", bstackOptions);

        // RemoteWebDriver initialization
        driver = new RemoteWebDriver(
                new URL("https://hub-cloud.browserstack.com/wd/hub"),
                caps
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
