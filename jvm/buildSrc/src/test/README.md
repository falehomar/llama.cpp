# Gradle Plugin Tests

This directory contains tests for the Gradle plugins in the buildSrc project. The tests follow the Test-Driven Development (TDD) principles outlined in the [buildSrc README](../../README.md).

## Test Structure

The tests are organized as follows:

- `java/io/github/llama/gradle/` - Tests for the Jextract plugin
- `java/io/github/llama/gradle/cmake/` - Tests for the CMake plugin
- `resources/projects/` - Test projects used by the tests

## Test Approach

The tests use the Gradle TestKit to test the plugins in a real Gradle environment. The tests create temporary projects, apply the plugins, and verify that the plugins behave as expected.

To make the tests more robust and less dependent on the environment, the tests use mock scripts instead of relying on real CMake or Jextract installations. These mock scripts create the necessary files and directories to make the tests pass, and they also write the arguments they were called with to a file so that the tests can verify that the correct arguments were passed.

## Running the Tests

The tests can be run using the Gradle test task:

```bash
./gradlew buildSrc:test
```

## Test Coverage

### Jextract Plugin Tests

The Jextract plugin tests verify that:

1. The plugin applies successfully and registers the expected tasks
2. The plugin correctly configures the Jextract task with the provided options
3. The plugin correctly handles custom output directories
4. The plugin correctly integrates with the Java plugin

### CMake Plugin Tests

The CMake plugin tests verify that:

1. The plugin applies successfully and registers the expected tasks
2. The plugin correctly configures the CMake generate task with the provided options
3. The plugin correctly configures the CMake build task with the provided options
4. The plugin correctly handles custom build directories
5. The plugin correctly handles cache variables

## Test Resources

The test resources include:

1. Basic and complex test projects for the CMake plugin
2. Basic and complex test projects for the Jextract plugin

These test projects include the necessary files (CMakeLists.txt, C++ source files, C header files, etc.) to test the plugins in a realistic environment.
