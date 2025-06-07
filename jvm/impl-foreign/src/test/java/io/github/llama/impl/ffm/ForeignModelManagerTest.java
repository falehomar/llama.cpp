package io.github.llama.impl.ffm;

import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.model.ModelManager;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.model.QuantizeParams;
import io.github.llama.api.tokenization.Tokenizer;
import io.github.llama.impl.llamacpp.ffm.llama_h;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ForeignModelManager.
 * Following TDD principles, these tests are written before implementing or fixing the actual functionality.
 */
public class ForeignModelManagerTest {

    // Path to a test model file - this should be a small test model
    // In a real implementation, this would point to a real model file
    private static final String TEST_MODEL_PATH = "/path/to/test/model.gguf";

    @BeforeAll
    public static void setUp() {
        // Initialize the backend before running any tests
        llama_h.llama_backend_init();
    }

    /**
     * Test for getInstance method.
     * This method should always return the same instance (singleton pattern).
     */
    @Test
    public void testGetInstance() {
        ModelManager manager1 = ForeignModelManager.getInstance();
        ModelManager manager2 = ForeignModelManager.getInstance();

        // Both instances should be the same object
        assertSame(manager1, manager2, "getInstance should always return the same instance");

        // The instance should not be null
        assertNotNull(manager1, "getInstance should not return null");
    }

    /**
     * Test for getDefaultModelParams method.
     * This method should return a non-null ModelParams object with default values.
     */
    @Test
    public void testGetDefaultModelParams() {
        ModelManager manager = ForeignModelManager.getInstance();

        // Get default model parameters
        ModelParams params = manager.getDefaultModelParams();

        // Params should not be null
        assertNotNull(params, "Default model parameters should not be null");

        // Check that the default values are set
        // These assertions might need to be adjusted based on the actual default values
        assertNotNull(params.isUseMemoryMapping(), "useMemoryMapping should be set");
        assertNotNull(params.isUseMemoryLocking(), "useMemoryLocking should be set");
        assertNotNull(params.getGpuLayerCount(), "gpuLayerCount should be set");
        assertNotNull(params.isVocabOnly(), "vocabOnly should be set");
    }

    /**
     * Test for loadModel method with invalid path.
     * This method should throw an exception when the model file doesn't exist.
     */
    @Test
    public void testLoadModelInvalidPath() {
        ModelManager manager = ForeignModelManager.getInstance();
        ModelParams params = manager.getDefaultModelParams();

        // Try to load a model from a non-existent path
        Path invalidPath = Path.of("/non/existent/path/model.gguf");

        // This should throw an exception (either IOException or IndexOutOfBoundsException)
        assertThrows(Exception.class, () -> {
            try (Model model = manager.loadModel(invalidPath, params)) {
                // This code should not be reached, but if it is, the model will be properly closed
            }
        }, "Loading a model from an invalid path should throw an exception");
    }

    /**
     * Test for loadModelFromSplits method with null or empty paths.
     * This method should throw an IOException when the paths list is null or empty.
     */
    @Test
    public void testLoadModelFromSplitsNullOrEmpty() {
        ModelManager manager = ForeignModelManager.getInstance();
        ModelParams params = manager.getDefaultModelParams();

        // Try to load a model with null paths
        assertThrows(IOException.class, () -> {
            manager.loadModelFromSplits(null, params);
        }, "Loading a model with null paths should throw IOException");

        // Try to load a model with empty paths
        assertThrows(IOException.class, () -> {
            manager.loadModelFromSplits(List.of(), params);
        }, "Loading a model with empty paths should throw IOException");
    }

    /**
     * Test for saveModel method with null model.
     * This method should throw an IOException when the model is null.
     */
    @Test
    public void testSaveModelNullModel() {
        ModelManager manager = ForeignModelManager.getInstance();

        // Try to save a null model
        assertThrows(IOException.class, () -> {
            manager.saveModel(null, Path.of("/some/path/model.gguf"));
        }, "Saving a null model should throw IOException");
    }

    /**
     * Test for saveModel method with wrong model type.
     * This method should throw an IOException when the model is not a ForeignModel.
     */
    @Test
    public void testSaveModelWrongType() {
        ModelManager manager = ForeignModelManager.getInstance();

        // Create a mock model that is not a ForeignModel
        Model nonForeignModel = Mockito.mock(Model.class);

        // Try to save a non-ForeignModel
        assertThrows(IOException.class, () -> {
            manager.saveModel(nonForeignModel, Path.of("/some/path/model.gguf"));
        }, "Saving a non-ForeignModel should throw IOException");
    }

    /**
     * Test for quantizeModel method.
     * This method should throw an IOException when the input file doesn't exist.
     */
    @Test
    public void testQuantizeModelInvalidInput() {
        ModelManager manager = ForeignModelManager.getInstance();
        QuantizeParams params = new QuantizeParams();

        // Try to quantize a model from a non-existent path
        Path invalidInputPath = Path.of("/non/existent/path/model.gguf");
        Path outputPath = Path.of("/some/path/quantized_model.gguf");

        // This should throw an IOException
        assertThrows(IOException.class, () -> {
            manager.quantizeModel(invalidInputPath, outputPath, params);
        }, "Quantizing a model from an invalid path should throw IOException");
    }
}
