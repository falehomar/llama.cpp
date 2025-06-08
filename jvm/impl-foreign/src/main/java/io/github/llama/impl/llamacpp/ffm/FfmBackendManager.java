package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.BackendManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link BackendManager} using Java's Foreign Function & Memory API.
 * This class provides access to the llama.cpp backend functionality.
 */
public class FfmBackendManager implements BackendManager {

    private static final Logger logger = LoggerFactory.getLogger(FfmBackendManager.class);

    private boolean initialized = false;
    private int numaStrategy = 0;
    private LogCallback logCallback = null;
    private Object userData = null;

    /**
     * Creates a new instance of the FfmBackendManager.
     */
    public FfmBackendManager() {
        logger.debug("Creating FfmBackendManager");
    }

    @Override
    public void initialize() {
        logger.info("Initializing llama.cpp backend");
        LlamaCPP_1.llama_backend_init();
        initialized = true;
        logger.debug("Backend initialized");
    }

    @Override
    public void cleanup() {
        logger.info("Cleaning up llama.cpp backend");
        LlamaCPP_1.llama_backend_free();
        initialized = false;
        logger.debug("Backend cleaned up");
    }

    @Override
    public void initializeNuma(int strategy) {
        logger.info("Initializing NUMA with strategy: {}", strategy);
        LlamaCPP_1.llama_numa_init(strategy);
        this.numaStrategy = strategy;
        logger.debug("NUMA initialized with strategy: {}", strategy);
    }

    @Override
    public long getTimeUs() {
        return LlamaCPP_1.llama_time_us();
    }

    @Override
    public int getMaxDevices() {
        logger.debug("Getting max devices");
        return (int) LlamaCPP_1.llama_max_devices();
    }

    @Override
    public boolean supportsMmap() {
        logger.debug("Checking if mmap is supported");
        return LlamaCPP_1.llama_supports_mmap();
    }

    @Override
    public boolean supportsMlock() {
        logger.debug("Checking if mlock is supported");
        return LlamaCPP_1.llama_supports_mlock();
    }

    @Override
    public boolean supportsGpuOffload() {
        logger.debug("Checking if GPU offload is supported");
        return LlamaCPP_1.llama_supports_gpu_offload();
    }

    @Override
    public boolean supportsRpc() {
        logger.debug("Checking if RPC is supported");
        return LlamaCPP_1.llama_supports_rpc();
    }

    @Override
    public String getSystemInfo() {
        logger.debug("Getting system info");
        // TODO: Implement actual system info retrieval using FFM API
        return "System information (placeholder)";
    }

    @Override
    public void setLogCallback(LogCallback callback, Object userData) {
        logger.debug("Setting log callback");
        this.logCallback = callback;
        this.userData = userData;
    }

    /**
     * Checks if the backend is initialized.
     *
     * @return true if the backend is initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Gets the current NUMA strategy.
     *
     * @return The current NUMA strategy
     */
    public int getNumaStrategy() {
        return numaStrategy;
    }

    /**
     * Gets the current log callback.
     *
     * @return The current log callback
     */
    public LogCallback getLogCallback() {
        return logCallback;
    }

    /**
     * Gets the current user data.
     *
     * @return The current user data
     */
    public Object getUserData() {
        return userData;
    }
}
