package io.github.llama.api.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface for managing models.
 * This interface provides methods for loading, saving, and managing models.
 */
public interface ModelManager {

    /**
     * Gets the default model parameters.
     *
     * @return Default model parameters
     */
    ModelParams getDefaultModelParams();

    /**
     * Loads a model from a file.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @return The loaded model
     * @throws IOException If the model cannot be loaded
     */
    Model loadModel(Path modelPath, ModelParams params) throws IOException;

    /**
     * Loads a model from multiple split files.
     *
     * @param modelPaths List of paths to the model split files
     * @param params Model parameters
     * @return The loaded model
     * @throws IOException If the model cannot be loaded
     */
    Model loadModelFromSplits(List<Path> modelPaths, ModelParams params) throws IOException;

    /**
     * Saves a model to a file.
     *
     * @param model The model to save
     * @param modelPath Path to the output file
     * @throws IOException If the model cannot be saved
     */
    void saveModel(Model model, Path modelPath) throws IOException;

    /**
     * Quantizes a model.
     *
     * @param inputPath Path to the input model file
     * @param outputPath Path to the output model file
     * @param params Quantization parameters
     * @throws IOException If the model cannot be quantized
     */
    void quantizeModel(Path inputPath, Path outputPath, QuantizeParams params) throws IOException;
}
