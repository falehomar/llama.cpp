package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.LLM;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.tokenization.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link LLM} using Java's Foreign Function & Memory API.
 * This class wraps a {@link FfmModel} and provides the LLM interface.
 */
public class FfmLLM implements LLM {

    private static final Logger logger = LoggerFactory.getLogger(FfmLLM.class);

    private final FfmModel model;
    private boolean closed = false;

    /**
     * Creates a new instance of the FfmLLM.
     *
     * @param model The model to wrap
     */
    public FfmLLM(FfmModel model) {
        this.model = model;
        logger.debug("Created FfmLLM wrapping model: {}", model.getModelInfo().getDescription());
    }

    @Override
    public ModelInfo getModelInfo() {
        checkClosed();
        return model.getModelInfo();
    }

    @Override
    public Context createContext(ContextParams params) {
        checkClosed();

        if (params == null) {
            logger.error("Cannot create context with null parameters");
            throw new IllegalArgumentException("Context parameters cannot be null");
        }

        logger.debug("Creating context with parameters: contextSize={}, batchSize={}, threadCount={}, logitsAll={}",
                params.getContextSize(), params.getBatchSize(), params.getThreadCount(), params.isLogitsAll());

        return new FfmContext(model, params);
    }

    @Override
    public Tokenizer getTokenizer() {
        checkClosed();
        return model.getTokenizer();
    }

    @Override
    public void close() {
        if (!closed) {
            logger.info("Closing LLM");
            model.close();
            closed = true;
            logger.debug("LLM closed");
        }
    }

    /**
     * Gets the wrapped model.
     *
     * @return The wrapped model
     */
    public FfmModel getWrappedModel() {
        return model;
    }

    /**
     * Checks if the LLM is closed and throws an exception if it is.
     *
     * @throws IllegalStateException if the LLM is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("LLM is closed");
        }
    }
}
