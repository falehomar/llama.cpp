package io.github.llama.impl.ffm;

import io.github.llama.api.BackendManager;
import io.github.llama.impl.llamacpp.ffm.LlamaCPP;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

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
        LlamaCPP.llama_backend_init();
    }

    @Override
    public void cleanup() {
        LlamaCPP.llama_backend_free();
    }

    @Override
    public void initializeNuma(int strategy) {
        LlamaCPP.llama_numa_init(strategy);
    }

    @Override
    public long getTimeUs() {
        return LlamaCPP.llama_time_us();
    }

    @Override
    public int getMaxDevices() {
        return (int) LlamaCPP.llama_max_devices();
    }

    @Override
    public boolean supportsMmap() {
        return LlamaCPP.llama_supports_mmap();
    }

    @Override
    public boolean supportsMlock() {
        return LlamaCPP.llama_supports_mlock();
    }

    @Override
    public boolean supportsGpuOffload() {
        return LlamaCPP.llama_supports_gpu_offload();
    }

    @Override
    public boolean supportsRpc() {
        return LlamaCPP.llama_supports_rpc();
    }

    @Override
    public String getSystemInfo() {
        MemorySegment infoSegment = LlamaCPP.llama_print_system_info();
        if (infoSegment == null || infoSegment.equals(MemorySegment.NULL)) {
            return "";
        }

        // Convert C string to Java string
        return readCString(infoSegment);
    }

    /**
     * Reads a C string from a memory segment.
     *
     * @param segment Memory segment containing a C string
     * @return Java string
     */
    private String readCString(MemorySegment segment) {
        // Find the null terminator
        long length = 0;
        while (segment.get(ValueLayout.JAVA_BYTE, length) != 0) {
            length++;
        }

        // Convert to Java string
        byte[] bytes = new byte[(int) length];
        for (int i = 0; i < length; i++) {
            bytes[i] = segment.get(ValueLayout.JAVA_BYTE, i);
        }

        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public void setLogCallback(LogCallback callback, Object userData) {
        // For now, just set the callback to null
        // In a real implementation, we would need to create a native callback
        // that calls our Java callback
        LlamaCPP.llama_log_set(MemorySegment.NULL, MemorySegment.NULL);
    }
}
