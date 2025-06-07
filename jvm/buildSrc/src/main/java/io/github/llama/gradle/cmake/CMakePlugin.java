package io.github.llama.gradle.cmake;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;

import java.io.File;

/**
 * Plugin for CMake integration with Gradle.
 */
public class CMakePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // Create the extension for configuration
        CMakeExtension extension = project.getExtensions().create("cmake", CMakeExtension.class);

        // Set default values
        extension.getCmakePath().convention("cmake");
        extension.getGenerator().convention("Ninja");
        extension.getSourceDir().convention(project.getLayout().getProjectDirectory());

        // Default build directory is build/cmake
        Provider<Directory> defaultBuildDir = project.getLayout().getBuildDirectory().dir("cmake");
        extension.getBuildDir().convention(defaultBuildDir);

        // Default build type is Release
        extension.getBuildType().convention("Release");

        // Default parallel jobs is number of processors
        extension.getParallelJobs().convention(Runtime.getRuntime().availableProcessors());

        // Register the generate task
        project.getTasks().register("cmakeGenerate", CMakeGenerateTask.class, task -> {
            task.getCmakePath().set(extension.getCmakePath());
            task.getGenerator().set(extension.getGenerator());
            task.getSourceDir().set(extension.getSourceDir());
            task.getBuildDir().set(extension.getBuildDir());
            task.getArguments().set(extension.getArguments());
            task.getCacheVariables().set(extension.getCacheVariables());
            task.getBuildType().set(extension.getBuildType());
        });

        // Register the build task
        project.getTasks().register("cmakeBuild", CMakeBuildTask.class, task -> {
            task.getCmakePath().set(extension.getCmakePath());
            task.getBuildDir().set(extension.getBuildDir());
            task.getArguments().set(extension.getArguments());
            task.getParallelJobs().set(extension.getParallelJobs());
            task.getConfiguration().set(extension.getBuildType());

            // Make sure generate runs before build
            task.dependsOn(project.getTasks().getByName("cmakeGenerate"));
        });
    }
}
