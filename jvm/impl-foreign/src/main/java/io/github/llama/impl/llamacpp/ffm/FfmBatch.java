package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.batch.Batch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Implementation of {@link Batch} using Java's Foreign Function & Memory API.
 * This class represents a batch of tokens for processing.
 */
public class FfmBatch implements Batch {

    private static final Logger logger = LoggerFactory.getLogger(FfmBatch.class);

    private final int maxTokenCount;
    private int[] tokens;
    private int tokenCount;
    private boolean closed = false;

    /**
     * Creates a new instance of the FfmBatch.
     *
     * @param maxTokenCount Maximum number of tokens the batch can hold
     */
    public FfmBatch(int maxTokenCount) {
        this.maxTokenCount = maxTokenCount;
        this.tokens = new int[maxTokenCount];
        this.tokenCount = 0;
        logger.debug("Created FfmBatch with maxTokenCount={}", maxTokenCount);
    }

    @Override
    public Batch addToken(int tokenId) {
        checkClosed();

        if (tokenCount >= maxTokenCount) {
            logger.error("Cannot add token: batch is full");
            throw new IllegalStateException("Batch is full");
        }

        tokens[tokenCount++] = tokenId;
        logger.debug("Added token {} to batch, new count: {}", tokenId, tokenCount);

        return this;
    }

    @Override
    public Batch addTokens(int[] tokenIds) {
        checkClosed();

        if (tokenIds == null) {
            logger.error("Cannot add null tokens");
            throw new IllegalArgumentException("Token IDs cannot be null");
        }

        if (tokenCount + tokenIds.length > maxTokenCount) {
            logger.error("Cannot add tokens: batch would overflow");
            throw new IllegalStateException("Batch would overflow");
        }

        System.arraycopy(tokenIds, 0, tokens, tokenCount, tokenIds.length);
        tokenCount += tokenIds.length;
        logger.debug("Added {} tokens to batch, new count: {}", tokenIds.length, tokenCount);

        return this;
    }

    @Override
    public int getTokenCount() {
        checkClosed();
        return tokenCount;
    }

    @Override
    public int getMaxTokenCount() {
        checkClosed();
        return maxTokenCount;
    }

    @Override
    public Batch clear() {
        checkClosed();
        tokenCount = 0;
        logger.debug("Cleared batch");
        return this;
    }

    @Override
    public void close() {
        if (!closed) {
            logger.info("Closing batch");
            // TODO: Implement actual cleanup using FFM API
            closed = true;
            logger.debug("Batch closed");
        }
    }

    /**
     * Gets a copy of the tokens in this batch.
     *
     * @return Array of token IDs
     */
    public int[] getTokens() {
        checkClosed();
        return Arrays.copyOf(tokens, tokenCount);
    }

    /**
     * Checks if the batch is closed and throws an exception if it is.
     *
     * @throws IllegalStateException if the batch is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Batch is closed");
        }
    }
}
