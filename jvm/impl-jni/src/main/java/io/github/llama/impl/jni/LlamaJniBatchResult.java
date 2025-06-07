package io.github.llama.impl.jni;

import io.github.llama.api.batch.BatchResult;

/**
 * JNI implementation of the BatchResult interface for llama.cpp models.
 */
public class LlamaJniBatchResult implements BatchResult {
    private final int tokenCount;

    /**
     * Creates a new batch result.
     *
     * @param tokenCount Number of tokens processed
     */
    LlamaJniBatchResult(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    @Override
    public int getTokenCount() {
        return tokenCount;
    }
}
