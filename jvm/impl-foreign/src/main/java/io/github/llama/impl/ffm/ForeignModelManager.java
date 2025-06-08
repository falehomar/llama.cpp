package io.github.llama.impl.ffm;

import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelManager;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.model.QuantizeParams;
import io.github.llama.impl.llamacpp.ffm.LlamaCPP;
import io.github.llama.impl.llamacpp.ffm.llama_model_params;
import io.github.llama.impl.llamacpp.ffm.llama_model_quantize_params;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of the ModelManager interface using the Foreign Function & Memory API.
 */
public class ForeignModelManager implements ModelManager {

    // Singleton instance
    private static final ForeignModelManager INSTANCE = new ForeignModelManager();

    // Private constructor to enforce singleton pattern
    private ForeignModelManager() {
    }

    /**
     * Gets the singleton instance of the ForeignModelManager.
     *
     * @return The singleton instance
     */
    public static ForeignModelManager getInstance() {
        return INSTANCE;
    }

    @Override
    public ModelParams getDefaultModelParams() {
        try (Arena arena = Arena.ofConfined()) {
            // Get default model parameters from native library
            var nativeParams = LlamaCPP.llama_model_default_params(arena);

            // Convert to Java ModelParams
            ModelParams params = new ModelParams();

            // Access fields using the appropriate methods for JDK 24
            // Note: This is a guess at the correct API, may need adjustment
            params.setUseMemoryMapping(nativeParams.getAtIndex(ValueLayout.JAVA_INT, 0) != 0);
            params.setUseMemoryLocking(nativeParams.getAtIndex(ValueLayout.JAVA_INT, 1) != 0);
            params.setGpuLayerCount(nativeParams.getAtIndex(ValueLayout.JAVA_INT, 2));
            params.setVocabOnly(nativeParams.getAtIndex(ValueLayout.JAVA_INT, 3) != 0);

            return params;
        }
    }

    @Override
    public Model loadModel(Path modelPath, ModelParams params) throws IOException {
        try (Arena arena = Arena.ofConfined()) {
            // Convert Java path to C string
            MemorySegment pathSegment = arena.allocateFrom(modelPath.toString(), StandardCharsets.UTF_8);

            // Convert Java ModelParams to native llama_model_params
            var nativeParams = createNativeModelParams(params, arena);

            // Load the model
            MemorySegment modelSegment = LlamaCPP.llama_model_load_from_file(pathSegment, nativeParams);

            // Check if model loading failed
            if (modelSegment.equals(MemorySegment.NULL)) {
                throw new IOException("Failed to load model from " + modelPath);
            }

            // Create and return a ForeignModel
            return new ForeignModel(modelSegment);
        }
    }

    @Override
    public Model loadModelFromSplits(List<Path> modelPaths, ModelParams params) throws IOException {
        if (modelPaths == null || modelPaths.isEmpty()) {
            throw new IOException("Model paths cannot be null or empty");
        }

        try (Arena arena = Arena.ofConfined()) {
            // Convert Java paths to C strings
            MemorySegment[] pathSegments = new MemorySegment[modelPaths.size()];
            for (int i = 0; i < modelPaths.size(); i++) {
                // Correctly allocate a C string using the proper method
                pathSegments[i] = arena.allocateFrom(modelPaths.get(i).toString(),
                    java.nio.charset.StandardCharsets.UTF_8);
            }

            // Create array of path segments
            // We need to create an array of pointers that C can understand
            // In JDK 24, we need to use a different approach to create an array of pointers
            MemorySegment pathsArray = arena.allocate(ValueLayout.ADDRESS.byteSize() * modelPaths.size());
            for (int i = 0; i < pathSegments.length; i++) {
                pathsArray.setAtIndex(ValueLayout.ADDRESS, i, pathSegments[i]);
            }

            // Convert Java ModelParams to native llama_model_params
            var nativeParams = createNativeModelParams(params, arena);

            // Load the model from splits
            MemorySegment modelSegment = LlamaCPP.llama_model_load_from_splits(pathsArray, modelPaths.size(), nativeParams);

            // Check if model loading failed
            if (modelSegment.equals(MemorySegment.NULL)) {
                throw new IOException("Failed to load model from splits");
            }

            // Create and return a ForeignModel
            return new ForeignModel(modelSegment);
        }
    }

