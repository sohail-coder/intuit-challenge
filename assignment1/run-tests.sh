#!/bin/bash

# Producer-Consumer Pattern Test Runner
# This script downloads dependencies, compiles, and runs all tests

set -e  # Exit on error

echo "=== Producer-Consumer Pattern Tests ==="
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
if ! command_exists java; then
    echo -e "${RED}✗ Java not found${NC}"
    echo "Please install Java 11 or higher"
    exit 1
fi

# Check for Maven
if ! command_exists mvn; then
    echo -e "${RED}✗ Maven not found${NC}"
    echo "Please install Maven:"
    echo "  - macOS: brew install maven"
    echo "  - Or download from: https://maven.apache.org/download.cgi"
    exit 1
fi

echo "=== Step 1: Downloading Dependencies ==="
mvn dependency:resolve -q

echo ""
echo "=== Step 2: Compiling Project ==="
mvn clean compile test-compile -q

echo ""
echo "=== Step 3: Running Tests ==="
echo ""

# Run all tests
mvn test

echo ""
echo "=== Tests Complete ==="
