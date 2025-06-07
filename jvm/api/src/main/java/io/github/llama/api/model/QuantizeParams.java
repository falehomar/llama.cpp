package io.github.llama.api.model;

/**
 * Class representing parameters for model quantization.
 */
public class QuantizeParams {
    private int quantizationType = 2; // Default to Q4_0
    private int threads = 4;
    private boolean allowRequantize = false;
    private boolean quantizeOutputTensor = true;
    private boolean onlyKeepDecoderLayers = false;

    /**
     * Gets the quantization type.
     *
     * @return Quantization type
     */
    public int getQuantizationType() {
        return quantizationType;
    }

    /**
     * Sets the quantization type.
     *
     * @param quantizationType Quantization type
     */
    public void setQuantizationType(int quantizationType) {
        this.quantizationType = quantizationType;
    }

    /**
     * Gets the number of threads to use for quantization.
     *
     * @return Number of threads
     */
    public int getThreads() {
        return threads;
    }

    /**
     * Sets the number of threads to use for quantization.
     *
     * @param threads Number of threads
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     * Gets whether to allow requantizing tensors that are already quantized.
     *
     * @return Whether to allow requantizing
     */
    public boolean isAllowRequantize() {
        return allowRequantize;
    }

    /**
     * Sets whether to allow requantizing tensors that are already quantized.
     *
     * @param allowRequantize Whether to allow requantizing
     */
    public void setAllowRequantize(boolean allowRequantize) {
        this.allowRequantize = allowRequantize;
    }

    /**
     * Gets whether to quantize the output tensor.
     *
     * @return Whether to quantize the output tensor
     */
    public boolean isQuantizeOutputTensor() {
        return quantizeOutputTensor;
    }

    /**
     * Sets whether to quantize the output tensor.
     *
     * @param quantizeOutputTensor Whether to quantize the output tensor
     */
    public void setQuantizeOutputTensor(boolean quantizeOutputTensor) {
        this.quantizeOutputTensor = quantizeOutputTensor;
    }

    /**
     * Gets whether to only keep decoder layers.
     *
     * @return Whether to only keep decoder layers
     */
    public boolean isOnlyKeepDecoderLayers() {
        return onlyKeepDecoderLayers;
    }

    /**
     * Sets whether to only keep decoder layers.
     *
     * @param onlyKeepDecoderLayers Whether to only keep decoder layers
     */
    public void setOnlyKeepDecoderLayers(boolean onlyKeepDecoderLayers) {
        this.onlyKeepDecoderLayers = onlyKeepDecoderLayers;
    }

    /**
     * Creates a new builder for QuantizeParams.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for QuantizeParams.
     */
    public static class Builder {
        private final QuantizeParams params = new QuantizeParams();

        /**
         * Sets the quantization type.
         *
         * @param quantizationType Quantization type
         * @return This builder for chaining
         */
        public Builder quantizationType(int quantizationType) {
            params.setQuantizationType(quantizationType);
            return this;
        }

        /**
         * Sets the number of threads to use for quantization.
         *
         * @param threads Number of threads
         * @return This builder for chaining
         */
        public Builder threads(int threads) {
            params.setThreads(threads);
            return this;
        }

        /**
         * Sets whether to allow requantizing tensors that are already quantized.
         *
         * @param allowRequantize Whether to allow requantizing
         * @return This builder for chaining
         */
        public Builder allowRequantize(boolean allowRequantize) {
            params.setAllowRequantize(allowRequantize);
            return this;
        }

        /**
         * Sets whether to quantize the output tensor.
         *
         * @param quantizeOutputTensor Whether to quantize the output tensor
         * @return This builder for chaining
         */
        public Builder quantizeOutputTensor(boolean quantizeOutputTensor) {
            params.setQuantizeOutputTensor(quantizeOutputTensor);
            return this;
        }

        /**
         * Sets whether to only keep decoder layers.
         *
         * @param onlyKeepDecoderLayers Whether to only keep decoder layers
         * @return This builder for chaining
         */
        public Builder onlyKeepDecoderLayers(boolean onlyKeepDecoderLayers) {
            params.setOnlyKeepDecoderLayers(onlyKeepDecoderLayers);
            return this;
        }

        /**
         * Builds the QuantizeParams.
         *
         * @return The built QuantizeParams
         */
        public QuantizeParams build() {
            return params;
        }
    }
}
