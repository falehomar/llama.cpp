package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.LLM;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link FfmContext} class.
 */
public class FfmContextTest {

    private static final Logger logger = LoggerFactory.getLogger(FfmContextTest.class);

    private FfmModel mockModel;
    private ContextParams contextParams;
    private FfmContext context;

    @BeforeEach
    public void setUp() {
        // Create a mock model
        mockModel = Mockito.mock(FfmModel.class);

        // Create context parameters
        contextParams = ContextParams.builder()
                .contextSize(2048)
                .batchSize(512)
                .threadCount(4)
                .logitsAll(false)
                .build();

        // Create the context
        context = new FfmContext(mockModel, contextParams);
        logger.info("Set up test context with parameters: contextSize={}, batchSize={}, threadCount={}, logitsAll={}",
                contextParams.getContextSize(), contextParams.getBatchSize(),
                contextParams.getThreadCount(), contextParams.isLogitsAll());
    }

    @Test
    public void testGetModel() {
        logger.info("Testing getModel");

        LLM model = context.getModel();
        assertSame(mockModel, model, "Model should be the same as the one passed to the constructor");
    }

    @Test
    public void testCreateBatch() {
        logger.info("Testing createBatch");

        int maxTokens = 100;
        Batch batch = context.createBatch(maxTokens);

        assertNotNull(batch, "Created batch should not be null");
        assertTrue(batch instanceof FfmBatch, "Batch should be an instance of FfmBatch");
        assertEquals(maxTokens, batch.getMaxTokenCount(), "Batch should have the specified max token count");
    }

    @Test
    public void testProcess() {
        logger.info("Testing process");

        // Create a mock batch
        FfmBatch mockBatch = Mockito.mock(FfmBatch.class);
        Mockito.when(mockBatch.getTokenCount()).thenReturn(10);

        BatchResult result = context.process(mockBatch);

        assertNotNull(result, "Process result should not be null");
        assertTrue(result.isSuccess(), "Process result should be successful");
    }

    @Test
    public void testProcessWithNullBatch() {
        logger.info("Testing process with null batch");

        BatchResult result = context.process(null);

        assertNotNull(result, "Process result should not be null");
        assertFalse(result.isSuccess(), "Process result should not be successful");
        assertNotNull(result.getErrorMessage(), "Error message should not be null");
    }

    @Test
    public void testGetLogits() {
        logger.info("Testing getLogits");

        float[] logits = context.getLogits();

        assertNotNull(logits, "Logits should not be null");
        assertTrue(logits.length > 0, "Logits array should not be empty");
    }

    @Test
    public void testCreateSampler() {
        logger.info("Testing createSampler");

        SamplerParams params = SamplerParams.builder()
                .temperature(0.7f)
                .topP(0.9f)
                .topK(40)
                .repetitionPenalty(1.1f)
                .maxTokens(100)
                .build();

        Sampler sampler = context.createSampler(params);

        assertNotNull(sampler, "Created sampler should not be null");
        assertTrue(sampler instanceof FfmSampler, "Sampler should be an instance of FfmSampler");
    }

    @Test
    public void testCreateSamplerWithNullParams() {
        logger.info("Testing createSampler with null params");

        assertThrows(IllegalArgumentException.class, () -> {
            context.createSampler(null);
        }, "Creating sampler with null params should throw IllegalArgumentException");
    }

    @Test
    public void testClose() {
        logger.info("Testing close");

        // This should not throw an exception
        context.close();

        // Verify that the context is closed
        assertThrows(IllegalStateException.class, () -> {
            context.getLogits();
        }, "Getting logits after closing should throw IllegalStateException");
    }
}
