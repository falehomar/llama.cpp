package io.github.llama.api.context;

/**
 * Class representing parameters for context creation.
 */
public class ContextParams {
    private int contextSize = 512;
    private int batchSize = 512;
    private int threadCount = 4;
    private boolean logitsAll = false;

    /**
     * Gets the context size.
     *
     * @return Context size
     */
    public int getContextSize() {
        return contextSize;
    }

    /**
     * Sets the context size.
     *
     * @param contextSize Context size
     */
    public void setContextSize(int contextSize) {
        this.contextSize = contextSize;
    }

    /**
     * Gets the batch size.
     *
     * @return Batch size
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Sets the batch size.
     *
     * @param batchSize Batch size
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Gets the thread count.
     *
     * @return Thread count
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * Sets the thread count.
     *
     * @param threadCount Thread count
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * Gets whether to compute logits for all tokens.
     *
     * @return Whether to compute logits for all tokens
     */
    public boolean isLogitsAll() {
        return logitsAll;
    }

    /**
     * Sets whether to compute logits for all tokens.
     *
     * @param logitsAll Whether to compute logits for all tokens
     */
    public void setLogitsAll(boolean logitsAll) {
        this.logitsAll = logitsAll;
    }

    /**
     * Creates a new builder for ContextParams.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ContextParams.
     */
    public static class Builder {
        private final ContextParams params = new ContextParams();

        /**
         * Sets the context size.
         *
         * @param contextSize Context size
         * @return This builder for chaining
         */
        public Builder contextSize(int contextSize) {
            params.setContextSize(contextSize);
            return this;
        }

        /**
         * Sets the batch size.
         *
         * @param batchSize Batch size
         * @return This builder for chaining
         */
        public Builder batchSize(int batchSize) {
            params.setBatchSize(batchSize);
            return this;
        }

        /**
         * Sets the thread count.
         *
         * @param threadCount Thread count
         * @return This builder for chaining
         */
        public Builder threadCount(int threadCount) {
            params.setThreadCount(threadCount);
            return this;
        }

        /**
         * Sets whether to compute logits for all tokens.
         *
         * @param logitsAll Whether to compute logits for all tokens
         * @return This builder for chaining
         */
        public Builder logitsAll(boolean logitsAll) {
            params.setLogitsAll(logitsAll);
            return this;
        }

        /**
         * Builds the ContextParams.
         *
         * @return The built ContextParams
         */
        public ContextParams build() {
            return params;
        }
    }
}
