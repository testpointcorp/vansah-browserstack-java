#!/bin/bash

# BrowserStack Configuration
export BROWSERSTACK_USERNAME="matteo_YUFeom"
export BROWSERSTACK_ACCESS_KEY="DwyHtNzD9giktNgBvxJu"
export BROWSERSTACK_BUILD_NAME="Vansah Build"
export BROWSERSTACK_PROJECT_NAME="Vansah BrowserStack"
export BROWSERSTACK_SESSION_NAME="Local run"

# Vansah Configuration (you need to get these)
export VANSAH_URL="https://prod.vansahnode.app"
export VANSAH_TOKEN="your_vansah_connect_token_here"
export VANSAH_ENVIRONMENT="QA"

# Jira + Vansah Integration (you need to get these)
export VANSAH_JIRA_ISSUE_KEY="your_jira_issue_key_here"
export VANSAH_TESTCASE_KEY="your_test_case_key_here"

echo "✅ BrowserStack credentials set!"
echo "⚠️  You still need to configure:"
echo "   - VANSAH_TOKEN (from https://www.vansah.com/)"
echo "   - VANSAH_JIRA_ISSUE_KEY (create an issue in Jira)"
echo "   - VANSAH_TESTCASE_KEY (create a test case in Jira)"
echo ""
echo "To run tests: mvn test"