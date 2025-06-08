package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.tokenization.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Model} using Java's Foreign Function & Memory API.
 * This class provides access to a loaded llama.cpp model.
 */
public class FfmModel implements Model {

    private static final Logger logger = LoggerFactory.getLogger(FfmModel.class);

    private final FfmModelInfo modelInfo;
    private final FfmTokenizer tokenizer;
    private boolean closed = false;

    /**
     * Creates a new instance of the FfmModel.
     *
     * @param modelInfo The model information
     * @param tokenizer The tokenizer
     */
    public FfmModel(FfmModelInfo modelInfo, FfmTokenizer tokenizer) {
        this.modelInfo = modelInfo;
        this.tokenizer = tokenizer;
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

    @Override
    public void close() {
        if (!closed) {
            logger.info("Closing model");
            // TODO: Implement actual cleanup using FFM API
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
