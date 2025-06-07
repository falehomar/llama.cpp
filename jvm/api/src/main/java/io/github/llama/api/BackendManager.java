package io.github.llama.api;

/**
 * Interface for managing the backend of the LLM system.
 * This interface provides methods for initializing and cleaning up the backend,
 * as well as getting system information and checking system capabilities.
 */
public interface BackendManager {

    /**
     * Initializes the backend.
     * This method must be called before using any other LLM functionality.
     */
    void initialize();

    /**
     * Cleans up resources used by the backend.
     * This method should be called when the application is shutting down.
     */
    void cleanup();

    /**
     * Initializes NUMA optimization strategy.
     *
     * @param strategy NUMA optimization strategy (0 = disabled, 1 = distribute, 2 = isolate, 3 = numactl, 4 = mirror)
     */
    void initializeNuma(int strategy);

    /**
     * Gets the current time in microseconds.
     *
     * @return Current time in microseconds
     */
    long getTimeUs();

    /**
     * Gets the maximum number of supported devices.
     *
     * @return Maximum number of supported devices
     */
    int getMaxDevices();

    /**
     * Checks if memory mapping is supported.
     *
     * @return true if memory mapping is supported, false otherwise
     */
    boolean supportsMmap();

    /**
     * Checks if memory locking is supported.
     *
     * @return true if memory locking is supported, false otherwise
     */
    boolean supportsMlock();

    /**
     * Checks if GPU offloading is supported.
     *
     * @return true if GPU offloading is supported, false otherwise
     */
    boolean supportsGpuOffload();

    /**
     * Checks if RPC is supported.
     *
     * @return true if RPC is supported, false otherwise
     */
    boolean supportsRpc();

    /**
     * Gets system information.
     *
     * @return String containing system information
     */
    String getSystemInfo();

    /**
     * Sets the logging callback.
     *
     * @param callback Callback function for handling log messages
     * @param userData User data to pass to the callback
     */
    void setLogCallback(LogCallback callback, Object userData);

    /**
     * Interface for log callback functions.
     */
    interface LogCallback {
        /**
         * Called when a log message is generated.
         *
         * @param level Log level (0 = error, 1 = warning, 2 = info, 3 = debug)
         * @param message Log message
         * @param userData User data passed to setLogCallback
         */
        void log(int level, String message, Object userData);
    }
}
