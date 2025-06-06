# Example CMakeLists.txt for adding a test to llama.cpp

# Include the llama.cpp build functions
include(${CMAKE_CURRENT_SOURCE_DIR}/../../tests/CMakeLists.txt)

# Build and test the simple test
llama_build_and_test(test-simple.cpp)
