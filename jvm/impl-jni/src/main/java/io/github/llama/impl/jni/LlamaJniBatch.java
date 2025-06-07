package io.github.llama.impl.jni;

import io.github.llama.api.batch.Batch;

/**
 * JNI implementation of the Batch interface for llama.cpp models.
 */
public class LlamaJniBatch implements Batch {
    private long nativeBatchPtr;
    private final int maxTokens;
    private int tokenCount = 0;
    private boolean closed = false;

    /**
     * Creates a new batch with a native pointer.
     *
     * @param nativeBatchPtr Native batch pointer
     * @param maxTokens Maximum number of tokens in the batch
     */
    LlamaJniBatch(long nativeBatchPtr, int maxTokens) {
        this.nativeBatchPtr = nativeBatchPtr;
        this.maxTokens = maxTokens;
    }

    @Override
    public boolean addToken(int token, int position) {
        checkClosed();

        if (tokenCount >= maxTokens) {
            return false;
        }

        boolean success = LlamaJniBackend.llama_batch_add(nativeBatchPtr, token, position);
        if (success) {
            tokenCount++;
        }

        return success;
    }

    @Override
    public int getTokenCount() {
        checkClosed();
        return tokenCount;
    }

    @Override
    public int getCapacity() {
        checkClosed();
        return maxTokens;
    }

    @Override
    public void clear() {
        checkClosed();
        // Create a new batch with the same capacity
        long newBatchPtr = LlamaJniBackend.llama_batch_create(maxTokens);

        if (newBatchPtr == 0) {
            throw new RuntimeException("Failed to create replacement batch");
        }

        // Free the old batch
        LlamaJniBackend.llama_batch_free(nativeBatchPtr);

        // Update the fields
        this.nativeBatchPtr = newBatchPtr;
        this.tokenCount = 0;
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            LlamaJniBackend.llama_batch_free(nativeBatchPtr);
            closed = true;
        }
    }

    /**
     * Gets the native pointer to the batch.
     *
     * @return Native batch pointer
     */
    long getNativeBatchPtr() {
        checkClosed();
        return nativeBatchPtr;
    }

    /**
     * Checks if the batch is closed.
     *
     * @throws IllegalStateException if the batch is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Batch is closed");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
