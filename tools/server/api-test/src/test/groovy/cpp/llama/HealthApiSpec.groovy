package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class HealthApiSpec extends BaseApiSpec {

    @Inject
    LlamaApiClient client

    def "health endpoint should return status ok"() {
        when:
        def response = client.getHealth()

        then:
        response.status() == HttpStatus.OK
        response.body().status == "ok"
    }
}
