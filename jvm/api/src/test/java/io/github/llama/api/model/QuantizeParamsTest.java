package io.github.llama.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the QuantizeParams class.
 */
public class QuantizeParamsTest {

    @Test
    public void testDefaultValues() {
        QuantizeParams params = new QuantizeParams();

        assertEquals(2, params.getQuantizationType(), "Default quantizationType should be 2 (Q4_0)");
        assertEquals(4, params.getThreads(), "Default threads should be 4");
        assertFalse(params.isAllowRequantize(), "Default allowRequantize should be false");
        assertTrue(params.isQuantizeOutputTensor(), "Default quantizeOutputTensor should be true");
        assertFalse(params.isOnlyKeepDecoderLayers(), "Default onlyKeepDecoderLayers should be false");
    }

    @Test
    public void testSetters() {
        QuantizeParams params = new QuantizeParams();

        params.setQuantizationType(3);
        params.setThreads(8);
        params.setAllowRequantize(true);
        params.setQuantizeOutputTensor(false);
        params.setOnlyKeepDecoderLayers(true);

        assertEquals(3, params.getQuantizationType(), "quantizationType should be 3 after setter");
        assertEquals(8, params.getThreads(), "threads should be 8 after setter");
        assertTrue(params.isAllowRequantize(), "allowRequantize should be true after setter");
        assertFalse(params.isQuantizeOutputTensor(), "quantizeOutputTensor should be false after setter");
        assertTrue(params.isOnlyKeepDecoderLayers(), "onlyKeepDecoderLayers should be true after setter");
    }

    @Test
    public void testBuilder() {
        QuantizeParams params = QuantizeParams.builder()
            .quantizationType(3)
            .threads(8)
            .allowRequantize(true)
            .quantizeOutputTensor(false)
            .onlyKeepDecoderLayers(true)
            .build();

        assertEquals(3, params.getQuantizationType(), "quantizationType should be 3 from builder");
        assertEquals(8, params.getThreads(), "threads should be 8 from builder");
        assertTrue(params.isAllowRequantize(), "allowRequantize should be true from builder");
        assertFalse(params.isQuantizeOutputTensor(), "quantizeOutputTensor should be false from builder");
        assertTrue(params.isOnlyKeepDecoderLayers(), "onlyKeepDecoderLayers should be true from builder");
    }
}
