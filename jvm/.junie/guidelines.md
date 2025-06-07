# llama.cpp Java API Development Guidelines

This document provides guidelines for developing and working with the llama.cpp Java API project.

## Build/Configuration Instructions

### Prerequisites

- Java 21 or later
- Gradle 7.0 or later
- CMake (for building the native library)
- C++ compiler compatible with your platform

### Building the Java-centric API

The Java-centric API can be built using Gradle:

```bash
./gradlew build
```

This will:
1. Build the native library using CMake
2. Copy the native library to the resources directory
3. Compile the Java code
4. Run the tests

### Building the SWIG Bindings

The SWIG bindings can be built using the provided build script:

```bash
./build.sh
```

This script:
1. Runs CMake to configure the build
2. Builds the native library
3. Generates the Java bindings using SWIG
4. Compiles the Java code

### Project Structure

The project consists of three main subprojects:

1. `api`: Contains the Java API interfaces and common classes
2. `impl-foreign`: Implementation using Java's Foreign and Native Memory API
3. `impl-llamacpp`: Implementation using SWIG-generated bindings

## Testing Information

### Test-Driven Development (TDD)

This project strictly follows Test-Driven Development (TDD) principles. All code contributions must adhere to the TDD workflow:

1. **Write a failing test first** that defines the expected behavior
2. **Write the minimal implementation** to make the test pass
3. **Refactor** the code while ensuring tests continue to pass

No new features or bug fixes should be implemented without corresponding tests written beforehand. Pull requests that don't follow this workflow will not be accepted.

### Running Tests

Tests can be run using Gradle:

```bash
./gradlew test
```

To run tests for a specific subproject:

```bash
./gradlew api:test
./gradlew impl-foreign:test
./gradlew impl-llamacpp:test
```

To run a specific test class:

```bash
./gradlew test --tests "io.github.llama.api.sampling.SamplerParamsTest"
```

### Writing Tests

Tests are written using JUnit 5 (Jupiter) and Mockito. All tests should be written before implementing the actual functionality, following strict TDD principles. Here's an example test for the `SamplerParams` class:

```java
package io.github.llama.api.sampling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SamplerParamsTest {

    @Test
    public void testDefaultValues() {
        SamplerParams params = new SamplerParams();

        assertEquals(0.8f, params.getTemperature(), "Default temperature should be 0.8");
        assertEquals(0.95f, params.getTopP(), "Default topP should be 0.95");
        assertEquals(40, params.getTopK(), "Default topK should be 40");
        assertEquals(1.1f, params.getRepetitionPenalty(), "Default repetitionPenalty should be 1.1");
        assertEquals(128, params.getMaxTokens(), "Default maxTokens should be 128");
    }

    @Test
    public void testBuilder() {
        SamplerParams params = SamplerParams.builder()
                .temperature(0.7f)
                .topP(0.9f)
                .topK(30)
                .repetitionPenalty(1.3f)
                .maxTokens(512)
                .build();

        assertEquals(0.7f, params.getTemperature(), "Builder should set temperature to 0.7");
        assertEquals(0.9f, params.getTopP(), "Builder should set topP to 0.9");
        assertEquals(30, params.getTopK(), "Builder should set topK to 30");
        assertEquals(1.3f, params.getRepetitionPenalty(), "Builder should set repetitionPenalty to 1.3");
        assertEquals(512, params.getMaxTokens(), "Builder should set maxTokens to 512");
    }
}
```

### Test Organization

- Place test classes in the same package as the class being tested
- Name test classes with the suffix `Test`
- Use descriptive method names that explain what is being tested
- Include assertions with meaningful error messages

## Additional Development Information

### Code Style

- Follow standard Java code style conventions
- Use 4 spaces for indentation
- Use camelCase for method and variable names
- Use PascalCase for class names
- Include JavaDoc comments for all public classes and methods

### API Design Principles

The Java-centric API follows these design principles:

1. **Java-centric**: The API uses Java idioms and patterns rather than directly exposing the C/C++ API
2. **Modular**: The API is organized into logical modules
3. **Resource Management**: Resources are properly managed using try-with-resources
4. **Builder Pattern**: Complex objects are created using the builder pattern
5. **Service Provider Interface**: Implementations are discovered using the Java SPI mechanism

### Native Library Integration

The project uses two approaches for integrating with the native library:

1. **Java Foreign and Native Memory API** (impl-foreign): Uses the new Java Foreign API for direct memory access
2. **SWIG Bindings** (impl-llamacpp): Uses SWIG-generated bindings for JNI access

When working with the native library:

- Be careful with memory management
- Always release native resources in a finally block or try-with-resources
- Be aware of the differences between Java and C/C++ data types

### Debugging

For debugging issues with the native library:

- Set the `java.library.path` system property to point to the native library
- Use `-verbose:jni` JVM option to see JNI loading information
- Check for common issues like mismatched library versions or architecture

### Performance Considerations

- Minimize JNI calls for better performance
- Batch operations when possible
- Reuse contexts and models rather than creating new ones
- Be aware of memory usage, especially with large models
