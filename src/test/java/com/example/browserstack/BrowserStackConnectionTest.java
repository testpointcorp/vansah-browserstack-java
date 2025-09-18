package com.example.browserstack;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class BrowserStackConnectionTest {

    @Test
    void testBrowserStackConnection() throws MalformedURLException, URISyntaxException {
        String username = System.getProperty("BROWSERSTACK_USERNAME", System.getenv("BROWSERSTACK_USERNAME"));
        String accessKey = System.getProperty("BROWSERSTACK_ACCESS_KEY", System.getenv("BROWSERSTACK_ACCESS_KEY"));
        
        System.out.println("Testing BrowserStack connection...");
        System.out.println("Username: " + username);
        System.out.println("Access Key: " + (accessKey != null ? accessKey.substring(0, 8) + "..." : "NULL"));
        
        if (username == null || accessKey == null) {
            throw new RuntimeException("Missing BROWSERSTACK_USERNAME or BROWSERSTACK_ACCESS_KEY");
        }

        // Simple capabilities
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "11");
        bstackOptions.put("sessionName", "Connection Test");
        bstackOptions.put("projectName", "Connection Test");
        bstackOptions.put("buildName", "Connection Test");

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");
        caps.setCapability("bstack:options", bstackOptions);

        String hub = "https://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";
        System.out.println("Hub URL: " + hub.replace(accessKey, "***"));
        
        RemoteWebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URI(hub).toURL(), caps);
            System.out.println("✅ Connection successful! Session ID: " + driver.getSessionId());
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            throw e;
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}