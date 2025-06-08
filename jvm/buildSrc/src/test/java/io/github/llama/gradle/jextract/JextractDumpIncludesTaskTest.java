package io.github.llama.gradle.jextract;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
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
 * Tests for the JextractDumpIncludesTask class.
 */
public class JextractDumpIncludesTaskTest {
    @TempDir
    public Path testProjectDir;

    private Project project;
    private File headerFile;
    private File dumpIncludesFile;

    @BeforeEach
    public void setup() throws IOException {
        // Create a test project
        project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.toFile())
                .build();

        // Create include directory and a simple C header file
        File includeDir = testProjectDir.resolve("include").toFile();
        includeDir.mkdirs();
        headerFile = new File(includeDir, "test.h");

        // Write a simple C header file
        Files.write(headerFile.toPath(), Arrays.asList(
                "#ifndef TEST_H",
                "#define TEST_H",
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
                "#endif // TEST_H"
        ));

        // Create output directory
        File outputDir = testProjectDir.resolve("build").toFile();
        outputDir.mkdirs();

        // Define the dump includes file
        dumpIncludesFile = testProjectDir.resolve("build/test.includes").toFile();
    }

    @Test
    public void testTaskFailsWhenOutputFileExists() throws IOException {
        // Create the task
        JextractDumpIncludesTask task = project.getTasks().create("testDumpIncludes", JextractDumpIncludesTask.class);

        // Configure the task
        task.getJextractPath().set("/path/to/jextract");
        task.getHeaderFile().set(headerFile);
        task.getDumpIncludesFile().set(dumpIncludesFile);

        // Create a file with the same name as the output file
        Files.write(dumpIncludesFile.toPath(), Arrays.asList("This file already exists"));

        // Verify that the task throws an exception when executed
        RuntimeException exception = assertThrows(RuntimeException.class, task::generate);

        // Verify the exception message
        String message = exception.getMessage();
        assertTrue(message.contains("Output file already exists"),
                   "Exception message should contain 'Output file already exists'");
        assertTrue(message.contains(dumpIncludesFile.getAbsolutePath()),
                   "Exception message should contain the path of the existing file");
    }

    @Test
    public void testTaskSucceedsWhenOutputFileDoesNotExist() {
        // Create the task
        JextractDumpIncludesTask task = project.getTasks().create("testDumpIncludes", JextractDumpIncludesTask.class);

        // Configure the task
        task.getJextractPath().set("echo");  // Use echo as a mock jextract command
        task.getHeaderFile().set(headerFile);
        task.getDumpIncludesFile().set(dumpIncludesFile);

        // Verify that the task does not throw an exception when executed
        // Note: This will not actually run the jextract command, but it will verify that
        // the file existence check passes when the file does not exist
        assertDoesNotThrow(task::generate);
    }
}
