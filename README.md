<div align="center">
   <a href="https://vansah.com"><img src="https://vansah.com/app/logo/vansahjira-logo.svg" /></a><br>
</div>

<p align="center">
This tutorial demonstrates you through the process of integrating Browserstack with Vansah to automatically send your test case results to Jira</p>
<p>By following the below steps, you can streamline your testing workflow, ensuring that test outcomes are recorded directly in your Jira workspace.</p>
<p align="center">
    <a href="https://vansah.com/"><b>Website</b></a> •
    <a href="https://vansah.com/connect-integrations/"><b>More Connect Integrations</b></a>
</p>


## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup](#Setup)
- [Project Layout](#project-layout)
- [Implementation to your project](#implementation-to-your-project)
- [Contributing](#contributing)
- [Developed By](#developed-by)

---


## Features

- ✅ Ready-to-run `JUnit 5 and Selenium` test on BrowserStack  
- ✅ Integrates with `Vansah Java binding` to create test runs and log steps (with screenshots)  
---

## Prerequisites

- Java 11+ and Maven 3.8+
- Access to ['BrowserStack'](https://www.browserstack.com/users/sign_in?utm_source=vansah) account with username and accesskey
- Make sure that [`Vansah`](https://marketplace.atlassian.com/apps/1224250/vansah-test-management-for-jira?tab=overview&hosting=cloud) is installed in your Jira workspace
- You need to Generate **Vansah** [`connect`](https://docs.vansah.com/docs-base/generate-a-vansah-api-token-from-jira-cloud/) token to authenticate with Vansah APIs.

## Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/testpointcorp/vansah-browserstack-java.git
   cd vansah-browserstack-java
   ```

2. **Configure environment variables**  
   Create a `.env` file using the example file provided at  
   `src/test/resources/.env.example`.

3. **Run the Maven project**
   ```bash
   mvn test
   ```
    - The test opens [selenium.vansah.io]("https://selenium.vansah.io/) on a real Chrome instance in BrowserStack.
    - It takes a screenshot, checks for the page title, adds step logs to Vansah, and marks the BrowserStack session *passed/failed*.
    - Screenshots are saved under `target/screenshots/` and uploaded to Vansah via `addTestLog(...)`.
4. **Verify the results**
   - Check your **BrowserStack dashboard** for the test run.  
   - View execution results in **Vansah for Jira**.

---

## Project layout

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
│        └─ .env.example        (sample file for env vars)
├─ target/                      (generated build artifacts)
│  ├─ surefire-reports/         (Maven test reports)
│  └─ screenshots/              (test screenshots)
├─ .git/                        (Git metadata)
├─ .env                         (local env vars; gitignored)
├─ .gitignore                   (ignore rules)
├─ pom.xml                      (Maven config)
├─ README.md                    (this file)

```

- `src/test/resources/.env.example` – Example env file; copy to project root as `.env`
- `target/` – Build output, reports, and screenshots (auto-generated)

## Implementation to your project

This demo project uses [`VansahNode.java`](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java) to send results to jira.

Before implementing the steps below in your own project, ensure that you first run and test this demo project.

1. Download the file from this repositry [`VansahNode.java`](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java)
2. Copy the [`VansahNode.java`](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java) file into your project.
3. Ensure the below dependencies are present in your `pom.xml` :
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

## Contributing

Feel free to open issues or submit pull requests to enhance this integration.

## Developed By

[Vansah](https://vansah.com/)
