package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class OpenAICompletionApiSpec extends BaseApiSpec {
    
    @Inject
    LlamaApiClient client
    
    def "OpenAI completions endpoint should generate text from prompt"() {
        given:
        def request = new LlamaApiClient.OpenAICompletionRequest(
            model: "default", // Will use the loaded model
            prompt: "Once upon a time",
            max_tokens: 20,
            temperature: 0.7f
        )
        
        when:
        def response = client.openaiCompletion(request)
        
        then:
        response.status() == HttpStatus.OK
        response.body().choices != null
        !response.body().choices.isEmpty()
        response.body().choices[0].text != null
        !response.body().choices[0].text.isEmpty()
        response.body().usage != null
        response.body().usage.prompt_tokens > 0
        response.body().usage.completion_tokens > 0
        response.body().usage.total_tokens == response.body().usage.prompt_tokens + response.body().usage.completion_tokens
    }
}
