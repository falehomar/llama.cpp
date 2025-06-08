package io.github.llama.tools.tokenization;

import io.github.llama.api.tokenization.SpecialToken;
import io.github.llama.api.tokenization.Tokenizer;
import jakarta.inject.Singleton;

/**
 * Service for tokenizing and detokenizing text using the llama.cpp Java API.
 */
@Singleton
public class TokenizationService {

    private final Tokenizer tokenizer;

    /**
     * Creates a new TokenizationService.
     *
     * @param tokenizer The tokenizer to use
     */
    public TokenizationService(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Tokenizes text into token IDs.
     *
     * @param text Text to tokenize
     * @return TokenizationResult containing token IDs
     */
    public TokenizationResult tokenizeText(String text) {
        int[] tokens = tokenizer.tokenize(text);
        return new TokenizationResult(tokens);
    }

    /**
     * Tokenizes text into token IDs with options.
     *
     * @param text Text to tokenize
     * @param addBos Whether to add a beginning-of-sequence token
     * @param addEos Whether to add an end-of-sequence token
     * @return TokenizationResult containing token IDs
     */
    public TokenizationResult tokenizeText(String text, boolean addBos, boolean addEos) {
        int[] tokens = tokenizer.tokenize(text, addBos, addEos);
        return new TokenizationResult(tokens);
    }

    /**
     * Detokenizes token IDs into text.
     *
     * @param tokens Token IDs to detokenize
     * @return Detokenized text
     */
    public String detokenize(int[] tokens) {
        return tokenizer.detokenize(tokens);
    }

    /**
     * Gets the vocabulary size of the tokenizer.
     *
     * @return Vocabulary size
     */
    public int getVocabularySize() {
        return tokenizer.getVocabularySize();
    }

    /**
     * Gets the ID of a special token.
     *
     * @param token Special token type
     * @return Token ID
     */
    public int getSpecialToken(SpecialToken token) {
        return tokenizer.getSpecialToken(token);
    }
}
