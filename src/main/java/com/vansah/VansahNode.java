package com.vansah;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Vansah API Binding Java Implementation
 * 
 * This class provides integration with Vansah test management platform
 * for reporting test results, logs, and screenshots.
 */
public class VansahNode {
    
    private static final Logger logger = Logger.getLogger(VansahNode.class.getName());
    
    // Configuration properties
    private String vansahToken;
    private String vansahUrl;
    private String projectKey;
    private String folderPath;
    private String advancedTestPlanKey;
    private String standardTestPlanKey;
    private String jiraIssueKey;
    private String environmentName;
    
    // Runtime state
    private String currentTestRunId;
    private String currentTestLogId;
    private boolean isConfigured = false;
    
    // API endpoints
    private static final String API_TEST_RUNS = "/api/v1/test-runs";
    private static final String API_TEST_LOGS = "/api/v1/test-logs";
    
    public VansahNode() {
        // Initialize with default values
        this.vansahUrl = "https://app.vansah.com";
    }
    
    // Configuration methods
    public void setVansahToken(String token) {
        this.vansahToken = token;
        validateConfiguration();
    }
    
    public void setVansahURL(String url) {
        this.vansahUrl = url;
        validateConfiguration();
    }
    
    public void setProjectKey(String key) {
        this.projectKey = key;
        validateConfiguration();
    }
    
    public void setFOLDERPATH(String path) {
        this.folderPath = path;
    }
    
    public void setTESTFOLDER_PATH(String path) {
        // This method is kept for compatibility but testFolderPath is not used
        // in the current implementation
    }
    
    public void setAdvancedTestPlanKey(String key) {
        this.advancedTestPlanKey = key;
    }
    
    public void setStandardTestPlanKey(String key) {
        this.standardTestPlanKey = key;
    }
    
    public void setJIRA_ISSUE_KEY(String key) {
        this.jiraIssueKey = key;
    }
    
    public void setENVIRONMENT_NAME(String env) {
        this.environmentName = env;
    }
    
    // Test run management methods
    public void addTestRunFromJIRAIssue(String testCaseKey) {
        if (!isConfigured) {
            logger.warning("Vansah not properly configured. Skipping test run creation.");
            return;
        }
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("testCaseKey", testCaseKey);
            payload.put("jiraIssueKey", jiraIssueKey);
            payload.put("projectKey", projectKey);
            if (environmentName != null) {
                payload.put("environment", environmentName);
            }
            
            HttpResponse<JsonNode> response = Unirest.post(vansahUrl + API_TEST_RUNS)
                    .header("Authorization", "Bearer " + vansahToken)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asJson();
            
            if (response.getStatus() == 201) {
                currentTestRunId = response.getBody().getObject().getString("id");
                logger.info(() -> "Test run created successfully: " + currentTestRunId);
            } else {
                logger.severe(() -> "Failed to create test run: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error creating test run from JIRA issue: " + e.getMessage());
        }
    }
    
    public void addTestRunFromTestFolder(String testCaseKey) {
        if (!isConfigured) {
            logger.warning("Vansah not properly configured. Skipping test run creation.");
            return;
        }
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("testCaseKey", testCaseKey);
            payload.put("folderPath", folderPath);
            payload.put("projectKey", projectKey);
            if (environmentName != null) {
                payload.put("environment", environmentName);
            }
            
            HttpResponse<JsonNode> response = Unirest.post(vansahUrl + API_TEST_RUNS)
                    .header("Authorization", "Bearer " + vansahToken)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asJson();
            
            if (response.getStatus() == 201) {
                currentTestRunId = response.getBody().getObject().getString("id");
                logger.info(() -> "Test run created successfully: " + currentTestRunId);
            } else {
                logger.severe(() -> "Failed to create test run: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error creating test run from test folder: " + e.getMessage());
        }
    }
    
    public void addTestRunFromAdvancedTestPlan(String assetType, String testCaseKey) {
        if (!isConfigured) {
            logger.warning("Vansah not properly configured. Skipping test run creation.");
            return;
        }
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("testCaseKey", testCaseKey);
            payload.put("testPlanKey", advancedTestPlanKey);
            payload.put("assetType", assetType);
            payload.put("projectKey", projectKey);
            if (environmentName != null) {
                payload.put("environment", environmentName);
            }
            
            HttpResponse<JsonNode> response = Unirest.post(vansahUrl + API_TEST_RUNS)
                    .header("Authorization", "Bearer " + vansahToken)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asJson();
            
            if (response.getStatus() == 201) {
                currentTestRunId = response.getBody().getObject().getString("id");
                logger.info(() -> "Test run created successfully: " + currentTestRunId);
            } else {
                logger.severe(() -> "Failed to create test run: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error creating test run from advanced test plan: " + e.getMessage());
        }
    }
    
