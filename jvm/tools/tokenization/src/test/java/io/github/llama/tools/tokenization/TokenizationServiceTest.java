package io.github.llama.tools.tokenization;

import io.github.llama.api.tokenization.SpecialToken;
import io.github.llama.api.tokenization.Tokenizer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@MicronautTest
@ExtendWith(MockitoExtension.class)
public class TokenizationServiceTest {

    @Mock
    private Tokenizer tokenizer;

    private TokenizationService tokenizationService;

    @BeforeEach
    public void setup() {
        tokenizationService = new TokenizationService(tokenizer);
    }

    @Test
    public void testTokenizeText() {
        // Given
        String inputText = "Hello, world!";
        int[] expectedTokens = {1, 2, 3, 4};
        when(tokenizer.tokenize(inputText)).thenReturn(expectedTokens);

        // When
        TokenizationResult result = tokenizationService.tokenizeText(inputText);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTokenIds()).isEqualTo(expectedTokens);
        verify(tokenizer).tokenize(inputText);
    }

    @Test
    public void testTokenizeWithOptions() {
        // Given
        String inputText = "Hello, world!";
        boolean addBos = true;
        boolean addEos = false;
        int[] expectedTokens = {1, 2, 3, 4};
        when(tokenizer.tokenize(inputText, addBos, addEos)).thenReturn(expectedTokens);

        // When
        TokenizationResult result = tokenizationService.tokenizeText(inputText, addBos, addEos);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTokenIds()).isEqualTo(expectedTokens);
        verify(tokenizer).tokenize(inputText, addBos, addEos);
    }

    @Test
    public void testDetokenize() {
        // Given
        int[] tokens = {1, 2, 3, 4};
        String expectedText = "Hello, world!";
        when(tokenizer.detokenize(tokens)).thenReturn(expectedText);

        // When
        String result = tokenizationService.detokenize(tokens);

        // Then
        assertThat(result).isEqualTo(expectedText);
        verify(tokenizer).detokenize(tokens);
    }

    @Test
    public void testGetVocabularySize() {
        // Given
        int expectedSize = 32000;
        when(tokenizer.getVocabularySize()).thenReturn(expectedSize);

        // When
        int result = tokenizationService.getVocabularySize();

        // Then
        assertThat(result).isEqualTo(expectedSize);
        verify(tokenizer).getVocabularySize();
    }

    @Test
    public void testGetSpecialToken() {
        // Given
        int expectedTokenId = 1;
        when(tokenizer.getSpecialToken(SpecialToken.BOS)).thenReturn(expectedTokenId);

        // When
        int result = tokenizationService.getSpecialToken(SpecialToken.BOS);

        // Then
        assertThat(result).isEqualTo(expectedTokenId);
        verify(tokenizer).getSpecialToken(SpecialToken.BOS);
    }
}
