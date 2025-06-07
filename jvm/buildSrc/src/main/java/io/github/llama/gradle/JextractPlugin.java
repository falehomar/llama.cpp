package io.github.llama.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;

/**
 * Plugin for generating Java bindings for C header files using jextract.
 */
public class JextractPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // Apply the Java plugin if it's not already applied
        project.getPlugins().apply(JavaPlugin.class);

        // Create the extension for configuration
        JextractExtension extension = project.getExtensions().create("jextract", JextractExtension.class);

        // Set default values
        extension.getJextractPath().convention(project.getRootProject().getProjectDir().toPath()
                .resolve("jextract/build/jextract/bin/jextract").toString());

        // Default output directory is build/generated/jextract
        Provider<Directory> defaultOutputDir = project.getLayout().getBuildDirectory().dir("generated/jextract");
        extension.getOutputDir().convention(defaultOutputDir);

        // Register the task
        project.getTasks().register("generateJextract", JextractTask.class, task -> {
            task.getJextractPath().set(extension.getJextractPath());
            task.getHeaderFile().set(extension.getHeaderFile());
            task.getTargetPackage().set(extension.getTargetPackage());
            task.getHeaderClassName().set(extension.getHeaderClassName());
            task.getOutputDir().set(extension.getOutputDir());
            task.getIncludePaths().set(extension.getIncludePaths());
            task.getIncludeFunctions().set(extension.getIncludeFunctions());
            task.getIncludeStructs().set(extension.getIncludeStructs());
            task.getIncludeTypedefs().set(extension.getIncludeTypedefs());
            task.getIncludeConstants().set(extension.getIncludeConstants());
            task.getIncludeVars().set(extension.getIncludeVars());
            task.getIncludeUnions().set(extension.getIncludeUnions());
        });

        // Add the generated sources to the main source set
        project.afterEvaluate(p -> {
            SourceSetContainer sourceSets = p.getExtensions().getByType(SourceSetContainer.class);
            SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);

            // Add the generated sources directory to the main source set
            mainSourceSet.getJava().srcDir(extension.getOutputDir());

            // Make sure the generateJextract task runs before compiling
            p.getTasks().getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
                    .dependsOn(p.getTasks().getByName("generateJextract"));
        });
    }
}
