package io.github.llama.api.sampling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SamplerParams}.
 */
public class SamplerParamsTest {

    /**
     * Test default values of SamplerParams.
     */
    @Test
    public void testDefaultValues() {
        SamplerParams params = new SamplerParams();

        assertEquals(0.8f, params.getTemperature(), "Default temperature should be 0.8");
        assertEquals(0.95f, params.getTopP(), "Default topP should be 0.95");
        assertEquals(40, params.getTopK(), "Default topK should be 40");
        assertEquals(1.1f, params.getRepetitionPenalty(), "Default repetitionPenalty should be 1.1");
        assertEquals(128, params.getMaxTokens(), "Default maxTokens should be 128");
    }

    /**
     * Test setting values using setters.
     */
    @Test
    public void testSetters() {
        SamplerParams params = new SamplerParams();

        params.setTemperature(0.5f);
        params.setTopP(0.8f);
        params.setTopK(20);
        params.setRepetitionPenalty(1.2f);
        params.setMaxTokens(256);

        assertEquals(0.5f, params.getTemperature(), "Temperature should be updated to 0.5");
        assertEquals(0.8f, params.getTopP(), "TopP should be updated to 0.8");
        assertEquals(20, params.getTopK(), "TopK should be updated to 20");
        assertEquals(1.2f, params.getRepetitionPenalty(), "RepetitionPenalty should be updated to 1.2");
        assertEquals(256, params.getMaxTokens(), "MaxTokens should be updated to 256");
    }

    /**
     * Test building SamplerParams using the builder.
     */
    @Test
    public void testBuilder() {
        SamplerParams params = SamplerParams.builder()
                .temperature(0.7f)
                .topP(0.9f)
                .topK(30)
                .repetitionPenalty(1.3f)
                .maxTokens(512)
                .build();

        assertEquals(0.7f, params.getTemperature(), "Builder should set temperature to 0.7");
        assertEquals(0.9f, params.getTopP(), "Builder should set topP to 0.9");
        assertEquals(30, params.getTopK(), "Builder should set topK to 30");
        assertEquals(1.3f, params.getRepetitionPenalty(), "Builder should set repetitionPenalty to 1.3");
        assertEquals(512, params.getMaxTokens(), "Builder should set maxTokens to 512");
    }

    /**
     * Test that the builder creates a new instance each time.
     */
    @Test
    public void testBuilderCreatesNewInstance() {
        SamplerParams params1 = SamplerParams.builder().build();
        SamplerParams params2 = SamplerParams.builder().build();

        assertNotSame(params1, params2, "Builder should create new instances");
    }
}
