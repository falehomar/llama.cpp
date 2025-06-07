package io.github.llama.gradle.jextract;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

/**
 * Extension for configuring jextract code generation.
 */
public abstract class JextractExtension {
    /**
     * The path to the jextract executable.
     */
    public abstract Property<String> getJextractPath();

    /**
     * The header file to process.
     */
    public abstract RegularFileProperty getHeaderFile();

    /**
     * The target package for generated code.
     */
    public abstract Property<String> getTargetPackage();

    /**
     * The header class name.
     */
    public abstract Property<String> getHeaderClassName();

    /**
     * The output directory for generated code.
     */
    public abstract DirectoryProperty getOutputDir();

    /**
     * Include paths for the header file.
     */
    public abstract ListProperty<String> getIncludePaths();

    /**
     * Function patterns to include.
     */
    public abstract ListProperty<String> getIncludeFunctions();

    /**
     * Struct patterns to include.
     */
    public abstract ListProperty<String> getIncludeStructs();

    /**
     * Typedef patterns to include.
     */
    public abstract ListProperty<String> getIncludeTypedefs();

    /**
     * Constant patterns to include.
     */
    public abstract ListProperty<String> getIncludeConstants();

    /**
     * Variable patterns to include.
     */
    public abstract ListProperty<String> getIncludeVars();

    /**
     * Union patterns to include.
     */
    public abstract ListProperty<String> getIncludeUnions();

    /**
     * The symbols class name.
     */
    public abstract Property<String> getSymbolsClassName();

    /**
     * Macro definitions (key-value pairs).
     */
    public abstract MapProperty<String, String> getDefineMacros();

    /**
     * File to dump included symbols into.
     */
    public abstract RegularFileProperty getDumpIncludesFile();

    /**
     * Libraries to load.
     */
    public abstract ListProperty<String> getLibraries();

    /**
     * Whether to use System.loadLibrary/System.load for loading libraries.
     */
    public abstract Property<Boolean> getUseSystemLoadLibrary();

    /**
     * Framework directories (macOS only).
     */
    public abstract ListProperty<String> getFrameworkDirs();

    /**
     * Frameworks to load (macOS only).
     */
    public abstract ListProperty<String> getFrameworks();

    /**
     * Whether to print debug information regardless of the log level.
     */
    public abstract Property<Boolean> getDebug();
}
