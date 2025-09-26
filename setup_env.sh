#!/bin/bash

# BrowserStack Local Testing Configuration (fill with your credentials)
export BROWSERSTACK_USERNAME="YOUR_BROWSERSTACK_USERNAME"
export BROWSERSTACK_ACCESS_KEY="YOUR_BROWSERSTACK_ACCESS_KEY"
export BROWSERSTACK_LOCAL_FOLDER_URL="http://YOUR_LOCAL_FOLDER.browserstack.com"
export BROWSERSTACK_BUILD_NAME="Vansah Build"
export BROWSERSTACK_PROJECT_NAME="Vansah BrowserStack"
export BROWSERSTACK_SESSION_NAME="Local run"

# Vansah Configuration (replace with real values)
export VANSAH_URL="https://prod.vansahnode.app"
export VANSAH_TOKEN="YOUR_VANSAH_TOKEN"
export VANSAH_ENVIRONMENT="QA"

# Jira + Vansah Integration (you need to get these)
export VANSAH_JIRA_ISSUE_KEY="YOUR_JIRA_ISSUE_KEY"
export VANSAH_TESTCASE_KEY="YOUR_TESTCASE_KEY"

echo "✅ BrowserStack credentials set!"
echo "⚠️  You still need to configure:"
echo "   - VANSAH_TOKEN (from https://www.vansah.com/)"
echo "   - VANSAH_JIRA_ISSUE_KEY (create an issue in Jira)"
echo "   - VANSAH_TESTCASE_KEY (create a test case in Jira)"
echo ""
echo "To run tests: mvn test"