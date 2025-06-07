package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject

class InfillApiSpec extends BaseApiSpec {
    
    @Inject
    InfillClient client
    
    def "infill endpoint should fill in text between prefix and suffix"() {
        given:
        def request = new InfillRequest(
            prompt: "def calculate_area(radius):\n    // calculate the area of a circle\n    return",
            suffix: "\n\ndef main():\n    print(calculate_area(5))"
        )
        
        when:
        def response = client.infill(request)
        
        then:
        response.status() == io.micronaut.http.HttpStatus.OK
        response.body().content != null
        !response.body().content.isEmpty()
    }
    
    @Client("\${llama.api.url:`http://localhost:8080`}")
    static interface InfillClient {
        
        @Post("/infill")
        HttpResponse<InfillResponse> infill(@Body InfillRequest request)
    }
    
    @Serdeable
    static class InfillRequest {
        String prompt
        String suffix
    }
    
    @Serdeable
    static class InfillResponse {
        String content
    }
}
