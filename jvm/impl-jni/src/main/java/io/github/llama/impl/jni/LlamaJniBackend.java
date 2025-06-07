package io.github.llama.impl.jni;

import io.github.llama.api.model.ModelParams;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.context.ContextParams;

/**
 * JNI backend implementation for llama.cpp
 * This class provides the native methods that interface with the llama.cpp C library.
 */
public class LlamaJniBackend {

    // Load the native library
    static {
        try {
            System.loadLibrary("llama_jni");
            System.out.println("Successfully loaded llama_jni native library");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load llama_jni: " + e.getMessage());
            System.err.println("Make sure the library is in your java.library.path");
            throw e;
        }
    }

    /**
     * Initialize the llama backend.
     * This should be called once at application startup.
     */
    public static native void llama_backend_init();

    /**
     * Clean up the llama backend.
     * This should be called once at application shutdown.
     */
    public static native void llama_backend_free();

    /**
     * Load a model from a file.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @return Pointer to the model as a long value
     */
    public static native long llama_model_load_from_file(String modelPath, ModelParams params);

    /**
     * Free a model and release its resources.
     *
     * @param model Pointer to the model
     */
    public static native void llama_model_free(long model);

    /**
     * Get information about a model.
     *
     * @param model Pointer to the model
     * @return Model information
     */
    public static native ModelInfo llama_get_model_info(long model);

    /**
     * Create a context for inference from a model.
     *
     * @param model Pointer to the model
     * @param params Context parameters
     * @return Pointer to the context as a long value
     */
    public static native long llama_context_create(long model, ContextParams params);

    /**
     * Free a context and release its resources.
     *
     * @param context Pointer to the context
     */
    public static native void llama_context_free(long context);

    /**
     * Create a batch for token processing.
     *
     * @param maxTokens Maximum number of tokens in the batch
     * @return Pointer to the batch as a long value
     */
    public static native long llama_batch_create(int maxTokens);

    /**
     * Add a token to a batch.
     *
     * @param batch Pointer to the batch
     * @param token Token to add
     * @param position Position of the token in the sequence
     * @return True if the token was added successfully, false if the batch is full
     */
    public static native boolean llama_batch_add(long batch, int token, int position);

    /**
     * Free a batch and release its resources.
     *
     * @param batch Pointer to the batch
     */
    public static native void llama_batch_free(long batch);

    /**
     * Process a batch of tokens through the model.
     *
     * @param context Pointer to the context
     * @param batch Pointer to the batch
     * @return 0 on success, negative value on error
     */
    public static native int llama_decode(long context, long batch);

    /**
     * Get the logits (probabilities) for the last processed token.
     *
     * @param context Pointer to the context
     * @return Array of logits for each token in the vocabulary
     */
    public static native float[] llama_get_logits(long context);

    /**
     * Convert a token to its string representation.
     *
     * @param model Pointer to the model
     * @param token Token to convert
     * @param buffer Buffer to store the result
     * @return Length of the string, or negative value on error
     */
    public static native int llama_token_to_piece(long model, int token, byte[] buffer);

    /**
     * Tokenize a string into tokens.
     *
     * @param model Pointer to the model
     * @param text Text to tokenize
     * @param addBos Whether to add BOS token
     * @return Array of token ids
     */
    public static native int[] llama_tokenize(long model, String text, boolean addBos);

    /**
     * Get default model parameters.
     *
     * @return Default model parameters
     */
    public static native ModelParams llama_get_model_default_params();

    /**
     * Sample methods
     */
    public static native float[] llama_sample_softmax(float[] logits);
    public static native float[] llama_sample_temperature(float[] logits, float temperature, long context);
    public static native float[] llama_sample_top_p(float[] probs, float topP, int minKeep, long context);
    public static native float[] llama_sample_top_k(float[] probs, int topK, long context);
    public static native int llama_sample_token(float[] probs, long seed);
}
