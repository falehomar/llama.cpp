package io.github.llama.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Task to run jextract and generate Java bindings for C header files.
 */
public abstract class JextractTask extends DefaultTask {

    /**
     * The path to the jextract executable.
     */
    @Input
    public abstract Property<String> getJextractPath();

    /**
     * The header file to process.
     */
    @InputFile
    public abstract RegularFileProperty getHeaderFile();

    /**
     * The target package for generated code.
     */
    @Input
    public abstract Property<String> getTargetPackage();

    /**
     * The header class name.
     */
    @Input
    @Optional
    public abstract Property<String> getHeaderClassName();

    /**
     * The output directory for generated code.
     */
    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    /**
     * Include paths for the header file.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludePaths();

    /**
     * Function patterns to include.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludeFunctions();

    /**
     * Struct patterns to include.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludeStructs();

    /**
     * Typedef patterns to include.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludeTypedefs();

    /**
     * Constant patterns to include.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludeConstants();

    /**
     * Variable patterns to include.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludeVars();

    /**
     * Union patterns to include.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludeUnions();

    @TaskAction
    public void generate() {
        getLogger().info("Generating Java bindings using jextract");

        List<String> args = new ArrayList<>();

        // Add output directory
        args.add("--output");
        args.add(getOutputDir().get().getAsFile().getAbsolutePath());

        // Add target package
        args.add("--target-package");
        args.add(getTargetPackage().get());

        // Add header class name if specified
        if (getHeaderClassName().isPresent()) {
            args.add("--header-class-name");
            args.add(getHeaderClassName().get());
        }

        // Add include functions if specified
        if (getIncludeFunctions().isPresent() && !getIncludeFunctions().get().isEmpty()) {
            for (String pattern : getIncludeFunctions().get()) {
                args.add("--include-function");
                args.add(pattern);
            }
        }

        // Add include structs if specified
        if (getIncludeStructs().isPresent() && !getIncludeStructs().get().isEmpty()) {
            for (String pattern : getIncludeStructs().get()) {
                args.add("--include-struct");
                args.add(pattern);
            }
        }

        // Add include typedefs if specified
        if (getIncludeTypedefs().isPresent() && !getIncludeTypedefs().get().isEmpty()) {
            for (String pattern : getIncludeTypedefs().get()) {
                args.add("--include-typedef");
                args.add(pattern);
            }
        }

        // Add include constants if specified
        if (getIncludeConstants().isPresent() && !getIncludeConstants().get().isEmpty()) {
            for (String pattern : getIncludeConstants().get()) {
                args.add("--include-constant");
                args.add(pattern);
            }
        }

        // Add include vars if specified
        if (getIncludeVars().isPresent() && !getIncludeVars().get().isEmpty()) {
            for (String pattern : getIncludeVars().get()) {
                args.add("--include-var");
                args.add(pattern);
            }
        }

        // Add include unions if specified
        if (getIncludeUnions().isPresent() && !getIncludeUnions().get().isEmpty()) {
            for (String pattern : getIncludeUnions().get()) {
                args.add("--include-union");
                args.add(pattern);
            }
        }

        // Add header file
        args.add(getHeaderFile().get().getAsFile().getAbsolutePath());

        // Add include paths if specified
        if (getIncludePaths().isPresent() && !getIncludePaths().get().isEmpty()) {
            for (String includePath : getIncludePaths().get()) {
                args.add("-I");
                args.add(includePath);
            }
        }

        // Execute jextract command
        getProject().exec(execSpec -> {
            execSpec.setExecutable(getJextractPath().get());
            execSpec.setArgs(args);
        });
    }
}
