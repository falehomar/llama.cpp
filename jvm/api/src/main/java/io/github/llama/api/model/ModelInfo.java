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
     * Gets the number of key-value heads.
     *
     * @return Number of key-value heads
     */
    int getKvHeadCount();

    /**
     * Gets the RoPE frequency scaling factor.
     *
     * @return RoPE frequency scaling factor
     */
    float getRopeFreqScaleTrain();

    /**
     * Gets the RoPE type.
     *
     * @return RoPE type
     */
    int getRopeType();

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

    /**
     * Gets the total size of all tensors in the model.
     *
     * @return Total size in bytes
     */
    long getSize();

    /**
     * Gets the default chat template.
     *
     * @return Default chat template, or null if not available
     */
    String getChatTemplate();

    /**
     * Checks if the model contains an encoder.
     *
     * @return true if the model contains an encoder, false otherwise
     */
    boolean hasEncoder();

    /**
     * Checks if the model contains a decoder.
     *
     * @return true if the model contains a decoder, false otherwise
     */
    boolean hasDecoder();

    /**
     * Gets the decoder start token.
     *
     * @return Decoder start token
     */
    int getDecoderStartToken();

    /**
     * Checks if the model is recurrent.
     *
     * @return true if the model is recurrent, false otherwise
     */
    boolean isRecurrent();
}
