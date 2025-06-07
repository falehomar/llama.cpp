cmake_minimum_required(VERSION 3.10)
project(cmake_plugin_test VERSION 1.0.0 LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add executable
add_executable(test_program test_program.cpp)

# Install target
install(TARGETS test_program
    RUNTIME DESTINATION bin
)
