package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class OpenAIEmbeddingApiSpec extends BaseApiSpec {
    
    @Inject
    LlamaApiClient client
    
    def "OpenAI embeddings endpoint should generate embeddings for text"() {
        given:
        def request = new LlamaApiClient.OpenAIEmbeddingRequest(
            model: "default", // Will use the loaded model
            input: "This is a test sentence for embeddings."
        )
        
        when:
        def response = client.openaiEmbedding(request)
        
        then:
        response.status() == HttpStatus.OK
        response.body().data != null
        !response.body().data.isEmpty()
        response.body().data[0].embedding != null
        !response.body().data[0].embedding.isEmpty()
        response.body().usage != null
        response.body().usage.prompt_tokens > 0
        response.body().usage.total_tokens == response.body().usage.prompt_tokens
    }
}
