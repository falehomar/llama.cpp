# llama.cpp Java API Implementation (Foreign Function & Memory API)

This module implements the Java API for llama.cpp using Java's Foreign Function & Memory API (FFM). The implementation follows the design outlined in the API design document.

## Implementation Approach

This implementation uses Java's Foreign Function & Memory API (introduced in Java 21) to directly interact with the native llama.cpp library without requiring JNI or SWIG. This approach offers several advantages:

1. **Performance**: Direct memory access without JNI overhead
2. **Safety**: Type-safe bindings with compile-time checking
3. **Maintainability**: Easier to update when the native API changes
4. **Portability**: Works across different platforms with minimal platform-specific code

## Current Implementation Status

The implementation is being developed following Test-Driven Development (TDD) principles. The current focus is on implementing the following components:

1. **Initialization and Backend Management** (Planned)
   - Backend initialization and cleanup
   - NUMA optimization
   - System information and capabilities

2. **Model Management** (Planned)
   - Loading and managing models
   - Model information and metadata
   - Tokenization and vocabulary (Incomplete)

3. **Context Management** (Planned)
   - Creating and managing contexts for inference
   - Context parameters and configuration
   - Batch processing
   - Sampling

4. **Advanced Features** (Planned)
   - Implementing advanced features like control vectors
   - Optimizing performance for different hardware
   - Supporting additional model types

## Building and Testing

The implementation uses Gradle for building and JUnit 5 for testing. The jextract plugin is used to generate Java bindings for the native llama.cpp library.

To build the implementation:

```bash
./gradlew :impl-foreign:build
```

To run the tests:

```bash
./gradlew :impl-foreign:test
```

## Dependencies

- Java 24 or later (for Foreign Function & Memory API)
- llama.cpp native library
- Gradle 7.0 or later
