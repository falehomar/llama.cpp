package io.github.llama.api.model;

import io.github.llama.api.tokenization.Tokenizer;

/**
 * Interface representing a loaded model.
 * This interface provides methods for accessing model information and resources.
 */
public interface Model extends AutoCloseable {

    /**
     * Gets information about the model.
     *
     * @return Model information
     */
    ModelInfo getModelInfo();

    /**
     * Gets the tokenizer for this model.
     *
     * @return The tokenizer
     */
    Tokenizer getTokenizer();

    /**
     * Closes the model and releases resources.
     */
    @Override
    void close();
}
