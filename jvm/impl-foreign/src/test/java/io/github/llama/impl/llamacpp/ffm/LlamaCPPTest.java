package io.github.llama.impl.llamacpp.ffm;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.Arena;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link LlamaCPP} class to ensure library loading and method generation.
 * This test verifies that the native library is loaded correctly and that all required
 * methods have been generated.
 */
public class LlamaCPPTest {

    private static final Logger logger = LoggerFactory.getLogger(LlamaCPPTest.class);

    /**
     * Test that the native library can be loaded and basic functions are accessible.
     * This test verifies that the library is loaded correctly by calling some basic
     * functions that don't require complex setup.
     */
    @Test
    public void testLibraryLoading() {
        logger.info("Testing library loading");

        // Test getting time - this should work without any initialization
        long time = LlamaCPP.llama_time_us();
        assertTrue(time > 0, "Time should be positive");

        // Test checking capabilities - these should work without initialization
        boolean supportsMmap = LlamaCPP.llama_supports_mmap();
        assertNotNull(supportsMmap, "supportsMmap should return a boolean");

        boolean supportsMlock = LlamaCPP.llama_supports_mlock();
        assertNotNull(supportsMlock, "supportsMlock should return a boolean");

        boolean supportsGpuOffload = LlamaCPP.llama_supports_gpu_offload();
        assertNotNull(supportsGpuOffload, "supportsGpuOffload should return a boolean");

        boolean supportsRpc = LlamaCPP.llama_supports_rpc();
        assertNotNull(supportsRpc, "supportsRpc should return a boolean");

        // Test constants
        int nullToken = LlamaCPP.LLAMA_TOKEN_NULL();
        assertEquals(-1, nullToken, "LLAMA_TOKEN_NULL should be -1");

        logger.info("Library loading test passed");
    }

    /**
     * Test backend initialization and cleanup.
     * This test verifies that the backend can be initialized and cleaned up properly.
     */
    @Test
    public void testBackendInitialization() {
        logger.info("Testing backend initialization");

        // Initialize the backend
        LlamaCPP.llama_backend_init();

        // Clean up the backend
        LlamaCPP.llama_backend_free();

        // If we got here without exceptions, the test passes
        logger.info("Backend initialization test passed");
    }

    /**
     * Test NUMA initialization.
     * This test verifies that NUMA can be initialized with different strategies.
     */
    @Test
    public void testNumaInitialization() {
        logger.info("Testing NUMA initialization");

        // Test with different strategies
        LlamaCPP.llama_numa_init(LlamaCPP.GGML_NUMA_STRATEGY_DISABLED()); // default
        LlamaCPP.llama_numa_init(LlamaCPP.GGML_NUMA_STRATEGY_DISTRIBUTE()); // distribute
        LlamaCPP.llama_numa_init(LlamaCPP.GGML_NUMA_STRATEGY_ISOLATE()); // isolate

        // If we got here without exceptions, the test passes
        logger.info("NUMA initialization test passed");
    }

    /**
     * Test sampler initialization functions.
     * This test verifies that various sampler initialization functions are accessible.
     */
    @Test
    public void testSamplerInitialization() {
        logger.info("Testing sampler initialization");

        try (Arena arena = Arena.ofConfined()) {
            // Initialize the backend first
            LlamaCPP.llama_backend_init();

            try {
                // Test various sampler initialization functions
                MemorySegment greedy = LlamaCPP.llama_sampler_init_greedy();
                assertNotNull(greedy, "Greedy sampler should not be null");
                LlamaCPP.llama_sampler_free(greedy);

                MemorySegment dist = LlamaCPP.llama_sampler_init_dist(42);
                assertNotNull(dist, "Distribution sampler should not be null");
                LlamaCPP.llama_sampler_free(dist);

                MemorySegment softmax = LlamaCPP.llama_sampler_init_softmax();
                assertNotNull(softmax, "Softmax sampler should not be null");
                LlamaCPP.llama_sampler_free(softmax);

                MemorySegment topK = LlamaCPP.llama_sampler_init_top_k(40);
                assertNotNull(topK, "Top-K sampler should not be null");
                LlamaCPP.llama_sampler_free(topK);

                MemorySegment topP = LlamaCPP.llama_sampler_init_top_p(0.9f, 1);
                assertNotNull(topP, "Top-P sampler should not be null");
                LlamaCPP.llama_sampler_free(topP);

                MemorySegment temp = LlamaCPP.llama_sampler_init_temp(0.8f);
                assertNotNull(temp, "Temperature sampler should not be null");
                LlamaCPP.llama_sampler_free(temp);
            } finally {
                // Clean up the backend
                LlamaCPP.llama_backend_free();
            }
        }

        logger.info("Sampler initialization test passed");
    }

    /**
     * Test token constants.
     * This test verifies that token constants are accessible.
     */
    @Test
    public void testTokenConstants() {
        logger.info("Testing token constants");

        // These should be accessible without initialization
        int nullToken = LlamaCPP.LLAMA_TOKEN_NULL();
        assertEquals(-1, nullToken, "LLAMA_TOKEN_NULL should be -1");

        int fileMagicGgla = LlamaCPP.LLAMA_FILE_MAGIC_GGLA();
        assertTrue(fileMagicGgla != 0, "LLAMA_FILE_MAGIC_GGLA should not be 0");

        int fileMagicGgsn = LlamaCPP.LLAMA_FILE_MAGIC_GGSN();
        assertTrue(fileMagicGgsn != 0, "LLAMA_FILE_MAGIC_GGSN should not be 0");

        int fileMagicGgsq = LlamaCPP.LLAMA_FILE_MAGIC_GGSQ();
        assertTrue(fileMagicGgsq != 0, "LLAMA_FILE_MAGIC_GGSQ should not be 0");

        int sessionMagic = LlamaCPP.LLAMA_SESSION_MAGIC();
        assertTrue(sessionMagic != 0, "LLAMA_SESSION_MAGIC should not be 0");

        int stateSeqMagic = LlamaCPP.LLAMA_STATE_SEQ_MAGIC();
        assertTrue(stateSeqMagic != 0, "LLAMA_STATE_SEQ_MAGIC should not be 0");

        logger.info("Token constants test passed");
    }
}
