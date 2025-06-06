#include "log.h"

#include <cstdlib>
#include <cassert>

// A simple test to demonstrate how to write tests for llama.cpp
int main() {
    // Initialize logging
    LOG_INF("Starting simple test\n");

    // Test a simple assertion
    int test_value = 42;
    LOG_INF("Testing assertion: test_value == 42\n");
    assert(test_value == 42);

    // Test logging at different levels
    LOG_INF("This is an info message\n");
    LOG_WRN("This is a warning message\n");
    LOG_ERR("This is an error message\n");
    LOG_DBG("This is a debug message\n");

    LOG_INF("All tests passed successfully!\n");
    return 0;
}
