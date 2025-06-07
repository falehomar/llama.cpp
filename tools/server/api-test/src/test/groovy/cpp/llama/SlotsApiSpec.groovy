package cpp.llama

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject

class SlotsApiSpec extends BaseApiSpec {

    @Inject
    SlotsClient client

    def "slots endpoint should return current slots"() {
        when:
        def response = client.getSlots()

        then:
        response.status() == io.micronaut.http.HttpStatus.OK
        response.body() != null
        response.body().slots != null
    }

    def "slots action endpoint should save a slot"() {
        given:
        // First we need to get a slot ID
        def slotsResponse = client.getSlots()
        def slotId = 0 // Default to slot 0 if no slots are available

        if (!slotsResponse.body().slots.isEmpty()) {
            slotId = slotsResponse.body().slots[0].id
        }

        when:
        def response = client.saveSlot(slotId, "save")

        then:
        response.status() == io.micronaut.http.HttpStatus.OK
    }

    @Client("\${llama.api.url:`http://localhost:8080`}")
    static interface SlotsClient {

        @Get("/slots")
        HttpResponse<SlotsResponse> getSlots()

        @Post("/slots/{id}")
        HttpResponse<Object> saveSlot(@PathVariable("id") Integer id, @QueryValue("action") String action)
    }

    @Serdeable
    static class SlotsResponse {
        List<Slot> slots

        @Serdeable
        static class Slot {
            Integer id
            String state
            Integer n_ctx
            Integer n_past
        }
    }
}
