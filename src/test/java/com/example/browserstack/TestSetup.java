package com.example.browserstack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vansah.VansahNode;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Base test setup class for BrowserStack and Vansah integration.
 * 
 * This class handles:
 * 1. Loading environment variables using Dotenv.
 * 2. Configuring BrowserStack WebDriver session and capabilities.
 * 3. Initializing VansahNode with required credentials and metadata.
 * 
 * All test classes extending this class inherit these configurations.
 */
public class TestSetup {

    /**
     * The WebDriver instance used for running browser-based tests on BrowserStack.
     */
    protected WebDriver driver;

    /**
     * The VansahNode instance used for logging test execution results to Vansah.
     */
    protected VansahNode vansah;

    /**
     * The unique key identifying the test case in Vansah.
     */
    protected String testCaseKey;

    /**
     * The name of the BrowserStack session used for tracking the current test execution.
     */
    protected String sessionName;

    /**
     * Sets up the WebDriver and Vansah configurations before each test run.
     * 
     * This method:
     * 1. Loads configuration variables from a .env file using Dotenv.
     * 2. Establishes a remote WebDriver session on BrowserStack with desired capabilities.
     * 3. Initializes Vansah integration with credentials and metadata from environment variables.
     * 
     * If required environment variables (like BrowserStack username or access key) are missing,
     * the test will fail immediately.
     *
     * @throws MalformedURLException if the BrowserStack hub URL is invalid
     * @throws URISyntaxException if URI syntax for the hub URL is malformed
     */
    @BeforeEach
    @SuppressWarnings({ "static-access" }) // Used by JUnit 5
    void setUp() throws MalformedURLException, URISyntaxException {
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

        this.sessionName = dotenv.get("BROWSERSTACK_SESSION_NAME", "Vansah + BrowserStack JUnit5 example");
        String buildName = dotenv.get("BROWSERSTACK_BUILD_NAME", "vansah-browserstack-build");
        String projectName = dotenv.get("BROWSERSTACK_PROJECT_NAME", "Vansah BrowserStack");

        // Configure BrowserStack capabilities
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

        // Initialize Vansah integration
        vansah = new VansahNode();
        String vansahUrl = dotenv.get("VANSAH_URL");
        String vansahToken = dotenv.get("VANSAH_TOKEN");
        String jiraIssueKey = dotenv.get("VANSAH_JIRA_ISSUE_KEY");
        String environment = dotenv.get("VANSAH_ENVIRONMENT");
        testCaseKey = dotenv.get("VANSAH_TESTCASE_KEY");
        String projectKey = dotenv.get("VANSAH_PROJECT_KEY");
        String folderPath = dotenv.get("VANSAH_FOLDER_PATH");
        String stpKey = System.getProperty("VANSAH_STP_KEY", System.getenv("VANSAH_STP_KEY"));
        String atpKey = System.getProperty("VANSAH_ATP_KEY", System.getenv("VANSAH_ATP_KEY"));
        String atpAsset = System.getProperty("VANSAH_ATP_ASSET_TYPE", System.getenv("VANSAH_ATP_ASSET_TYPE")); // folder|issue

        if (vansahUrl != null)
            vansah.setVansahURL(vansahUrl);
        if (vansahToken != null)
            vansah.setVansahToken(vansahToken);
        if (environment != null)
            vansah.setENVIRONMENT_NAME(environment);
        if (projectKey != null)
            VansahNode.setProjectKey(projectKey);
        if (jiraIssueKey != null)
            vansah.setJIRA_ISSUE_KEY(jiraIssueKey);
        if(folderPath != null)
            vansah.setFOLDERPATH(folderPath);
        if(stpKey != null)
            vansah.setStandardTestPlanKey(stpKey);
        if(atpKey != null && atpAsset != null)
            vansah.setAdvancedTestPlanKey(atpKey); //Define atpAsset type   

        // vansah.addTestRunFromAdvancedTestPlan(atpAsset, testCaseKey);
        // vansah.addTestRunFromStandardTestPlan(testCaseKey);            
    }
}
