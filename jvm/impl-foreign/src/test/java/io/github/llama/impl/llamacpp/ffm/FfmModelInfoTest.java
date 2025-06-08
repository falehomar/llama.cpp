package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.model.ModelInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link FfmModelInfo} class.
 */
public class FfmModelInfoTest {

    private static final Logger logger = LoggerFactory.getLogger(FfmModelInfoTest.class);

    private FfmModelInfo modelInfo;

    @BeforeEach
    public void setUp() {
        logger.info("Setting up test model info");

        // Create a test model info with the builder
        modelInfo = new FfmModelInfo.Builder()
                .parameterCount(7000000000L)
                .contextSize(4096)
                .embeddingSize(4096)
                .layerCount(32)
                .headCount(32)
                .kvHeadCount(8)
                .ropeFreqScaleTrain(1.0f)
                .ropeType(0)
                .addMetadata("name", "test-model")
                .addMetadata("family", "llama")
                .description("Test Model 7B")
                .size(7000000000L)
                .chatTemplate("<|im_start|>user\n{prompt}<|im_end|>\n<|im_start|>assistant\n")
                .hasEncoder(false)
                .hasDecoder(true)
                .decoderStartToken(1)
                .recurrent(false)
                .build();
    }

    @Test
    public void testGetParameterCount() {
        logger.info("Testing getParameterCount");
        assertEquals(7000000000L, modelInfo.getParameterCount(), "Parameter count should match");
    }

    @Test
    public void testGetContextSize() {
        logger.info("Testing getContextSize");
        assertEquals(4096, modelInfo.getContextSize(), "Context size should match");
    }

    @Test
    public void testGetEmbeddingSize() {
        logger.info("Testing getEmbeddingSize");
        assertEquals(4096, modelInfo.getEmbeddingSize(), "Embedding size should match");
    }

    @Test
    public void testGetLayerCount() {
        logger.info("Testing getLayerCount");
        assertEquals(32, modelInfo.getLayerCount(), "Layer count should match");
    }

    @Test
    public void testGetHeadCount() {
        logger.info("Testing getHeadCount");
        assertEquals(32, modelInfo.getHeadCount(), "Head count should match");
    }

    @Test
    public void testGetKvHeadCount() {
        logger.info("Testing getKvHeadCount");
        assertEquals(8, modelInfo.getKvHeadCount(), "KV head count should match");
    }

    @Test
    public void testGetRopeFreqScaleTrain() {
        logger.info("Testing getRopeFreqScaleTrain");
        assertEquals(1.0f, modelInfo.getRopeFreqScaleTrain(), "RoPE frequency scale should match");
    }

    @Test
    public void testGetRopeType() {
        logger.info("Testing getRopeType");
        assertEquals(0, modelInfo.getRopeType(), "RoPE type should match");
    }

    @Test
    public void testGetMetadata() {
        logger.info("Testing getMetadata");
        assertEquals("test-model", modelInfo.getMetadata("name"), "Metadata 'name' should match");
        assertEquals("llama", modelInfo.getMetadata("family"), "Metadata 'family' should match");
        assertNull(modelInfo.getMetadata("nonexistent"), "Nonexistent metadata should return null");
    }

    @Test
    public void testGetMetadataKeys() {
        logger.info("Testing getMetadataKeys");
        Set<String> keys = modelInfo.getMetadataKeys();
        assertEquals(2, keys.size(), "Should have 2 metadata keys");
        assertTrue(keys.contains("name"), "Should contain 'name' key");
        assertTrue(keys.contains("family"), "Should contain 'family' key");
    }

    @Test
    public void testGetDescription() {
        logger.info("Testing getDescription");
        assertEquals("Test Model 7B", modelInfo.getDescription(), "Description should match");
    }

    @Test
    public void testGetSize() {
        logger.info("Testing getSize");
        assertEquals(7000000000L, modelInfo.getSize(), "Size should match");
    }

    @Test
    public void testGetChatTemplate() {
        logger.info("Testing getChatTemplate");
        assertEquals("<|im_start|>user\n{prompt}<|im_end|>\n<|im_start|>assistant\n",
                modelInfo.getChatTemplate(), "Chat template should match");
    }

    @Test
    public void testHasEncoder() {
        logger.info("Testing hasEncoder");
        assertFalse(modelInfo.hasEncoder(), "Should not have encoder");
    }

    @Test
    public void testHasDecoder() {
        logger.info("Testing hasDecoder");
        assertTrue(modelInfo.hasDecoder(), "Should have decoder");
    }

    @Test
    public void testGetDecoderStartToken() {
        logger.info("Testing getDecoderStartToken");
        assertEquals(1, modelInfo.getDecoderStartToken(), "Decoder start token should match");
    }

    @Test
    public void testIsRecurrent() {
        logger.info("Testing isRecurrent");
        assertFalse(modelInfo.isRecurrent(), "Should not be recurrent");
    }

    @Test
    public void testBuilder() {
        logger.info("Testing builder with different values");

        FfmModelInfo customInfo = new FfmModelInfo.Builder()
                .parameterCount(13000000000L)
                .contextSize(8192)
                .embeddingSize(5120)
                .layerCount(40)
                .headCount(40)
                .kvHeadCount(8)
                .ropeFreqScaleTrain(2.0f)
                .ropeType(1)
                .addMetadata("name", "custom-model")
                .description("Custom Model 13B")
                .size(13000000000L)
                .chatTemplate(null)
                .hasEncoder(true)
                .hasDecoder(true)
                .decoderStartToken(2)
                .recurrent(true)
                .build();

        assertEquals(13000000000L, customInfo.getParameterCount(), "Custom parameter count should match");
        assertEquals(8192, customInfo.getContextSize(), "Custom context size should match");
        assertEquals(5120, customInfo.getEmbeddingSize(), "Custom embedding size should match");
        assertEquals(40, customInfo.getLayerCount(), "Custom layer count should match");
        assertEquals(40, customInfo.getHeadCount(), "Custom head count should match");
        assertEquals(8, customInfo.getKvHeadCount(), "Custom KV head count should match");
        assertEquals(2.0f, customInfo.getRopeFreqScaleTrain(), "Custom RoPE frequency scale should match");
        assertEquals(1, customInfo.getRopeType(), "Custom RoPE type should match");
        assertEquals("custom-model", customInfo.getMetadata("name"), "Custom metadata should match");
        assertEquals("Custom Model 13B", customInfo.getDescription(), "Custom description should match");
        assertEquals(13000000000L, customInfo.getSize(), "Custom size should match");
        assertNull(customInfo.getChatTemplate(), "Custom chat template should be null");
        assertTrue(customInfo.hasEncoder(), "Custom should have encoder");
        assertTrue(customInfo.hasDecoder(), "Custom should have decoder");
        assertEquals(2, customInfo.getDecoderStartToken(), "Custom decoder start token should match");
        assertTrue(customInfo.isRecurrent(), "Custom should be recurrent");
    }
}
