package com.example.browserstack;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * This test class demonstrates a Selenium BrowserStack integration
 * that captures test execution results and logs them to Vansah.
 *
 * It extends TestSetup, which initializes WebDriver and Vansah integration
 * setup.
 *
 * The test includes:
 * 1. Launching a target URL
 * 2. Asserting the page title
 * 3. Capturing screenshots
 * 4. Logging results to Vansah
 * 5. Updating session status in BrowserStack
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VansahBrowserStackTest extends TestSetup {

    /**
     * Visits the target website and validates that the page title is not empty.
     * 
     * If successful, it logs the step result in Vansah for a Jira Work item and
     * sets the BrowserStack session status
     * to "passed". Otherwise, it marks the session as "failed" and logs the error
     * in Vansah.
     *
     * @throws IOException           if an error occurs during screenshot capture or
     *                               file operations
     * @throws MalformedURLException if the provided URL is malformed
     * @throws URISyntaxException    if the URL syntax is invalid
     */
    @Test
    @Order(1)
    void demoJiraWorkItemAssertTitle() throws Exception {
        try {
            vansah.addTestRunFromJIRAIssue(testCaseKey);
            driver.get("https://selenium.vansah.io/");
            takeStepScreenshotAndLogToVansah("passed", "Loaded home page", 1);

            String title = driver.getTitle();
            Assertions.assertTrue(title != null && !title.isEmpty(), "Title should not be empty");

            setBrowserStackStatus("passed", "Title check passed");
            safeAddTestLog("passed", "Title is present: " + title, 2, null);
        } catch (AssertionError | RuntimeException e) {
            String safeMessage = e.getMessage()
                    .replace("<", "")
                    .replace(">", "");
            setBrowserStackStatus("failed", safeMessage);
            safeUpdateTestLog("failed", "Failure: " + safeMessage);
            // Assertions.fail(e); throw error
        }
    }

    /**
     * Visits the target website and validates that the page title is not empty.
     * 
     * If successful, it logs the step result in Vansah for a Test Folder in Vansah
     * and sets the BrowserStack session status
     * to "passed". Otherwise, it marks the session as "failed" and logs the error
     * in Vansah.
     * 
     * @throws Exception
     */
    @Test
    @Order(2)
    void demoTestFolderAssertTitle() throws Exception {
        try {
            vansah.addTestRunFromTestFolder(testCaseKey);
            driver.get("https://selenium.vansah.io/");
            takeStepScreenshotAndLogToVansah("passed", "Loaded home page", 1);

            String title = driver.getTitle();
            Assertions.assertTrue("Selenium Website Testing Page – Use this page to automate ",title,"Title should match exactly");

            setBrowserStackStatus("passed", "Title check passed");
            safeAddTestLog("passed", "Title is present: " + title, 2, null);
        } catch (AssertionError | RuntimeException e) {
            setBrowserStackStatus("failed", e.getMessage());
            safeUpdateTestLog("failed", "Failure: " + e.getMessage());
            //Assertions.fail(e); throw error
        }
    }

    /**
     * Updates the BrowserStack session status based on test outcome.
     * 
     * <p>
     * This uses BrowserStack’s JavaScript executor API to set session metadata
     * for visibility in the BrowserStack dashboard.
     *
     * @param status the test result status, typically "passed" or "failed"
     * @param reason the descriptive reason or message for the status
     */
    private void setBrowserStackStatus(String status, String reason) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"" + status
                            + "\", \"reason\": \"" + reason + "\"}}");
        } catch (Exception ignored) {
        }
    }

    /**
     * Captures a screenshot of the current browser step, saves it locally,
     * and logs it to Vansah with a descriptive comment.
     *
     * @param result  the result of the step ("passed" or "failed")
     * @param comment a short description of the test step
     * @param step    the numerical order of the step in the test sequence
     * @throws IOException if an error occurs while creating directories or copying
     *                     files
     */
    private void takeStepScreenshotAndLogToVansah(String result, String comment, int step) throws IOException{
        if (!(driver instanceof TakesScreenshot))
            return;

        File tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path out = Path.of("target", "screenshots");
        Files.createDirectories(out);

        // Generate a unique filename using timestamp and random suffix
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);

        String safeSessionName = sessionName.replaceAll("[^A-Za-z0-9._-]", "_");
        String fileName = String.format("%s_step%d_%s_%s.png", safeSessionName, step, timestamp, uniqueId);

        Path file = out.resolve(fileName);
        Files.copy(tmp.toPath(), file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        safeAddTestLog(result, comment, step, file.toFile());
    }

    /**
     * Ensures proper cleanup of browser resources after each test execution.
     * 
     * <p>
     * Quits the WebDriver instance to prevent resource leaks between tests.
     */
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Safely adds a test log entry to Vansah, including optional screenshot
     * attachments.
     * 
     * <p>
     * If Vansah is not initialized or an exception occurs, a warning is printed
     * instead of throwing an error.
     *
     * @param result     the outcome of the step ("passed", "failed", etc.)
     * @param comment    a descriptive note about the step
     * @param step       the step number in the sequence
     * @param screenshot optional screenshot file, can be {@code null}
     */
    private void safeAddTestLog(String result, String comment, Integer step, File screenshot) {
        if (vansah == null)
            return;
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

    /**
     * Safely updates the last test log entry in Vansah with a new status and
     * comment.
     * 
     * <p>
     * If Vansah is not initialized or an exception occurs, it logs a warning to the
     * console.
     *
     * @param result  the new test status ("passed", "failed", etc.)
     * @param comment the comment to associate with the update
     */
    private void safeUpdateTestLog(String result, String comment) {
        if (vansah == null)
            return;
        try {
            vansah.updateTestLog(result, comment);
        } catch (Exception e) {
            System.out.println("[WARN] Unable to update Vansah test log: " + e.getMessage());
        }
    }
}
