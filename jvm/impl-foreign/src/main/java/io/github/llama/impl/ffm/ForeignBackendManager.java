package io.github.llama.impl.ffm;

import io.github.llama.api.BackendManager;
import io.github.llama.impl.llamacpp.ffm.llama_h;

import java.lang.foreign.MemorySegment;

/**
 * Implementation of the BackendManager interface using the Foreign Function & Memory API.
 */
public class ForeignBackendManager implements BackendManager {

    // Singleton instance
    private static final ForeignBackendManager INSTANCE = new ForeignBackendManager();

    // Private constructor to enforce singleton pattern
    private ForeignBackendManager() {
    }

    /**
     * Gets the singleton instance of the ForeignBackendManager.
     *
     * @return The singleton instance
     */
    public static ForeignBackendManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void initialize() {
        llama_h.llama_backend_init();
    }

    @Override
    public void cleanup() {
        llama_h.llama_backend_free();
    }

    @Override
    public void initializeNuma(int strategy) {
        llama_h.llama_numa_init(strategy);
    }

    @Override
    public long getTimeUs() {
        return llama_h.llama_time_us();
    }

    @Override
    public int getMaxDevices() {
        return (int) llama_h.llama_max_devices();
    }

    @Override
    public boolean supportsMmap() {
        return llama_h.llama_supports_mmap();
    }

    @Override
    public boolean supportsMlock() {
        return llama_h.llama_supports_mlock();
    }

    @Override
    public boolean supportsGpuOffload() {
        return llama_h.llama_supports_gpu_offload();
    }

    @Override
    public boolean supportsRpc() {
        return llama_h.llama_supports_rpc();
    }

    @Override
    public String getSystemInfo() {
        MemorySegment infoSegment = llama_h.llama_print_system_info();
        if (infoSegment == null || infoSegment.equals(MemorySegment.NULL)) {
            return "";
        }
        return infoSegment.toString();
    }

    @Override
    public void setLogCallback(LogCallback callback, Object userData) {
        // For now, just set the callback to null
        // In a real implementation, we would need to create a native callback
        // that calls our Java callback
        llama_h.llama_log_set(MemorySegment.NULL, MemorySegment.NULL);
    }
}