    public void addTestRunFromStandardTestPlan(String testCaseKey) {
        if (!isConfigured) {
            logger.warning("Vansah not properly configured. Skipping test run creation.");
            return;
        }
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("testCaseKey", testCaseKey);
            payload.put("testPlanKey", standardTestPlanKey);
            payload.put("projectKey", projectKey);
            if (environmentName != null) {
                payload.put("environment", environmentName);
            }
            
            HttpResponse<JsonNode> response = Unirest.post(vansahUrl + API_TEST_RUNS)
                    .header("Authorization", "Bearer " + vansahToken)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asJson();
            
            if (response.getStatus() == 201) {
                currentTestRunId = response.getBody().getObject().getString("id");
                logger.info(() -> "Test run created successfully: " + currentTestRunId);
            } else {
                logger.severe(() -> "Failed to create test run: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error creating test run from standard test plan: " + e.getMessage());
        }
    }
    
    // Test log management methods
    public void addTestLog(String result, String comment, Integer step) {
        addTestLog(result, comment, step, null);
    }
    
    public void addTestLog(String result, String comment, Integer step, File screenshotFile) {
        if (!isConfigured || currentTestRunId == null) {
            logger.warning("Vansah not properly configured or no active test run. Skipping test log creation.");
            return;
        }
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("testRunId", currentTestRunId);
            payload.put("result", result);
            payload.put("comment", comment);
            payload.put("step", step);
            
            if (screenshotFile != null && screenshotFile.exists()) {
                String screenshotData = encodeFileToBase64(screenshotFile);
                payload.put("screenshot", screenshotData);
                payload.put("screenshotName", screenshotFile.getName());
            }
            
            HttpResponse<JsonNode> response = Unirest.post(vansahUrl + API_TEST_LOGS)
                    .header("Authorization", "Bearer " + vansahToken)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asJson();
            
            if (response.getStatus() == 201) {
                currentTestLogId = response.getBody().getObject().getString("id");
                logger.info(() -> "Test log added successfully: " + currentTestLogId);
            } else {
                logger.severe(() -> "Failed to add test log: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error adding test log: " + e.getMessage());
        }
    }
    
    public void updateTestLog(String result, String comment) {
        updateTestLog(result, comment, null);
    }
    
    public void updateTestLog(String result, String comment, File screenshotFile) {
        if (!isConfigured || currentTestLogId == null) {
            logger.warning("Vansah not properly configured or no active test log. Skipping test log update.");
            return;
        }
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("result", result);
            payload.put("comment", comment);
            
            if (screenshotFile != null && screenshotFile.exists()) {
                String screenshotData = encodeFileToBase64(screenshotFile);
                payload.put("screenshot", screenshotData);
                payload.put("screenshotName", screenshotFile.getName());
            }
            
            HttpResponse<JsonNode> response = Unirest.put(vansahUrl + API_TEST_LOGS + "/" + currentTestLogId)
                    .header("Authorization", "Bearer " + vansahToken)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asJson();
            
            if (response.getStatus() == 200) {
                logger.info(() -> "Test log updated successfully: " + currentTestLogId);
            } else {
                logger.severe(() -> "Failed to update test log: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error updating test log: " + e.getMessage());
        }
    }
    
    public void removeTestRun() {
        if (currentTestRunId == null) {
            logger.warning("No active test run to remove.");
            return;
        }
        
        try {
            HttpResponse<JsonNode> response = Unirest.delete(vansahUrl + API_TEST_RUNS + "/" + currentTestRunId)
                    .header("Authorization", "Bearer " + vansahToken)
                    .asJson();
            
            if (response.getStatus() == 200) {
                logger.info(() -> "Test run removed successfully: " + currentTestRunId);
                currentTestRunId = null;
                currentTestLogId = null;
            } else {
                logger.severe(() -> "Failed to remove test run: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error removing test run: " + e.getMessage());
        }
    }
    
    public void removeTestLog() {
        if (currentTestLogId == null) {
            logger.warning("No active test log to remove.");
            return;
        }
        
        try {
            HttpResponse<JsonNode> response = Unirest.delete(vansahUrl + API_TEST_LOGS + "/" + currentTestLogId)
                    .header("Authorization", "Bearer " + vansahToken)
                    .asJson();
            
            if (response.getStatus() == 200) {
                logger.info(() -> "Test log removed successfully: " + currentTestLogId);
                currentTestLogId = null;
            } else {
                logger.severe(() -> "Failed to remove test log: " + response.getStatus() + " - " + response.getBody());
            }
        } catch (UnirestException e) {
            logger.severe(() -> "Error removing test log: " + e.getMessage());
        }
    }
    
    // Helper methods
    private void validateConfiguration() {
        isConfigured = StringUtils.isNotBlank(vansahToken) && 
                      StringUtils.isNotBlank(vansahUrl) && 
                      StringUtils.isNotBlank(projectKey);
    }
    
    private String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            logger.severe(() -> "Error encoding file to base64: " + e.getMessage());
            return null;
        }
    }
}