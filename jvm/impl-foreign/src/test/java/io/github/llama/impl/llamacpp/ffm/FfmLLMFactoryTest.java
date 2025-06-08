package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.LLM;
import io.github.llama.api.LLMFactoryRegistry;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.spi.LLMFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link FfmLLMFactory}.
 */
public class FfmLLMFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger(FfmLLMFactoryTest.class);

    @TempDir
    Path tempDir;

    private Path testModelPath;
    private FfmLLMFactory factory;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a dummy model file for testing
        testModelPath = tempDir.resolve("test-model.gguf");
        Files.createFile(testModelPath);

        // Write some dummy content to the file
        Files.writeString(testModelPath, "This is a dummy model file for testing");

        factory = new FfmLLMFactory();
    }

    @Test
    public void testFactoryRegistration() {
        logger.info("Testing factory registration via SPI");

        // Get the factory registry
        LLMFactoryRegistry registry = LLMFactoryRegistry.getInstance();

        // Get all factories
        List<LLMFactory> factories = registry.getFactories();

        // Check if our factory is registered
        boolean found = false;
        for (LLMFactory factory : factories) {
            if (factory instanceof FfmLLMFactory) {
                found = true;
                break;
            }
        }

        assertTrue(found, "FfmLLMFactory should be registered via SPI");

        // Get the factory by name
        LLMFactory factoryByName = registry.getFactory("ffm");
        assertNotNull(factoryByName, "Factory should be found by name");
        assertEquals("ffm", factoryByName.getName(), "Factory name should be 'ffm'");
    }

    @Test
    public void testSupportsModel() {
        logger.info("Testing model detection");

        // Test with a valid model file
        assertTrue(factory.supportsModel(testModelPath), "Factory should support .gguf files");

        // Test with a non-existent file
        assertFalse(factory.supportsModel(tempDir.resolve("non-existent.gguf")),
                "Factory should not support non-existent files");

        // Test with null
        assertFalse(factory.supportsModel(null), "Factory should not support null paths");

        // Test with unsupported extension
        try {
            Path unsupportedFile = tempDir.resolve("unsupported.txt");
            Files.createFile(unsupportedFile);
            assertFalse(factory.supportsModel(unsupportedFile),
                    "Factory should not support files with unsupported extensions");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Test
    public void testCreateLLM() throws IOException {
        logger.info("Testing LLM creation");

        // Create an LLM with default parameters
        LLM llm = factory.createLLM(testModelPath, null);

        // Check that the LLM is not null
        assertNotNull(llm, "Created LLM should not be null");

        // Check that it's an instance of FfmLLM
        assertTrue(llm instanceof FfmLLM, "Created LLM should be an instance of FfmLLM");

        // Create an LLM with custom parameters
        ModelParams params = ModelParams.builder()
                .useMemoryMapping(false)
                .useMemoryLocking(true)
                .gpuLayerCount(2)
                .build();

        LLM llmWithParams = factory.createLLM(testModelPath, params);
        assertNotNull(llmWithParams, "Created LLM with params should not be null");

        // Test with null path
        assertThrows(IllegalArgumentException.class, () -> factory.createLLM(null, null),
                "Creating LLM with null path should throw IllegalArgumentException");

        // Test with non-existent file
        Path nonExistentPath = tempDir.resolve("non-existent.gguf");
        assertThrows(IOException.class, () -> factory.createLLM(nonExistentPath, null),
                "Creating LLM with non-existent file should throw IOException");
    }
}
