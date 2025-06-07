package io.github.llama.gradle.cmake;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to run CMake install.
 */
public abstract class CMakeInstallTask extends DefaultTask {

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
     * The directory where CMake will install the built artifacts.
     */
    @OutputDirectory
    public abstract DirectoryProperty getInstallDir();

    /**
     * Additional CMake install arguments.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getArguments();

    /**
     * Build configuration to install (Debug, Release, etc.).
     */
    @Input
    @Optional
    public abstract Property<String> getConfiguration();

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

        // Add configuration if specified
        if (getConfiguration().isPresent()) {
            args.add("--config");
            args.add(getConfiguration().get());
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
