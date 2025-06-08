package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.model.QuantizeParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link FfmModelManager} class.
 */
public class FfmModelManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(FfmModelManagerTest.class);

    @TempDir
    Path tempDir;

    private FfmModelManager modelManager;
    private Path testModelPath;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a mock backend manager
        FfmBackendManager backendManager = new FfmBackendManager();

        // Initialize the model manager
        modelManager = new FfmModelManager(backendManager);

        // Create a dummy model file for testing
        testModelPath = tempDir.resolve("test_model.gguf");
        Files.createFile(testModelPath);
        logger.info("Created test model file at: {}", testModelPath);
    }

    @Test
    public void testGetDefaultModelParams() {
        logger.info("Testing getDefaultModelParams");

        ModelParams params = modelManager.getDefaultModelParams();
        assertNotNull(params, "Default model parameters should not be null");
        assertTrue(params.isUseMemoryMapping(), "Default should use memory mapping");
        assertFalse(params.isUseMemoryLocking(), "Default should not use memory locking");
        assertEquals(0, params.getGpuLayerCount(), "Default GPU layer count should be 0");
        assertFalse(params.isVocabOnly(), "Default should not be vocab only");
    }

    @Test
    public void testLoadModel() throws IOException {
        logger.info("Testing loadModel");

        ModelParams params = new ModelParams();
        Model model = modelManager.loadModel(testModelPath, params);

        assertNotNull(model, "Loaded model should not be null");
        assertTrue(model instanceof FfmModel, "Model should be an instance of FfmModel");
    }

    @Test
    public void testLoadModelWithNullPath() {
        logger.info("Testing loadModel with null path");

        ModelParams params = new ModelParams();
        assertThrows(IOException.class, () -> {
            modelManager.loadModel(null, params);
        }, "Loading model with null path should throw IOException");
    }

    @Test
    public void testLoadModelFromSplits() throws IOException {
        logger.info("Testing loadModelFromSplits");

        // Create dummy split files
        Path splitPath1 = tempDir.resolve("test_model.00.gguf");
        Path splitPath2 = tempDir.resolve("test_model.01.gguf");
        Files.createFile(splitPath1);
        Files.createFile(splitPath2);

        List<Path> modelPaths = Arrays.asList(splitPath1, splitPath2);
        ModelParams params = new ModelParams();

        Model model = modelManager.loadModelFromSplits(modelPaths, params);

        assertNotNull(model, "Loaded model should not be null");
        assertTrue(model instanceof FfmModel, "Model should be an instance of FfmModel");
    }

    @Test
    public void testLoadModelFromSplitsWithNullPaths() {
        logger.info("Testing loadModelFromSplits with null paths");

        ModelParams params = new ModelParams();
        assertThrows(IOException.class, () -> {
            modelManager.loadModelFromSplits(null, params);
        }, "Loading model with null paths should throw IOException");
    }

    @Test
    public void testSaveModel() throws IOException {
        logger.info("Testing saveModel");

        // Create a mock model
        FfmModel mockModel = Mockito.mock(FfmModel.class);
        Path outputPath = tempDir.resolve("output_model.gguf");

        // This should not throw an exception
        modelManager.saveModel(mockModel, outputPath);
    }

    @Test
    public void testSaveModelWithNullModel() {
        logger.info("Testing saveModel with null model");

        Path outputPath = tempDir.resolve("output_model.gguf");
        assertThrows(IOException.class, () -> {
            modelManager.saveModel(null, outputPath);
        }, "Saving null model should throw IOException");
    }

    @Test
    public void testSaveModelWithNullPath() {
        logger.info("Testing saveModel with null path");

        FfmModel mockModel = Mockito.mock(FfmModel.class);
        assertThrows(IOException.class, () -> {
            modelManager.saveModel(mockModel, null);
        }, "Saving model with null path should throw IOException");
    }

    @Test
    public void testQuantizeModel() throws IOException {
        logger.info("Testing quantizeModel");

        Path outputPath = tempDir.resolve("quantized_model.gguf");
        QuantizeParams params = new QuantizeParams();

        // This should not throw an exception
        modelManager.quantizeModel(testModelPath, outputPath, params);
    }

    @Test
    public void testQuantizeModelWithNullInputPath() {
        logger.info("Testing quantizeModel with null input path");

        Path outputPath = tempDir.resolve("quantized_model.gguf");
        QuantizeParams params = new QuantizeParams();

        assertThrows(IOException.class, () -> {
            modelManager.quantizeModel(null, outputPath, params);
        }, "Quantizing model with null input path should throw IOException");
    }

    @Test
    public void testQuantizeModelWithNullOutputPath() {
        logger.info("Testing quantizeModel with null output path");

        QuantizeParams params = new QuantizeParams();

        assertThrows(IOException.class, () -> {
            modelManager.quantizeModel(testModelPath, null, params);
        }, "Quantizing model with null output path should throw IOException");
    }
}
