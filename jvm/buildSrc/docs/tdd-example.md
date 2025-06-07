# Test-Driven Development (TDD) Example: CMake Install Task

This document provides an example of how to follow Test-Driven Development (TDD) principles when developing Gradle plugins for the llama.cpp Java API project. It demonstrates the implementation of a new feature - the CMake Install Task - using the TDD approach.

## TDD Process Overview

The TDD process consists of the following steps:

1. **Write Tests First**: Before writing any implementation code, write tests that define the expected behavior
2. **Run Tests (They Should Fail)**: Verify that the tests fail as expected before implementation
3. **Write Implementation Code**: Write the minimal code needed to make the tests pass
4. **Run Tests Again (They Should Pass)**: Verify that the implementation satisfies the tests
5. **Refactor**: Improve the code while ensuring tests continue to pass
6. **Repeat**: Continue the cycle for each new feature or change

## Example: Adding CMake Install Task

### Step 1: Write Tests First

We started by writing tests for the new CMakeInstallTask feature before implementing it. The tests define the expected behavior of the task:

- `testCMakeInstallTask`: Tests that the install task works with the default install directory
- `testCMakeInstallWithCustomPrefix`: Tests that the install task works with a custom install directory

The tests verify that:
- The task executes successfully
- The install directory is created
- The installed files exist in the expected locations
- The CMake command is called with the correct arguments

### Step 2: Run Tests (They Should Fail)

We ran the tests and they failed as expected, with the error:

```
Could not set unknown property 'installDir' for extension 'cmake' of type io.github.llama.gradle.cmake.CMakeExtension.
```

This confirmed that our tests were correctly testing for functionality that didn't exist yet.

### Step 3: Write Implementation Code

We implemented the minimal code needed to make the tests pass:

1. Added the `installDir` property to the CMakeExtension class
2. Created a new CMakeInstallTask class with the necessary properties and methods
3. Updated the CMakePlugin class to register the new task and set default values

### Step 4: Run Tests Again (They Should Pass)

We ran the tests again and they passed, confirming that our implementation satisfied the requirements defined in the tests.

### Step 5: Refactor

We refactored the code to add a configuration property to the CMakeInstallTask, making it more flexible and consistent with the CMakeBuildTask:

1. Added the configuration property to the CMakeInstallTask class
2. Updated the install method to use the configuration property
3. Updated the CMakePlugin class to configure the property

### Step 6: Run Tests After Refactoring

We ran the tests again after refactoring, and they still passed, confirming that our refactoring didn't break anything.

## Conclusion

This example demonstrates how to follow the TDD process when developing Gradle plugins for the llama.cpp Java API project. By writing tests first, we ensure that our implementation satisfies the requirements and that we don't implement features that aren't needed. The tests also serve as documentation for how the feature is expected to work.

Following TDD principles leads to more reliable, maintainable, and well-designed code. It also makes it easier to make changes in the future, as the tests provide a safety net that catches regressions.

## Files Created/Modified

- `buildSrc/src/test/java/io/github/llama/gradle/cmake/CMakeInstallTaskTest.java`: Tests for the new feature
- `buildSrc/src/main/java/io/github/llama/gradle/cmake/CMakeExtension.java`: Added the installDir property
- `buildSrc/src/main/java/io/github/llama/gradle/cmake/CMakeInstallTask.java`: Implemented the install task
- `buildSrc/src/main/java/io/github/llama/gradle/cmake/CMakePlugin.java`: Updated to register and configure the new task
