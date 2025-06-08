package io.github.llama.impl.ffm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import io.github.llama.impl.llamacpp.ffm.LlamaCPP;
/**
 * Test class for the Initialization and Backend Management functions.
 */
public class BackendManagementTest {

    /**
     * Test for llama_backend_init and llama_backend_free.
     * These functions should not throw any exceptions.
     */
    @Test
    public void testBackendInitAndFree() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        // Free the backend
        LlamaCPP.llama_backend_free();
    }

    /**
     * Test for llama_numa_init.
     * This function should not throw any exceptions.
     */
    @Test
    public void testNumaInit() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Initialize NUMA with DISABLED strategy
            LlamaCPP.llama_numa_init(0); // GGML_NUMA_STRATEGY_DISABLED
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }

    /**
     * Test for llama_time_us.
     * This function should return a non-negative value.
     */
    @Test
    public void testTimeUs() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Get current time in microseconds
            long timeUs = LlamaCPP.llama_time_us();

            // Time should be non-negative
            assertTrue(timeUs >= 0, "Time in microseconds should be non-negative");

            // Sleep for a short time
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }

            // Get time again
            long timeUs2 = LlamaCPP.llama_time_us();

            // Second time should be greater than first time
            assertTrue(timeUs2 > timeUs, "Second time should be greater than first time");
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }

    /**
     * Test for llama_max_devices.
     * This function should return a non-negative value.
     */
    @Test
    public void testMaxDevices() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Get maximum number of devices
            long maxDevices = LlamaCPP.llama_max_devices();

            // Max devices should be non-negative
            assertTrue(maxDevices >= 0, "Maximum number of devices should be non-negative");
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }

    /**
     * Test for llama_supports_mmap.
     * This function should return a boolean value.
     */
    @Test
    public void testSupportsMmap() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Check if memory mapping is supported
            boolean supportsMmap = LlamaCPP.llama_supports_mmap();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("Memory mapping supported: " + supportsMmap);
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }

    /**
     * Test for llama_supports_mlock.
     * This function should return a boolean value.
     */
    @Test
    public void testSupportsMlock() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Check if memory locking is supported
            boolean supportsMlock = LlamaCPP.llama_supports_mlock();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("Memory locking supported: " + supportsMlock);
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }

    /**
     * Test for llama_supports_gpu_offload.
     * This function should return a boolean value.
     */
    @Test
    public void testSupportsGpuOffload() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Check if GPU offloading is supported
            boolean supportsGpuOffload = LlamaCPP.llama_supports_gpu_offload();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("GPU offloading supported: " + supportsGpuOffload);
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }

    /**
     * Test for llama_supports_rpc.
     * This function should return a boolean value.
     */
    @Test
    public void testSupportsRpc() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Check if RPC is supported
            boolean supportsRpc = LlamaCPP.llama_supports_rpc();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("RPC supported: " + supportsRpc);
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }

    /**
     * Test for llama_print_system_info.
     * This function should return a non-null string.
     */
    @Test
    public void testPrintSystemInfo() {
        // Initialize the backend
        LlamaCPP.llama_backend_init();

        try {
            // Get system information
            var systemInfo = LlamaCPP.llama_print_system_info();

            // System info should not be null
            assertNotNull(systemInfo, "System information should not be null");

            // Print system information for debugging
            System.out.println("System information: " + systemInfo);
        } finally {
            // Free the backend
            LlamaCPP.llama_backend_free();
        }
    }


}