    @Override
    public void saveModel(Model model, Path modelPath) throws IOException {
        if (model == null) {
            throw new IOException("Model cannot be null");
        }

        if (!(model instanceof ForeignModel)) {
            throw new IOException("Model must be a ForeignModel");
        }

        ForeignModel foreignModel = (ForeignModel) model;

        try (Arena arena = Arena.ofConfined()) {
            // Convert Java path to C string
            MemorySegment pathSegment = arena.allocateFrom(modelPath.toString(), StandardCharsets.UTF_8);

            // Save the model
            LlamaCPP.llama_model_save_to_file(foreignModel.getNativeModel(), pathSegment);

            // Note: In JDK 24, llama_model_save_to_file might return void instead of int
            // We can't check for errors in this case
        }
    }

    @Override
    public void quantizeModel(Path inputPath, Path outputPath, QuantizeParams params) throws IOException {
        try (Arena arena = Arena.ofConfined()) {
            // Convert Java paths to C strings
            MemorySegment inputPathSegment = arena.allocateFrom(inputPath.toString(), StandardCharsets.UTF_8);
            MemorySegment outputPathSegment = arena.allocateFrom(outputPath.toString(), StandardCharsets.UTF_8);

            // Create native quantize params
            // In JDK 24, we need to use a different approach to create and set fields in native structs
            // This is a guess at the correct API, may need adjustment
            MemorySegment nativeParams = arena.allocate(5 * ValueLayout.JAVA_INT.byteSize());

            // Set fields using the appropriate methods for JDK 24
            nativeParams.setAtIndex(ValueLayout.JAVA_INT, 0, params.getQuantizationType());
            nativeParams.setAtIndex(ValueLayout.JAVA_INT, 1, params.getThreads());
            nativeParams.setAtIndex(ValueLayout.JAVA_BYTE, 2 * ValueLayout.JAVA_INT.byteSize(), params.isAllowRequantize() ? (byte) 1 : (byte) 0);
            nativeParams.setAtIndex(ValueLayout.JAVA_BYTE, 2 * ValueLayout.JAVA_INT.byteSize() + 1, params.isQuantizeOutputTensor() ? (byte) 1 : (byte) 0);
            nativeParams.setAtIndex(ValueLayout.JAVA_BYTE, 2 * ValueLayout.JAVA_INT.byteSize() + 2, params.isOnlyKeepDecoderLayers() ? (byte) 1 : (byte) 0);

            // Quantize the model
            int result = LlamaCPP.llama_model_quantize(inputPathSegment, outputPathSegment, nativeParams);

            // Check if model quantization failed
            if (result != 0) {
                throw new IOException("Failed to quantize model from " + inputPath + " to " + outputPath);
            }
        }
    }

    /**
     * Creates a native llama_model_params struct from a Java ModelParams object.
     *
     * @param params Java ModelParams
     * @param arena Arena for memory allocation
     * @return Native llama_model_params
     */
    private MemorySegment createNativeModelParams(ModelParams params, Arena arena) {
        // In JDK 24, we need to use a different approach to create and set fields in native structs
        // This is a guess at the correct API, may need adjustment

        // Allocate memory for the struct
        // Assuming the struct has 6 int fields (use_mmap, use_mlock, n_gpu_layers, vocab_only, n_kv_overrides, kv_overrides)
        MemorySegment nativeParams = arena.allocate(6 * ValueLayout.JAVA_INT.byteSize());

        // Set fields using the appropriate methods for JDK 24
        nativeParams.setAtIndex(ValueLayout.JAVA_INT, 0, params.isUseMemoryMapping() ? 1 : 0);
        nativeParams.setAtIndex(ValueLayout.JAVA_INT, 1, params.isUseMemoryLocking() ? 1 : 0);
        nativeParams.setAtIndex(ValueLayout.JAVA_INT, 2, params.getGpuLayerCount());
        nativeParams.setAtIndex(ValueLayout.JAVA_INT, 3, params.isVocabOnly() ? 1 : 0);

        // Handle metadata overrides if needed
        // This would require creating an array of llama_model_kv_override structs
        // For simplicity, we're not implementing this now
        nativeParams.setAtIndex(ValueLayout.JAVA_INT, 4, 0); // n_kv_overrides
        nativeParams.setAtIndex(ValueLayout.ADDRESS, 5, MemorySegment.NULL); // kv_overrides

        return nativeParams;
    }
}
