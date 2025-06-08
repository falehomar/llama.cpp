package io.github.llama.tools.tokenization;

import io.github.llama.api.tokenization.SpecialToken;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

/**
 * REST controller for tokenization operations.
 */
@Controller("/api/tokenize")
public class TokenizationController {

    private final TokenizationService tokenizationService;

    /**
     * Creates a new TokenizationController.
     *
     * @param tokenizationService The tokenization service to use
     */
    @Inject
    public TokenizationController(TokenizationService tokenizationService) {
        this.tokenizationService = tokenizationService;
    }

    /**
     * Tokenizes text.
     *
     * @param request TokenizationRequest containing text to tokenize
     * @return TokenizationResult containing token IDs
     */
    @Post(produces = MediaType.APPLICATION_JSON)
    public HttpResponse<TokenizationResult> tokenize(@Body TokenizationRequest request) {
        TokenizationResult result;

        if (request.getAddBos() != null || request.getAddEos() != null) {
            boolean addBos = request.getAddBos() != null ? request.getAddBos() : false;
            boolean addEos = request.getAddEos() != null ? request.getAddEos() : false;
            result = tokenizationService.tokenizeText(request.getText(), addBos, addEos);
        } else {
            result = tokenizationService.tokenizeText(request.getText());
        }

        return HttpResponse.ok(result);
    }

    /**
     * Detokenizes token IDs to text.
     *
     * @param request DetokenizationRequest containing token IDs
     * @return DetokenizationResult containing the detokenized text
     */
    @Post(uri = "/detokenize", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<DetokenizationResult> detokenize(@Body DetokenizationRequest request) {
        String text = tokenizationService.detokenize(request.getTokenIds());
        return HttpResponse.ok(new DetokenizationResult(text));
    }

    /**
     * Gets information about the tokenizer.
     *
     * @return TokenizerInfoResult containing vocabulary size and special tokens
     */
    @Get(uri = "/info", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<TokenizerInfoResult> getTokenizerInfo() {
        int vocabSize = tokenizationService.getVocabularySize();
        int bosToken = tokenizationService.getSpecialToken(SpecialToken.BOS);
        int eosToken = tokenizationService.getSpecialToken(SpecialToken.EOS);

        return HttpResponse.ok(new TokenizerInfoResult(vocabSize, bosToken, eosToken));
    }

    /**
     * Request object for tokenization.
     */
    @Serdeable
    public static class TokenizationRequest {
        private String text;
        private Boolean addBos;
        private Boolean addEos;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Boolean getAddBos() {
            return addBos;
        }

        public void setAddBos(Boolean addBos) {
            this.addBos = addBos;
        }

        public Boolean getAddEos() {
            return addEos;
        }

        public void setAddEos(Boolean addEos) {
            this.addEos = addEos;
        }
    }

    /**
     * Request object for detokenization.
     */
    @Serdeable
    public static class DetokenizationRequest {
        private int[] tokenIds;

        public int[] getTokenIds() {
            return tokenIds;
        }

        public void setTokenIds(int[] tokenIds) {
            this.tokenIds = tokenIds;
        }
    }

    /**
     * Result object for detokenization.
     */
    @Serdeable
    public static class DetokenizationResult {
        private final String text;

        public DetokenizationResult(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Result object for tokenizer information.
     */
    @Serdeable
    public static class TokenizerInfoResult {
        private final int vocabularySize;
        private final int bosToken;
        private final int eosToken;

        public TokenizerInfoResult(int vocabularySize, int bosToken, int eosToken) {
            this.vocabularySize = vocabularySize;
            this.bosToken = bosToken;
            this.eosToken = eosToken;
        }

        public int getVocabularySize() {
            return vocabularySize;
        }

        public int getBosToken() {
            return bosToken;
        }

        public int getEosToken() {
            return eosToken;
        }
    }
}
