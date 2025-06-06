package io.github.llama.api.sampling;

/**
 * Interface for sampling tokens from logits.
 */
public interface Sampler extends AutoCloseable {
    /**
     * Samples a token from logits.
     *
     * @param logits Array of logits
     * @return Sampled token ID
     */
    int sample(float[] logits);

    /**
     * Closes the sampler and releases resources.
     */
    @Override
    void close();
}
