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

```java
// CMakeInstallTaskTest.java
@Test
public void testCMakeInstallTask() throws IOException {
    // Test setup...

    // Write build.gradle with plugin applied and install directory configured
    Files.write(buildFile.toPath(), Arrays.asList(
        "plugins {",
        "    id 'io.github.llama.cmake'",
        "}",
        "",
        "cmake {",
        "    cmakePath = '" + mockCMakeScript.getAbsolutePath().replace("\\", "\\\\") + "'",
        "    generator = 'Ninja'",
        "    buildType = 'Debug'",
        "    installDir = layout.buildDirectory.dir('install')",
        "}"
    ));

    // Run the install task
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.toFile())
        .withPluginClasspath()
        .withArguments("cmakeInstall", "--stacktrace")
        .build();

    // Verify that the install task succeeds
    assertEquals(TaskOutcome.SUCCESS, result.task(":cmakeInstall").getOutcome());

    // Verify that the install directory was created
    File installDir = testProjectDir.resolve("build/install").toFile();
    assertTrue(installDir.exists(), "Install directory should be created");

    // Verify that installed files exist
    File installedExecutable = new File(installDir, "bin/test_app");
    assertTrue(installedExecutable.exists(), "Installed executable should exist");

    // Verify that the mock CMake script was called with the expected arguments
    assertTrue(argsContent.contains("--install"), "Install command should be included in arguments");
    assertTrue(argsContent.contains("--prefix"), "Prefix argument should be included");
}
```

We also wrote a test for a custom install directory:

```java
@Test
public void testCMakeInstallWithCustomPrefix() throws IOException {
    // Test setup...

    // Write build.gradle with plugin applied and custom install directory
    Files.write(buildFile.toPath(), Arrays.asList(
        "plugins {",
        "    id 'io.github.llama.cmake'",
        "}",
        "",
        "cmake {",
        "    cmakePath = '" + mockCMakeScript.getAbsolutePath().replace("\\", "\\\\") + "'",
        "    generator = 'Ninja'",
        "    buildType = 'Debug'",
        "    installDir = layout.buildDirectory.dir('custom-install')",
        "}"
    ));

    // Run the install task
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.toFile())
        .withPluginClasspath()
        .withArguments("cmakeInstall", "--stacktrace")
        .build();

    // Verify that the install task succeeds
    assertEquals(TaskOutcome.SUCCESS, result.task(":cmakeInstall").getOutcome());

    // Verify that the custom install directory was created
    File customInstallDir = testProjectDir.resolve("build/custom-install").toFile();
    assertTrue(customInstallDir.exists(), "Custom install directory should be created");

    // Verify that installed files exist in the custom directory
    File installedExecutable = new File(customInstallDir, "bin/test_app");
    assertTrue(installedExecutable.exists(), "Installed executable should exist in custom directory");

    // Verify that the mock CMake script was called with the expected arguments
    assertTrue(argsContent.contains("--install"), "Install command should be included in arguments");
    assertTrue(argsContent.contains("--prefix"), "Prefix argument should be included");
    assertTrue(argsContent.contains("custom-install"), "Custom install directory should be included in arguments");
}
```

### Step 2: Run Tests (They Should Fail)

We ran the tests and they failed as expected, with the error:

```
Could not set unknown property 'installDir' for extension 'cmake' of type io.github.llama.gradle.cmake.CMakeExtension.
```

This confirmed that our tests were correctly testing for functionality that didn't exist yet.

### Step 3: Write Implementation Code

We implemented the minimal code needed to make the tests pass:

1. Added the `installDir` property to the CMakeExtension class:

```java
/**
 * The directory where CMake will install the built artifacts.
 */
public abstract DirectoryProperty getInstallDir();
```

2. Created a new CMakeInstallTask class:

```java
public abstract class CMakeInstallTask extends DefaultTask {
    @Input
    public abstract Property<String> getCmakePath();

    @InputDirectory
    public abstract DirectoryProperty getBuildDir();

    @OutputDirectory
    public abstract DirectoryProperty getInstallDir();

    @Input
    @Optional
    public abstract ListProperty<String> getArguments();

    @TaskAction
    public void install() {
        getLogger().info("Installing CMake project");

        List<String> args = new ArrayList<>();
        args.add("--install");
        args.add(".");

        // Add install directory if specified
        if (getInstallDir().isPresent()) {
            args.add("--prefix");
            args.add(getInstallDir().get().getAsFile().getAbsolutePath());
        }

        // Add additional arguments if specified
        if (getArguments().isPresent() && !getArguments().get().isEmpty()) {
            args.addAll(getArguments().get());
        }

        // Execute CMake install command
        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(getBuildDir().get().getAsFile());
            execSpec.setExecutable(getCmakePath().get());
            execSpec.setArgs(args);
        });

        getLogger().info("CMake install completed");
    }
}
```

3. Updated the CMakePlugin class to register the new task and set default values:

```java
// Default install directory is build/install
Provider<Directory> defaultInstallDir = project.getLayout().getBuildDirectory().dir("install");
extension.getInstallDir().convention(defaultInstallDir);

// Register the install task
project.getTasks().register("cmakeInstall", CMakeInstallTask.class, task -> {
    task.getCmakePath().set(extension.getCmakePath());
    task.getBuildDir().set(extension.getBuildDir());
    task.getInstallDir().set(extension.getInstallDir());
    task.getArguments().set(extension.getArguments());

    // Make sure build runs before install
    task.dependsOn(project.getTasks().getByName("cmakeBuild"));
});
```

### Step 4: Run Tests Again (They Should Pass)

We ran the tests again and they passed, confirming that our implementation satisfied the requirements defined in the tests.

### Step 5: Refactor

We refactored the code to add a configuration property to the CMakeInstallTask, making it more flexible and consistent with the CMakeBuildTask:

1. Added the configuration property to the CMakeInstallTask class:

```java
/**
 * Build configuration to install (Debug, Release, etc.).
 */
@Input
@Optional
public abstract Property<String> getConfiguration();
```

2. Updated the install method to use the configuration property:

```java
// Add configuration if specified
if (getConfiguration().isPresent()) {
    args.add("--config");
    args.add(getConfiguration().get());
}
```

3. Updated the CMakePlugin class to configure the property:

```java
project.getTasks().register("cmakeInstall", CMakeInstallTask.class, task -> {
    task.getCmakePath().set(extension.getCmakePath());
    task.getBuildDir().set(extension.getBuildDir());
    task.getInstallDir().set(extension.getInstallDir());
    task.getArguments().set(extension.getArguments());
    task.getConfiguration().set(extension.getBuildType());

    // Make sure build runs before install
    task.dependsOn(project.getTasks().getByName("cmakeBuild"));
});
```

### Step 6: Run Tests After Refactoring

We ran the tests again after refactoring, and they still passed, confirming that our refactoring didn't break anything.

## Conclusion

This example demonstrates how to follow the TDD process when developing Gradle plugins for the llama.cpp Java API project. By writing tests first, we ensure that our implementation satisfies the requirements and that we don't implement features that aren't needed. The tests also serve as documentation for how the feature is expected to work.

Following TDD principles leads to more reliable, maintainable, and well-designed code. It also makes it easier to make changes in the future, as the tests provide a safety net that catches regressions.
