#!/bin/bash

# Assignment 2 - Sales Data Analysis Test Runner
# Automatically handles dependencies, compilation, and test execution

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${CYAN}════════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}     Assignment 2 - Running Unit Tests${NC}"
echo -e "${CYAN}════════════════════════════════════════════════════════════${NC}"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java is not installed${NC}"
    echo -e "${YELLOW}Please install Java 17 or higher${NC}"
    echo -e "  macOS:   brew install openjdk@17"
    echo -e "  Ubuntu:  sudo apt install openjdk-17-jdk"
    echo -e "  Fedora:  sudo dnf install java-17-openjdk"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F'.' '{print $1}')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}✗ Java 17 or higher is required${NC}"
    echo -e "${YELLOW}Current version: $(java -version 2>&1 | head -n 1)${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Java $(java -version 2>&1 | awk -F '"' '/version/ {print $2}') detected${NC}"

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for Maven
if ! command_exists mvn; then
    echo -e "${YELLOW}⚠ Maven not found${NC}"
    
    # Try to install Maven on macOS
    if [[ "$OSTYPE" == "darwin"* ]]; then
        if command_exists brew; then
            echo -e "${BLUE}📦 Installing Maven via Homebrew...${NC}"
            brew install maven
            MVN_CMD="mvn"
        else
            echo -e "${RED}✗ Homebrew not found. Please install Maven manually:${NC}"
            echo -e "  ${YELLOW}brew install maven${NC}"
            echo -e "  OR download from: https://maven.apache.org/download.cgi"
            exit 1
        fi
    else
        echo -e "${RED}✗ Please install Maven manually:${NC}"
        echo -e "  Ubuntu:  ${YELLOW}sudo apt install maven${NC}"
        echo -e "  Fedora:  ${YELLOW}sudo dnf install maven${NC}"
        echo -e "  Manual:  ${YELLOW}https://maven.apache.org/download.cgi${NC}"
        exit 1
    fi
else
    MVN_CMD="mvn"
    echo -e "${GREEN}✓ Maven $(mvn -version | head -n 1 | awk '{print $3}') detected${NC}"
fi

echo ""

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Verify we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}✗ Error: pom.xml not found in current directory${NC}"
    echo -e "${YELLOW}Please run this script from the assignment2 directory${NC}"
    exit 1
fi

echo -e "${BLUE}═══ Step 1: Downloading Dependencies ═══${NC}"
$MVN_CMD dependency:resolve dependency:resolve-plugins -q
echo -e "${GREEN}✓ Dependencies downloaded${NC}"
echo ""

echo -e "${BLUE}═══ Step 2: Compiling Project and Tests ═══${NC}"
$MVN_CMD clean compile test-compile -q
echo -e "${GREEN}✓ Compilation successful${NC}"
echo ""

echo -e "${BLUE}═══ Step 3: Running Unit Tests ═══${NC}"
echo ""

# Run tests and capture output
TEST_OUTPUT=$($MVN_CMD test 2>&1)
TEST_EXIT_CODE=$?

# Display the test output
echo "$TEST_OUTPUT"

echo ""

# Parse test results
if [ $TEST_EXIT_CODE -eq 0 ]; then
    TESTS_RUN=$(echo "$TEST_OUTPUT" | grep "Tests run:" | tail -1 | awk '{print $3}' | tr -d ',')
    FAILURES=$(echo "$TEST_OUTPUT" | grep "Tests run:" | tail -1 | awk '{print $5}' | tr -d ',')
    ERRORS=$(echo "$TEST_OUTPUT" | grep "Tests run:" | tail -1 | awk '{print $7}' | tr -d ',')
    SKIPPED=$(echo "$TEST_OUTPUT" | grep "Tests run:" | tail -1 | awk '{print $9}')
    
    echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}     ✓ All Tests Passed!${NC}"
    echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
    echo ""
    echo -e "${MAGENTA}Test Summary:${NC}"
    echo -e "  Total Tests:  ${GREEN}${TESTS_RUN}${NC}"
    echo -e "  Passed:       ${GREEN}${TESTS_RUN}${NC}"
    echo -e "  Failures:     ${GREEN}${FAILURES}${NC}"
    echo -e "  Errors:       ${GREEN}${ERRORS}${NC}"
    echo -e "  Skipped:      ${CYAN}${SKIPPED}${NC}"
    echo ""
    echo -e "${BLUE}Coverage Includes:${NC}"
    echo -e "  ${GREEN}✓${NC} Functional programming tests"
    echo -e "  ${GREEN}✓${NC} Stream operations validation"
    echo -e "  ${GREEN}✓${NC} Data aggregation accuracy"
    echo -e "  ${GREEN}✓${NC} Lambda expression functionality"
    echo -e "  ${GREEN}✓${NC} Edge case handling"
    echo -e "  ${GREEN}✓${NC} Empty data set tests"
    echo ""
else
    echo -e "${RED}════════════════════════════════════════════════════════════${NC}"
    echo -e "${RED}     ✗ Some Tests Failed${NC}"
    echo -e "${RED}════════════════════════════════════════════════════════════${NC}"
    echo ""
    echo -e "${YELLOW}Please review the test output above for details.${NC}"
    echo ""
    exit 1
fi

