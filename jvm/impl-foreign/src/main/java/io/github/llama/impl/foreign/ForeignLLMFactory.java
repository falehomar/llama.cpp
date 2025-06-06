package io.github.llama.impl.foreign;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.spi.LLMFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of the LLMFactory interface for creating ForeignLLM instances.
 */
public class ForeignLLMFactory implements LLMFactory {
    private static final String FACTORY_NAME = "foreign";
    private static final String[] SUPPORTED_EXTENSIONS = {".gguf"};

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public LLM createLLM(Path modelPath, ModelParams params) throws IOException {
        // Initialize the native library if not already initialized
        if (!NativeLibrary.isInitialized()) {
            NativeLibrary.initialize();
        }

        // Create a new ForeignLLM instance
        return new ForeignLLM(modelPath, params);
    }

    @Override
    public boolean supportsModel(Path modelPath) {
        // Check if the file exists
        if (!Files.exists(modelPath) || !Files.isRegularFile(modelPath)) {
            return false;
        }

        // Check if the file has a supported extension
        String fileName = modelPath.getFileName().toString().toLowerCase();
        for (String extension : SUPPORTED_EXTENSIONS) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }
}
