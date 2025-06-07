package io.github.llama.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Test class for the ModelParams class.
 */
public class ModelParamsTest {

    @Test
    public void testDefaultValues() {
        ModelParams params = new ModelParams();

        assertTrue(params.isUseMemoryMapping(), "Default useMemoryMapping should be true");
        assertFalse(params.isUseMemoryLocking(), "Default useMemoryLocking should be false");
        assertEquals(0, params.getGpuLayerCount(), "Default gpuLayerCount should be 0");
        assertFalse(params.isVocabOnly(), "Default vocabOnly should be false");
        assertTrue(params.getMetadataOverrides().isEmpty(), "Default metadataOverrides should be empty");
    }

    @Test
    public void testSetters() {
        ModelParams params = new ModelParams();

        params.setUseMemoryMapping(false);
        params.setUseMemoryLocking(true);
        params.setGpuLayerCount(32);
        params.setVocabOnly(true);

        assertFalse(params.isUseMemoryMapping(), "useMemoryMapping should be false after setter");
        assertTrue(params.isUseMemoryLocking(), "useMemoryLocking should be true after setter");
        assertEquals(32, params.getGpuLayerCount(), "gpuLayerCount should be 32 after setter");
        assertTrue(params.isVocabOnly(), "vocabOnly should be true after setter");
    }

    @Test
    public void testMetadataOverrides() {
        ModelParams params = new ModelParams();

        // Add metadata overrides
        params.getMetadataOverrides().put("key1", "value1");
        params.getMetadataOverrides().put("key2", "value2");

        Map<String, String> overrides = params.getMetadataOverrides();
        assertEquals(2, overrides.size(), "There should be 2 metadata overrides");
        assertEquals("value1", overrides.get("key1"), "Override for key1 should be value1");
        assertEquals("value2", overrides.get("key2"), "Override for key2 should be value2");
    }

    @Test
    public void testBuilder() {
        ModelParams params = ModelParams.builder()
            .useMemoryMapping(false)
            .useMemoryLocking(true)
            .gpuLayerCount(32)
            .vocabOnly(true)
            .addMetadataOverride("key1", "value1")
            .addMetadataOverride("key2", "value2")
            .build();

        assertFalse(params.isUseMemoryMapping(), "useMemoryMapping should be false from builder");
        assertTrue(params.isUseMemoryLocking(), "useMemoryLocking should be true from builder");
        assertEquals(32, params.getGpuLayerCount(), "gpuLayerCount should be 32 from builder");
        assertTrue(params.isVocabOnly(), "vocabOnly should be true from builder");

        Map<String, String> overrides = params.getMetadataOverrides();
        assertEquals(2, overrides.size(), "There should be 2 metadata overrides from builder");
        assertEquals("value1", overrides.get("key1"), "Override for key1 should be value1 from builder");
        assertEquals("value2", overrides.get("key2"), "Override for key2 should be value2 from builder");
    }
}
