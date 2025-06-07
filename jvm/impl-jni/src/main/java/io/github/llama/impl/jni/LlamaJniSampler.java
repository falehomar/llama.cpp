package io.github.llama.impl.jni;

import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;

import java.util.Arrays;
import java.util.Random;

/**
 * JNI implementation of the Sampler interface for llama.cpp models.
 */
public class LlamaJniSampler implements Sampler {
    private final LlamaJniContext context;
    private final SamplerParams params;
    private final Random random;

    /**
     * Creates a new sampler for a context.
     *
     * @param context The context
     * @param params The sampler parameters
     */
    LlamaJniSampler(LlamaJniContext context, SamplerParams params) {
        this.context = context;
        this.params = params;
        this.random = new Random(params.getSeed() != 0 ? params.getSeed() : System.currentTimeMillis());
    }

    @Override
    public int sample() {
        // Get logits from the context
        float[] logits = context.getLogits();

        // Apply temperature
        float temperature = params.getTemperature();
        if (temperature > 0) {
            for (int i = 0; i < logits.length; i++) {
                logits[i] /= temperature;
            }
        }

        // Convert logits to probabilities
        float[] probabilities = softmax(logits);

        // Apply top-p sampling
        float topP = params.getTopP();
        if (topP < 1.0f) {
            probabilities = applyTopP(probabilities, topP);
        }

        // Apply top-k sampling
        int topK = params.getTopK();
        if (topK > 0 && topK < probabilities.length) {
            probabilities = applyTopK(probabilities, topK);
        }

        // Sample a token
        return sampleFromDistribution(probabilities);
    }

    /**
     * Applies softmax function to logits.
     *
     * @param logits Raw logits
     * @return Probability distribution
     */
    private float[] softmax(float[] logits) {
        float[] probs = new float[logits.length];

        // Find max logit to avoid overflow
        float maxLogit = Float.NEGATIVE_INFINITY;
        for (float logit : logits) {
            if (logit > maxLogit) {
                maxLogit = logit;
            }
        }

        // Compute exp and sum
        float sum = 0.0f;
        for (int i = 0; i < logits.length; i++) {
            probs[i] = (float) Math.exp(logits[i] - maxLogit);
            sum += probs[i];
        }

        // Normalize
        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
        }

        return probs;
    }

    /**
     * Applies top-p (nucleus) sampling to probabilities.
     *
     * @param probs Probability distribution
     * @param p The cumulative probability threshold
     * @return Filtered probability distribution
     */
    private float[] applyTopP(float[] probs, float p) {
        // Create index-probability pairs
        class Pair implements Comparable<Pair> {
            final int index;
            final float prob;

            Pair(int index, float prob) {
                this.index = index;
                this.prob = prob;
            }

            @Override
            public int compareTo(Pair other) {
                return Float.compare(other.prob, this.prob); // Descending order
            }
        }

        Pair[] pairs = new Pair[probs.length];
        for (int i = 0; i < probs.length; i++) {
            pairs[i] = new Pair(i, probs[i]);
        }

        // Sort by probability (descending)
        Arrays.sort(pairs);

        // Find cut-off point
        float cumProb = 0.0f;
        int lastIndex = 0;
        for (int i = 0; i < pairs.length; i++) {
            cumProb += pairs[i].prob;
            lastIndex = i;
            if (cumProb >= p) {
                break;
            }
        }

        // Create new distribution
        float[] filteredProbs = new float[probs.length];
        float sum = 0.0f;
        for (int i = 0; i <= lastIndex; i++) {
            filteredProbs[pairs[i].index] = pairs[i].prob;
            sum += pairs[i].prob;
        }

        // Renormalize
        for (int i = 0; i < filteredProbs.length; i++) {
            filteredProbs[i] /= sum;
        }

        return filteredProbs;
    }

    /**
     * Applies top-k sampling to probabilities.
     *
     * @param probs Probability distribution
     * @param k The number of top probabilities to keep
     * @return Filtered probability distribution
     */
    private float[] applyTopK(float[] probs, int k) {
        // Create index-probability pairs
        class Pair implements Comparable<Pair> {
            final int index;
            final float prob;

            Pair(int index, float prob) {
                this.index = index;
                this.prob = prob;
            }

            @Override
            public int compareTo(Pair other) {
                return Float.compare(other.prob, this.prob); // Descending order
            }
        }

        Pair[] pairs = new Pair[probs.length];
        for (int i = 0; i < probs.length; i++) {
            pairs[i] = new Pair(i, probs[i]);
        }

        // Sort by probability (descending)
        Arrays.sort(pairs);

        // Create new distribution
        float[] filteredProbs = new float[probs.length];
        float sum = 0.0f;
        for (int i = 0; i < k && i < pairs.length; i++) {
            filteredProbs[pairs[i].index] = pairs[i].prob;
            sum += pairs[i].prob;
        }

        // Renormalize
        for (int i = 0; i < filteredProbs.length; i++) {
            filteredProbs[i] /= sum;
        }

        return filteredProbs;
    }

    /**
     * Samples a token from a probability distribution.
     *
     * @param probs Probability distribution
     * @return Sampled token
     */
    private int sampleFromDistribution(float[] probs) {
        float value = random.nextFloat();
        float cumProb = 0.0f;

        for (int i = 0; i < probs.length; i++) {
            cumProb += probs[i];
            if (value < cumProb) {
                return i;
            }
        }

        // Fallback
        return probs.length - 1;
    }
}
