package io.github.llama.impl.foreign;

import io.github.llama.api.model.ModelInfo;

import java.lang.foreign.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the ModelInfo interface using the Java Foreign and Native Memory API with jextract-generated bindings.
 */
public class JextractForeignModelInfo implements ModelInfo {
    private final MemorySegment modelHandle;
    private final Map<String, String> metadata;

    /**
     * Creates a new JextractForeignModelInfo instance.
     *
     * @param modelHandle Memory segment containing the model handle
     */
    public JextractForeignModelInfo(MemorySegment modelHandle) {
        this.modelHandle = modelHandle;
        this.metadata = loadMetadata();
    }

    @Override
    public long getParameterCount() {
        return JextractNativeLibrary.llamaModelNParams(modelHandle);
    }

    @Override
    public int getContextSize() {
        return JextractNativeLibrary.llamaModelNCtxTrain(modelHandle);
    }

    @Override
    public int getEmbeddingSize() {
        return JextractNativeLibrary.llamaModelNEmbd(modelHandle);
    }

    @Override
    public int getLayerCount() {
        return JextractNativeLibrary.llamaModelNLayer(modelHandle);
    }

    @Override
    public int getHeadCount() {
        return JextractNativeLibrary.llamaModelNHead(modelHandle);
    }

    @Override
    public String getMetadata(String key) {
        return metadata.get(key);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return Collections.unmodifiableSet(metadata.keySet());
    }

    @Override
    public String getDescription() {
        return JextractNativeLibrary.llamaModelDesc(modelHandle);
    }

    /**
     * Loads metadata from the model.
     *
     * @return Map of metadata key-value pairs
     */
    private Map<String, String> loadMetadata() {
        Map<String, String> result = new HashMap<>();

        // TODO: Implement using jextract-generated bindings to call llama_model_meta_count and llama_model_meta_key_by_index/llama_model_meta_val_str_by_index

        return result;
    }
}
