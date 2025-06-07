package io.github.llama.impl.ffm;

import io.github.llama.api.BackendManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ForeignBackendManager.
 */
public class ForeignBackendManagerTest {

    @Test
    public void testInitializeAndCleanup() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        // Cleanup should not throw any exceptions
        manager.cleanup();
    }

    @Test
    public void testInitializeNuma() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Initialize NUMA with DISABLED strategy
            manager.initializeNuma(0);
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testGetTimeUs() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Get current time in microseconds
            long time1 = manager.getTimeUs();

            // Sleep for a short time
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }

            // Get time again
            long time2 = manager.getTimeUs();

            // Second time should be greater than first time
            assertTrue(time2 > time1, "Second time should be greater than first time");
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testGetMaxDevices() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Get maximum number of devices
            int maxDevices = manager.getMaxDevices();

            // Max devices should be non-negative
            assertTrue(maxDevices >= 0, "Maximum number of devices should be non-negative");
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testSupportsMmap() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Check if memory mapping is supported
            boolean supportsMmap = manager.supportsMmap();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("Memory mapping supported: " + supportsMmap);
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testSupportsMlock() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Check if memory locking is supported
            boolean supportsMlock = manager.supportsMlock();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("Memory locking supported: " + supportsMlock);
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testSupportsGpuOffload() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Check if GPU offloading is supported
            boolean supportsGpuOffload = manager.supportsGpuOffload();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("GPU offloading supported: " + supportsGpuOffload);
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testSupportsRpc() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Check if RPC is supported
            boolean supportsRpc = manager.supportsRpc();

            // Just verify that we can call the function, no assertion on the result
            // as it depends on the platform
            System.out.println("RPC supported: " + supportsRpc);
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testGetSystemInfo() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Get system information
            String systemInfo = manager.getSystemInfo();

            // System info should not be null
            assertNotNull(systemInfo, "System information should not be null");

            // Print system information for debugging
            System.out.println("System information: " + systemInfo);
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }

    @Test
    public void testSetLogCallback() {
        BackendManager manager = ForeignBackendManager.getInstance();

        // Initialize the backend
        manager.initialize();

        try {
            // Set log callback to null
            manager.setLogCallback(null, null);
        } finally {
            // Cleanup
            manager.cleanup();
        }
    }
}
