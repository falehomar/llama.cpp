package io.github.llama.api.context;

import io.github.llama.api.LLM;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;

/**
 * Interface representing a context for inference.
 */
public interface Context extends AutoCloseable {
    /**
     * Gets the model associated with this context.
     *
     * @return The model
     */
    LLM getModel();

    /**
     * Creates a new batch for processing.
     *
     * @param maxTokens Maximum number of tokens in the batch
     * @return A new batch
     */
    Batch createBatch(int maxTokens);

    /**
     * Processes a batch of tokens.
     *
     * @param batch The batch to process
     * @return Result of processing the batch
     */
    BatchResult process(Batch batch);

    /**
     * Gets the logits from the last processed batch.
     *
     * @return Array of logits
     */
    float[] getLogits();

    /**
     * Creates a sampler for generating tokens.
     *
     * @param params Sampler parameters
     * @return A new sampler
     */
    Sampler createSampler(SamplerParams params);

    /**
     * Closes the context and releases resources.
     */
    @Override
    void close();
}
