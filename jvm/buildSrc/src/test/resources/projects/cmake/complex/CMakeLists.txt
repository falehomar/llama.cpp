cmake_minimum_required(VERSION 3.10)
project(ComplexCMakeTest)

# Set C++ standard
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Define a library
add_library(math_lib src/math_lib.cpp)

# Define an executable that uses the library
add_executable(complex_app src/main.cpp)
target_link_libraries(complex_app PRIVATE math_lib)

# Add a custom target
add_custom_target(generate_docs
    COMMAND ${CMAKE_COMMAND} -E echo "Generating documentation..."
    COMMENT "This would generate documentation in a real project"
)

# Add a custom command
add_custom_command(
    TARGET complex_app POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E echo "Built complex_app successfully"
    COMMENT "Post-build message"
)

# Set some cache variables for testing
set(TEST_VARIABLE "test_value" CACHE STRING "A test variable")
set(COMPLEX_TEST_ENABLED ON CACHE BOOL "Enable complex tests")
