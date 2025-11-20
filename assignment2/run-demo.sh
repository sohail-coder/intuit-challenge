#!/bin/bash

# Assignment 2 - Sales Data Analysis Demo Runner
# Automatically handles dependencies, compilation, and execution

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}════════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}     Assignment 2 - Sales Data Analysis Demo${NC}"
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

echo -e "${BLUE}═══ Step 2: Compiling Project ═══${NC}"
$MVN_CMD clean compile -q
echo -e "${GREEN}✓ Project compiled successfully${NC}"
echo ""

echo -e "${BLUE}═══ Step 3: Running Sales Analysis Demo ═══${NC}"
echo ""
echo -e "${CYAN}───────────────────────────────────────────────────────────${NC}"
echo ""
$MVN_CMD exec:java -q

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}     ✓ Demo completed successfully!${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "Testing Objectives Demonstrated:"
echo -e "  ${GREEN}✓${NC} Functional Programming"
echo -e "  ${GREEN}✓${NC} Stream Operations"
echo -e "  ${GREEN}✓${NC} Data Aggregation"
echo -e "  ${GREEN}✓${NC} Lambda Expressions"
echo ""

