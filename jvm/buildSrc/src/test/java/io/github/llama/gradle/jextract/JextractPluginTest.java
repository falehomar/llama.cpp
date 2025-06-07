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
        assertTrue(output.contains("generateJextract"), "generateJextract task should be listed");
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
            .withArguments("generateJextract", "--stacktrace")
            .build();

        // Verify that the generate task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateJextract").getOutcome());

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
            .withArguments("generateJextract", "--stacktrace")
            .build();

        // Verify that the generate task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateJextract").getOutcome());

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

        // Verify that the generateJextract task was executed as a dependency
        assertNotNull(result.task(":generateJextract"), "generateJextract task should be executed");
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateJextract").getOutcome());

        // Verify that the generated Java file was compiled
        File classFile = testProjectDir.resolve("build/classes/java/main/com/example/test/TestHeader.class").toFile();
        assertTrue(classFile.exists(), "Generated Java file should be compiled");
    }
}
