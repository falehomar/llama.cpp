package io.github.llama.impl.jni;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.tokenization.Tokenizer;

/**
 * JNI implementation of the LLM interface for llama.cpp models.
 */
public class LlamaJniModel implements LLM {
    // Native pointer to the llama model
    private final long nativeModelPtr;
    private final Tokenizer tokenizer;
    private boolean closed = false;

    /**
     * Creates a new Llama model using JNI.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     */
    public LlamaJniModel(String modelPath, ModelParams params) {
        // Initialize the backend if not already done
        LlamaJniBackend.llama_backend_init();

        // Load the model
        this.nativeModelPtr = LlamaJniBackend.llama_model_load_from_file(modelPath, params);
        if (this.nativeModelPtr == 0) {
            throw new RuntimeException("Failed to load model: " + modelPath);
        }

        // Create the tokenizer
        this.tokenizer = new LlamaJniTokenizer(this.nativeModelPtr);
    }

    @Override
    public ModelInfo getModelInfo() {
        checkClosed();
        return LlamaJniBackend.llama_get_model_info(nativeModelPtr);
    }

    @Override
    public Context createContext(ContextParams params) {
        checkClosed();
        return new LlamaJniContext(this, params);
    }

    @Override
    public Tokenizer getTokenizer() {
        checkClosed();
        return tokenizer;
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            LlamaJniBackend.llama_model_free(nativeModelPtr);
            closed = true;
        }
    }

    /**
     * Gets the native pointer to the model.
     *
     * @return Native model pointer
     */
    long getNativeModelPtr() {
        checkClosed();
        return nativeModelPtr;
    }

    /**
     * Checks if the model is closed.
     *
     * @throws IllegalStateException if the model is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Model is closed");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
