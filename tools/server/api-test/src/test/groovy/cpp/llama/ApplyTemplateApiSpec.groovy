package cpp.llama

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject

class ApplyTemplateApiSpec extends BaseApiSpec {

    @Inject
    TemplateClient client

    def "apply-template endpoint should format conversation with chat template"() {
        given:
        def messages = [
            [
                "role": "system",
                "content": "You are a helpful assistant."
            ],
            [
                "role": "user",
                "content": "What is the capital of France?"
            ]
        ]

        def request = new TemplateRequest(
            messages: messages,
            template_name: "chatml" // Using the ChatML template
        )

        when:
        def response = client.applyTemplate(request)

        then:
        response.status() == io.micronaut.http.HttpStatus.OK
        response.body().prompt != null
        !response.body().prompt.isEmpty()
        response.body().prompt.contains("system")
        response.body().prompt.contains("user")
    }

    @Client("\${llama.api.url:`http://localhost:8080`}")
    static interface TemplateClient {

        @Post("/apply-template")
        HttpResponse<TemplateResponse> applyTemplate(@Body TemplateRequest request)
    }

    @Serdeable
    static class TemplateRequest {
        List<Map<String, String>> messages
        String template_name
    }

    @Serdeable
    static class TemplateResponse {
        String prompt
    }
}
