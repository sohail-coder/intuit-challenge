#!/bin/bash

# Producer-Consumer Pattern Demo Runner
# This script downloads dependencies, compiles, and runs the demo

set -e  # Exit on error

echo "=== Producer-Consumer Pattern Demo ==="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for Java
echo -n "Checking for Java... "
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}✓${NC} $JAVA_VERSION"
else
    echo -e "${RED}✗ Java not found${NC}"
    echo "Please install Java 11 or higher"
    exit 1
fi

# Check for Maven
echo -n "Checking for Maven... "
if command_exists mvn; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo -e "${GREEN}✓${NC} $MVN_VERSION"
    MVN_CMD="mvn"
else
    echo -e "${YELLOW}⚠ Maven not found${NC}"
    
    # Try to install Maven via Homebrew (macOS)
    if [[ "$OSTYPE" == "darwin"* ]] && command_exists brew; then
        echo "Attempting to install Maven via Homebrew..."
        if brew install maven; then
            echo -e "${GREEN}✓ Maven installed successfully${NC}"
            MVN_CMD="mvn"
        else
            echo -e "${RED}✗ Failed to install Maven${NC}"
            echo "Please install Maven manually from https://maven.apache.org/download.cgi"
            exit 1
        fi
    else
        echo -e "${RED}✗ Maven not found and cannot auto-install${NC}"
        echo "Please install Maven:"
        echo "  - macOS: brew install maven"
        echo "  - Or download from: https://maven.apache.org/download.cgi"
        exit 1
    fi
fi

echo ""
echo "=== Step 1: Downloading Dependencies ==="
echo "This may take a few moments on first run..."
$MVN_CMD dependency:resolve

echo ""
echo "=== Step 2: Compiling Project ==="
$MVN_CMD clean compile

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi

echo ""
echo "=== Step 3: Running Producer-Consumer Demo ==="
echo ""

# Run the demo (mainClass is configured in pom.xml)
$MVN_CMD exec:java -q

echo ""
echo ""
echo "=== Demo Complete ==="
echo ""
echo "To run tests, use:"
echo "  $MVN_CMD test"
echo ""
echo "To run the demo again:"
echo "  $MVN_CMD exec:java"
echo ""
