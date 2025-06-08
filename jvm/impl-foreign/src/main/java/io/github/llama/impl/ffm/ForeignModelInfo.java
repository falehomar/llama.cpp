package io.github.llama.impl.ffm;

import io.github.llama.api.model.ModelInfo;
import io.github.llama.impl.llamacpp.ffm.LlamaCPP;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the ModelInfo interface using the Foreign Function & Memory API.
 */
public class ForeignModelInfo implements ModelInfo {

    // Native model pointer
    private final MemorySegment nativeModel;

    /**
     * Creates a new ForeignModelInfo.
     *
     * @param nativeModel Native model pointer
     */
    public ForeignModelInfo(MemorySegment nativeModel) {
        this.nativeModel = nativeModel;
    }

    @Override
    public long getParameterCount() {
        return LlamaCPP.llama_model_n_params(nativeModel);
    }

    @Override
    public int getContextSize() {
        return LlamaCPP.llama_model_n_ctx_train(nativeModel);
    }

    @Override
    public int getEmbeddingSize() {
        return LlamaCPP.llama_model_n_embd(nativeModel);
    }

    @Override
    public int getLayerCount() {
        return LlamaCPP.llama_model_n_layer(nativeModel);
    }

    @Override
    public int getHeadCount() {
        return LlamaCPP.llama_model_n_head(nativeModel);
    }

    @Override
    public int getKvHeadCount() {
        return LlamaCPP.llama_model_n_head_kv(nativeModel);
    }

    @Override
    public float getRopeFreqScaleTrain() {
        return LlamaCPP.llama_model_rope_freq_scale_train(nativeModel);
    }

    @Override
    public int getRopeType() {
        return LlamaCPP.llama_model_rope_type(nativeModel);
    }

    @Override
    public String getMetadata(String key) {
        try (Arena arena = Arena.ofConfined()) {
            // Convert Java string to C string
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            MemorySegment keySegment = arena.allocate(keyBytes.length + 1);
            keySegment.copyFrom(MemorySegment.ofArray(keyBytes));

            // Allocate buffer for the value
            int bufSize = 1024;
            MemorySegment bufSegment = arena.allocate(bufSize);

            // Get metadata value
            int result = LlamaCPP.llama_model_meta_val_str(nativeModel, keySegment, bufSegment, bufSize);

            // Check if metadata was found
            if (result == 0) {
                return null;
            }

            // Convert C string to Java string
            return readCString(bufSegment);
        }
    }

    @Override
    public Set<String> getMetadataKeys() {
        Set<String> keys = new HashSet<>();

        try (Arena arena = Arena.ofConfined()) {
            // Get metadata count
            int count = LlamaCPP.llama_model_meta_count(nativeModel);

            // Allocate buffer for the key
            int bufSize = 1024;
            MemorySegment bufSegment = arena.allocate(bufSize);

            // Get all metadata keys
            for (int i = 0; i < count; i++) {
                LlamaCPP.llama_model_meta_key_by_index(nativeModel, i, bufSegment, bufSize);
                keys.add(readCString(bufSegment));
            }
        }

        return keys;
    }

    @Override
    public String getDescription() {
        try (Arena arena = Arena.ofConfined()) {
            // Allocate buffer for the description
            int bufSize = 1024;
            MemorySegment bufSegment = arena.allocate(bufSize);

            // Get description
            LlamaCPP.llama_model_desc(nativeModel, bufSegment, bufSize);

            // Convert C string to Java string
            return readCString(bufSegment);
        }
    }

    @Override
    public long getSize() {
        return LlamaCPP.llama_model_size(nativeModel);
    }

    @Override
    public String getChatTemplate() {
        try (Arena arena = Arena.ofConfined()) {
            // Get chat template
            MemorySegment templateSegment = LlamaCPP.llama_model_chat_template(nativeModel, MemorySegment.NULL);

            // Check if template was found
            if (templateSegment.equals(MemorySegment.NULL)) {
                return null;
            }

            // Convert C string to Java string
            return readCString(templateSegment);
        }
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

        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public boolean hasEncoder() {
        return LlamaCPP.llama_model_has_encoder(nativeModel);
    }

    @Override
    public boolean hasDecoder() {
        return LlamaCPP.llama_model_has_decoder(nativeModel);
    }

    @Override
    public int getDecoderStartToken() {
        return LlamaCPP.llama_model_decoder_start_token(nativeModel);
    }

    @Override
    public boolean isRecurrent() {
        return LlamaCPP.llama_model_is_recurrent(nativeModel);
    }
}
