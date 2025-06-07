package cpp.llama

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject

class RerankingApiSpec extends BaseApiSpec {

    @Inject
    RerankClient client

    def "reranking endpoint should rank documents by relevance to query"() {
        given:
        def documents = [
            "Paris is the capital of France.",
            "Berlin is the capital of Germany.",
            "London is the capital of England.",
            "France is known for its cuisine."
        ]

        def request = new RerankRequest(
            query: "What is the capital of France?",
            documents: documents
        )

        when:
        def response = client.rerank(request)

        then:
        response.status() == io.micronaut.http.HttpStatus.OK
        response.body().results != null
        response.body().results.size() == documents.size()
        // The Paris document should have a higher score and be ranked higher
        def parisIndex = response.body().results.findIndexOf { it.document.contains("Paris") }
        def berlinIndex = response.body().results.findIndexOf { it.document.contains("Berlin") }
        parisIndex < berlinIndex
    }

    @Client("\${llama.api.url:`http://localhost:8080`}")
    static interface RerankClient {

        @Post("/rerank")
        HttpResponse<RerankResponse> rerank(@Body RerankRequest request)
    }

    @Serdeable
    static class RerankRequest {
        String query
        List<String> documents
    }

    @Serdeable
    static class RerankResponse {
        List<RerankResult> results

        @Serdeable
        static class RerankResult {
            String document
            Integer index
            Float score
        }
    }
}
