package io.github.llama.gradle.cmake;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

/**
 * Extension for configuring CMake build.
 */
public abstract class CMakeExtension {
    /**
     * The path to the CMake executable.
     */
    public abstract Property<String> getCmakePath();

    /**
     * The CMake generator to use (default: Ninja).
     */
    public abstract Property<String> getGenerator();

    /**
     * The source directory containing CMakeLists.txt.
     */
    public abstract DirectoryProperty getSourceDir();

    /**
     * The build directory where CMake will generate build files.
     */
    public abstract DirectoryProperty getBuildDir();

    /**
     * Additional CMake arguments.
     */
    public abstract ListProperty<String> getArguments();

    /**
     * CMake cache variables (key-value pairs).
     */
    public abstract MapProperty<String, String> getCacheVariables();

    /**
     * Build type (Debug, Release, RelWithDebInfo, MinSizeRel).
     */
    public abstract Property<String> getBuildType();

    /**
     * Number of parallel jobs for build (default: number of processors).
     */
    public abstract Property<Integer> getParallelJobs();
}
