package io.github.llama.impl.jni;

import io.github.llama.api.LLM;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;
import io.github.llama.api.tokenization.Tokenizer;

/**
 * Test class for the JNI implementation of llama.cpp.
 */
public class LlamaJniTest {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: LlamaJniTest <model_path>");
            System.exit(1);
        }

        String modelPath = args[0];

        System.out.println("===== Llama.cpp JNI Test =====");

        try {
            // Initialize backend
            System.out.println("Initializing llama backend...");
            LlamaJniBackend.llama_backend_init();

            // Get default params
            System.out.println("Getting default model parameters...");
            ModelParams modelParams = LlamaJniBackend.llama_get_model_default_params();
            System.out.println("Default model params: use_mmap=" + modelParams.isUseMemoryMapping() +
                               ", gpu_layers=" + modelParams.getGpuLayerCount());

            // Load model
            System.out.println("Loading model from: " + modelPath);
            LLM model = new LlamaJniModel(modelPath, modelParams);

            // Get model info
            ModelInfo info = model.getModelInfo();
            System.out.println("Model loaded: " + info.getName());
            System.out.println("Vocab size: " + info.getVocabSize());
            System.out.println("Context size: " + info.getContextSize());

            // Create context
            System.out.println("Creating context...");
            ContextParams contextParams = new ContextParams();
            contextParams.setContextSize(2048); // Set a reasonable context size
            contextParams.setThreadCount(4);  // Use 4 threads
            Context context = model.createContext(contextParams);

            // Test tokenization
            String text = "Hello, world!";
            System.out.println("Tokenizing: \"" + text + "\"");
            Tokenizer tokenizer = model.getTokenizer();
            int[] tokens = tokenizer.encode(text);
            System.out.println("Tokens: " + tokens.length);
            for (int i = 0; i < tokens.length; i++) {
                System.out.println("  " + i + ": " + tokens[i] + " -> \"" + tokenizer.decode(new int[]{tokens[i]}) + "\"");
            }

            // Test batch processing
            System.out.println("Processing tokens...");
            Batch batch = context.createBatch(tokens.length);
            for (int i = 0; i < tokens.length; i++) {
                batch.add(tokens[i], i);
            }
            BatchResult result = context.process(batch);
            System.out.println("Batch processing completed with: " + result.getStatus());

            // Test sampling
            System.out.println("Testing sampling...");
            SamplerParams samplerParams = new SamplerParams();
            samplerParams.setTemperature(0.8f);
            samplerParams.setTopP(0.9f);
            samplerParams.setTopK(40);
            samplerParams.setSeed(123456L);

            Sampler sampler = context.createSampler(samplerParams);
            System.out.println("Sampling next token...");
            int nextToken = sampler.sample();
            System.out.println("Next token: " + nextToken + " -> \"" + tokenizer.decode(new int[]{nextToken}) + "\"");

            // Clean up
            System.out.println("Cleaning up...");
            context.close();
            model.close();

            System.out.println("===== Test completed successfully =====");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure backend is freed
            LlamaJniBackend.llama_backend_free();
        }
    }
}
