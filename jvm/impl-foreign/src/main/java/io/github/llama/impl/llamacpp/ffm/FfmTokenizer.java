package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.tokenization.SpecialToken;
import io.github.llama.api.tokenization.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link Tokenizer} using Java's Foreign Function & Memory API.
 * This class provides tokenization and detokenization functionality for llama.cpp models.
 */
public class FfmTokenizer implements Tokenizer {

    private static final Logger logger = LoggerFactory.getLogger(FfmTokenizer.class);

    private final int vocabularySize;
    private final Map<SpecialToken, Integer> specialTokens;
    private final Map<Integer, String> tokenTexts;
    private boolean closed = false;

    /**
     * Creates a new instance of the FfmTokenizer.
     *
     * @param vocabularySize Size of the vocabulary
     * @param specialTokens Map of special tokens to their IDs
     * @param tokenTexts Map of token IDs to their text representations
     */
    public FfmTokenizer(int vocabularySize, Map<SpecialToken, Integer> specialTokens, Map<Integer, String> tokenTexts) {
        this.vocabularySize = vocabularySize;
        this.specialTokens = new HashMap<>(specialTokens);
        this.tokenTexts = new HashMap<>(tokenTexts);
        logger.debug("Created FfmTokenizer with vocabulary size: {}", vocabularySize);
    }

    @Override
    public int[] tokenize(String text) {
        return tokenize(text, false, false);
    }

    @Override
    public int[] tokenize(String text, boolean addBos, boolean addEos) {
        checkClosed();
        logger.debug("Tokenizing text: '{}', addBos: {}, addEos: {}", text, addBos, addEos);

        // For now, return a simple array with some dummy tokens
        // In a real implementation, this would use LlamaCPP.llama_tokenize
        int[] result;
        int offset = 0;

        if (addBos) {
            offset++;
        }
        if (addEos) {
            offset++;
        }

        result = new int[text.length() + offset];

        if (addBos) {
            result[0] = getSpecialToken(SpecialToken.BOS);
            offset = 1;
        } else {
            offset = 0;
        }

        // Simple character-by-character tokenization for testing
        for (int i = 0; i < text.length(); i++) {
            result[i + offset] = text.charAt(i);
        }

        if (addEos) {
            result[result.length - 1] = getSpecialToken(SpecialToken.EOS);
        }

        return result;
    }

    @Override
    public String detokenize(int[] tokens) {
        checkClosed();
        logger.debug("Detokenizing {} tokens", tokens.length);

        // For token IDs, use the token text map
        StringBuilder sb = new StringBuilder();

        for (int token : tokens) {
            // Skip special tokens
            if (token == getSpecialToken(SpecialToken.BOS) ||
                token == getSpecialToken(SpecialToken.EOS) ||
                token == getSpecialToken(SpecialToken.PAD)) {
                continue;
            }

            // If we have a text representation for this token, use it
            String text = getTokenText(token);
            if (text != null) {
                sb.append(text);
            } else {
                // Check if this is a printable ASCII character
                if (token >= 32 && token <= 127) {
                    // If it's a printable ASCII character, append it
                    sb.append((char) token);
                } else {
                    // Otherwise, log a warning and skip it
                    logger.warn("Unknown token ID: {}", token);
                }
            }
        }

        return sb.toString();
    }

    @Override
    public int getVocabularySize() {
        checkClosed();
        return vocabularySize;
    }

    @Override
    public int getSpecialToken(SpecialToken token) {
        checkClosed();
        Integer tokenId = specialTokens.get(token);
        if (tokenId == null) {
            logger.warn("Special token not found: {}", token);
            return -1;
        }
        return tokenId;
    }

    @Override
    public String getTokenText(int tokenId) {
        checkClosed();
        return tokenTexts.get(tokenId);
    }

    /**
     * Closes the tokenizer and releases resources.
     */
    public void close() {
        if (!closed) {
            logger.info("Closing tokenizer");
            // No actual cleanup needed for now as we don't have any native resources
            closed = true;
            logger.debug("Tokenizer closed");
        }
    }

    /**
     * Checks if the tokenizer is closed and throws an exception if it is.
     *
     * @throws IllegalStateException if the tokenizer is closed
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Tokenizer is closed");
        }
    }

    /**
     * Builder for FfmTokenizer.
     */
    public static class Builder {
        private int vocabularySize = 0;
        private final Map<SpecialToken, Integer> specialTokens = new HashMap<>();
        private final Map<Integer, String> tokenTexts = new HashMap<>();

        public Builder vocabularySize(int vocabularySize) {
            this.vocabularySize = vocabularySize;
            return this;
        }

        public Builder addSpecialToken(SpecialToken token, int tokenId) {
            specialTokens.put(token, tokenId);
            return this;
        }

        public Builder addTokenText(int tokenId, String text) {
            tokenTexts.put(tokenId, text);
            return this;
        }

        public FfmTokenizer build() {
            return new FfmTokenizer(vocabularySize, specialTokens, tokenTexts);
        }
    }
}
