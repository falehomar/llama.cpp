package io.github.llama.impl.foreign;

import io.github.llama.api.model.ModelInfo;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the ModelInfo interface using the Java Foreign and Native Memory API.
 */
public class ForeignModelInfo implements ModelInfo {
    private final MemorySegment modelHandle;
    private final Map<String, String> metadata;

    /**
     * Creates a new ForeignModelInfo instance.
     *
     * @param modelHandle Memory segment containing the model handle
     */
    public ForeignModelInfo(MemorySegment modelHandle) {
        this.modelHandle = modelHandle;
        this.metadata = loadMetadata();
    }

    @Override
    public long getParameterCount() {
        return NativeLibrary.llamaModelNParams(modelHandle);
    }

    @Override
    public int getContextSize() {
        return NativeLibrary.llamaModelNCtxTrain(modelHandle);
    }

    @Override
    public int getEmbeddingSize() {
        return NativeLibrary.llamaModelNEmbd(modelHandle);
    }

    @Override
    public int getLayerCount() {
        return NativeLibrary.llamaModelNLayer(modelHandle);
    }

    @Override
    public int getHeadCount() {
        return NativeLibrary.llamaModelNHead(modelHandle);
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
        return NativeLibrary.llamaModelDesc(modelHandle);
    }

    /**
     * Loads metadata from the model.
     *
     * @return Map of metadata key-value pairs
     */
    private Map<String, String> loadMetadata() {
        Map<String, String> result = new HashMap<>();

        // TODO: Implement using Java Foreign API to call llama_model_meta_count and llama_model_meta_key_by_index/llama_model_meta_val_str_by_index

        return result;
    }
}
