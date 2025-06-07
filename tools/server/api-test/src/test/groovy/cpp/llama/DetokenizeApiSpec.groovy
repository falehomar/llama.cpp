package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class DetokenizeApiSpec extends BaseApiSpec {
    
    @Inject
    LlamaApiClient client
    
    def "detokenize endpoint should convert tokens to text"() {
        given:
        // First tokenize some text to get valid tokens
        def tokenizeRequest = new LlamaApiClient.TokenizeRequest(
            content: "Hello, world!"
        )
        def tokenizeResponse = client.tokenize(tokenizeRequest)
        def tokens = tokenizeResponse.body().tokens
        
        def request = new LlamaApiClient.DetokenizeRequest(
            tokens: tokens
        )
        
        when:
        def response = client.detokenize(request)
        
        then:
        response.status() == HttpStatus.OK
        response.body().content != null
        response.body().content == "Hello, world!"
    }
}
