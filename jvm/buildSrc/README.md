# Gradle Plugin Development Guidelines

This document provides guidelines for developing Gradle plugins for the llama.cpp Java API project. It also includes a review of the currently implemented plugins and how they adhere to these guidelines.

## General Guidelines

### Plugin Structure

1. **Package Organization**
   - Place plugins in the `io.github.llama.gradle` package or a subpackage
   - Use a descriptive subpackage name for related plugins (e.g., `cmake` for CMake-related plugins)

2. **Class Organization**
   - Each plugin should consist of at least three classes:
     - **Plugin class**: Implements `Plugin<Project>` and contains the main plugin logic
     - **Extension class**: Provides configuration options for the plugin
     - **Task class(es)**: Implements the actual functionality of the plugin

3. **Naming Conventions**
   - Plugin class: `<Feature>Plugin` (e.g., `CMakePlugin`)
   - Extension class: `<Feature>Extension` (e.g., `CMakeExtension`)
   - Task class: `<Feature><Action>Task` (e.g., `CMakeGenerateTask`)

### Plugin Implementation

1. **Extension Configuration**
   - Create an extension to allow users to configure the plugin
   - Use the Gradle Properties API for extension properties
   - Provide sensible default values for all properties
   - Use the `convention()` method to set default values

2. **Task Registration**
   - Register tasks using the `project.getTasks().register()` method
   - Configure tasks with values from the extension
   - Establish task dependencies as needed

3. **Documentation**
   - Include JavaDoc comments for all classes, methods, and properties
   - Provide a README.md file for each plugin with usage examples

4. **Error Handling**
   - Validate user input and provide meaningful error messages
   - Fail fast when required properties are missing or invalid

5. **Integration with Gradle**
   - Respect Gradle's incremental build system
   - Use Gradle's task input/output annotations correctly
   - Integrate with existing Gradle plugins when appropriate

## Test-Driven Development (TDD)

All plugin development in this project must follow strict Test-Driven Development principles. TDD is not optional; it is a core requirement for maintaining high-quality, reliable plugins.

### TDD Process

1. **Write Tests First**: Before writing any implementation code, write tests that define the expected behavior
   - Tests should be comprehensive and cover all functionality
   - Include both positive tests (expected behavior) and negative tests (error handling)
   - Consider edge cases and boundary conditions

2. **Run Tests (They Should Fail)**: Verify that the tests fail as expected before implementation
   - This confirms that the tests are actually testing something
   - Failing tests provide a clear target for implementation

3. **Write Implementation Code**: Write the minimal code needed to make the tests pass
   - Focus on making the tests pass, not on perfection
   - Avoid implementing features not covered by tests

4. **Run Tests Again (They Should Pass)**: Verify that the implementation satisfies the tests
   - All tests should pass before moving forward
   - If tests fail, fix the implementation, not the tests (unless the tests are incorrect)

5. **Refactor**: Improve the code while ensuring tests continue to pass
   - Clean up code structure and improve readability
   - Eliminate duplication
   - Enhance performance if needed
   - Run tests after each significant change

6. **Repeat**: Continue the cycle for each new feature or change

### Benefits of TDD for Gradle Plugins

- **Reliability**: Plugins are thoroughly tested before use
- **Maintainability**: Tests document expected behavior
- **Confidence**: Changes can be made with less risk of regression
- **Design Quality**: TDD encourages better API design and separation of concerns

## Gradle Plugin Testing Guidelines

### Test Types

1. **Unit Tests**
   - Test individual classes in isolation
   - Mock dependencies using Mockito or similar frameworks
   - Focus on testing business logic

2. **Functional Tests**
   - Test the plugin as a whole
   - Verify that tasks perform their intended actions
   - Test integration with the Gradle API

3. **Integration Tests**
   - Test the plugin in a real Gradle build
   - Verify behavior in different environments
   - Test compatibility with other plugins

### Testing Framework

Use the Gradle TestKit for testing plugins:

```groovy
dependencies {
    testImplementation gradleTestKit()
    testImplementation 'junit:junit:4.13.2'
}
```

### Test Project Structure

Organize test projects as follows:

```
src/
  test/
    groovy/                 // Test classes
    resources/
      projects/             // Test projects
        basic/              // A basic test project
          src/
          build.gradle
        complex/            // A more complex test project
          src/
          build.gradle
```

