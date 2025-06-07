package io.github.llama.impl.jni;

import io.github.llama.api.tokenization.Tokenizer;
import io.github.llama.api.tokenization.SpecialToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * JNI implementation of the Tokenizer interface for llama.cpp models.
 */
public class LlamaJniTokenizer implements Tokenizer {
    private final long modelPtr;

    /**
     * Creates a new tokenizer for a model.
     *
     * @param modelPtr Native model pointer
     */
    LlamaJniTokenizer(long modelPtr) {
        this.modelPtr = modelPtr;
    }

    @Override
    public int[] encode(String text) {
        return encode(text, false);
    }

    @Override
    public int[] encode(String text, boolean addBos) {
        return LlamaJniBackend.llama_tokenize(modelPtr, text, addBos);
    }

    @Override
    public String decode(int token) {
        byte[] buffer = new byte[16]; // Usually enough for a single token
        int length = LlamaJniBackend.llama_token_to_piece(modelPtr, token, buffer);

        if (length < 0) {
            // Try with a larger buffer if the first attempt failed
            buffer = new byte[64];
            length = LlamaJniBackend.llama_token_to_piece(modelPtr, token, buffer);

            if (length < 0) {
                throw new RuntimeException("Failed to decode token: " + token);
            }
        }

        try {
            return new String(buffer, 0, length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    @Override
    public String decode(int[] tokens) {
        StringBuilder sb = new StringBuilder();
        for (int token : tokens) {
            sb.append(decode(token));
        }
        return sb.toString();
    }

    @Override
    public List<SpecialToken> getSpecialTokens() {
        // Create a list of special tokens from the model
        List<SpecialToken> specialTokens = new ArrayList<>();

        // Add common special tokens that are typically in llama.cpp models
        // This would need to be filled with actual data from the model
        // For now we only include placeholder logic

        return specialTokens;
    }
}
