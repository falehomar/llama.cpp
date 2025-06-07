package io.github.llama.impl.foreign;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.spi.LLMFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Factory for creating JextractForeignLLM instances.
 * This factory uses jextract-generated bindings to interact with the native library.
 */
public class JextractForeignLLMFactory implements LLMFactory {
    /**
     * Checks if the factory supports the given model.
     *
     * @param modelPath Path to the model file
     * @return Whether the factory supports the model
     */
    @Override
    public boolean supportsModel(Path modelPath) {
        // Check if the model file exists
        if (!Files.exists(modelPath)) {
            return false;
        }

        // Check if the model file is a regular file
        if (!Files.isRegularFile(modelPath)) {
            return false;
        }

        // Check if the model file is readable
        if (!Files.isReadable(modelPath)) {
            return false;
        }

        // Check if the model file has a supported extension
        String fileName = modelPath.getFileName().toString().toLowerCase();
        return fileName.endsWith(".gguf");
    }

    /**
     * Creates a new LLM instance for the given model.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @return A new LLM instance
     * @throws IOException If the model cannot be loaded
     */
    @Override
    public LLM createLLM(Path modelPath, ModelParams params) throws IOException {
        return new JextractForeignLLM(modelPath, params);
    }

    /**
     * Gets the name of the factory.
     *
     * @return The name of the factory
     */
    @Override
    public String getName() {
        return "jextract-foreign";
    }

}