### Writing Effective Tests

1. **Test Setup**
   - Create a test project with the necessary structure
   - Apply your plugin to the test project
   - Configure the plugin as needed

2. **Test Execution**
   - Execute Gradle tasks using the TestKit
   - Capture build output for verification
   - Set appropriate Gradle properties

3. **Test Verification**
   - Verify task outcomes (SUCCESS, FAILED, etc.)
   - Check that files were created/modified as expected
   - Validate that the plugin behaved correctly

### Example Test

```java
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExamplePluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "  id 'io.github.llama.example'",
            "}",
            "example {",
            "  someProperty = 'test-value'",
            "}"
        ));
    }

    @Test
    public void testExampleTask() {
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withArguments("exampleTask", "--stacktrace")
            .build();

        assertEquals(SUCCESS, result.task(":exampleTask").getOutcome());
        // Additional assertions to verify the task's behavior
    }
}
```

### Best Practices

1. **Test Coverage**
   - Aim for high test coverage (>80%)
   - Test all public APIs and extension points
   - Test error conditions and edge cases

2. **Test Independence**
   - Each test should be independent
   - Clean up resources after tests
   - Don't rely on test execution order

3. **Test Readability**
   - Use descriptive test names
   - Follow the Arrange-Act-Assert pattern
   - Document complex test scenarios

4. **Continuous Testing**
   - Run tests frequently during development
   - Include tests in CI/CD pipelines
   - Fix failing tests immediately

## Implemented Plugins

### JextractPlugin

**Purpose**: Generates Java bindings for C header files using jextract.

**Components**:
- `JextractPlugin`: Main plugin class
- `JextractExtension`: Configuration options
- `JextractTask`: Task to run jextract

**Adherence to Guidelines**:
- ✅ Follows the recommended package organization
- ✅ Uses the recommended class organization
- ✅ Follows naming conventions
- ✅ Provides an extension for configuration
- ✅ Sets sensible default values
- ✅ Registers and configures tasks properly
- ✅ Establishes task dependencies
- ✅ Includes JavaDoc comments
- ✅ Integrates with the Java plugin

**Notable Features**:
- Automatically adds generated sources to the main source set
- Ensures the generate task runs before compilation
- Supports various jextract options through the extension

### CMakePlugin

**Purpose**: Integrates CMake builds with Gradle projects.

**Components**:
- `CMakePlugin`: Main plugin class
- `CMakeExtension`: Configuration options
- `CMakeGenerateTask`: Task to generate CMake build files
- `CMakeBuildTask`: Task to build the CMake project

**Adherence to Guidelines**:
- ✅ Follows the recommended package organization
- ✅ Uses the recommended class organization
- ✅ Follows naming conventions
- ✅ Provides an extension for configuration
- ✅ Sets sensible default values
- ✅ Registers and configures tasks properly
- ✅ Establishes task dependencies
- ✅ Includes JavaDoc comments
- ✅ Uses proper task input/output annotations

**Notable Features**:
- Supports configurable CMake generator (default: Ninja)
- Provides separate tasks for generation and building
- Supports parallel builds
- Allows configuration of CMake cache variables

## Future Plugin Development

When developing new plugins for this project, follow these steps:

1. **Identify the Need**: Determine what functionality the plugin should provide
2. **Design the API**: Design the extension and task interfaces
3. **Implement the Plugin**: Follow the guidelines above
4. **Test the Plugin**: Create test cases to verify functionality
5. **Document the Plugin**: Add JavaDoc comments and create a README.md file
6. **Review the Implementation**: Ensure adherence to these guidelines

## Example Plugin Implementation

Here's a skeleton example of a new plugin following these guidelines:

```java
// Plugin class
public class ExamplePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Create extension
        ExampleExtension extension = project.getExtensions().create("example", ExampleExtension.class);

        // Set default values
        extension.getSomeProperty().convention("defaultValue");

        // Register task
        project.getTasks().register("exampleTask", ExampleTask.class, task -> {
            task.getSomeProperty().set(extension.getSomeProperty());
        });
    }
}

// Extension class
public abstract class ExampleExtension {
    public abstract Property<String> getSomeProperty();
}

// Task class
public abstract class ExampleTask extends DefaultTask {
    @Input
    public abstract Property<String> getSomeProperty();

    @TaskAction
    public void execute() {
        // Task implementation
    }
}
```
