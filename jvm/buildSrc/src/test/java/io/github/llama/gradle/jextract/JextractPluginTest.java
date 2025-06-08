package io.github.llama.gradle.jextract;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Jextract Gradle plugin.
 */
public class JextractPluginTest {
    @TempDir
    public Path testProjectDir;

    private File buildFile;
    private File headerFile;

    @BeforeEach
    public void setup() throws IOException {
        // Create build.gradle file
        buildFile = testProjectDir.resolve("build.gradle").toFile();

        // Create include directory and a simple C header file
        File includeDir = testProjectDir.resolve("include").toFile();
        includeDir.mkdirs();
        headerFile = new File(includeDir, "test.h");

        // Write a simple C header file
        Files.write(headerFile.toPath(), Arrays.asList(
            "#ifndef TEST_H",
            "#define TEST_H",
            "",
            "#ifdef __cplusplus",
            "extern \"C\" {",
            "#endif",
            "",
            "typedef struct {",
            "    int x;",
            "    int y;",
            "} Point;",
            "",
            "int add(int a, int b);",
            "",
            "#define MAX_POINTS 100",
            "",
            "#ifdef __cplusplus",
            "}",
            "#endif",
            "",
            "#endif // TEST_H"
        ));
    }

    @Test
    public void testPluginAppliesSuccessfully() throws IOException {
        // Write build.gradle with plugin applied
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    // Mock jextract path for testing",
            "    jextractPath = 'echo'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "}"
        ));

        // Run the build
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("tasks", "--all")
            .build();

        // Verify that the build succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":tasks").getOutcome());

        // Verify that the plugin task is listed
        String output = result.getOutput();
        assertTrue(output.contains("jextract"), "jextract task should be listed");
    }

    @Test
    public void testJextractTaskConfiguration() throws IOException {
        // Write build.gradle with plugin applied and task configured
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    // Mock jextract path for testing",
            "    jextractPath = 'echo'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "    headerClassName = 'TestHeader'",
            "    includePaths = ['include']",
            "    includeFunctions = ['add']",
            "    includeStructs = ['Point']",
            "    includeConstants = ['MAX_POINTS']",
            "}"
        ));

        // Create a mock jextract script for testing
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt"
        ));
        mockJextractScript.setExecutable(true);

        // Update the jextract path to use the mock script
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    jextractPath = '" + mockJextractScript.getAbsolutePath().replace("\\", "\\\\") + "'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "    headerClassName = 'TestHeader'",
            "    includePaths = ['include']",
            "    includeFunctions = ['add']",
            "    includeStructs = ['Point']",
            "    includeConstants = ['MAX_POINTS']",
            "}"
        ));

        // Run the generate task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("jextract", "--stacktrace")
            .build();

        // Verify that the generate task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":jextract").getOutcome());

        // Verify that the output directory was created
        File outputDir = testProjectDir.resolve("build/generated/jextract").toFile();
        assertTrue(outputDir.exists(), "Output directory should be created");

        // Verify that the mock jextract script was called with the expected arguments
        File argsFile = testProjectDir.resolve("jextract-args.txt").toFile();
        assertTrue(argsFile.exists(), "Arguments file should be created");

        String argsContent = Files.readString(argsFile.toPath());
        assertTrue(argsContent.contains("--output"), "Output argument should be included");
        assertTrue(argsContent.contains("--target-package com.example.test"),
                   "Target package argument should be included");
        assertTrue(argsContent.contains("--header-class-name TestHeader"),
                   "Header class name argument should be included");
        assertTrue(argsContent.contains("--include-function add"),
                   "Include function argument should be included");
        assertTrue(argsContent.contains("--include-struct Point"),
                   "Include struct argument should be included");
        assertTrue(argsContent.contains("--include-constant MAX_POINTS"),
                   "Include constant argument should be included");
        assertTrue(argsContent.contains("-I include"),
                   "Include path argument should be included");
    }

    @Test
    public void testCustomOutputDirectory() throws IOException {
        // Write build.gradle with custom output directory
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    // Mock jextract path for testing",
            "    jextractPath = 'echo'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "    outputDir = layout.buildDirectory.dir('custom-jextract-output')",
            "}"
        ));

        // Create a mock jextract script for testing
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt",
            "mkdir -p \"$2\""  // Create the output directory
        ));
        mockJextractScript.setExecutable(true);

        // Update the jextract path to use the mock script
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    jextractPath = '" + mockJextractScript.getAbsolutePath().replace("\\", "\\\\") + "'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "    outputDir = layout.buildDirectory.dir('custom-jextract-output')",
            "}"
        ));

        // Run the generate task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("jextract", "--stacktrace")
            .build();

        // Verify that the generate task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":jextract").getOutcome());

        // Verify that the custom output directory was created
        File customOutputDir = testProjectDir.resolve("build/custom-jextract-output").toFile();
        assertTrue(customOutputDir.exists(), "Custom output directory should be created");

        // Verify that the mock jextract script was called with the expected arguments
        File argsFile = testProjectDir.resolve("jextract-args.txt").toFile();
        assertTrue(argsFile.exists(), "Arguments file should be created");

        String argsContent = Files.readString(argsFile.toPath());
        assertTrue(argsContent.contains("build/custom-jextract-output"),
                   "Custom output directory should be included in arguments");
    }

    @Test
    public void testJavaPluginIntegration() throws IOException {
        // Write build.gradle with plugin applied
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    // Mock jextract path for testing",
            "    jextractPath = 'echo'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "}"
        ));

        // Create a mock jextract script that creates a Java file
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "mkdir -p \"$2/com/example/test\"",
            "echo \"package com.example.test;\" > \"$2/com/example/test/TestHeader.java\"",
            "echo \"public class TestHeader {\" >> \"$2/com/example/test/TestHeader.java\"",
            "echo \"    public static int add(int a, int b) { return a + b; }\" >> \"$2/com/example/test/TestHeader.java\"",
            "echo \"}\" >> \"$2/com/example/test/TestHeader.java\""
        ));
        mockJextractScript.setExecutable(true);

        // Update the jextract path to use the mock script
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    jextractPath = '" + mockJextractScript.getAbsolutePath().replace("\\", "\\\\") + "'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "}"
        ));

        // Run the compileJava task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("compileJava", "--stacktrace")
            .build();

        // Verify that the compileJava task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());

        // Verify that the jextract task was executed as a dependency
        assertNotNull(result.task(":jextract"), "jextract task should be executed");
        assertEquals(TaskOutcome.SUCCESS, result.task(":jextract").getOutcome());

        // Verify that the generated Java file was compiled
        File classFile = testProjectDir.resolve("build/classes/java/main/com/example/test/TestHeader.class").toFile();
        assertTrue(classFile.exists(), "Generated Java file should be compiled");
    }

    @Test
    public void testDebugFlagOption() throws IOException {
        // Create a mock jextract script that logs the arguments it receives
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt",
            "# Find the output directory argument",
            "OUTPUT_DIR=\"\"",
            "for i in $(seq 1 $#); do",
            "  if [ \"${!i}\" = \"--output\" ]; then",
            "    next=$((i+1))",
            "    OUTPUT_DIR=\"${!next}\"",
            "    break",
            "  fi",
            "done",
            "# Create the output directory if found",
            "if [ -n \"$OUTPUT_DIR\" ]; then",
            "  mkdir -p \"$OUTPUT_DIR\"",
            "fi"
        ));
        mockJextractScript.setExecutable(true);

        // Write build.gradle with plugin applied and debug enabled
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    jextractPath = '" + mockJextractScript.getAbsolutePath().replace("\\", "\\\\") + "'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "    debug = true",
            "}"
        ));

        // Run the jextract task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("jextract", "--stacktrace")
            .build();

        // Verify that the jextract task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":jextract").getOutcome());

        // Verify that the mock jextract script was called with the debug flag
        File argsLogFile = testProjectDir.resolve("jextract-args.txt").toFile();
        assertTrue(argsLogFile.exists(), "Arguments log file should be created");

        String argsContent = Files.readString(argsLogFile.toPath());
        System.out.println("[DEBUG] Args log content: " + argsContent);

        assertTrue(argsContent.contains("-Djextract.debug=true"),
                   "Debug flag should be included in command line arguments when debug is enabled");
    }

    @Test
    public void testDumpIncludesFailsWhenFileExists() throws IOException {
        // Create a mock jextract script for testing
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt"
        ));
        mockJextractScript.setExecutable(true);

        // Write build.gradle with plugin applied and dump-includes task configured
        // Explicitly set the dumpIncludesFile to ensure we know exactly where the output will go
        // Also add a doFirst action to check if the file exists and fail if it does
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    jextractPath = '" + mockJextractScript.getAbsolutePath().replace("\\", "\\\\") + "'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "    dumpIncludesFile = file('src/main/resources/jextract/explicit-test.includes')",
            "}",
            "",
            "// Use the default implementation of the dump-includes task",
            "// which should check if the output file exists and throw an exception",
            "// No need to override the task as this should be built into JextractDumpIncludesTask"
        ));

        // Create the output directory
        File outputDir = testProjectDir.resolve("src/main/resources/jextract").toFile();
        outputDir.mkdirs();

        // Create a file with the same name as the explicitly configured dump-includes output
        File existingFile = testProjectDir.resolve("src/main/resources/jextract/explicit-test.includes").toFile();
        Files.write(existingFile.toPath(), Arrays.asList("This file already exists"));

        // Run the dump-includes task and expect it to fail
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("dump-includes", "--stacktrace")
            .buildAndFail(); // Use buildAndFail since we expect the task to fail

        // Verify that the task failed with the expected error message
        String output = result.getOutput();
        assertTrue(output.contains("Output file already exists"),
                   "Task should fail with 'Output file already exists' error message");
        assertTrue(output.contains(existingFile.getAbsolutePath()),
                   "Error message should include the path of the existing file");
    }

    @Test
    public void testArgsFileSupport() throws IOException {
        // Create a mock jextract script that logs the arguments it receives
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt",
            "# Find the output directory argument",
            "OUTPUT_DIR=\"\"",
            "for arg in \"$@\"; do",
            "  if [ \"$prev_arg\" = \"--output\" ]; then",
            "    OUTPUT_DIR=\"$arg\"",
            "    break",
            "  fi",
            "  prev_arg=\"$arg\"",
            "done",
            "# Create output directory and a dummy Java file",
            "if [ -n \"$OUTPUT_DIR\" ]; then",
            "  mkdir -p \"$OUTPUT_DIR/com/example/test\"",
            "  echo \"package com.example.test;\" > \"$OUTPUT_DIR/com/example/test/TestHeader.java\"",
            "  echo \"public class TestHeader {}\" >> \"$OUTPUT_DIR/com/example/test/TestHeader.java\"",
            "fi"
        ));
        mockJextractScript.setExecutable(true);

        // Write build.gradle with plugin applied
        Files.write(buildFile.toPath(), Arrays.asList(
            "plugins {",
            "    id 'java'",
            "    id 'io.github.llama.jextract'",
            "}",
            "",
            "jextract {",
            "    jextractPath = '" + mockJextractScript.getAbsolutePath().replace("\\", "\\\\") + "'",
            "    headerFile = file('include/test.h')",
            "    targetPackage = 'com.example.test'",
            "}"
        ));

        // Create src/main/resources/jextract directory
        File resourcesDir = testProjectDir.resolve("src/main/resources/jextract").toFile();
        resourcesDir.mkdirs();

        // Create an args file with some test include directives
        File argsFile = new File(resourcesDir, "test.includes");
        Files.write(argsFile.toPath(), Arrays.asList(
            "--include-function add",
            "--include-struct Point",
            "--include-constant MAX_POINTS"
        ));

        // Run the jextract task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("jextract", "--stacktrace")
            .build();

        // Verify that the jextract task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":jextract").getOutcome());

        // Verify that the mock jextract script was called with the args file
        File argsLogFile = testProjectDir.resolve("jextract-args.txt").toFile();
        assertTrue(argsLogFile.exists(), "Arguments log file should be created");

        String argsContent = Files.readString(argsLogFile.toPath());

        // Debug logging
        System.out.println("[DEBUG] Args file path: " + argsFile.getAbsolutePath());
        System.out.println("[DEBUG] Args log content: " + argsContent);

        // On macOS, /var is a symlink to /private/var, so the absolute path might have /private prepended to it
        String argsFilePath = argsFile.getAbsolutePath();
        String argsFilePathWithPrivate = "/private" + argsFilePath;

        assertTrue(argsContent.contains("@" + argsFilePath) || argsContent.contains("@" + argsFilePathWithPrivate),
                   "Args file should be included in command line arguments");
    }
}
