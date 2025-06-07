package io.github.llama.impl.jni;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.spi.LLMFactory;

/**
 * Factory for creating JNI-based LLM instances.
 */
public class LlamaJniLLMFactory implements LLMFactory {
    private static final String TYPE = "llama.cpp";
    private static boolean initialized = false;

    /**
     * Initialize the llama.cpp backend.
     */
    public static synchronized void initialize() {
        if (!initialized) {
            LlamaJniBackend.llama_backend_init();
            initialized = true;
        }
    }

    /**
     * Clean up the llama.cpp backend.
     */
    public static synchronized void shutdown() {
        if (initialized) {
            LlamaJniBackend.llama_backend_free();
            initialized = false;
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public LLM create(String modelPath, ModelParams params) {
        initialize();
        return new LlamaJniModel(modelPath, params);
    }

    @Override
    public ModelParams getDefaultModelParams() {
        return LlamaJniBackend.llama_get_model_default_params();
    }
}
