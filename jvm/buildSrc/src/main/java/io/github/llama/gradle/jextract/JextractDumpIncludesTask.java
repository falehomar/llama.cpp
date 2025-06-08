package io.github.llama.gradle.jextract;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Task to run jextract to dump all symbols encountered in a header file.
 * This dump can be manipulated and then used as an argument file for filtering symbols.
 */
public abstract class JextractDumpIncludesTask extends DefaultTask {

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
     * File to dump included symbols into.
     * If not specified, it will be derived from the header file name.
     */
    @OutputFile
    @Optional
    public abstract RegularFileProperty getDumpIncludesFile();

    /**
     * Include paths for the header file.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getIncludePaths();

    /**
     * Macro definitions (key-value pairs).
     */
    @Input
    @Optional
    public abstract MapProperty<String, String> getDefineMacros();

    /**
     * Framework directories (macOS only).
     */
    @Input
    @Optional
    public abstract ListProperty<String> getFrameworkDirs();

    /**
     * Whether to print debug information regardless of the log level.
     */
    @Input
    @Optional
    public abstract Property<Boolean> getDebug();


    @TaskAction
    public void generate() {
        getLogger().info("Dumping symbols from header file using jextract");

        // Check if the output file already exists
        if (getDumpIncludesFile().get().getAsFile().exists()) {
            throw new RuntimeException("Test verification: Output file already exists: " + getDumpIncludesFile().get().getAsFile().getAbsolutePath());
        }

        List<String> args = new ArrayList<>();

        // Add dump includes file
        args.add("--dump-includes");
        args.add(getDumpIncludesFile().get().getAsFile().getAbsolutePath());

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

        // Add framework dirs if specified (macOS only)
        if (getFrameworkDirs().isPresent() && !getFrameworkDirs().get().isEmpty()) {
            for (String frameworkDir : getFrameworkDirs().get()) {
                args.add("-F");
                args.add(frameworkDir);
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
            System.out.println("Executing jextract dump-includes command: " + fullCommand);
        } else {
            // Otherwise, use the standard logger at lifecycle level
            getLogger().lifecycle("Executing jextract dump-includes command: {}", fullCommand);
        }

        // Execute jextract command
        getProject().exec(execSpec -> {
            execSpec.setExecutable(getJextractPath().get());
            execSpec.setArgs(args);
        });
    }
}
