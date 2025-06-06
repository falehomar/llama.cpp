cmake_minimum_required(VERSION 3.21)
project(llama-java VERSION 0.1.0 LANGUAGES CXX C)

# Find required packages
find_package(SWIG 4.0 COMPONENTS java)
if(SWIG_FOUND)
    message("SWIG found: ${SWIG_EXECUTABLE}")
    if(NOT SWIG_java_FOUND)
        message(WARNING "SWIG Java bindings cannot be generated")
    else ()
        message(STATUS "SWIG Java found")

    endif()
endif()

include(${SWIG_USE_FILE})

# Find Java
find_package(JNI 17 REQUIRED)
find_package(Java 17 REQUIRED Development)
include(UseJava)


# Add main llama.cpp library
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/.. ${CMAKE_CURRENT_BINARY_DIR}/llama.cpp)

# Set SWIG flags
set(CMAKE_SWIG_FLAGS -package com.llamacpp.core)

# Include directories
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../include)
include_directories(${JNI_INCLUDE_DIRS})

# Set source files
set(SWIG_INTERFACE ${CMAKE_CURRENT_SOURCE_DIR}/src/main/swig/llama.i)
set_source_files_properties(${SWIG_INTERFACE} PROPERTIES CPLUSPLUS ON)

# Generate SWIG wrapper
swig_add_library(llama-jni
    TYPE SHARED
    LANGUAGE java
    SOURCES ${SWIG_INTERFACE}
)

# Link against llama library
target_link_libraries(llama-jni PRIVATE llama ${JNI_LIBRARIES})

# Set output directory for generated Java files
set(CMAKE_SWIG_OUTDIR ${CMAKE_CURRENT_SOURCE_DIR}/src/main/java/com/llamacpp/core)

# Install rules
install(TARGETS llama-jni
    LIBRARY DESTINATION lib
    RUNTIME DESTINATION bin
)

# Create jar file
add_jar(llama-java
    SOURCES ${CMAKE_SWIG_OUTDIR}/*.java
    OUTPUT_NAME llama
    VERSION ${PROJECT_VERSION}
)
