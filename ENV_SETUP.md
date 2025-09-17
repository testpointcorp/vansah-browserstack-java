# 🔑 Environment Variables Setup Guide

## BrowserStack Configuration

**Get these from:** https://www.browserstack.com/ → Account Settings → Access Key

```bash
export BROWSERSTACK_USERNAME="your_browserstack_username"
export BROWSERSTACK_ACCESS_KEY="your_browserstack_access_key"
export BROWSERSTACK_BUILD_NAME="Vansah Build"
export BROWSERSTACK_PROJECT_NAME="Vansah BrowserStack"
export BROWSERSTACK_SESSION_NAME="Local run"
```

## Vansah Configuration

**Get token from:** https://www.vansah.com/ → Settings → Vansah API Tokens

```bash
export VANSAH_URL="https://prod.vansahnode.app"
export VANSAH_TOKEN="your_vansah_connect_token"
export VANSAH_ENVIRONMENT="QA"
```

## Jira + Vansah Integration

**You need access to a Jira instance with Vansah installed:**

### Option A: Jira Issue Style (Recommended)
```bash
export VANSAH_JIRA_ISSUE_KEY="KAN-123"        # Create an issue in Jira
export VANSAH_TESTCASE_KEY="KAN-C17"          # Create a test case in Jira
```

### Option B: Folder Style
```bash
export VANSAH_FOLDER_PATH="vansah test automation/regression 2025/"
export VANSAH_TESTCASE_KEY="KAN-C17"
```

### Option C: Standard Test Plan
```bash
export VANSAH_STP_KEY="KAN-P18"
export VANSAH_TESTCASE_KEY="KAN-C17"
```

### Option D: Advanced Test Plan
```bash
export VANSAH_ATP_KEY="KAN-P17"
export VANSAH_ATP_ASSET_TYPE="folder"  # or 'issue'
export VANSAH_TESTCASE_KEY="KAN-C17"
```

## Quick Start

1. **Create accounts:**
   - BrowserStack: https://www.browserstack.com/ (free trial available)
   - Vansah: https://www.vansah.com/
   - Jira: https://www.atlassian.com/software/jira (if you don't have one)

2. **Get credentials:**
   - BrowserStack: Account Settings → Access Key
   - Vansah: Settings → Vansah API Tokens
   - Jira: Create a test case and issue, copy their keys

3. **Set environment variables:**
   ```bash
   # Copy the example above and replace with your actual values
   export BROWSERSTACK_USERNAME="your_actual_username"
   # ... etc
   ```

4. **Run the test:**
   ```bash
   mvn test
   ```

## Troubleshooting

- **401 Auth errors:** Check your BrowserStack or Vansah credentials
- **Results not in Vansah:** Verify your Jira keys are correct and Vansah is properly installed
- **Test fails:** Check that all required environment variables are set