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
 * Tests for the JextractDumpIncludesTask.
 */
public class JextractDumpIncludesTaskTest {
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

    /**
     * Test that the dump-includes task works with a specified dumpIncludesFile.
     */
    @Test
    public void testDumpIncludesWithSpecifiedFile() throws IOException {
        // Create a mock jextract script for testing
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt",
            "# Create the dump file with some mock content",
            "echo \"FUNCTION add\" > \"$2\""
        ));
        mockJextractScript.setExecutable(true);

        // Write build.gradle with plugin applied and dumpIncludesFile specified
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
            "    dumpIncludesFile = file('build/test-dump.includes')",
            "}"
        ));

        // Run the dump-includes task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("dump-includes", "--stacktrace")
            .build();

        // Verify that the dump-includes task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":dump-includes").getOutcome());

        // Verify that the specified dump file was created
        File dumpFile = testProjectDir.resolve("build/test-dump.includes").toFile();
        assertTrue(dumpFile.exists(), "Dump file should be created");

        // Verify that the mock jextract script was called with the expected arguments
        File argsFile = testProjectDir.resolve("jextract-args.txt").toFile();
        assertTrue(argsFile.exists(), "Arguments file should be created");

        String argsContent = Files.readString(argsFile.toPath());
        assertTrue(argsContent.contains("--dump-includes"), "Dump includes argument should be included");
        assertTrue(argsContent.contains("build/test-dump.includes"),
                   "Specified dump file path should be included in arguments");
    }

    /**
     * Test that the dump-includes task works without a specified dumpIncludesFile,
     * deriving the file name from the header file.
     */
    @Test
    public void testDumpIncludesWithDerivedFile() throws IOException {
        // Create a mock jextract script for testing
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt",
            "# Create the dump file with some mock content",
            "echo \"FUNCTION add\" > \"$2\""
        ));
        mockJextractScript.setExecutable(true);

        // Write build.gradle with plugin applied but no dumpIncludesFile specified
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
            "    // No dumpIncludesFile specified - should be derived from header file name",
            "}"
        ));

        // Run the dump-includes task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("dump-includes", "--stacktrace")
            .build();

        // Verify that the dump-includes task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":dump-includes").getOutcome());

        // Verify that the derived dump file was created (should be in build/test.includes)
        File dumpFile = testProjectDir.resolve("build/test.includes").toFile();
        assertTrue(dumpFile.exists(), "Derived dump file should be created");

        // Verify that the mock jextract script was called with the expected arguments
        File argsFile = testProjectDir.resolve("jextract-args.txt").toFile();
        assertTrue(argsFile.exists(), "Arguments file should be created");

        String argsContent = Files.readString(argsFile.toPath());
        assertTrue(argsContent.contains("--dump-includes"), "Dump includes argument should be included");
        assertTrue(argsContent.contains("build/test.includes"),
                   "Derived dump file path should be included in arguments");
    }

    /**
     * Test that the dump-includes task correctly handles include paths and other options.
     */
    @Test
    public void testDumpIncludesWithOptions() throws IOException {
        // Create a mock jextract script for testing
        File mockJextractScript = testProjectDir.resolve("mock-jextract.sh").toFile();
        Files.write(mockJextractScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock jextract called with: $@\" > jextract-args.txt",
            "# Create the dump file with some mock content",
            "echo \"FUNCTION add\" > \"$2\""
        ));
        mockJextractScript.setExecutable(true);

        // Write build.gradle with plugin applied and various options
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
            "    includePaths = ['include', '/usr/include']",
            "    defineMacros = [",
            "        'DEBUG': '1',",
            "        'VERSION': '1.0.0'",
            "    ]",
            "    dumpIncludesFile = file('build/test-dump.includes')",
            "}"
        ));

        // Run the dump-includes task
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withArguments("dump-includes", "--stacktrace")
            .build();

        // Verify that the dump-includes task succeeds
        assertEquals(TaskOutcome.SUCCESS, result.task(":dump-includes").getOutcome());

        // Verify that the mock jextract script was called with the expected arguments
        File argsFile = testProjectDir.resolve("jextract-args.txt").toFile();
        assertTrue(argsFile.exists(), "Arguments file should be created");

        String argsContent = Files.readString(argsFile.toPath());
        assertTrue(argsContent.contains("-I include"), "Include path should be included in arguments");
        assertTrue(argsContent.contains("-I /usr/include"), "Include path should be included in arguments");
        assertTrue(argsContent.contains("-D DEBUG=1"), "Define macro should be included in arguments");
        assertTrue(argsContent.contains("-D VERSION=1.0.0"), "Define macro should be included in arguments");
    }
}
