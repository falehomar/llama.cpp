package io.github.llama.tools.tokenization;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Represents the result of a tokenization operation.
 */
@Serdeable
public class TokenizationResult {

    private final int[] tokenIds;

    /**
     * Creates a new tokenization result.
     *
     * @param tokenIds The token IDs resulting from tokenization
     */
    public TokenizationResult(int[] tokenIds) {
        this.tokenIds = tokenIds;
    }

    /**
     * Gets the token IDs resulting from tokenization.
     *
     * @return Array of token IDs
     */
    public int[] getTokenIds() {
        return tokenIds;
    }
}
