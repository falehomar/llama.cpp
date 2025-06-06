package io.github.llama.api.model;

import java.util.Set;

/**
 * Interface representing information about a model.
 */
public interface ModelInfo {
    /**
     * Gets the number of parameters in the model.
     *
     * @return Number of parameters
     */
    long getParameterCount();

    /**
     * Gets the context size used during training.
     *
     * @return Context size
     */
    int getContextSize();

    /**
     * Gets the embedding size.
     *
     * @return Embedding size
     */
    int getEmbeddingSize();

    /**
     * Gets the number of layers.
     *
     * @return Number of layers
     */
    int getLayerCount();

    /**
     * Gets the number of attention heads.
     *
     * @return Number of attention heads
     */
    int getHeadCount();

    /**
     * Gets a metadata value as a string.
     *
     * @param key Metadata key
     * @return Metadata value, or null if not found
     */
    String getMetadata(String key);

    /**
     * Gets all metadata keys.
     *
     * @return Set of metadata keys
     */
    Set<String> getMetadataKeys();

    /**
     * Gets a description of the model.
     *
     * @return Model description
     */
    String getDescription();
}
