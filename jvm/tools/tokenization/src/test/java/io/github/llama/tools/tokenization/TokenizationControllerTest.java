package io.github.llama.tools.tokenization;

import io.github.llama.api.tokenization.SpecialToken;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@MicronautTest
public class TokenizationControllerTest {

    @Inject
    private TokenizationService tokenizationService;

    @Inject
    @Client("/")
    private HttpClient client;

    @Test
    public void testTokenize() {
        // Given
        String text = "Hello, world!";
        int[] tokenIds = {1, 2, 3, 4};

        TokenizationResult mockResult = new TokenizationResult(tokenIds);
        when(tokenizationService.tokenizeText(text)).thenReturn(mockResult);

        // When
        TokenizationController.TokenizationRequest request = new TokenizationController.TokenizationRequest();
        request.setText(text);

        TokenizationResult result = client.toBlocking()
            .retrieve(HttpRequest.POST("/api/tokenize", request), TokenizationResult.class);

        // Then
        assertNotNull(result);
        assertEquals(tokenIds.length, result.getTokenIds().length);
        for (int i = 0; i < tokenIds.length; i++) {
            assertEquals(tokenIds[i], result.getTokenIds()[i]);
        }
    }

    @Test
    public void testDetokenize() {
        // Given
        int[] tokenIds = {1, 2, 3, 4};
        String expectedText = "Hello, world!";

        when(tokenizationService.detokenize(tokenIds)).thenReturn(expectedText);

        // When
        TokenizationController.DetokenizationRequest request = new TokenizationController.DetokenizationRequest();
        request.setTokenIds(tokenIds);

        TokenizationController.DetokenizationResult result = client.toBlocking()
            .retrieve(HttpRequest.POST("/api/tokenize/detokenize", request), TokenizationController.DetokenizationResult.class);

        // Then
        assertNotNull(result);
        assertEquals(expectedText, result.getText());
    }

    @Test
    public void testGetTokenizerInfo() {
        // Given
        int vocabSize = 32000;
        int bosToken = 1;
        int eosToken = 2;

        when(tokenizationService.getVocabularySize()).thenReturn(vocabSize);
        when(tokenizationService.getSpecialToken(SpecialToken.BOS)).thenReturn(bosToken);
        when(tokenizationService.getSpecialToken(SpecialToken.EOS)).thenReturn(eosToken);

        // When
        TokenizationController.TokenizerInfoResult result = client.toBlocking()
            .retrieve(HttpRequest.GET("/api/tokenize/info"), TokenizationController.TokenizerInfoResult.class);

        // Then
        assertNotNull(result);
        assertEquals(vocabSize, result.getVocabularySize());
        assertEquals(bosToken, result.getBosToken());
        assertEquals(eosToken, result.getEosToken());
    }

    @MockBean(TokenizationService.class)
    TokenizationService tokenizationService() {
        return Mockito.mock(TokenizationService.class);
    }
}
