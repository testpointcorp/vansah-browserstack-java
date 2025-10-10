package com.example.browserstack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vansah.VansahNode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VansahOnlyTest {

    private WebDriver driver;
    private VansahNode vansah;
    private String testCaseKey;
    private String sessionName;

    @BeforeEach
    @SuppressWarnings("unused") // Used by JUnit 5
    void setUp() {
        // Setup local Chrome driver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in background
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        sessionName = "Vansah Local Test";

        // Vansah setup
        vansah = new VansahNode();
        String vansahUrl = System.getProperty("VANSAH_URL", System.getenv("VANSAH_URL"));
        String vansahToken = System.getProperty("VANSAH_TOKEN", System.getenv("VANSAH_TOKEN"));
        String jiraIssueKey = System.getProperty("VANSAH_JIRA_ISSUE_KEY", System.getenv("VANSAH_JIRA_ISSUE_KEY"));
        String environment = System.getProperty("VANSAH_ENVIRONMENT", System.getenv("VANSAH_ENVIRONMENT"));
        testCaseKey = System.getProperty("VANSAH_TESTCASE_KEY", System.getenv("VANSAH_TESTCASE_KEY"));
        String projectKey = System.getProperty("VANSAH_PROJECT_KEY", System.getenv("VANSAH_PROJECT_KEY"));

        if (vansahUrl != null) vansah.setVansahURL(vansahUrl);
        if (vansahToken != null) vansah.setVansahToken(vansahToken);
        if (environment != null) vansah.setENVIRONMENT_NAME(environment);
        if (projectKey != null) VansahNode.setProjectKey(projectKey);
        if (jiraIssueKey != null) {
            vansah.setJIRA_ISSUE_KEY(jiraIssueKey);
        }

        // Create test run if configured
        try {
            if (testCaseKey != null && jiraIssueKey != null) {
                vansah.addTestRunFromJIRAIssue(testCaseKey);
                System.out.println("✅ Vansah test run created for: " + testCaseKey);
            } else {
                System.out.println("ℹ️  Vansah not fully configured. Test will run locally without Vansah integration.");
            }
        } catch (Exception e) {
            Assertions.fail("Failed to create Vansah test run: " + e.getMessage(), e);
        }
    }

    @Test
    @Order(1)
    void testWebsiteAndLogToVansah() throws IOException {
        try {
            // Test a website
            driver.get("https://www.example.com/");
            takeStepScreenshotAndLogToVansah("passed", "Loaded example.com home page", 1);
            
            String title = driver.getTitle();
            Assertions.assertTrue(title != null && !title.isEmpty(), "Title should not be empty");
            safeAddTestLog("passed", "Title is present: " + title, 2, null);
            
            // Test page content
            String bodyText = driver.findElement(By.tagName("body")).getText();
            Assertions.assertTrue(bodyText.contains("Example Domain"), "Page should contain 'Example Domain'");
            safeAddTestLog("passed", "Page content verified: " + bodyText.substring(0, Math.min(50, bodyText.length())) + "...", 3, null);
            
            System.out.println("✅ Test completed successfully!");
            
        } catch (AssertionError | RuntimeException e) {
            safeUpdateTestLog("failed", "Failure: " + e.getMessage());
            System.err.println("❌ Test failed: " + e.getMessage());
            Assertions.fail(e);
        }
    }

    private void takeStepScreenshotAndLogToVansah(String result, String comment, int step) throws IOException {
        if (!(driver instanceof TakesScreenshot)) return;
        
        File tmp = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        Path out = Path.of("target", "screenshots");
        Files.createDirectories(out);
        Path file = out.resolve(sessionName.replaceAll("[^A-Za-z0-9._-]", "_") + "_step" + step + ".png");
        Files.copy(tmp.toPath(), file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        // Log to Vansah if configured
        safeAddTestLog(result, comment, step, file.toFile());
        
        System.out.println("📸 Screenshot saved: " + file);
    }

    @AfterEach
    @SuppressWarnings("unused") // Used by JUnit 5
    void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser closed");
        }
    }

    private void safeAddTestLog(String result, String comment, Integer step, File screenshot) {
        if (vansah == null) return;
        try {
            if (screenshot != null) {
                vansah.addTestLog(result, comment, step, screenshot);
            } else {
                vansah.addTestLog(result, comment, step);
            }
        } catch (Exception e) {
            System.out.println("[WARN] Unable to add Vansah test log: " + e.getMessage());
        }
    }

    private void safeUpdateTestLog(String result, String comment) {
        if (vansah == null) return;
        try {
            vansah.updateTestLog(result, comment);
        } catch (Exception e) {
            System.out.println("[WARN] Unable to update Vansah test log: " + e.getMessage());
        }
    }
}