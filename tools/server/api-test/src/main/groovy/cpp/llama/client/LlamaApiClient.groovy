package cpp.llama.client

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable

@Client("\${llama.api.url:`http://localhost:8080`}")
interface LlamaApiClient {

    @Get("/health")
    HttpResponse<HealthResponse> getHealth()

    @Get("/models")
    HttpResponse<ModelsResponse> getModels()

    @Post("/completion")
    HttpResponse<CompletionResponse> getCompletion(@Body CompletionRequest request)

    @Post("/tokenize")
    HttpResponse<TokenizeResponse> tokenize(@Body TokenizeRequest request)

    @Post("/detokenize")
    HttpResponse<DetokenizeResponse> detokenize(@Body DetokenizeRequest request)

    @Post("/embedding")
    HttpResponse<EmbeddingResponse> embedding(@Body EmbeddingRequest request)

    @Post("/v1/completions")
    HttpResponse<OpenAICompletionResponse> openaiCompletion(@Body OpenAICompletionRequest request)

    @Post("/v1/chat/completions")
    HttpResponse<OpenAIChatCompletionResponse> openaiChatCompletion(@Body OpenAIChatCompletionRequest request)

    @Post("/v1/embeddings")
    HttpResponse<OpenAIEmbeddingResponse> openaiEmbedding(@Body OpenAIEmbeddingRequest request)

    // Response and Request DTOs

    @Serdeable
    static class HealthResponse {
        String status
    }

    @Serdeable
    static class ModelsResponse {
        List<Model> models

        @Serdeable
        static class Model {
            String id
            String name
            Integer n_gpu_layers
            Map<String, Object> metadata
        }
    }

    @Serdeable
    static class CompletionRequest {
        String prompt
        Integer n_predict = 128
        Float temperature = 0.8f
        Integer top_k = 40
        Float top_p = 0.9f
        Float min_p = 0.1f
    }

    @Serdeable
    static class CompletionResponse {
        String content
        Integer timings
        Integer generation_settings
    }

    @Serdeable
    static class TokenizeRequest {
        String content
    }

    @Serdeable
    static class TokenizeResponse {
        List<Integer> tokens
    }

    @Serdeable
    static class DetokenizeRequest {
        List<Integer> tokens
    }

    @Serdeable
    static class DetokenizeResponse {
        String content
    }

    @Serdeable
    static class EmbeddingRequest {
        String content
    }

    @Serdeable
    static class EmbeddingResponse {
        List<Float> embedding
    }

    @Serdeable
    static class OpenAICompletionRequest {
        String model = "default"
        String prompt
        Integer max_tokens = 128
        Float temperature = 0.8f
        Integer top_k = 40
        Float top_p = 0.9f
    }

    @Serdeable
    static class OpenAICompletionResponse {
        String id
        String object = "text_completion"
        Long created
        String model
        List<Choice> choices
        Usage usage

        @Serdeable
        static class Choice {
            String text
            Integer index
            String finish_reason
        }

        @Serdeable
        static class Usage {
            Integer prompt_tokens
            Integer completion_tokens
            Integer total_tokens
        }
    }

    @Serdeable
    static class OpenAIChatCompletionRequest {
        String model = "default"
        List<Message> messages
        Integer max_tokens = 128
        Float temperature = 0.8f

        @Serdeable
        static class Message {
            String role
            String content
        }
    }

    @Serdeable
    static class OpenAIChatCompletionResponse {
        String id
        String object = "chat.completion"
        Long created
        String model
        List<Choice> choices
        Usage usage

        @Serdeable
        static class Choice {
            Message message
            Integer index
            String finish_reason
        }

        @Serdeable
        static class Message {
            String role
            String content
        }

        @Serdeable
        static class Usage {
            Integer prompt_tokens
            Integer completion_tokens
            Integer total_tokens
        }
    }

    @Serdeable
    static class OpenAIEmbeddingRequest {
        String model = "default"
        String input
    }

    @Serdeable
    static class OpenAIEmbeddingResponse {
        String object = "list"
        List<EmbeddingData> data
        String model
        Usage usage

        @Serdeable
        static class EmbeddingData {
            Integer index
            List<Float> embedding
            String object = "embedding"
        }

        @Serdeable
        static class Usage {
            Integer prompt_tokens
            Integer total_tokens
        }
    }
}
