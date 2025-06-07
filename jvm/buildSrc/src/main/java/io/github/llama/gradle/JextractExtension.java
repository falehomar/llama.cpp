package io.github.llama.gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
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
}
