#!/bin/bash

# BrowserStack Configuration
export BROWSERSTACK_USERNAME="matteo_YUFeom"
export BROWSERSTACK_ACCESS_KEY="DwyHtNzD9giktNgBvxJu"
export BROWSERSTACK_BUILD_NAME="Vansah Build"
export BROWSERSTACK_PROJECT_NAME="Vansah BrowserStack"
export BROWSERSTACK_SESSION_NAME="Local run"

# Vansah Configuration (you need to get these)
export VANSAH_URL="https://prod.vansahnode.app"
export VANSAH_TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJjb20udmFuc2FoLmppcmEudmFuc2FoLXBsdWdpbiIsImlhdCI6MTc1ODE3MDE0OSwic3ViIjoiNzEyMDIwOjVjZjJhNjg5LTI4ODgtNDNjMC1hMTI2LTUwMDM5MzgzNGJiMyIsImV4cCI6Mjc1ODE3MDE0OSwiYXVkIjpbImMzZDVkMzIzLWVmMTQtMzhiOS04MWI1LTNjMDg4Y2JhNjJmNiJdLCJ0eXBlIjoiY29ubmVjdCJ9._XvdoKzZYcFWoE1IqJSnEq2X6xWIinaIjeTYai4lozc"
export VANSAH_ENVIRONMENT="QA"

# Jira + Vansah Integration (you need to get these)
export VANSAH_JIRA_ISSUE_KEY="BS-1"
export VANSAH_TESTCASE_KEY="BS-C1"

echo "✅ BrowserStack credentials set!"
echo "⚠️  You still need to configure:"
echo "   - VANSAH_TOKEN (from https://www.vansah.com/)"
echo "   - VANSAH_JIRA_ISSUE_KEY (create an issue in Jira)"
echo "   - VANSAH_TESTCASE_KEY (create a test case in Jira)"
echo ""
echo "To run tests: mvn test"