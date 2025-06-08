package io.github.llama.gradle.jextract;

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

        // Default debug value is false
        extension.getDebug().convention(false);

        // Register the jextract task
        project.getTasks().register("jextract", JextractTask.class, task -> {
            // Set the task group
            task.setGroup("jextract");

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

            // Configure new options
            task.getSymbolsClassName().set(extension.getSymbolsClassName());
            task.getDefineMacros().set(extension.getDefineMacros());
            task.getDumpIncludesFile().set(extension.getDumpIncludesFile());
            task.getLibraries().set(extension.getLibraries());
            task.getUseSystemLoadLibrary().set(extension.getUseSystemLoadLibrary());
            task.getFrameworkDirs().set(extension.getFrameworkDirs());
            task.getFrameworks().set(extension.getFrameworks());
            task.getDebug().set(extension.getDebug());
        });

        // Register the dump-includes task
        project.getTasks().register("dump-includes", JextractDumpIncludesTask.class, task -> {
            // Set the task group
            task.setGroup("jextract");

            task.getJextractPath().set(extension.getJextractPath());
            task.getHeaderFile().set(extension.getHeaderFile());

            // Set a convention for dumpIncludesFile based on the header file name
            // This will only be used if dumpIncludesFile is not explicitly set
            task.getDumpIncludesFile().convention(
                project.provider(() -> {
                    if (extension.getDumpIncludesFile().isPresent()) {
                        return extension.getDumpIncludesFile().get();
                    } else if (extension.getHeaderFile().isPresent()) {
                        String headerFileName = extension.getHeaderFile().get().getAsFile().getName();
                        String baseName = headerFileName.contains(".")
                            ? headerFileName.substring(0, headerFileName.lastIndexOf('.'))
                            : headerFileName;
                        return project.getLayout().getBuildDirectory()
                            .file(baseName + ".includes").get();
                    } else {
                        return null;
                    }
                })
            );

            task.getIncludePaths().set(extension.getIncludePaths());
            task.getDefineMacros().set(extension.getDefineMacros());
            task.getFrameworkDirs().set(extension.getFrameworkDirs());
            task.getDebug().set(extension.getDebug());
        });

        // Add the generated sources to the main source set
        project.afterEvaluate(p -> {
            SourceSetContainer sourceSets = p.getExtensions().getByType(SourceSetContainer.class);
            SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);

            // Add the generated sources directory to the main source set
            mainSourceSet.getJava().srcDir(extension.getOutputDir());

            // Make sure the jextract task runs before compiling
            p.getTasks().getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
                    .dependsOn(p.getTasks().getByName("jextract"));
        });
    }
}
