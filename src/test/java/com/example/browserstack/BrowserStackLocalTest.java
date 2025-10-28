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

import com.browserstack.local.Local;
import com.vansah.VansahNode;

import io.github.cdimascio.dotenv.Dotenv;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BrowserStackLocalTest {

    private WebDriver driver;
    private VansahNode vansah;
    private String testCaseKey;
    private String sessionName;
    private Local bsLocal; // BrowserStack Local instance

    @BeforeEach
    void setUp() throws Exception {
        // Load environment variables
        Dotenv dotenv = Dotenv.configure()
                .directory(new File("").getAbsolutePath())
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String username = dotenv.get("BROWSERSTACK_USERNAME");
        String accessKey = dotenv.get("BROWSERSTACK_ACCESS_KEY");

        if (username == null || accessKey == null) {
            Assertions.fail("Missing BROWSERSTACK_USERNAME or BROWSERSTACK_ACCESS_KEY env vars.");
        }

        sessionName = System.getProperty("BROWSERSTACK_SESSION_NAME", "Vansah + BrowserStack Local Test");
        String buildName = System.getProperty("BROWSERSTACK_BUILD_NAME", "vansah-browserstack-local");
        String projectName = System.getProperty("BROWSERSTACK_PROJECT_NAME", "Vansah BrowserStack Local");

        // Start BrowserStack Local
        bsLocal = new Local();
        Map<String, String> options = new HashMap<>();
        options.put("key", accessKey);
        options.put("localIdentifier", "vansah-test");
        bsLocal.start(options);

        // Wait until local is running
        while (!bsLocal.isRunning()) {
            Thread.sleep(500);
        }

        // BrowserStack capabilities
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "11");
        bstackOptions.put("sessionName", sessionName);
        bstackOptions.put("projectName", projectName);
        bstackOptions.put("buildName", buildName);
        bstackOptions.put("local", "true");
        bstackOptions.put("localIdentifier", "vansah-test");

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");
        caps.setCapability("bstack:options", bstackOptions);

        String hub = "https://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";
        driver = new RemoteWebDriver(new URI(hub).toURL(), caps);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Vansah setup
        vansah = new VansahNode();
        String vansahUrl = dotenv.get("VANSAH_URL");
        String vansahToken = dotenv.get("VANSAH_TOKEN");
        String jiraIssueKey = dotenv.get("VANSAH_JIRA_ISSUE_KEY");
        String environment = dotenv.get("VANSAH_ENVIRONMENT");
        testCaseKey = dotenv.get("VANSAH_TESTCASE_KEY");
        String projectKey = dotenv.get("VANSAH_PROJECT_KEY");

        if (vansahUrl != null) vansah.setVansahURL(vansahUrl);
        if (vansahToken != null) vansah.setVansahToken(vansahToken);
        if (environment != null) vansah.setENVIRONMENT_NAME(environment);
        if (projectKey != null) VansahNode.setProjectKey(projectKey);
        if (jiraIssueKey != null) vansah.setJIRA_ISSUE_KEY(jiraIssueKey);

        try {
            if (testCaseKey != null && jiraIssueKey != null) {
                vansah.addTestRunFromJIRAIssue(testCaseKey);
            } else {
                System.out.println("[INFO] Vansah is not fully configured. Test will run on BrowserStack Local, but results will not be pushed to Vansah until env vars are set.");
            }
        } catch (Exception e) {
            Assertions.fail("Failed to create Vansah test run: " + e.getMessage(), e);
        }
    }

    @Test
    @Order(1)
    void testLocalWebsiteAndAssertTitle() throws IOException, MalformedURLException, URISyntaxException {
        try {
            String localUrl = "http://localhost:5173/auth";
            driver.get(localUrl);
            takeStepScreenshotAndLogToVansah("passed", "Loaded local website: " + localUrl, 1);

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
            ((JavascriptExecutor) driver).executeScript(
                "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"" 
                + status + "\", \"reason\": \"" + reason + "\"}}");
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
    void tearDown() throws Exception {
        if (driver != null) driver.quit();
        if (bsLocal != null && bsLocal.isRunning()) {
            try { bsLocal.stop(); } catch(Exception ignored) {}
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
