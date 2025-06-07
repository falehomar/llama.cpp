package io.github.llama.gradle.cmake;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to run CMake build.
 */
public abstract class CMakeBuildTask extends DefaultTask {

    /**
     * The path to the CMake executable.
     */
    @Input
    public abstract Property<String> getCmakePath();

    /**
     * The build directory where CMake generated build files.
     */
    @InputDirectory
    public abstract DirectoryProperty getBuildDir();

    /**
     * Additional CMake build arguments.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getArguments();

    /**
     * Number of parallel jobs for build.
     */
    @Input
    @Optional
    public abstract Property<Integer> getParallelJobs();

    /**
     * Build target (default is empty, which builds the default target).
     */
    @Input
    @Optional
    public abstract Property<String> getTarget();

    /**
     * Build configuration (Debug, Release, etc.).
     */
    @Input
    @Optional
    public abstract Property<String> getConfiguration();

    @TaskAction
    public void build() {
        getLogger().info("Building CMake project");

        List<String> args = new ArrayList<>();
        args.add("--build");
        args.add(".");

        // Add configuration if specified
        if (getConfiguration().isPresent()) {
            args.add("--config");
            args.add(getConfiguration().get());
        }

        // Add parallel jobs if specified
        if (getParallelJobs().isPresent()) {
            args.add("--parallel");
            args.add(getParallelJobs().get().toString());
        }

        // Add target if specified
        if (getTarget().isPresent()) {
            args.add("--target");
            args.add(getTarget().get());
        }

        // Add additional arguments if specified
        if (getArguments().isPresent() && !getArguments().get().isEmpty()) {
            args.addAll(getArguments().get());
        }

        // Execute CMake build command
        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(getBuildDir().get().getAsFile());
            execSpec.setExecutable(getCmakePath().get());
            execSpec.setArgs(args);
        });

        getLogger().info("CMake build completed");
    }
}
