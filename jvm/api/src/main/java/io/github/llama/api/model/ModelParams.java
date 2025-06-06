package io.github.llama.api.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing parameters for model loading.
 */
public class ModelParams {
    private boolean useMemoryMapping = true;
    private boolean useMemoryLocking = false;
    private int gpuLayerCount = 0;
    private boolean vocabOnly = false;
    private Map<String, String> metadataOverrides = new HashMap<>();

    /**
     * Gets whether to use memory mapping.
     *
     * @return Whether to use memory mapping
     */
    public boolean isUseMemoryMapping() {
        return useMemoryMapping;
    }

    /**
     * Sets whether to use memory mapping.
     *
     * @param useMemoryMapping Whether to use memory mapping
     */
    public void setUseMemoryMapping(boolean useMemoryMapping) {
        this.useMemoryMapping = useMemoryMapping;
    }

    /**
     * Gets whether to use memory locking.
     *
     * @return Whether to use memory locking
     */
    public boolean isUseMemoryLocking() {
        return useMemoryLocking;
    }

    /**
     * Sets whether to use memory locking.
     *
     * @param useMemoryLocking Whether to use memory locking
     */
    public void setUseMemoryLocking(boolean useMemoryLocking) {
        this.useMemoryLocking = useMemoryLocking;
    }

    /**
     * Gets the number of layers to offload to GPU.
     *
     * @return Number of layers to offload to GPU
     */
    public int getGpuLayerCount() {
        return gpuLayerCount;
    }

    /**
     * Sets the number of layers to offload to GPU.
     *
     * @param gpuLayerCount Number of layers to offload to GPU
     */
    public void setGpuLayerCount(int gpuLayerCount) {
        this.gpuLayerCount = gpuLayerCount;
    }

    /**
     * Gets whether to load only the vocabulary.
     *
     * @return Whether to load only the vocabulary
     */
    public boolean isVocabOnly() {
        return vocabOnly;
    }

    /**
     * Sets whether to load only the vocabulary.
     *
     * @param vocabOnly Whether to load only the vocabulary
     */
    public void setVocabOnly(boolean vocabOnly) {
        this.vocabOnly = vocabOnly;
    }

    /**
     * Gets the metadata overrides.
     *
     * @return Metadata overrides
     */
    public Map<String, String> getMetadataOverrides() {
        return metadataOverrides;
    }

    /**
     * Sets the metadata overrides.
     *
     * @param metadataOverrides Metadata overrides
     */
    public void setMetadataOverrides(Map<String, String> metadataOverrides) {
        this.metadataOverrides = metadataOverrides;
    }

    /**
     * Creates a new builder for ModelParams.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ModelParams.
     */
    public static class Builder {
        private final ModelParams params = new ModelParams();

        /**
         * Sets whether to use memory mapping.
         *
         * @param useMemoryMapping Whether to use memory mapping
         * @return This builder for chaining
         */
        public Builder useMemoryMapping(boolean useMemoryMapping) {
            params.setUseMemoryMapping(useMemoryMapping);
            return this;
        }

        /**
         * Sets whether to use memory locking.
         *
         * @param useMemoryLocking Whether to use memory locking
         * @return This builder for chaining
         */
        public Builder useMemoryLocking(boolean useMemoryLocking) {
            params.setUseMemoryLocking(useMemoryLocking);
            return this;
        }

        /**
         * Sets the number of layers to offload to GPU.
         *
         * @param gpuLayerCount Number of layers to offload to GPU
         * @return This builder for chaining
         */
        public Builder gpuLayerCount(int gpuLayerCount) {
            params.setGpuLayerCount(gpuLayerCount);
            return this;
        }

        /**
         * Sets whether to load only the vocabulary.
         *
         * @param vocabOnly Whether to load only the vocabulary
         * @return This builder for chaining
         */
        public Builder vocabOnly(boolean vocabOnly) {
            params.setVocabOnly(vocabOnly);
            return this;
        }

        /**
         * Adds a metadata override.
         *
         * @param key Metadata key
         * @param value Metadata value
         * @return This builder for chaining
         */
        public Builder addMetadataOverride(String key, String value) {
            params.getMetadataOverrides().put(key, value);
            return this;
        }

        /**
         * Builds the ModelParams.
         *
         * @return The built ModelParams
         */
        public ModelParams build() {
            return params;
        }
    }
}
