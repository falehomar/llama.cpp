package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.tokenization.SpecialToken;
import io.github.llama.api.tokenization.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link FfmTokenizer} class.
 */
public class FfmTokenizerTest {

    private static final Logger logger = LoggerFactory.getLogger(FfmTokenizerTest.class);

    private FfmTokenizer tokenizer;

    @BeforeEach
    public void setUp() {
        logger.info("Setting up test tokenizer");

        // Create a test tokenizer with the builder
        tokenizer = new FfmTokenizer.Builder()
                .vocabularySize(32000)
                .addSpecialToken(SpecialToken.BOS, 1)
                .addSpecialToken(SpecialToken.EOS, 2)
                .addSpecialToken(SpecialToken.PAD, 0)
                .addSpecialToken(SpecialToken.UNK, 3)
                .addTokenText(1, "<s>")
                .addTokenText(2, "</s>")
                .addTokenText(0, "<pad>")
                .addTokenText(3, "<unk>")
                .addTokenText(100, "Hello")
                .addTokenText(101, " world")
                .build();
    }

    @AfterEach
    public void tearDown() {
        logger.info("Closing test tokenizer");
        if (tokenizer != null) {
            tokenizer.close();
        }
    }

    @Test
    public void testGetVocabularySize() {
        logger.info("Testing getVocabularySize");
        assertEquals(32000, tokenizer.getVocabularySize(), "Vocabulary size should match");
    }

    @Test
    public void testGetSpecialToken() {
        logger.info("Testing getSpecialToken");
        assertEquals(1, tokenizer.getSpecialToken(SpecialToken.BOS), "BOS token should match");
        assertEquals(2, tokenizer.getSpecialToken(SpecialToken.EOS), "EOS token should match");
        assertEquals(0, tokenizer.getSpecialToken(SpecialToken.PAD), "PAD token should match");
        assertEquals(3, tokenizer.getSpecialToken(SpecialToken.UNK), "UNK token should match");
    }

    @Test
    public void testGetTokenText() {
        logger.info("Testing getTokenText");
        assertEquals("<s>", tokenizer.getTokenText(1), "BOS token text should match");
        assertEquals("</s>", tokenizer.getTokenText(2), "EOS token text should match");
        assertEquals("<pad>", tokenizer.getTokenText(0), "PAD token text should match");
        assertEquals("<unk>", tokenizer.getTokenText(3), "UNK token text should match");
        assertEquals("Hello", tokenizer.getTokenText(100), "Token text should match");
        assertEquals(" world", tokenizer.getTokenText(101), "Token text should match");
        assertNull(tokenizer.getTokenText(999), "Nonexistent token should return null");
    }

    @Test
    public void testTokenizeSimple() {
        logger.info("Testing tokenize (simple)");

        // For the placeholder implementation, each character becomes a token
        int[] tokens = tokenizer.tokenize("Hello");
        assertEquals(5, tokens.length, "Token count should match");
        assertEquals('H', tokens[0], "Token should match");
        assertEquals('e', tokens[1], "Token should match");
        assertEquals('l', tokens[2], "Token should match");
        assertEquals('l', tokens[3], "Token should match");
        assertEquals('o', tokens[4], "Token should match");
    }

    @Test
    public void testTokenizeWithSpecialTokens() {
        logger.info("Testing tokenize with special tokens");

        int[] tokens = tokenizer.tokenize("Hello", true, true);
        assertEquals(7, tokens.length, "Token count should match");
        assertEquals(1, tokens[0], "BOS token should be first");
        assertEquals('H', tokens[1], "Token should match");
        assertEquals('e', tokens[2], "Token should match");
        assertEquals('l', tokens[3], "Token should match");
        assertEquals('l', tokens[4], "Token should match");
        assertEquals('o', tokens[5], "Token should match");
        assertEquals(2, tokens[6], "EOS token should be last");
    }

    @Test
    public void testDetokenizeSimple() {
        logger.info("Testing detokenize (simple)");

        int[] tokens = new int[] {'H', 'e', 'l', 'l', 'o'};
        String text = tokenizer.detokenize(tokens);
        assertEquals("Hello", text, "Detokenized text should match");
    }

    @Test
    public void testDetokenizeWithSpecialTokens() {
        logger.info("Testing detokenize with special tokens");

        int[] tokens = new int[] {1, 'H', 'e', 'l', 'l', 'o', 2};
        String text = tokenizer.detokenize(tokens);
        assertEquals("Hello", text, "Detokenized text should match (special tokens removed)");
    }

    @Test
    public void testDetokenizeWithKnownTokens() {
        logger.info("Testing detokenize with known tokens");

        int[] tokens = new int[] {100, 101};
        String text = tokenizer.detokenize(tokens);
        assertEquals("Hello world", text, "Detokenized text should match");
    }

    @Test
    public void testTokenizerClosed() {
        logger.info("Testing tokenizer after closing");

        tokenizer.close();

        // All operations should throw IllegalStateException after closing
        assertThrows(IllegalStateException.class, () -> tokenizer.getVocabularySize(),
                "Should throw IllegalStateException after closing");
        assertThrows(IllegalStateException.class, () -> tokenizer.getSpecialToken(SpecialToken.BOS),
                "Should throw IllegalStateException after closing");
        assertThrows(IllegalStateException.class, () -> tokenizer.getTokenText(1),
                "Should throw IllegalStateException after closing");
        assertThrows(IllegalStateException.class, () -> tokenizer.tokenize("Hello"),
                "Should throw IllegalStateException after closing");
        assertThrows(IllegalStateException.class, () -> tokenizer.detokenize(new int[] {1, 2, 3}),
                "Should throw IllegalStateException after closing");
    }

    @Test
    public void testBuilder() {
        logger.info("Testing builder with different values");

        FfmTokenizer customTokenizer = new FfmTokenizer.Builder()
                .vocabularySize(50000)
                .addSpecialToken(SpecialToken.BOS, 10)
                .addSpecialToken(SpecialToken.EOS, 20)
                .addTokenText(10, "<start>")
                .addTokenText(20, "<end>")
                .build();

        assertEquals(50000, customTokenizer.getVocabularySize(), "Custom vocabulary size should match");
        assertEquals(10, customTokenizer.getSpecialToken(SpecialToken.BOS), "Custom BOS token should match");
        assertEquals(20, customTokenizer.getSpecialToken(SpecialToken.EOS), "Custom EOS token should match");
        assertEquals("<start>", customTokenizer.getTokenText(10), "Custom token text should match");
        assertEquals("<end>", customTokenizer.getTokenText(20), "Custom token text should match");

        customTokenizer.close();
    }
}
