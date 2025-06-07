package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class EmbeddingApiSpec extends BaseApiSpec {
    
    @Inject
    LlamaApiClient client
    
    def "embedding endpoint should generate embeddings for text"() {
        given:
        def request = new LlamaApiClient.EmbeddingRequest(
            content: "This is a test sentence for embeddings."
        )
        
        when:
        def response = client.embedding(request)
        
        then:
        response.status() == HttpStatus.OK
        response.body().embedding != null
        !response.body().embedding.isEmpty()
    }
}
