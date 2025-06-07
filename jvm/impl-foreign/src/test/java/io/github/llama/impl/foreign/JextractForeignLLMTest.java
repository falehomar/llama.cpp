package io.github.llama.impl.foreign;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.model.ModelParams;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test class for the JextractForeignLLM implementation.
 * This class demonstrates how to use the Java API with jextract-generated bindings.
 *
 * Command-line options:
 * --info-only    Load model, print info, and exit without running inference
 * --help         Print usage information and exit
 *
 * If a model path is provided as an argument, it will be used instead of the default path.
 */
public class JextractForeignLLMTest {
    static String MODEL_PATH = "/Users/e168693/.ollama/models/blobs/sha256-4ad960d180b16f56024f5b704697e5dd5b0837167c2e515ef0569abfc599743c";

    /**
     * Prints usage information for the test program.
     */
    private static void printUsage() {
        System.out.println("Usage: JextractForeignLLMTest [options] [model_path]");
        System.out.println("Options:");
        System.out.println("  --info-only    Load model, print info, and exit without running inference");
        System.out.println("  --help         Print this help message and exit");
        System.out.println();
        System.out.println("If model_path is not provided, a default path will be used.");
    }

    public static void main(String[] args) {
        // Parse command-line arguments
        boolean infoOnly = false;
        String modelPathArg = null;

        for (String arg : args) {
            if (arg.equals("--info-only")) {
                infoOnly = true;
            } else if (arg.equals("--help")) {
                printUsage();
                return;
            } else if (!arg.startsWith("--")) {
                modelPathArg = arg;
            }
        }

        // Get the model path
        Path modelPath = Paths.get(modelPathArg != null ? modelPathArg : MODEL_PATH);

        try {
            // Create model parameters
            ModelParams modelParams = ModelParams.builder()
                .useMemoryMapping(true)
                .gpuLayerCount(0)
                .build();

            // Create a factory
            JextractForeignLLMFactory factory = new JextractForeignLLMFactory();

            // Check if the factory supports the model
            if (!factory.supportsModel(modelPath)) {
                System.err.println("Model not supported: " + modelPath);
                System.exit(1);
            }

            // Load the model
            System.out.println("Loading model with jextract bindings: " + modelPath);
            try (LLM llm = factory.createLLM(modelPath, modelParams)) {
                // Get model information
                ModelInfo modelInfo = llm.getModelInfo();
                System.out.println("Model description: " + modelInfo.getDescription());
                System.out.println("Parameter count: " + modelInfo.getParameterCount());
                System.out.println("Context size: " + modelInfo.getContextSize());
                System.out.println("Embedding size: " + modelInfo.getEmbeddingSize());
                System.out.println("Layer count: " + modelInfo.getLayerCount());
                System.out.println("Head count: " + modelInfo.getHeadCount());

                // If info-only mode is enabled, exit here
                if (infoOnly) {
                    System.out.println("Info-only mode: exiting without running inference");
                    return;
                }

                // For now, we'll just print the model info and exit
                // Full inference will be implemented in a future update
                System.out.println("Full inference not yet implemented with jextract bindings");
            }

            System.out.println("Test completed successfully");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
