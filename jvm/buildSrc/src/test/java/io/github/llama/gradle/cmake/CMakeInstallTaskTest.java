package io.github.llama.gradle.cmake;

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
 * Tests for the CMake Install task in the Gradle plugin.
 * Following TDD principles, this test is written before implementing the feature.
 */
public class CMakeInstallTaskTest {
    @TempDir
    public Path testProjectDir;

    private File buildFile;
    private File cmakeListsFile;
    private File sourceCppFile;

    @BeforeEach
    public void setup() throws IOException {
        // Create build.gradle file
        buildFile = testProjectDir.resolve("build.gradle").toFile();

        // Create CMakeLists.txt file
        cmakeListsFile = testProjectDir.resolve("CMakeLists.txt").toFile();

        // Create src directory and a simple C++ file
        File srcDir = testProjectDir.resolve("src").toFile();
        srcDir.mkdirs();
        sourceCppFile = new File(srcDir, "main.cpp");

        // Write a simple C++ file
        Files.write(sourceCppFile.toPath(), Arrays.asList(
            "#include <iostream>",
            "",
            "int main() {",
            "    std::cout << \"Hello, World!\" << std::endl;",
            "    return 0;",
            "}"
        ));

        // Write a CMakeLists.txt file with install targets
        Files.write(cmakeListsFile.toPath(), Arrays.asList(
            "cmake_minimum_required(VERSION 3.10)",
            "project(TestProject)",
            "",
            "add_executable(test_app src/main.cpp)",
            "",
            "install(TARGETS test_app DESTINATION bin)",
            "install(FILES README.md DESTINATION share/doc/test_app)"
        ));

        // Create a README.md file
        Files.write(testProjectDir.resolve("README.md"), Arrays.asList(
            "# Test Project",
            "",
            "This is a test project for CMake install."
        ));
    }

    @Test
    public void testCMakeInstallTask() throws IOException {
        // Create a mock CMake script for testing
        File mockCMakeScript = testProjectDir.resolve("mock-cmake.sh").toFile();
        File argsFile = testProjectDir.resolve("cmake-args.txt").toFile();

        Files.write(mockCMakeScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock CMake called with: $@\" > " + argsFile.getAbsolutePath(),
            "if [ \"$1\" = \"--install\" ]; then",
            "  # Create install directory structure",
            "  mkdir -p " + testProjectDir.resolve("build/install/bin").toFile().getAbsolutePath(),
            "  mkdir -p " + testProjectDir.resolve("build/install/share/doc/test_app").toFile().getAbsolutePath(),
            "  # Create mock installed files",
            "  touch " + testProjectDir.resolve("build/install/bin/test_app").toFile().getAbsolutePath(),
            "  cp " + testProjectDir.resolve("README.md").toFile().getAbsolutePath() + " " +
                      testProjectDir.resolve("build/install/share/doc/test_app").toFile().getAbsolutePath(),
            "elif [ \"$1\" = \"--build\" ]; then",
            "  mkdir -p " + testProjectDir.resolve("build/cmake").toFile().getAbsolutePath(),
            "  touch " + testProjectDir.resolve("build/cmake/test_app").toFile().getAbsolutePath(),
            "else",
            "  mkdir -p " + testProjectDir.resolve("build/cmake").toFile().getAbsolutePath(),
            "  touch " + testProjectDir.resolve("build/cmake/CMakeCache.txt").toFile().getAbsolutePath(),
            "  touch " + testProjectDir.resolve("build/cmake/build.ninja").toFile().getAbsolutePath(),
            "fi"
        ));
        mockCMakeScript.setExecutable(true);

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

        File installedDoc = new File(installDir, "share/doc/test_app/README.md");
        assertTrue(installedDoc.exists(), "Installed documentation should exist");

        // Verify that the mock CMake script was called with the expected arguments
        assertTrue(argsFile.exists(), "Arguments file should be created");

        String argsContent = Files.readString(argsFile.toPath());
        assertTrue(argsContent.contains("--install"), "Install command should be included in arguments");
        assertTrue(argsContent.contains("--prefix"), "Prefix argument should be included");
    }

    @Test
    public void testCMakeInstallWithCustomPrefix() throws IOException {
        // Create a mock CMake script for testing
        File mockCMakeScript = testProjectDir.resolve("mock-cmake.sh").toFile();
        File argsFile = testProjectDir.resolve("cmake-args.txt").toFile();

        Files.write(mockCMakeScript.toPath(), Arrays.asList(
            "#!/bin/sh",
            "echo \"Mock CMake called with: $@\" > " + argsFile.getAbsolutePath(),
            "if [ \"$1\" = \"--install\" ]; then",
            "  # Create custom install directory structure",
            "  mkdir -p " + testProjectDir.resolve("build/custom-install/bin").toFile().getAbsolutePath(),
            "  mkdir -p " + testProjectDir.resolve("build/custom-install/share/doc/test_app").toFile().getAbsolutePath(),
            "  # Create mock installed files",
            "  touch " + testProjectDir.resolve("build/custom-install/bin/test_app").toFile().getAbsolutePath(),
            "  cp " + testProjectDir.resolve("README.md").toFile().getAbsolutePath() + " " +
                      testProjectDir.resolve("build/custom-install/share/doc/test_app").toFile().getAbsolutePath(),
            "elif [ \"$1\" = \"--build\" ]; then",
            "  mkdir -p " + testProjectDir.resolve("build/cmake").toFile().getAbsolutePath(),
            "  touch " + testProjectDir.resolve("build/cmake/test_app").toFile().getAbsolutePath(),
            "else",
            "  mkdir -p " + testProjectDir.resolve("build/cmake").toFile().getAbsolutePath(),
            "  touch " + testProjectDir.resolve("build/cmake/CMakeCache.txt").toFile().getAbsolutePath(),
            "  touch " + testProjectDir.resolve("build/cmake/build.ninja").toFile().getAbsolutePath(),
            "fi"
        ));
        mockCMakeScript.setExecutable(true);

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

        File installedDoc = new File(customInstallDir, "share/doc/test_app/README.md");
        assertTrue(installedDoc.exists(), "Installed documentation should exist in custom directory");

        // Verify that the mock CMake script was called with the expected arguments
        assertTrue(argsFile.exists(), "Arguments file should be created");

        String argsContent = Files.readString(argsFile.toPath());
        assertTrue(argsContent.contains("--install"), "Install command should be included in arguments");
        assertTrue(argsContent.contains("--prefix"), "Prefix argument should be included");
        assertTrue(argsContent.contains("custom-install"), "Custom install directory should be included in arguments");
    }
}
