package io.github.llama.api.batch;

/**
 * Interface representing a batch of tokens for processing.
 */
public interface Batch extends AutoCloseable {
    /**
     * Adds a token to the batch.
     *
     * @param tokenId Token ID
     * @return This batch for chaining
     */
    Batch addToken(int tokenId);

    /**
     * Adds multiple tokens to the batch.
     *
     * @param tokenIds Array of token IDs
     * @return This batch for chaining
     */
    Batch addTokens(int[] tokenIds);

    /**
     * Gets the number of tokens in the batch.
     *
     * @return Number of tokens
     */
    int getTokenCount();

    /**
     * Gets the maximum number of tokens the batch can hold.
     *
     * @return Maximum token count
     */
    int getMaxTokenCount();

    /**
     * Clears the batch.
     *
     * @return This batch for chaining
     */
    Batch clear();

    /**
     * Closes the batch and releases resources.
     */
    @Override
    void close();
}
