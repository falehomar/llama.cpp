package io.github.llama.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

/**
 * Test class for the BackendManager interface.
 */
public class BackendManagerTest {

    /**
     * Test class that implements BackendManager for testing.
     */
    private static class TestBackendManager implements BackendManager {
        private boolean initialized = false;
        private boolean cleaned = false;
        private int numaStrategy = -1;
        private LogCallback logCallback = null;
        private Object userData = null;

        @Override
        public void initialize() {
            initialized = true;
        }

        @Override
        public void cleanup() {
            cleaned = true;
        }

        @Override
        public void initializeNuma(int strategy) {
            numaStrategy = strategy;
        }

        @Override
        public long getTimeUs() {
            return System.nanoTime() / 1000;
        }

        @Override
        public int getMaxDevices() {
            return 16;
        }

        @Override
        public boolean supportsMmap() {
            return true;
        }

        @Override
        public boolean supportsMlock() {
            return true;
        }

        @Override
        public boolean supportsGpuOffload() {
            return false;
        }

        @Override
        public boolean supportsRpc() {
            return false;
        }

        @Override
        public String getSystemInfo() {
            return "Test System Info";
        }

        @Override
        public void setLogCallback(LogCallback callback, Object userData) {
            this.logCallback = callback;
            this.userData = userData;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public boolean isCleaned() {
            return cleaned;
        }

        public int getNumaStrategy() {
            return numaStrategy;
        }

        public LogCallback getLogCallback() {
            return logCallback;
        }

        public Object getUserData() {
            return userData;
        }
    }

    @Test
    public void testInitializeAndCleanup() {
        TestBackendManager manager = new TestBackendManager();

        // Test initialize
        manager.initialize();
        assertTrue(manager.isInitialized(), "Backend should be initialized");

        // Test cleanup
        manager.cleanup();
        assertTrue(manager.isCleaned(), "Backend should be cleaned up");
    }

    @Test
    public void testInitializeNuma() {
        TestBackendManager manager = new TestBackendManager();

        // Test initialize NUMA
        manager.initializeNuma(1); // DISTRIBUTE strategy
        assertEquals(1, manager.getNumaStrategy(), "NUMA strategy should be set to DISTRIBUTE");
    }

    @Test
    public void testGetTimeUs() {
        TestBackendManager manager = new TestBackendManager();

        // Test getTimeUs
        long time1 = manager.getTimeUs();

        // Sleep for a short time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        long time2 = manager.getTimeUs();

        // Second time should be greater than first time
        assertTrue(time2 > time1, "Second time should be greater than first time");
    }

    @Test
    public void testGetMaxDevices() {
        TestBackendManager manager = new TestBackendManager();

        // Test getMaxDevices
        int maxDevices = manager.getMaxDevices();
        assertEquals(16, maxDevices, "Maximum number of devices should be 16");
    }

    @Test
    public void testSupportsMmap() {
        TestBackendManager manager = new TestBackendManager();

        // Test supportsMmap
        boolean supportsMmap = manager.supportsMmap();
        assertTrue(supportsMmap, "Memory mapping should be supported");
    }

    @Test
    public void testSupportsMlock() {
        TestBackendManager manager = new TestBackendManager();

        // Test supportsMlock
        boolean supportsMlock = manager.supportsMlock();
        assertTrue(supportsMlock, "Memory locking should be supported");
    }

    @Test
    public void testSupportsGpuOffload() {
        TestBackendManager manager = new TestBackendManager();

        // Test supportsGpuOffload
        boolean supportsGpuOffload = manager.supportsGpuOffload();
        assertFalse(supportsGpuOffload, "GPU offloading should not be supported");
    }

    @Test
    public void testSupportsRpc() {
        TestBackendManager manager = new TestBackendManager();

        // Test supportsRpc
        boolean supportsRpc = manager.supportsRpc();
        assertFalse(supportsRpc, "RPC should not be supported");
    }

    @Test
    public void testGetSystemInfo() {
        TestBackendManager manager = new TestBackendManager();

        // Test getSystemInfo
        String systemInfo = manager.getSystemInfo();
        assertEquals("Test System Info", systemInfo, "System info should match expected value");
    }

    @Test
    public void testSetLogCallback() {
        TestBackendManager manager = new TestBackendManager();

        // Create a mock callback
        BackendManager.LogCallback callback = Mockito.mock(BackendManager.LogCallback.class);
        Object userData = new Object();

        // Test setLogCallback
        manager.setLogCallback(callback, userData);

        // Verify that the callback and user data were set
        assertEquals(callback, manager.getLogCallback(), "Log callback should be set");
        assertEquals(userData, manager.getUserData(), "User data should be set");
    }
}
