package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class TokenizeApiSpec extends BaseApiSpec {
    
    @Inject
    LlamaApiClient client
    
    def "tokenize endpoint should convert text to tokens"() {
        given:
        def request = new LlamaApiClient.TokenizeRequest(
            content: "Hello, world!"
        )
        
        when:
        def response = client.tokenize(request)
        
        then:
        response.status() == HttpStatus.OK
        response.body().tokens != null
        !response.body().tokens.isEmpty()
    }
}
