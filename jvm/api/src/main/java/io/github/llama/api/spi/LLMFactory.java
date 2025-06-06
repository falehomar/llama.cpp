package io.github.llama.api.spi;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelParams;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for creating LLM instances.
 */
public interface LLMFactory {
    /**
     * Gets the name of this factory.
     *
     * @return Factory name
     */
    String getName();

    /**
     * Creates an LLM from a model file.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @return A new LLM instance
     * @throws IOException If the model cannot be loaded
     */
    LLM createLLM(Path modelPath, ModelParams params) throws IOException;

    /**
     * Checks if this factory supports the given model file.
     *
     * @param modelPath Path to the model file
     * @return True if the factory supports the model, false otherwise
     */
    boolean supportsModel(Path modelPath);
}
