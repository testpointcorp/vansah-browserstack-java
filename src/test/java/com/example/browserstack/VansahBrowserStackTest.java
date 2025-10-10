package com.example.browserstack;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vansah.VansahNode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VansahBrowserStackTest {

    private WebDriver driver;
    private VansahNode vansah;
    private String testCaseKey;
    private String sessionName;

    @BeforeEach
    @SuppressWarnings("unused") // Used by JUnit 5
    void setUp() throws MalformedURLException, URISyntaxException {
        String username = System.getProperty("BROWSERSTACK_USERNAME", System.getenv("BROWSERSTACK_USERNAME"));
        String accessKey = System.getProperty("BROWSERSTACK_ACCESS_KEY", System.getenv("BROWSERSTACK_ACCESS_KEY"));
        if (username == null || accessKey == null) {
            Assertions.fail("Missing BROWSERSTACK_USERNAME or BROWSERSTACK_ACCESS_KEY env vars.");
        }

        sessionName = System.getProperty("BROWSERSTACK_SESSION_NAME", "Vansah + BrowserStack JUnit5 example");
        String buildName = System.getProperty("BROWSERSTACK_BUILD_NAME", "vansah-browserstack-build");
        String projectName = System.getProperty("BROWSERSTACK_PROJECT_NAME", "Vansah BrowserStack");

        // W3C Capabilities
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "11");
        bstackOptions.put("sessionName", sessionName);
        bstackOptions.put("projectName", projectName);
        bstackOptions.put("buildName", buildName);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");
        caps.setCapability("bstack:options", bstackOptions);

        String hub = "https://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";
        driver = new RemoteWebDriver(new URI(hub).toURL(), caps);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

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

        // Prefer JIRA issue based run if keys are present; else, fall back to folder/plan based via env vars
        try {
            if (testCaseKey != null && jiraIssueKey != null) {
                vansah.addTestRunFromJIRAIssue(testCaseKey);
            } else {
                String folderPath = System.getProperty("VANSAH_FOLDER_PATH", System.getenv("VANSAH_FOLDER_PATH"));
                String stpKey = System.getProperty("VANSAH_STP_KEY", System.getenv("VANSAH_STP_KEY"));
                String atpKey = System.getProperty("VANSAH_ATP_KEY", System.getenv("VANSAH_ATP_KEY"));
                String atpAsset = System.getProperty("VANSAH_ATP_ASSET_TYPE", System.getenv("VANSAH_ATP_ASSET_TYPE")); // folder|issue

                if (testCaseKey != null && folderPath != null) {
                    vansah.setFOLDERPATH(folderPath);
                    vansah.addTestRunFromTestFolder(testCaseKey);
                } else if (testCaseKey != null && atpKey != null && atpAsset != null) {
                    vansah.setAdvancedTestPlanKey(atpKey);
                    vansah.addTestRunFromAdvancedTestPlan(atpAsset, testCaseKey);
                } else if (testCaseKey != null && stpKey != null) {
                    vansah.setStandardTestPlanKey(stpKey);
                    vansah.addTestRunFromStandardTestPlan(testCaseKey);
                } else {
                    System.out.println("[INFO] Vansah is not fully configured. The test will run on BrowserStack, but results will not be pushed to Vansah until env vars are set.");
                }
            }
        } catch (Exception e) {
            Assertions.fail("Failed to create Vansah test run: " + e.getMessage(), e);
        }
    }

    @Test
    @Order(1)
    void visitExampleDotComAndAssertTitle() throws IOException, MalformedURLException, URISyntaxException {
        try {
            driver.get("https://www.example.com/");
            takeStepScreenshotAndLogToVansah("passed", "Loaded example.com home page", 1);
            String title = driver.getTitle();
            Assertions.assertTrue(title != null && !title.isEmpty(), "Title should not be empty");
            setBrowserStackStatus("passed", "Title check passed");
            safeAddTestLog("passed", "Title is present: " + title, 2, null);
        } catch (AssertionError | RuntimeException e) {
            setBrowserStackStatus("failed", e.getMessage());
            safeUpdateTestLog("failed", "Failure: " + e.getMessage());
            Assertions.fail(e);
        }
    }

    private void setBrowserStackStatus(String status, String reason) {
        try {
            ((JavascriptExecutor) driver).executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"" + status + "\", \"reason\": \"" + reason + "\"}}");
        } catch (Exception ignored) {}
    }

    private void takeStepScreenshotAndLogToVansah(String result, String comment, int step) throws IOException {
        if (!(driver instanceof TakesScreenshot)) return;
        File tmp = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        Path out = Path.of("target", "screenshots");
        Files.createDirectories(out);
        Path file = out.resolve(sessionName.replaceAll("[^A-Za-z0-9._-]", "_") + "_step" + step + ".png");
        Files.copy(tmp.toPath(), file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        safeAddTestLog(result, comment, step, file.toFile());
    }

    @AfterEach
    @SuppressWarnings("unused") // Used by JUnit 5
    void tearDown() {
        if (driver != null) {
            driver.quit();
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