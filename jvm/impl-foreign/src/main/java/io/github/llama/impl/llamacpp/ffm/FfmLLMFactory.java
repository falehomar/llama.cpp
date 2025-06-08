package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.spi.LLMFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of {@link LLMFactory} for creating LLM instances using Java's Foreign Function & Memory API.
 * This factory creates {@link FfmLLM} instances from model files.
 */
public class FfmLLMFactory implements LLMFactory {

    private static final Logger logger = LoggerFactory.getLogger(FfmLLMFactory.class);
    private final FfmModelManager modelManager;
    private final FfmBackendManager backendManager;

    /**
     * Creates a new instance of the FfmLLMFactory.
     */
    public FfmLLMFactory() {
        logger.debug("Creating FfmLLMFactory");
        this.backendManager = new FfmBackendManager();
        this.backendManager.initialize();
        this.modelManager = new FfmModelManager(backendManager);
    }

    @Override
    public String getName() {
        return "ffm";
    }

    @Override
    public LLM createLLM(Path modelPath, ModelParams params) throws IOException {
        logger.info("Creating LLM from model: {}", modelPath);

        if (modelPath == null) {
            throw new IllegalArgumentException("Model path cannot be null");
        }

        if (!Files.exists(modelPath)) {
            throw new IOException("Model file does not exist: " + modelPath);
        }

        if (params == null) {
            params = modelManager.getDefaultModelParams();
            logger.debug("Using default model parameters");
        }

        return (LLM) modelManager.loadModel(modelPath, params);
    }

    @Override
    public boolean supportsModel(Path modelPath) {
        if (modelPath == null) {
            return false;
        }

        if (!Files.exists(modelPath)) {
            return false;
        }

        try {
            // Check if the file has a valid extension
            String fileName = modelPath.getFileName().toString().toLowerCase();
            return fileName.endsWith(".gguf") ||
                   fileName.endsWith(".bin") ||
                   fileName.endsWith(".ggml");
        } catch (Exception e) {
            logger.error("Error checking if model is supported", e);
            return false;
        }
    }
}
