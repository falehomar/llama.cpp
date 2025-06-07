package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class OpenAIChatCompletionApiSpec extends BaseApiSpec {
    
    @Inject
    LlamaApiClient client
    
    def "OpenAI chat completions endpoint should generate response to chat messages"() {
        given:
        def messages = [
            new LlamaApiClient.OpenAIChatCompletionRequest.Message(
                role: "system",
                content: "You are a helpful assistant."
            ),
            new LlamaApiClient.OpenAIChatCompletionRequest.Message(
                role: "user",
                content: "What is the capital of France?"
            )
        ]
        
        def request = new LlamaApiClient.OpenAIChatCompletionRequest(
            model: "default", // Will use the loaded model
            messages: messages,
            max_tokens: 50,
            temperature: 0.7f
        )
        
        when:
        def response = client.openaiChatCompletion(request)
        
        then:
        response.status() == HttpStatus.OK
        response.body().choices != null
        !response.body().choices.isEmpty()
        response.body().choices[0].message != null
        response.body().choices[0].message.role == "assistant"
        response.body().choices[0].message.content != null
        !response.body().choices[0].message.content.isEmpty()
        response.body().usage != null
        response.body().usage.prompt_tokens > 0
        response.body().usage.completion_tokens > 0
        response.body().usage.total_tokens == response.body().usage.prompt_tokens + response.body().usage.completion_tokens
    }
}
