package io.github.llama.impl.jni;

import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;

/**
 * JNI implementation of the Sampler interface for llama.cpp models.
 * Uses native llama.cpp sampling methods for better performance and consistency.
 */
public class LlamaJniSampler implements Sampler {
    private final LlamaJniContext context;
    private final SamplerParams params;
    private final long contextPtr;
    private final long seed;

    /**
     * Creates a new sampler for a context.
     *
     * @param context The context
     * @param params The sampler parameters
     */
    LlamaJniSampler(LlamaJniContext context, SamplerParams params) {
        this.context = context;
        this.params = params;
        this.contextPtr = context.getNativeContextPtr();
        this.seed = params.getSeed() != 0 ? params.getSeed() : System.currentTimeMillis();
    }

    @Override
    public int sample() {
        // Get logits from the context
        float[] logits = context.getLogits();

        // Apply temperature
        float temperature = params.getTemperature();
        if (temperature > 0) {
            logits = LlamaJniBackend.llama_sample_temperature(logits, temperature, contextPtr);
        }

        // Convert logits to probabilities using softmax
        float[] probabilities = LlamaJniBackend.llama_sample_softmax(logits);

        // Apply top-p sampling
        float topP = params.getTopP();
        if (topP < 1.0f) {
            int minKeep = 1;
            probabilities = LlamaJniBackend.llama_sample_top_p(probabilities, topP, minKeep, contextPtr);
        }

        // Apply top-k sampling
        int topK = params.getTopK();
        if (topK > 0 && topK < probabilities.length) {
            probabilities = LlamaJniBackend.llama_sample_top_k(probabilities, topK, contextPtr);
        }

        // Sample a token using the llama.cpp sampling function
        return LlamaJniBackend.llama_sample_token(probabilities, seed);
    }
}
