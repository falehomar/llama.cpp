package cpp.llama

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject

class PropertiesApiSpec extends BaseApiSpec {

    @Inject
    PropertiesClient client

    def "props GET endpoint should return server properties"() {
        when:
        def response = client.getProperties()

        then:
        response.status() == io.micronaut.http.HttpStatus.OK
        response.body().props != null
    }

    def "props POST endpoint should update server properties"() {
        given:
        // Get current properties first
        def currentProps = client.getProperties().body().props

        def request = new PropertiesRequest(
            // If n_parallel is already 1, set it to 2, otherwise set it to 1
            props: [n_parallel: (currentProps.n_parallel == 1) ? 2 : 1]
        )

        when:
        def updateResponse = client.updateProperties(request)

        then:
        updateResponse.status() == io.micronaut.http.HttpStatus.OK
        updateResponse.body().props != null

        // Verify the property was updated
        updateResponse.body().props.n_parallel == request.props.n_parallel

        // Reset to the original value
        def resetRequest = new PropertiesRequest(
            props: [n_parallel: currentProps.n_parallel]
        )
        client.updateProperties(resetRequest)
    }

    @Client("\${llama.api.url:`http://localhost:8080`}")
    static interface PropertiesClient {

        @Get("/props")
        HttpResponse<PropertiesResponse> getProperties()

        @Post("/props")
        HttpResponse<PropertiesResponse> updateProperties(@Body PropertiesRequest request)
    }

    @Serdeable
    static class PropertiesRequest {
        Map<String, Object> props
    }

    @Serdeable
    static class PropertiesResponse {
        Map<String, Object> props
    }
}
