package io.github.llama.api.tokenization;

/**
 * Interface for tokenizing and detokenizing text.
 */
public interface Tokenizer {
    /**
     * Tokenizes text into an array of token IDs.
     *
     * @param text Text to tokenize
     * @return Array of token IDs
     */
    int[] tokenize(String text);

    /**
     * Tokenizes text into an array of token IDs with options.
     *
     * @param text Text to tokenize
     * @param addBos Whether to add a beginning-of-sequence token
     * @param addEos Whether to add an end-of-sequence token
     * @return Array of token IDs
     */
    int[] tokenize(String text, boolean addBos, boolean addEos);

    /**
     * Detokenizes an array of token IDs into text.
     *
     * @param tokens Array of token IDs
     * @return Detokenized text
     */
    String detokenize(int[] tokens);

    /**
     * Gets the size of the vocabulary.
     *
     * @return Vocabulary size
     */
    int getVocabularySize();

    /**
     * Gets the token ID for a special token.
     *
     * @param token Special token type
     * @return Token ID
     */
    int getSpecialToken(SpecialToken token);

    /**
     * Gets the text for a token ID.
     *
     * @param tokenId Token ID
     * @return Token text
     */
    String getTokenText(int tokenId);
}
