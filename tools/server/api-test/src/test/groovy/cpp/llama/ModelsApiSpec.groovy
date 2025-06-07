package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class ModelsApiSpec extends BaseApiSpec {

    @Inject
    LlamaApiClient client

    def "models endpoint should return available models"() {
        when:
        def response = client.getModels()

        then:
        response.status() == HttpStatus.OK
        response.body().models != null
        !response.body().models.isEmpty()
    }
}
