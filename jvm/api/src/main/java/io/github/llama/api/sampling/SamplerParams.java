package io.github.llama.api.sampling;

/**
 * Class representing parameters for sampler creation.
 */
public class SamplerParams {
    private float temperature = 0.8f;
    private float topP = 0.95f;
    private int topK = 40;
    private float repetitionPenalty = 1.1f;
    private int maxTokens = 128;

    /**
     * Gets the temperature.
     *
     * @return Temperature
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * Sets the temperature.
     *
     * @param temperature Temperature
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * Gets the top-p value.
     *
     * @return Top-p value
     */
    public float getTopP() {
        return topP;
    }

    /**
     * Sets the top-p value.
     *
     * @param topP Top-p value
     */
    public void setTopP(float topP) {
        this.topP = topP;
    }

    /**
     * Gets the top-k value.
     *
     * @return Top-k value
     */
    public int getTopK() {
        return topK;
    }

    /**
     * Sets the top-k value.
     *
     * @param topK Top-k value
     */
    public void setTopK(int topK) {
        this.topK = topK;
    }

    /**
     * Gets the repetition penalty.
     *
     * @return Repetition penalty
     */
    public float getRepetitionPenalty() {
        return repetitionPenalty;
    }

    /**
     * Sets the repetition penalty.
     *
     * @param repetitionPenalty Repetition penalty
     */
    public void setRepetitionPenalty(float repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    /**
     * Gets the maximum number of tokens to generate.
     *
     * @return Maximum number of tokens
     */
    public int getMaxTokens() {
        return maxTokens;
    }

    /**
     * Sets the maximum number of tokens to generate.
     *
     * @param maxTokens Maximum number of tokens
     */
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    /**
     * Creates a new builder for SamplerParams.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for SamplerParams.
     */
    public static class Builder {
        private final SamplerParams params = new SamplerParams();

        /**
         * Sets the temperature.
         *
         * @param temperature Temperature
         * @return This builder for chaining
         */
        public Builder temperature(float temperature) {
            params.setTemperature(temperature);
            return this;
        }

        /**
         * Sets the top-p value.
         *
         * @param topP Top-p value
         * @return This builder for chaining
         */
        public Builder topP(float topP) {
            params.setTopP(topP);
            return this;
        }

        /**
         * Sets the top-k value.
         *
         * @param topK Top-k value
         * @return This builder for chaining
         */
        public Builder topK(int topK) {
            params.setTopK(topK);
            return this;
        }

        /**
         * Sets the repetition penalty.
         *
         * @param repetitionPenalty Repetition penalty
         * @return This builder for chaining
         */
        public Builder repetitionPenalty(float repetitionPenalty) {
            params.setRepetitionPenalty(repetitionPenalty);
            return this;
        }

        /**
         * Sets the maximum number of tokens to generate.
         *
         * @param maxTokens Maximum number of tokens
         * @return This builder for chaining
         */
        public Builder maxTokens(int maxTokens) {
            params.setMaxTokens(maxTokens);
            return this;
        }

        /**
         * Builds the SamplerParams.
         *
         * @return The built SamplerParams
         */
        public SamplerParams build() {
            return params;
        }
    }
}
