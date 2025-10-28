<div align="center">
   <a href="https://vansah.com"><img src="https://vansah.com/app/logo/vansahjira-logo.svg" /></a><br>
</div>

<p align="center">
A minimal public template project showing how to run Selenium tests on BrowserStack and send the results to Vansah Test Management for Jira using the official Vansah Java binding.</p>
<p align="center">
    <a href="https://vansah.com/"><b>Website</b></a> •
    <a href="https://vansah.com/connect-integrations/"><b>More Connect Integrations</b></a>
</p>


## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Get the Code](#get-the-code)
- [Add Vansah Java Binding](#add-the-vansah-java-binding-one-time)
- [Configure Secrets](#configure-secrets-env-vars)
- [Run Locally](#run-locally)
- [Run in GitHub Actions](#run-in-github-actions-optional-but-recommended)
- [Project Layout](#project-layout)
- [Customization](#customise)
- [Troubleshooting](#troubleshooting)
- [Credits & Docs](#credits--docs)

---


## Features

- ✅ Ready-to-run **JUnit 5 Selenium test** on BrowserStack  
- ✅ Integrates with **Vansah Java binding (`VansahNode`)** to create test runs and log steps (with screenshots)  
- ✅ Optional **GitHub Actions workflow** for CI execution on BrowserStack  

---

## Prerequisites

- Java 11+ and Maven 3.8+
- A BrowserStack account (free trial is fine) and your **Username** and **Access Key**
- Vansah installed in your Jira Cloud and a **Vansah Connect API token** (from Vansah Settings → *Vansah API Tokens*)
- Decide **how** you want to execute in Vansah (any one is fine):
  - by *Jira issue* + *Test Case key*
  - by *Folder path* + *Test Case key*
  - by *Standard Test Plan (STP)* + *Test Case key*
  - by *Advanced Test Plan (ATP)* + *Test Case key*

## Get the code

```bash
git clone <your-new-repo> vansah-browserstack-junit5
cd vansah-browserstack-junit5
```

## Add the Vansah Java binding (one-time)

This project ships with a **stub** `VansahNode.java` so it compiles out‑of‑the‑box. Replace it with the real file to actually push results to Vansah:

1. Open: https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java
2. Copy the raw file into your project at: `src/main/java/com/vansah/VansahNode.java` (overwriting the stub).
3. Ensure the Vansah binding prerequisites are present in `pom.xml` (already added here):
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

> Why not Maven Central? At the time of writing, the Vansah binding is distributed as a single source file. Copying it in keeps things simple.

## Configure secrets (env vars)

Create a `.env` (or export environment variables) based on `src/test/resources/.env.example` for `Mac/linux` users:

```bash
# BrowserStack
export BROWSERSTACK_USERNAME=...
export BROWSERSTACK_ACCESS_KEY=...
export BROWSERSTACK_BUILD_NAME="Vansah Build"
export BROWSERSTACK_PROJECT_NAME="Vansah BrowserStack"
export BROWSERSTACK_SESSION_NAME="Local run"

# Vansah
export VANSAH_URL="https://prod.vansahnode.app"
export VANSAH_TOKEN="..."               # Your Vansah Connect token
export VANSAH_ENVIRONMENT="QA"

# Choose ONE style for Vansah execution + provide your Test Case key
export VANSAH_TESTCASE_KEY="KAN-C17"

# (A) Jira Issue style
export VANSAH_JIRA_ISSUE_KEY="KAN-123"

# or (B) Folder style
# export VANSAH_FOLDER_PATH="vansah test automation/regression 2025/"

# or (C) Standard Test Plan (STP)
# export VANSAH_STP_KEY="KAN-P18"

# or (D) Advanced Test Plan (ATP)
# export VANSAH_ATP_KEY="KAN-P17"
# export VANSAH_ATP_ASSET_TYPE="folder" # or 'issue'
```
## Create a `.env`  based on `src/test/resources/.env.example` for `windows` user
```bash
# BrowserStack
BROWSERSTACK_USERNAME=...
BROWSERSTACK_ACCESS_KEY=...
BROWSERSTACK_BUILD_NAME="Vansah Build"
BROWSERSTACK_PROJECT_NAME="Vansah BrowserStack"
BROWSERSTACK_SESSION_NAME="Local run"

# Vansah
VANSAH_URL="https://prod.vansahnode.app"
VANSAH_TOKEN=...
VANSAH_ENVIRONMENT="QA"

# Choose ONE style for Vansah execution + provide your Test Case key
VANSAH_TESTCASE_KEY="DT-C117"

# (A) Jira Issue style
VANSAH_JIRA_ISSUE_KEY="DT-29"
VANSAH_PROJECT_KEY="DT"


```
## If you are testing local application please note to open `BrowserStackLocalTest.java` and paste url here
```bash
  String localUrl = "http://localhost:5173/auth";

```

## Run locally

```bash
mvn -q test
```

- The test opens **example.com** on a real Chrome instance in BrowserStack.
- It takes a screenshot, adds step logs to Vansah, and marks the BrowserStack session *passed/failed*.
- Screenshots are saved under `target/screenshots/` and uploaded to Vansah via `addTestLog(...)`.

## See it in action

- **BrowserStack Dashboard** → verify your session, build name and status.
- **Vansah in Jira** → open the Test Case or plan you targeted and check the new run & logs.

## Run in GitHub Actions (optional, but recommended)

1. Push this project to a public GitHub repo.
2. In your repo → **Settings → Secrets and variables → Actions**, add:
   - `BROWSERSTACK_USERNAME`, `BROWSERSTACK_ACCESS_KEY`
   - `VANSAH_URL`, `VANSAH_TOKEN`
   - plus: `VANSAH_JIRA_ISSUE_KEY` and `VANSAH_TESTCASE_KEY` (or your chosen style vars)
3. Trigger the workflow: **Actions → BrowserStack JUnit5 (Vansah) → Run workflow**.

The workflow file is at `.github/workflows/browserstack.yml` and uses Maven to run `mvn test` on Ubuntu.

## Project layout

```text
vansah-browserstack-junit5/
├─ .github/workflows/browserstack.yml   # CI that runs the test on BrowserStack
├─ src/
│  ├─ main/java/com/vansah/VansahNode.java     # Replace stub with real one from Vansah repo
│  └─ test/java/com/example/browserstack/VansahBrowserStackTest.java
├─ src/test/resources/.env.example
├─ pom.xml
└─ README.md
```

## Customise

- Edit `VansahBrowserStackTest.java` to point at your own app under test and to add more `addTestLog(...)` steps.
- Swap `browserName`, `os` and `osVersion` in the capabilities to test different platforms/browsers.
- You can mark sessions on BrowserStack using:
  ```java
  ((JavascriptExecutor) driver).executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"passed\", \"reason\": \"...\"}}");
  ```

## Troubleshooting

- **401 / Auth errors**: check `BROWSERSTACK_*` or `VANSAH_TOKEN`.
- **Results not in Vansah**: did you replace the stub `VansahNode.java` with the real one? Are your keys (issue/folder/STP/ATP + testcase) valid?
- **Proxy / custom region**: use `vansah.setVansahURL(...)` to point to your region URL (see your Vansah API Tokens page).

---

## Credits & Docs

- Vansah Java binding repo and README explain all supported methods.
- BrowserStack JUnit 5 docs show framework setup and SDK options.