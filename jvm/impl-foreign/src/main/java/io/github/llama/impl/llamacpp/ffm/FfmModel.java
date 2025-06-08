package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.tokenization.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.MemorySegment;

/**
 * Implementation of {@link Model} using Java's Foreign Function & Memory API.
 * This class provides access to a loaded llama.cpp model.
 */
public class FfmModel implements Model {

    private static final Logger logger = LoggerFactory.getLogger(FfmModel.class);

    private final FfmModelInfo modelInfo;
    private final FfmTokenizer tokenizer;
    private final MemorySegment modelHandle;
    private boolean closed = false;

    /**
     * Creates a new instance of the FfmModel.
     *
     * @param modelInfo   The model information
     * @param tokenizer   The tokenizer
     * @param modelHandle The native model handle
     */
    public FfmModel(FfmModelInfo modelInfo, FfmTokenizer tokenizer, MemorySegment modelHandle) {
        this.modelInfo = modelInfo;
        this.tokenizer = tokenizer;
        this.modelHandle = modelHandle;
        logger.debug("Created FfmModel with info: {}", modelInfo.getDescription());
    }

    @Override
    public ModelInfo getModelInfo() {
        checkClosed();
        return modelInfo;
    }

    @Override
    public Tokenizer getTokenizer() {
        checkClosed();
        return tokenizer;
    }

    /**
     * Gets the native model handle.
     *
     * @return The native model handle
     */
    public MemorySegment getModelHandle() {
        checkClosed();
        return modelHandle;
    }

    @Override
    public void close() {
        if (!closed) {
            logger.info("Closing model");
            if (modelHandle != null && !modelHandle.equals(MemorySegment.NULL)) {
                LlamaCPP.llama_model_free(modelHandle);
            }
            closed = true;
            logger.debug("Model closed");
        }
    }

    /**
     * Checks if the model is closed and throws an exception if it is.
     *
     * @throws IllegalStateException if the model is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Model is closed");
        }
    }
}
