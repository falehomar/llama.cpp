package io.github.llama.impl.ffm;

import io.github.llama.api.LLM;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;
import io.github.llama.impl.llamacpp.ffm.llama_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

/**
 * Implementation of the Context interface using the Foreign Function & Memory API.
 */
public class ForeignContext implements Context {

    // Native context pointer
    private final MemorySegment nativeContext;

    // Model
    private final LLM model;

    // Context parameters
    private final ContextParams params;

    /**
     * Creates a new ForeignContext.
     *
     * @param model  The model
     * @param params Context parameters
     * @throws IllegalArgumentException If model or params is null
     */
    public ForeignContext(LLM model, ContextParams params) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("Context parameters cannot be null");
        }

        this.model = model;
        this.params = params;

        // Get the native model pointer
        MemorySegment nativeModel = ((ForeignModel) model).getNativeModel();

        // Create native context parameters
        try (Arena arena = Arena.ofConfined()) {
            // Get default context parameters
            MemorySegment nativeParams = llama_h.llama_context_default_params();

            // Set context parameters
            nativeParams.set(llama_h.C_INT, llama_h.llama_context_params.n_ctx$offset(), params.getContextSize());
            nativeParams.set(llama_h.C_INT, llama_h.llama_context_params.n_batch$offset(), params.getBatchSize());
            nativeParams.set(llama_h.C_INT, llama_h.llama_context_params.n_threads$offset(), params.getThreadCount());
            nativeParams.set(llama_h.C_BOOL, llama_h.llama_context_params.logits_all$offset(), params.isLogitsAll());

            // Create native context
            this.nativeContext = llama_h.llama_init_from_model(nativeModel, nativeParams);

            // Check if context creation failed
            if (this.nativeContext.equals(MemorySegment.NULL)) {
                throw new RuntimeException("Failed to create context");
            }
        }
    }

    /**
     * Gets the native context pointer.
     *
     * @return Native context pointer
     */
    public MemorySegment getNativeContext() {
        return nativeContext;
    }

    @Override
    public LLM getModel() {
        return model;
    }

    @Override
    public Batch createBatch(int maxTokens) {
        if (maxTokens <= 0) {
            throw new IllegalArgumentException("Max tokens must be positive");
        }

        return new ForeignBatch(maxTokens);
    }

    @Override
    public BatchResult process(Batch batch) {
        if (batch == null) {
            throw new IllegalArgumentException("Batch cannot be null");
        }

        // Cast to ForeignBatch
        ForeignBatch foreignBatch = (ForeignBatch) batch;

        // Get native batch
        MemorySegment nativeBatch = foreignBatch.getNativeBatch();

        // Process batch
        int result = llama_h.llama_decode(nativeContext, nativeBatch);

        // Check if processing failed
        if (result != 0) {
            throw new RuntimeException("Failed to process batch");
        }

        // Return batch result
        return result == 0 ? BatchResult.success() : BatchResult.failure("Error code: " + result);
    }

    @Override
    public float[] getLogits() {
        // Get logits
        MemorySegment logitsSegment = llama_h.llama_get_logits(nativeContext);

        // Check if logits retrieval failed
        if (logitsSegment.equals(MemorySegment.NULL)) {
            throw new RuntimeException("Failed to get logits");
        }

        // Get vocabulary size
        int vocabSize = llama_h.llama_n_vocab(((ForeignModel) model).getNativeModel());

        // Copy logits to Java array
        float[] logits = new float[vocabSize];
        for (int i = 0; i < vocabSize; i++) {
            logits[i] = logitsSegment.getAtIndex(ValueLayout.JAVA_FLOAT, i);
        }

        return logits;
    }

    @Override
    public Sampler createSampler(SamplerParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Sampler parameters cannot be null");
        }

        return new ForeignSampler(this, params);
    }

    @Override
    public void close() {
        // Free the context
        llama_h.llama_free(nativeContext);
    }

    /**
     * Implementation of the Batch interface using the Foreign Function & Memory API.
     */
    private static class ForeignBatch implements Batch {
        private final MemorySegment nativeBatch;
        private final int maxTokens;
        private int tokenCount;

        /**
         * Creates a new ForeignBatch.
         *
         * @param maxTokens Maximum number of tokens in the batch
         */
        public ForeignBatch(int maxTokens) {
            this.maxTokens = maxTokens;
            this.tokenCount = 0;

            // Create native batch
            this.nativeBatch = llama_h.llama_batch_init(maxTokens, 0, 1);

            // Check if batch creation failed
            if (this.nativeBatch.equals(MemorySegment.NULL)) {
                throw new RuntimeException("Failed to create batch");
            }
        }

        /**
         * Gets the native batch pointer.
         *
         * @return Native batch pointer
         */
        public MemorySegment getNativeBatch() {
            return nativeBatch;
        }

        @Override
        public Batch addToken(int tokenId) {
            if (tokenCount >= maxTokens) {
                throw new IllegalStateException("Batch is full");
            }

            // Add token to batch
            llama_h.llama_batch_add(nativeBatch, tokenId, 0, new int[]{0}, false);
            tokenCount++;

            return this;
        }

        @Override
        public Batch addTokens(int[] tokenIds) {
            if (tokenIds == null) {
                throw new IllegalArgumentException("Token IDs cannot be null");
            }

            if (tokenCount + tokenIds.length > maxTokens) {
                throw new IllegalStateException("Batch would overflow");
            }

            // Add tokens to batch
            for (int tokenId : tokenIds) {
                addToken(tokenId);
            }

            return this;
        }

        @Override
        public int getTokenCount() {
            return tokenCount;
        }

        @Override
        public int getMaxTokenCount() {
            return maxTokens;
        }

        @Override
        public Batch clear() {
            // Clear batch
            llama_h.llama_batch_clear(nativeBatch);
            tokenCount = 0;

            return this;
        }

        @Override
        public void close() {
            // Free the batch
            llama_h.llama_batch_free(nativeBatch);
        }
    }

    // BatchResult is a concrete class, so we use it directly

    /**
     * Implementation of the Sampler interface using the Foreign Function & Memory API.
     */
    private static class ForeignSampler implements Sampler {
        private final ForeignContext context;
        private final SamplerParams params;

        /**
         * Creates a new ForeignSampler.
         *
         * @param context The context
         * @param params  Sampler parameters
         */
        public ForeignSampler(ForeignContext context, SamplerParams params) {
            this.context = context;
            this.params = params;
        }

        @Override
        public int sample(float[] logits) {
            if (logits == null) {
                throw new IllegalArgumentException("Logits cannot be null");
            }

            // TODO: Implement sampling using llama_sampling_* functions
            // This requires adding the sampling functions to the jextract configuration

            // For now, just return the token with the highest probability
            int maxIndex = 0;
            float maxValue = Float.NEGATIVE_INFINITY;

            for (int i = 0; i < logits.length; i++) {
                if (logits[i] > maxValue) {
                    maxValue = logits[i];
                    maxIndex = i;
                }
            }

            return maxIndex;
        }

        @Override
        public void close() {
            // Nothing to close
        }
    }
}
