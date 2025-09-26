package com.example.browserstack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vansah.VansahNode;

public class LocalTest {

    @Test
    void testLocalBrowserAndVansah() throws IOException {
        System.out.println("🧪 Testing local browser and Vansah integration...");
        
        // Setup Chrome driver locally
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in background
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            
            // Test basic functionality
            driver.get("https://www.example.com/");
            String title = driver.getTitle();
            System.out.println("✅ Page loaded successfully. Title: " + title);
            
            // Test screenshot
            if (driver instanceof org.openqa.selenium.TakesScreenshot) {
                File screenshot = ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
                Path screenshotPath = Path.of("target", "screenshots", "local_test.png");
                Files.createDirectories(screenshotPath.getParent());
                Files.copy(screenshot.toPath(), screenshotPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Screenshot saved: " + screenshotPath);
            }
            
            // Test Vansah integration (without actual API calls)
            VansahNode vansah = new VansahNode();
            System.out.println("✅ VansahNode created successfully");
            
            // Simulate test log (won't actually send to Vansah without proper config)
            System.out.println("ℹ️  Vansah integration ready (needs proper credentials)");
            
        } finally {
            if (driver != null) {
                driver.quit();
                System.out.println("✅ Browser closed successfully");
            }
        }
        
        System.out.println("🎉 Local test completed successfully!");
    }
}