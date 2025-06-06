package io.github.llama.api;

import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.tokenization.Tokenizer;

/**
 * The main interface representing a large language model.
 */
public interface LLM extends AutoCloseable {
    /**
     * Gets information about the model.
     *
     * @return Model information
     */
    ModelInfo getModelInfo();

    /**
     * Creates a new context for inference.
     *
     * @param params Context parameters
     * @return A new context
     */
    Context createContext(ContextParams params);

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
