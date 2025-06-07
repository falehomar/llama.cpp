package io.github.llama.gradle.cmake;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Task to run CMake to generate build files.
 */
public abstract class CMakeGenerateTask extends DefaultTask {

    /**
     * The path to the CMake executable.
     */
    @Input
    public abstract Property<String> getCmakePath();

    /**
     * The CMake generator to use.
     */
    @Input
    public abstract Property<String> getGenerator();

    /**
     * The source directory containing CMakeLists.txt.
     */
    @InputDirectory
    public abstract DirectoryProperty getSourceDir();

    /**
     * The build directory where CMake will generate build files.
     */
    @OutputDirectory
    public abstract DirectoryProperty getBuildDir();

    /**
     * Additional CMake arguments.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getArguments();

    /**
     * CMake cache variables (key-value pairs).
     */
    @Input
    @Optional
    public abstract MapProperty<String, String> getCacheVariables();

    /**
     * Build type (Debug, Release, RelWithDebInfo, MinSizeRel).
     */
    @Input
    @Optional
    public abstract Property<String> getBuildType();

    @TaskAction
    public void generate() {
        getLogger().info("Generating CMake build files");

        // Create build directory if it doesn't exist
        File buildDir = getBuildDir().get().getAsFile();
        if (!buildDir.exists()) {
            buildDir.mkdirs();
        }

        List<String> args = new ArrayList<>();

        // Add generator
        args.add("-G");
        args.add(getGenerator().get());

        // Add build type if specified
        if (getBuildType().isPresent()) {
            args.add("-DCMAKE_BUILD_TYPE=" + getBuildType().get());
        }

        // Add cache variables if specified
        if (getCacheVariables().isPresent()) {
            Map<String, String> cacheVars = getCacheVariables().get();
            for (Map.Entry<String, String> entry : cacheVars.entrySet()) {
                args.add("-D" + entry.getKey() + "=" + entry.getValue());
            }
        }

        // Add additional arguments if specified
        if (getArguments().isPresent() && !getArguments().get().isEmpty()) {
            args.addAll(getArguments().get());
        }

        // Add source directory
        args.add(getSourceDir().get().getAsFile().getAbsolutePath());

        // Execute CMake command
        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(buildDir);
            execSpec.setExecutable(getCmakePath().get());
            execSpec.setArgs(args);
        });

        getLogger().info("CMake generation completed");
    }
}
