#!/bin/bash

# Script to run the llama.cpp server API tests

# Set root directory
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && cd ../../../.. && pwd)"
API_TEST_DIR="${REPO_ROOT}/tools/server/api-test"

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
