<div align="center">
   <a href="https://vansah.com"><img src="https://vansah.com/app/logo/vansahjira-logo.svg" /></a><br>
</div>

<p align="center">
This repository provides a Java-based integration between Vansah and BrowserStack, enabling seamless test result synchronization and automated test execution reporting. It allows users to connect their BrowserStack test runs directly with Vansah Test Management, ensuring real-time visibility of automated test outcomes within Jira or other supported platforms.</p>
<p align="center"><b>With this integration, teams can:</b></p>
<div align="center">

  <ul style="display: inline-block; text-align: left;">
    <li>Automatically update Vansah test results from BrowserStack executions</li>
    <li>Link automated tests with Vansah Test Cases and Runs</li>
    <li>Streamline CI/CD workflows for improved traceability and quality reporting</li>
  </ul>
</div>

---


## Features

- ✅ Ready-to-run `JUnit 5 and Selenium` test on BrowserStack  
- ✅ Integrates with [`Vansah Java binding`](https://github.com/testpointcorp/Vansah-API-Binding-Java) to create test runs and log steps (with screenshots)  
---
<p style="font-size:30px"><b>Managing the Vansah ⇄ BrowserStack Java Integration</b></p>

## 1) Prerequisites

- Java 11+ 
- Maven or Gradle
- [`BrowserStack`](https://www.browserstack.com/users/sign_in?utm_source=vansah) (username and accesskey)
- [`Vansah`](https://marketplace.atlassian.com/apps/1224250/vansah-test-management-for-jira?tab=overview&hosting=cloud) access (Workspace URL and API token)
- Generate **Vansah** [`connect`](https://help.vansah.com/en/articles/9824979-generate-a-vansah-api-token-from-jira) token to authenticate with Vansah APIs

## 2) Install/Clone

   ```bash
   git clone https://github.com/testpointcorp/vansah-browserstack-java.git
   cd vansah-browserstack-java
   ```

## 3) Configure environment variables 
  Set these in your shell, CI secrets, or .env (don’t commit secrets):
  ```bash
   export BROWSERSTACK_USERNAME=<your-username>
   export BROWSERSTACK_ACCESS_KEY=<your-access-key>

   export VANSAH_BASE_URL=<https://your-workspace.vansah.com>
   export VANSAH_API_TOKEN=<your-vansah-api-token>
   export VANSAH_PROJECT_KEY=<Jira/Vansah project key, e.g. QA>
   export VANSAH_TEST_PLAN_KEY=<optional: plan/run key if you use plans>

  ```
  Tip: In CI, store these as encrypted secrets and map them to env vars at runtime.
---

## 4) Project Setup
<ul>
<li>Point your WebDriver or test runner to BrowserStack (e.g., remote Selenium/Appium URL).</li>
<li>Ensure your tests can read the env vars above (e.g., via System.getenv()).</li>
<li>(Optional) Create a config.properties and load it if you prefer file-based config.</li>
</ul>
Example Java access:

   ```bash
   String bsUser = System.getenv("BROWSERSTACK_USERNAME");
   String bsKey  = System.getenv("BROWSERSTACK_ACCESS_KEY");
   String vansahUrl = System.getenv("VANSAH_BASE_URL");
   String vansahToken = System.getenv("VANSAH_API_TOKEN");

```
---

## 5) Map Tests to Vansah
<p style="font-size:20px"><b>Decide how you’ll link automated tests to Vansah Test Cases:</b></p>
<ul>
<li><b>By key in annotation</b> (recommended), e.g. @TestCaseKey("VAN-123")</li>
<li><b>By naming convention</b> (e.g., test method or class name matches case key)</li>
<li><b>By metadata </b>(tags/categories)</li>
</ul>
---


## 6)  Execute Tests on BrowserStack
<p>Maven:</p>

```bash
mvn -Dtest=*Test test

```
<p>Gradle:</p>

```bash
./gradlew test

```
<p>Typical BrowserStack WebDriver URL:</p>

```bash 
https://<BROWSERSTACK_USERNAME>:<BROWSERSTACK_ACCESS_KEY>@hub-cloud.browserstack.com/wd/hub

```


---
## 7) Report Results to Vansah
<p>After each test finishes, send the outcome (PASS/FAIL/SKIP), duration, environment, and useful artifacts (e.g., BrowserStack session URL).</p>
Typical flow isnide your test listener (JUbit/TestNG):
<ol>
<li>Capture test status and error message (if any)</li>
<li>Fetch the BrowserStack session ID to build a run link</li>
<li>Post to Vansah’s results endpoint using VANSAH_API_TOKEN</li>
<li>Include mapping info (e.g., test case key) and context (browser/OS, build)</li>
</ol>

``` text 
If this repo includes a utility class or listener for Vansah reporting, register it in your test framework so it runs automatically on success/failure.

```
---
## 8) CI/CD Integration
<p>Add a job to run on every push/PR or nightly:<p>
<p><b>GitHub Actions (example)</b></p>

```bash 
name: CI - BrowserStack + Vansah
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    env:
      BROWSERSTACK_USERNAME: ${{ secrets.BROWSERSTACK_USERNAME }}
      BROWSERSTACK_ACCESS_KEY: ${{ secrets.BROWSERSTACK_ACCESS_KEY }}
      VANSAH_BASE_URL: ${{ secrets.VANSAH_BASE_URL }}
      VANSAH_API_TOKEN: ${{ secrets.VANSAH_API_TOKEN }}
      VANSAH_PROJECT_KEY: QA
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - run: mvn -q -DskipTests install
      - run: mvn -B test

```

<p><b>Jenkins (snippet)</b></p>

```bash 

environment {
  BROWSERSTACK_USERNAME = credentials('bs-username')
  BROWSERSTACK_ACCESS_KEY = credentials('bs-access-key')
  VANSAH_API_TOKEN = credentials('vansah-token')
  VANSAH_BASE_URL = 'https://your-workspace.vansah.com'
}
stages {
  stage('Build & Test') {
    steps {
      sh 'mvn -q -DskipTests install'
      sh 'mvn -B test'
    }
  }
}


```
---



## 9) Viewing Results & Traceability

- **BrowserStack:** See live/saved sessions, videos, and logs per build.  
- **Vansah:** Open the project/plan/run to see:
  - Test case execution status  
  - Linked BrowserStack session URL  
  - Error messages & stack traces  
  - Execution metadata (build, environment, browser/OS)

---

## 10) Troubleshooting
- **Auth errors:** Check env vars and token scopes. Regenerate tokens if needed.
- **No results in Vansah:** Verify your mapping (case keys), endpoint URL, and that the reporter/listener is registered.
- **BrowserStack session not linked:** Ensure you capture and send the session ID/URL in the Vansah payload.
- **Network/SSL issues**  Allowlist outbound calls to Vansah and BrowserStack; check corporate proxy settings.

---

## 11) Maintenance & Best Practices

- **Pin dependency versions** to avoid unexpected changes.
- **Tag builds** in BrowserStack (build name/version) and pass the same build info to Vansah.
- **Capture artifacts** (logs, screenshots, videos) and send references in Vansah results.
- **Secret hygiene**: Rotate tokens regularly; never commit secrets.
- **Branch strategy**: Use separate BrowserStack builds/Vansah runs for feature branches vs. main.

## 12) Upgrading 
- **Pull latest cahnges:**
```bash 
git pull origin main

```
- **Reinstall if the repo provides a library:**
```bash 
mvn -q -DskipTests install

```
- **Review release notes/changelog for breaking changes.**

---

## 13) Quick FAQ
<p><b>Q: Can I run locally and still report to Vansah?</b></p>
Yes—set the env vars locally. Results post the same way.
<p><b>Q: How do I link multiple Vansah cases to one test?</b></p>
Use a delimiter in your annotation/metadata (e.g., @TestCaseKey("VAN-101,VAN-102")) and iterate when reporting.
<p><b>Q: How do I attach BrowserStack artifacts?</b></p>
Send the session URL (and any available API links) in the Vansah result payload; include screenshot/log links if your flow supports them.
