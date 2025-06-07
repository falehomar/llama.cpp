package io.github.llama.impl.foreign;

import io.github.llama.api.LLM;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;

import java.lang.foreign.*;

/**
 * Implementation of the Context interface using the Java Foreign and Native Memory API with jextract-generated bindings.
 */
public class JextractForeignContext implements Context {
    private final JextractForeignLLM llm;
    private final MemorySegment contextHandle;
    private final ContextParams params;
    private boolean closed = false;
    private float[] logits;

    /**
     * Creates a new JextractForeignContext instance.
     *
     * @param llm The LLM instance
     * @param params Context parameters
     */
    public JextractForeignContext(JextractForeignLLM llm, ContextParams params) {
        this.llm = llm;
        this.params = params;
        this.contextHandle = createContext(llm.getModelHandle(), params);
        this.logits = new float[0];
    }

    /**
     * Creates a context from a model.
     *
     * @param modelHandle Memory segment containing the model handle
     * @param params Context parameters
     * @return Memory segment containing the context handle
     */
    private MemorySegment createContext(MemorySegment modelHandle, ContextParams params) {
        // Get default context parameters
        MemorySegment contextParams = JextractNativeLibrary.llamaContextDefaultParams();

        // Set parameters based on the ContextParams object
        try (Arena arena = Arena.ofConfined()) {
            // Set n_ctx
            int contextSize = params.getContextSize();
            contextParams.set(ValueLayout.JAVA_INT, 0, contextSize); // Offset 0 for n_ctx

            // Set n_batch
            int batchSize = params.getBatchSize();
            contextParams.set(ValueLayout.JAVA_INT, 4, batchSize); // Offset 4 for n_batch

            // Set n_threads
            int threadCount = params.getThreadCount();
            contextParams.set(ValueLayout.JAVA_INT, 16, threadCount); // Offset 16 for n_threads

            // Create the context
            return JextractNativeLibrary.llamaInitFromModel(modelHandle, contextParams);
        }
    }

    @Override
    public LLM getModel() {
        checkClosed();
        return llm;
    }

    @Override
    public Batch createBatch(int maxTokens) {
        checkClosed();
        return new ForeignBatch(maxTokens); // We'll keep using the existing batch for now
    }

    @Override
    public BatchResult process(Batch batch) {
        checkClosed();

        if (!(batch instanceof ForeignBatch)) {
            throw new IllegalArgumentException("Batch must be a ForeignBatch");
        }

        ForeignBatch foreignBatch = (ForeignBatch) batch;

        try {
            // TODO: Implement using jextract-generated bindings to call llama_decode

            // Update logits
            updateLogits();

            return BatchResult.success();
        } catch (Exception e) {
            return BatchResult.failure(e.getMessage());
        }
    }

    /**
     * Updates the logits array with the latest values from the context.
     */
    private void updateLogits() {
        // TODO: Implement using jextract-generated bindings to call llama_get_logits
    }

    @Override
    public float[] getLogits() {
        checkClosed();
        return logits.clone();
    }

    @Override
    public Sampler createSampler(SamplerParams params) {
        checkClosed();
        return new ForeignSampler(contextHandle, params); // We'll keep using the existing sampler for now
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        // Free the context
        try {
            if (contextHandle != MemorySegment.NULL) {
                freeContext(contextHandle);
            }
        } catch (Exception e) {
            // Log the error but continue with cleanup
            System.err.println("Error freeing context: " + e.getMessage());
        }

        closed = true;
    }

    /**
     * Frees a context.
     *
     * @param contextHandle Memory segment containing the context handle
     */
    private void freeContext(MemorySegment contextHandle) {
        JextractNativeLibrary.llamaFree(contextHandle);
    }

    /**
     * Gets the context handle.
     *
     * @return Memory segment containing the context handle
     */
    MemorySegment getContextHandle() {
        return contextHandle;
    }

    /**
     * Checks if the context is closed.
     *
     * @throws IllegalStateException If the context is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Context is closed");
        }
    }
}
