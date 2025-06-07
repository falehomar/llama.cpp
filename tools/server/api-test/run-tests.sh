#!/bin/bash

# Script to run the llama.cpp server API tests

# Source the script to set Java 21
source "$(dirname "${BASH_SOURCE[0]}")/set-java-version.sh"
# The sdkjava21 function is automatically called when the script is sourced

# Set root directory
API_TEST_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${API_TEST_DIR}" && cd ../../.. && pwd)"

# Check if server is running
echo "Checking if llama.cpp server is running..."
if ! curl -s http://localhost:8080/health > /dev/null; then
    echo "Error: llama.cpp server is not running. Please start it with:"
    echo "  cd ${REPO_ROOT}/tools/server"
    echo "  ./run-webui.sh"
    exit 1
fi

# Run the tests
echo "Running llama.cpp API tests..."
cd "${API_TEST_DIR}"
./gradlew test

# Check if tests succeeded
if [ $? -eq 0 ]; then
    echo "All tests passed!"
else
    echo "Some tests failed. Please check the test reports for details."
    echo "Test reports can be found at: ${API_TEST_DIR}/build/reports/tests/test/index.html"
fi
