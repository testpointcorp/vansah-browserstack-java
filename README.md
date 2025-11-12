<div align="center">
   <a href="https://vansah.com"><img src="https://vansah.com/app/logo/vansahjira-logo.svg" alt="Vansah Logo"/></a><br>
</div>

<p align="left">
This repository provides a Java-based integration between <strong>Vansah</strong> and <strong>BrowserStack</strong>, enabling seamless Test Result synchronization and automated execution reporting.
</p>

<p align="left">
It allows teams to connect BrowserStack Test Runs directly with Vansah Test Management, ensuring real-time visibility of automated test outcomes within Jira.
</p>

<p align="left">
With this integration, you can:
<ul>
  <li>Automatically update Vansah Test Results from BrowserStack executions</li>
  <li>Link automated tests with Vansah Test Cases and Test Runs</li>
  <li>Streamline CI/CD workflows for improved traceability and reporting</li>
</ul>
</p>

<hr>

<p align="center">
    <a href="https://vansah.com/"><b>Website</b></a> •
    <a href="https://vansah.com/connect-integrations/"><b>More Connect Integrations</b></a>
</p>

<hr>

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Install / Clone](#install--clone)
- [Configure Credentials (Environment Variables)](#configure-credentials-environment-variables)
- [Execute Tests on BrowserStack](#execute-tests-on-browserstack)
- [Viewing Results & Traceability](#viewing-results--traceability)
- [Project Layout](#project-layout)
- [Implementation in Your Project](#implementation-in-your-project)
- [Reference](#reference)
- [Contributing](#contributing)
- [Developed By](#developed-by)

---

## Features

- ✅ Ready-to-run **JUnit 5** and **Selenium** tests on BrowserStack  
- ✅ Integrates with [Vansah Java Binding](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java) to create Test Runs and Log Steps (with screenshots)

---

## Prerequisites

- Java 11+ and Maven 3.8+  
- Access to a [BrowserStack](https://www.browserstack.com/users/sign_in?utm_source=vansah) account with Username and Access key  
- Ensure [Vansah](https://marketplace.atlassian.com/apps/1224250/vansah-test-management-for-jira?tab=overview&hosting=cloud) is installed in your Jira workspace  
- Generate a [Vansah Connect Token](https://help.vansah.com/en/articles/9824979-generate-a-vansah-api-token-from-jira) to authenticate with Vansah APIs  
- Ensure that you have [Test Case Key](https://help.vansah.com/en/articles/12814796-what-is-a-test-case#h_529238a0fc) Linked to a [Jira Issue](https://help.vansah.com/en/articles/9822263-linking-test-cases-to-a-jira-issue), [Test Folder](https://help.vansah.com/en/articles/9822265-linking-test-cases-to-a-test-folder), and optional to a [Test Plan](https://help.vansah.com/en/articles/9855761-understanding-advanced-test-plans)

---

## Install / Clone

```bash
git clone https://github.com/testpointcorp/vansah-browserstack-java.git
cd vansah-browserstack-java
```

---

## Configure Credentials (Environment Variables)

Create a `.env` file in your project root (do **not** commit this file to version control).  
Below is an example configuration for BrowserStack and Vansah:

```bash
# -------------------------------
# BrowserStack Configuration
# -------------------------------
export BROWSERSTACK_USERNAME=<your-username>
export BROWSERSTACK_ACCESS_KEY=<your-access-key>
export BROWSERSTACK_BUILD_NAME="Vansah Build"
export BROWSERSTACK_PROJECT_NAME="Vansah BrowserStack"
export BROWSERSTACK_SESSION_NAME="Vansah Trial Runs"

# -------------------------------
# Vansah Configuration
# -------------------------------
export VANSAH_BASE_URL= # https://help.vansah.com/en/articles/10407923-vansah-api-connect-url
export VANSAH_API_TOKEN= # https://help.vansah.com/en/articles/9824979-generate-a-vansah-api-token-from-jira
export VANSAH_ENVIRONMENT= # https://help.vansah.com/en/articles/9821822-test-environments
export VANSAH_TESTCASE_KEY= # https://help.vansah.com/en/articles/12814796-what-is-a-test-case#h_529238a0fc

# -------------------------------
# Choose ONE style for Vansah execution
# -------------------------------

# (A) Jira Issue style
export VANSAH_JIRA_ISSUE_KEY= # https://help.vansah.com/en/articles/9822263-linking-test-cases-to-a-jira-issue
export VANSAH_PROJECT_KEY= # https://help.vansah.com/en/articles/9821808-updating-your-jira-project-key

# (B) Folder style
export VANSAH_FOLDER_PATH= # https://help.vansah.com/en/articles/9824708-test-folder-path-in-test-case-export

# (C) Standard Test Plan (STP)
# export VANSAH_STP_KEY= # https://help.vansah.com/en/articles/9824605-creating-a-test-plan-from-a-test-folder

# (D) Advanced Test Plan (ATP)
# export VANSAH_ATP_KEY= # https://help.vansah.com/en/articles/9855761-understanding-advanced-test-plans
# export VANSAH_ATP_ASSET_TYPE="folder"  # or 'issue'
```
Alternatively, create a `.env` file using the sample file located at:
```
src/test/resources/.env.example
```

---

## Execute Tests on BrowserStack

Run the Maven project to execute your test suite:

```bash
mvn test
```

- The test opens [selenium.vansah.io](https://selenium.vansah.io/) on a real Chrome instance in BrowserStack.  
- It validates the page title, captures Screenshots, Logs each step to Vansah, and updates the BrowserStack session status as *passed/failed*.  
- Screenshots are stored under `target/screenshots/` and automatically uploaded to Vansah via the `addTestLog(...)` method.

---

## Viewing Results & Traceability

<ul>
  <li><strong>BrowserStack:</strong> View live and saved sessions, videos, and execution logs for each build.</li>
  <li><strong>Vansah:</strong> Open your project, Test Plan, or Test Run to access:</li>
  <ul>
    <li>Test case execution status</li>
    <li>Linked BrowserStack session URL</li>
    <li>Error messages and stack traces</li>
    <li>Execution metadata (build, environment, browser/OS)</li>
  </ul>
</ul>

---

## Project Layout

```text
vansah-browserstack-java/
├─ src/
│  ├─ main/
│  │  └─ java/
│  │     └─ com/
│  │        └─ vansah/
│  │           └─ VansahNode.java
│  └─ test/
│     ├─ java/
│     │  └─ com/
│     │     └─ example/
│     │        └─ browserstack/
│     │           ├─ TestSetup.java
│     │           └─ VansahBrowserStackTest.java
│     └─ resources/
│        └─ .env.example
├─ target/                      
│  ├─ surefire-reports/         
│  └─ screenshots/              
├─ .gitignore                   
├─ pom.xml                      
├─ README.md                    
```

**Notes:**
- Copy `src/test/resources/.env.example` to your project root as `.env`.  
- The `target/` directory stores build artifacts, reports, and screenshots.  

---

## Implementation in Your Project

This demo project leverages [`VansahNode.java`](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java) to send results to Jira.

Before integrating it into your own project, ensure this demo runs successfully.

1. Download [`VansahNode.java`](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java) from the repository.  
2. Copy it into your project’s Java source directory.  
3. Confirm the following dependencies exist in your `pom.xml`:

```xml
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-lang3</artifactId>
  <version>3.12.0</version>
</dependency>
<dependency>
  <groupId>com.mashape.unirest</groupId>
  <artifactId>unirest-java</artifactId>
  <version>1.4.9</version>
</dependency>
<dependency>
  <groupId>io.github.cdimascio</groupId>
  <artifactId>dotenv-kotlin</artifactId>
  <version>6.5.1</version>
</dependency>
```

---

## Reference

[Refer to our help page](https://help.vansah.com/en/articles/12805701-integrating-browserstack-with-vansah)  
[Get Vansah API Connect URL](https://help.vansah.com/en/articles/10407923-vansah-api-connect-url)  
[Generate a Vansah Api Token](https://help.vansah.com/en/articles/9824979-generate-a-vansah-api-token-from-jira)  
[Generate and configure Vansah Test Environment](https://help.vansah.com/en/articles/9821822-test-environments)  
[What is the Test Case in Vansah](https://help.vansah.com/en/articles/12814796-what-is-a-test-case#h_529238a0fc)  
[Linking a Test Case to a Jira Issue](https://help.vansah.com/en/articles/9822263-linking-test-cases-to-a-jira-issue)  
[Get Vansah Project Key](https://help.vansah.com/en/articles/9821808-updating-your-jira-project-key)  
[Link Test Cases to Test Folder](https://help.vansah.com/en/articles/9822265-linking-test-cases-to-a-test-folder)  
[Get Vansah Folder Path](https://help.vansah.com/en/articles/9824708-test-folder-path-in-test-case-export)  

## Contributing

We welcome contributions!  
Feel free to open issues or submit pull requests to improve this integration.

---

## Developed By

[Vansah](https://vansah.com/)

