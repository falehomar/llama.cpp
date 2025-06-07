package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class CompletionApiSpec extends BaseApiSpec {

    @Inject
    LlamaApiClient client

    def "completion endpoint should generate text from prompt"() {
        given:
        def request = new LlamaApiClient.CompletionRequest(
            prompt: "Once upon a time",
            n_predict: 20,
            temperature: 0.7f
        )

        when:
        def response = client.getCompletion(request)

        then:
        response.status() == HttpStatus.OK
        response.body().content != null
        !response.body().content.isEmpty()
    }
}
