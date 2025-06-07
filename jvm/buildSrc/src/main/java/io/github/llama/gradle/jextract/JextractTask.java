package io.github.llama.gradle.jextract;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * The symbols class name.
     */
    @Input
    @Optional
    public abstract Property<String> getSymbolsClassName();

    /**
     * Macro definitions (key-value pairs).
     */
    @Input
    @Optional
    public abstract MapProperty<String, String> getDefineMacros();

    /**
     * File to dump included symbols into.
     */
    @OutputFile
    @Optional
    public abstract RegularFileProperty getDumpIncludesFile();

    /**
     * Libraries to load.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getLibraries();

    /**
     * Whether to use System.loadLibrary/System.load for loading libraries.
     */
    @Input
    @Optional
    public abstract Property<Boolean> getUseSystemLoadLibrary();

    /**
     * Framework directories (macOS only).
     */
    @Input
    @Optional
    public abstract ListProperty<String> getFrameworkDirs();

    /**
     * Frameworks to load (macOS only).
     */
    @Input
    @Optional
    public abstract ListProperty<String> getFrameworks();

    /**
     * Whether to print debug information regardless of the log level.
     */
    @Input
    @Optional
    public abstract Property<Boolean> getDebug();

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

        // Add symbols class name if specified
        if (getSymbolsClassName().isPresent()) {
            args.add("--symbols-class-name");
            args.add(getSymbolsClassName().get());
        }

        // Add dump includes file if specified
        if (getDumpIncludesFile().isPresent()) {
            args.add("--dump-includes");
            args.add(getDumpIncludesFile().get().getAsFile().getAbsolutePath());
        }

        // Add use system load library if specified and true
        if (getUseSystemLoadLibrary().isPresent() && getUseSystemLoadLibrary().get()) {
            args.add("--use-system-load-library");
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

        // Add define macros if specified
        if (getDefineMacros().isPresent() && !getDefineMacros().get().isEmpty()) {
            Map<String, String> macros = getDefineMacros().get();
            for (Map.Entry<String, String> entry : macros.entrySet()) {
                args.add("-D");
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    args.add(entry.getKey());
                } else {
                    args.add(entry.getKey() + "=" + entry.getValue());
                }
            }
        }

        // Add libraries if specified
        if (getLibraries().isPresent() && !getLibraries().get().isEmpty()) {
            for (String library : getLibraries().get()) {
                args.add("-l");
                args.add(library);
            }
        }

        // Add framework dirs if specified (macOS only)
        if (getFrameworkDirs().isPresent() && !getFrameworkDirs().get().isEmpty()) {
            for (String frameworkDir : getFrameworkDirs().get()) {
                args.add("-F");
                args.add(frameworkDir);
            }
        }

        // Add frameworks if specified (macOS only)
        if (getFrameworks().isPresent() && !getFrameworks().get().isEmpty()) {
            for (String framework : getFrameworks().get()) {
                args.add("--framework");
                args.add(framework);
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

        // Build the full command for logging
        StringBuilder commandBuilder = new StringBuilder(getJextractPath().get());
        for (String arg : args) {
            commandBuilder.append(" ").append(arg);
        }
        String fullCommand = commandBuilder.toString();

        // Log the command being executed
        if (getDebug().isPresent() && getDebug().get()) {
            // If debug is enabled, print the command regardless of log level
            System.out.println("Executing jextract command: " + fullCommand);
        } else {
            // Otherwise, use the standard logger at lifecycle level
            getLogger().lifecycle("Executing jextract command: {}", fullCommand);
        }

        // Execute jextract command
        getProject().exec(execSpec -> {
            execSpec.setExecutable(getJextractPath().get());
            execSpec.setArgs(args);
        });
    }
}
