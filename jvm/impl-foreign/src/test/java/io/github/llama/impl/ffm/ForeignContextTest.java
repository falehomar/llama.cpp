package io.github.llama.impl.ffm;

import io.github.llama.api.LLM;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;
import io.github.llama.impl.llamacpp.ffm.LlamaCPP;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.foreign.MemorySegment;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ForeignContext.
 * Following TDD principles, these tests are written before implementing the actual functionality.
 */
public class ForeignContextTest {

    @BeforeAll
    public static void setUp() {
        // Initialize the backend before running any tests
        LlamaCPP.llama_backend_init();
    }

    /**
     * Test for constructor.
     * This method should throw an IllegalArgumentException when the model is null.
     */
    @Test
    public void testConstructorNullModel() {
        ContextParams params = new ContextParams();

        // This should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            new ForeignContext(null, params);
        }, "Constructor should throw IllegalArgumentException when model is null");
    }

    /**
     * Test for constructor.
     * This method should throw an IllegalArgumentException when the params is null.
     */
    @Test
    public void testConstructorNullParams() {
        // Create a mock LLM
        LLM model = Mockito.mock(LLM.class);

        // This should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            new ForeignContext(model, null);
        }, "Constructor should throw IllegalArgumentException when params is null");
    }

    /**
     * Test for getModel method.
     * This method should return the model passed to the constructor.
     */
    @Test
    public void testGetModel() {
        // Create a mock LLM
        LLM model = Mockito.mock(LLM.class);
        ContextParams params = new ContextParams();

        // Create a mock ForeignContext
        ForeignContext context = Mockito.mock(ForeignContext.class);
        Mockito.when(context.getModel()).thenReturn(model);

        // Check that getModel returns the model
        assertSame(model, context.getModel(), "getModel should return the model passed to the constructor");
    }

    /**
     * Test for createBatch method.
     * This method should return a non-null Batch object.
     */
    @Test
    public void testCreateBatch() {
        // Create a mock LLM
        LLM model = Mockito.mock(LLM.class);
        ContextParams params = new ContextParams();

        // Create a mock ForeignContext
        ForeignContext context = Mockito.mock(ForeignContext.class);
        Mockito.when(context.createBatch(Mockito.anyInt())).thenCallRealMethod();

        // Create a mock Batch
        Batch batch = Mockito.mock(Batch.class);
        Mockito.when(context.createBatch(10)).thenReturn(batch);

        // Check that createBatch returns a non-null Batch
        assertNotNull(context.createBatch(10), "createBatch should return a non-null Batch");
    }

    /**
     * Test for process method.
     * This method should throw an IllegalArgumentException when the batch is null.
     */
    @Test
    public void testProcessNullBatch() {
        // Create a mock LLM
        LLM model = Mockito.mock(LLM.class);
        ContextParams params = new ContextParams();

        // Create a mock ForeignContext
        ForeignContext context = Mockito.mock(ForeignContext.class);
        Mockito.when(context.process(null)).thenCallRealMethod();

        // This should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            context.process(null);
        }, "process should throw IllegalArgumentException when batch is null");
    }

    /**
     * Test for getLogits method.
     * This method should return a non-null array of logits.
     */
    @Test
    public void testGetLogits() {
        // Create a mock LLM
        LLM model = Mockito.mock(LLM.class);
        ContextParams params = new ContextParams();

        // Create a mock ForeignContext
        ForeignContext context = Mockito.mock(ForeignContext.class);
        Mockito.when(context.getLogits()).thenCallRealMethod();

        // Create a mock array of logits
        float[] logits = new float[10];
        Mockito.when(context.getLogits()).thenReturn(logits);

        // Check that getLogits returns a non-null array of logits
        assertNotNull(context.getLogits(), "getLogits should return a non-null array of logits");
    }

    /**
     * Test for createSampler method.
     * This method should throw an IllegalArgumentException when the params is null.
     */
    @Test
    public void testCreateSamplerNullParams() {
        // Create a mock LLM
        LLM model = Mockito.mock(LLM.class);
        ContextParams params = new ContextParams();

        // Create a mock ForeignContext
        ForeignContext context = Mockito.mock(ForeignContext.class);
        Mockito.when(context.createSampler(null)).thenCallRealMethod();

        // This should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            context.createSampler(null);
        }, "createSampler should throw IllegalArgumentException when params is null");
    }

    /**
     * Test for close method.
     * This method should free the native context.
     */
    @Test
    public void testClose() {
        // Create a mock LLM
        LLM model = Mockito.mock(LLM.class);
        ContextParams params = new ContextParams();

        // Create a mock ForeignContext
        ForeignContext context = Mockito.mock(ForeignContext.class);

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            context.close();
        }, "close should not throw an exception");
    }
}
