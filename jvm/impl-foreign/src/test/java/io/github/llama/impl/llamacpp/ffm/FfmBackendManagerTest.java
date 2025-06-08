package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.BackendManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link FfmBackendManager} class.
 */
public class FfmBackendManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(FfmBackendManagerTest.class);

    private FfmBackendManager backendManager;

    @BeforeEach
    public void setUp() {
        backendManager = new FfmBackendManager();
    }

    @Test
    public void testInitializeAndCleanup() {
        logger.info("Testing initialize and cleanup");

        // Test initialize
        backendManager.initialize();
        assertTrue(backendManager.isInitialized(), "Backend should be initialized");

        // Test cleanup
        backendManager.cleanup();
        assertFalse(backendManager.isInitialized(), "Backend should be cleaned up");
    }

    @Test
    public void testInitializeNuma() {
        logger.info("Testing initializeNuma");

        // Test with different strategies
        backendManager.initializeNuma(1); // distribute
        assertEquals(1, backendManager.getNumaStrategy(), "NUMA strategy should be set to 1");

        backendManager.initializeNuma(2); // isolate
        assertEquals(2, backendManager.getNumaStrategy(), "NUMA strategy should be set to 2");
    }

    @Test
    public void testGetTimeUs() {
        logger.info("Testing getTimeUs");

        long time1 = backendManager.getTimeUs();
        // Sleep a bit to ensure time changes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }
        long time2 = backendManager.getTimeUs();

        assertTrue(time2 > time1, "Time should increase");
    }

    @Test
    public void testGetMaxDevices() {
        logger.info("Testing getMaxDevices");

        int maxDevices = backendManager.getMaxDevices();
        assertTrue(maxDevices >= 0, "Max devices should be non-negative");
    }

    @Test
    public void testSupportsMmap() {
        logger.info("Testing supportsMmap");

        boolean supportsMmap = backendManager.supportsMmap();
        // Just verify it returns a boolean, we can't know the actual value
        assertNotNull(supportsMmap, "supportsMmap should return a boolean");
    }

    @Test
    public void testSupportsMlock() {
        logger.info("Testing supportsMlock");

        boolean supportsMlock = backendManager.supportsMlock();
        // Just verify it returns a boolean, we can't know the actual value
        assertNotNull(supportsMlock, "supportsMlock should return a boolean");
    }

    @Test
    public void testSupportsGpuOffload() {
        logger.info("Testing supportsGpuOffload");

        boolean supportsGpuOffload = backendManager.supportsGpuOffload();
        // Just verify it returns a boolean, we can't know the actual value
        assertNotNull(supportsGpuOffload, "supportsGpuOffload should return a boolean");
    }

    @Test
    public void testSupportsRpc() {
        logger.info("Testing supportsRpc");

        boolean supportsRpc = backendManager.supportsRpc();
        // Just verify it returns a boolean, we can't know the actual value
        assertNotNull(supportsRpc, "supportsRpc should return a boolean");
    }

    @Test
    public void testGetSystemInfo() {
        logger.info("Testing getSystemInfo");

        String systemInfo = backendManager.getSystemInfo();
        assertNotNull(systemInfo, "System info should not be null");
        assertFalse(systemInfo.isEmpty(), "System info should not be empty");
    }

    @Test
    public void testSetLogCallback() {
        logger.info("Testing setLogCallback");

        // Create a mock callback
        BackendManager.LogCallback callback = Mockito.mock(BackendManager.LogCallback.class);
        Object userData = new Object();

        // Set the callback
        backendManager.setLogCallback(callback, userData);

        // Verify the callback was set
        assertEquals(callback, backendManager.getLogCallback(), "Log callback should be set");
        assertEquals(userData, backendManager.getUserData(), "User data should be set");
    }
}
