package io.github.llama.impl.foreign;

import io.github.llama.api.LLM;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;
import io.github.llama.api.tokenization.SpecialToken;
import io.github.llama.api.tokenization.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test class for the Foreign LLM implementation.
 * This class demonstrates how to use the Java API.
 */
public class ForeignLLMTest {
    static String MODEL_PATH = "/Users/e168693/.ollama/models/blobs/sha256-4ad960d180b16f56024f5b704697e5dd5b0837167c2e515ef0569abfc599743c";
    public static void main(String[] args) {


        // Get the model path
        Path modelPath =Paths.get((args.length < 1)?MODEL_PATH: args[0]);

        try {
            // Create model parameters
            ModelParams modelParams = ModelParams.builder()
                .useMemoryMapping(true)
                .gpuLayerCount(0)
                .build();

            // Create a factory
            ForeignLLMFactory factory = new ForeignLLMFactory();

            // Check if the factory supports the model
            if (!factory.supportsModel(modelPath)) {
                System.err.println("Model not supported: " + modelPath);
                System.exit(1);
            }

            // Load the model
            System.out.println("Loading model: " + modelPath);
            try (LLM llm = factory.createLLM(modelPath, modelParams)) {
                // Get model information
                ModelInfo modelInfo = llm.getModelInfo();
                System.out.println("Model description: " + modelInfo.getDescription());
                System.out.println("Parameter count: " + modelInfo.getParameterCount());
                System.out.println("Context size: " + modelInfo.getContextSize());
                System.out.println("Embedding size: " + modelInfo.getEmbeddingSize());
                System.out.println("Layer count: " + modelInfo.getLayerCount());
                System.out.println("Head count: " + modelInfo.getHeadCount());

                // Create context parameters
                ContextParams contextParams = ContextParams.builder()
                    .contextSize(2048)
                    .threadCount(4)
                    .build();

                // Create a context
                System.out.println("Creating context");
                try (Context context = llm.createContext(contextParams)) {
                    // Get the tokenizer
                    Tokenizer tokenizer = llm.getTokenizer();

                    // Tokenize input text
                    String inputText = "Hello, how are you?";
                    System.out.println("Tokenizing: " + inputText);
                    int[] tokens = tokenizer.tokenize(inputText, true, false);
                    System.out.println("Token count: " + tokens.length);

                    // Create a batch
                    try (Batch batch = context.createBatch(tokens.length)) {
                        // Add tokens to the batch
                        batch.addTokens(tokens);

                        // Process the batch
                        System.out.println("Processing batch");
                        BatchResult result = context.process(batch);
                        if (!result.isSuccess()) {
                            System.err.println("Batch processing failed: " + result.getErrorMessage());
                            System.exit(1);
                        }

                        // Create a sampler
                        SamplerParams samplerParams = SamplerParams.builder()
                            .temperature(0.7f)
                            .topP(0.9f)
                            .maxTokens(100)
                            .build();

                        // Sample tokens
                        System.out.println("Sampling tokens");
                        try (Sampler sampler = context.createSampler(samplerParams)) {
                            StringBuilder output = new StringBuilder();
                            for (int i = 0; i < samplerParams.getMaxTokens(); i++) {
                                int tokenId = sampler.sample(context.getLogits());
                                if (tokenId == tokenizer.getSpecialToken(SpecialToken.EOS)) {
                                    break;
                                }

                                String tokenText = tokenizer.getTokenText(tokenId);
                                output.append(tokenText);

                                // Add the sampled token to the batch for the next iteration
                                batch.clear();
                                batch.addToken(tokenId);
                                result = context.process(batch);
                                if (!result.isSuccess()) {
                                    System.err.println("Batch processing failed: " + result.getErrorMessage());
                                    break;
                                }
                            }

                            System.out.println("Generated text: " + output.toString());
                        }
                    }
                }
            }

            System.out.println("Test completed successfully");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
