package io.github.llama.impl.jni;

import io.github.llama.api.LLM;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;

/**
 * JNI implementation of the Context interface for llama.cpp models.
 */
public class LlamaJniContext implements Context {
    private final LlamaJniModel model;
    private final long nativeContextPtr;
    private boolean closed = false;

    /**
     * Creates a new context for a model.
     *
     * @param model The model
     * @param params The context parameters
     */
    LlamaJniContext(LlamaJniModel model, ContextParams params) {
        this.model = model;
        this.nativeContextPtr = LlamaJniBackend.llama_context_create(model.getNativeModelPtr(), params);

        if (this.nativeContextPtr == 0) {
            throw new RuntimeException("Failed to create context");
        }
    }

    @Override
    public LLM getModel() {
        checkClosed();
        return model;
    }

    @Override
    public Batch createBatch(int maxTokens) {
        checkClosed();
        long nativeBatchPtr = LlamaJniBackend.llama_batch_create(maxTokens);
        if (nativeBatchPtr == 0) {
            throw new RuntimeException("Failed to create batch");
        }
        return new LlamaJniBatch(nativeBatchPtr, maxTokens);
    }

    @Override
    public BatchResult process(Batch batch) {
        checkClosed();

        if (!(batch instanceof LlamaJniBatch)) {
            throw new IllegalArgumentException("Batch must be created by this context");
        }

        LlamaJniBatch llamaBatch = (LlamaJniBatch) batch;
        int result = LlamaJniBackend.llama_decode(nativeContextPtr, llamaBatch.getNativeBatchPtr());

        if (result < 0) {
            throw new RuntimeException("Failed to process batch: error code " + result);
        }

        return new LlamaJniBatchResult(result);
    }

    @Override
    public float[] getLogits() {
        checkClosed();
        return LlamaJniBackend.llama_get_logits(nativeContextPtr);
    }

    @Override
    public Sampler createSampler(SamplerParams params) {
        checkClosed();
        return new LlamaJniSampler(this, params);
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            LlamaJniBackend.llama_context_free(nativeContextPtr);
            closed = true;
        }
    }

    /**
     * Gets the native pointer to the context.
     *
     * @return Native context pointer
     */
    long getNativeContextPtr() {
        checkClosed();
        return nativeContextPtr;
    }

    /**
     * Checks if the context is closed.
     *
     * @throws IllegalStateException if the context is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Context is closed");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
