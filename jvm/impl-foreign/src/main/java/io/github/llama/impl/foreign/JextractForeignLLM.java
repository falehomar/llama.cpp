package io.github.llama.impl.foreign;

import io.github.llama.api.LLM;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.tokenization.Tokenizer;

import java.io.IOException;
import java.lang.foreign.*;
import java.nio.file.Path;

/**
 * Implementation of the LLM interface using the Java Foreign and Native Memory API with jextract-generated bindings.
 */
public class JextractForeignLLM implements LLM {
    private final MemorySegment modelHandle;
    private final ModelInfo modelInfo;
    private final Tokenizer tokenizer;
    private boolean closed = false;

    /**
     * Creates a new JextractForeignLLM instance.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @throws IOException If the model cannot be loaded
     */
    public JextractForeignLLM(Path modelPath, ModelParams params) throws IOException {
        // Ensure the native library is initialized
        if (!JextractNativeLibrary.isInitialized()) {
            JextractNativeLibrary.initialize();
        }

        // Load the model
        try {
            modelHandle = loadModel(modelPath, params);
            modelInfo = new JextractForeignModelInfo(modelHandle);
            tokenizer = new ForeignTokenizer(modelHandle); // We'll keep using the existing tokenizer for now
        } catch (Exception e) {
            throw new IOException("Failed to load model: " + modelPath, e);
        }
    }

    /**
     * Loads a model from a file.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @return Memory segment containing the model handle
     */
    private MemorySegment loadModel(Path modelPath, ModelParams params) {
        // Get default model parameters
        MemorySegment modelParams = JextractNativeLibrary.llamaModelDefaultParams();

        // Set parameters based on the ModelParams object
        try (Arena arena = Arena.ofConfined()) {
            // Set use_mmap
            boolean useMmap = params.isUseMemoryMapping();
            modelParams.set(ValueLayout.JAVA_BOOLEAN, 16, useMmap); // Offset 16 for use_mmap

            // Set use_mlock
            boolean useMlock = params.isUseMemoryLocking();
            modelParams.set(ValueLayout.JAVA_BOOLEAN, 17, useMlock); // Offset 17 for use_mlock

            // Set n_gpu_layers
            int gpuLayerCount = params.getGpuLayerCount();
            modelParams.set(ValueLayout.JAVA_INT, 0, gpuLayerCount); // Offset 0 for n_gpu_layers

            // Set vocab_only
            boolean vocabOnly = params.isVocabOnly();
            modelParams.set(ValueLayout.JAVA_BOOLEAN, 18, vocabOnly); // Offset 18 for vocab_only

            // Load the model
            return JextractNativeLibrary.llamaModelLoadFromFile(modelPath.toString(), modelParams);
        }
    }

    @Override
    public ModelInfo getModelInfo() {
        checkClosed();
        return modelInfo;
    }

    @Override
    public Context createContext(ContextParams params) {
        checkClosed();
        return new JextractForeignContext(this, params);
    }

    @Override
    public Tokenizer getTokenizer() {
        checkClosed();
        return tokenizer;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        // Free the model
        try {
            if (modelHandle != MemorySegment.NULL) {
                freeModel(modelHandle);
            }
        } catch (Exception e) {
            // Log the error but continue with cleanup
            System.err.println("Error freeing model: " + e.getMessage());
        }

        closed = true;
    }

    /**
     * Frees a model.
     *
     * @param modelHandle Memory segment containing the model handle
     */
    private void freeModel(MemorySegment modelHandle) {
        JextractNativeLibrary.llamaModelFree(modelHandle);
    }

    /**
     * Gets the model handle.
     *
     * @return Memory segment containing the model handle
     */
    MemorySegment getModelHandle() {
        return modelHandle;
    }

    /**
     * Checks if the LLM is closed.
     *
     * @throws IllegalStateException If the LLM is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("LLM is closed");
        }
    }
}
