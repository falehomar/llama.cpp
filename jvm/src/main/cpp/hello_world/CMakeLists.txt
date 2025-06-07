cmake_minimum_required(VERSION 3.10)
project(hello_world VERSION 1.0.0 LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add executable
add_executable(hello_world hello_world.cpp)

# Install target
install(TARGETS hello_world
    RUNTIME DESTINATION bin
)
