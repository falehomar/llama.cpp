# llama.cpp API Documentation

This document provides an overview of the llama.cpp APIs used in the server implementation, including their purposes and the header/source files where they are defined and implemented.

## API Overview

The llama.cpp library provides a C API for working with Large Language Models (LLMs). The API is defined in `include/llama.h` and implemented across multiple source files in the `src/` directory.

## Core API Categories

The API can be divided into several categories:

1. **Model Management** - Loading, initializing, and freeing models
2. **Context Management** - Creating and managing contexts for inference
3. **Tokenization** - Converting between text and tokens
4. **Batch Processing** - Processing batches of tokens
5. **KV Cache Management** - Managing the key-value cache
6. **Sampling** - Generating text from model outputs
7. **State Management** - Saving and loading model state

## API Functions Used in Server

### Model Management

| Function | Purpose | Header | Implementation |
|----------|---------|--------|---------------|
| `llama_model_load_from_file` | Loads a model from a file | llama.h | src/llama.cpp |
| `llama_model_free` | Frees a model | llama.h | src/llama.cpp |
| `llama_get_model` | Gets the model from a context | llama.h | src/llama-context.cpp |
| `llama_model_get_vocab` | Gets the vocabulary from a model | llama.h | src/llama-model.cpp |
| `llama_model_n_ctx_train` | Gets the context size used during training | llama.h | src/llama-model.cpp |
| `llama_model_n_embd` | Gets the embedding size | llama.h | src/llama-model.cpp |

### Context Management

| Function | Purpose | Header | Implementation |
|----------|---------|--------|---------------|
| `llama_init_from_model` | Initializes a context from a model | llama.h | src/llama-context.cpp |
| `llama_free` | Frees a context | llama.h | src/llama-context.cpp |
| `llama_n_ctx` | Gets the context size | llama.h | src/llama-context.cpp |
| `llama_n_batch` | Gets the batch size | llama.h | src/llama-context.cpp |
| `llama_n_ubatch` | Gets the micro batch size | llama.h | src/llama-context.cpp |
| `llama_set_embeddings` | Sets whether to return embeddings | llama.h | src/llama-context.cpp |
| `llama_pooling_type` | Gets the pooling type | llama.h | src/llama-context.cpp |

### Tokenization

| Function | Purpose | Header | Implementation |
|----------|---------|--------|---------------|
| `llama_vocab_get_add_bos` | Checks if BOS token should be added | llama.h | src/llama-vocab.cpp |
| `llama_vocab_eos` | Gets the EOS token | llama.h | src/llama-vocab.cpp |
| `llama_vocab_is_eog` | Checks if a token is an end-of-generation token | llama.h | src/llama-vocab.cpp |
| `llama_vocab_n_tokens` | Gets the number of tokens in the vocabulary | llama.h | src/llama-vocab.cpp |

### Batch Processing

| Function | Purpose | Header | Implementation |
|----------|---------|--------|---------------|
| `llama_batch_init` | Initializes a batch | llama.h | src/llama-batch.cpp |
| `llama_batch_free` | Frees a batch | llama.h | src/llama-batch.cpp |
| `llama_encode` | Encodes a batch of tokens | llama.h | src/llama-context.cpp |
| `llama_decode` | Decodes a batch of tokens | llama.h | src/llama-context.cpp |
| `llama_get_logits` | Gets the logits from the last decode | llama.h | src/llama-context.cpp |
| `llama_get_logits_ith` | Gets the logits for a specific token | llama.h | src/llama-context.cpp |
| `llama_get_embeddings` | Gets the embeddings from the last decode | llama.h | src/llama-context.cpp |
| `llama_get_embeddings_ith` | Gets the embeddings for a specific token | llama.h | src/llama-context.cpp |
| `llama_get_embeddings_seq` | Gets the embeddings for a sequence | llama.h | src/llama-context.cpp |

### KV Cache Management

| Function | Purpose | Header | Implementation |
|----------|---------|--------|---------------|
| `llama_kv_self_clear` | Clears the KV cache | llama.h | src/llama-kv-cache.cpp |
| `llama_kv_self_seq_rm` | Removes tokens from the KV cache | llama.h | src/llama-kv-cache.cpp |
| `llama_kv_self_seq_add` | Adds tokens to the KV cache | llama.h | src/llama-kv-cache.cpp |
| `llama_kv_self_seq_pos_min` | Gets the minimum position in the KV cache | llama.h | src/llama-kv-cache.cpp |
| `llama_kv_self_seq_pos_max` | Gets the maximum position in the KV cache | llama.h | src/llama-kv-cache.cpp |
| `llama_kv_self_can_shift` | Checks if the KV cache can be shifted | llama.h | src/llama-kv-cache.cpp |
| `llama_kv_self_update` | Updates the KV cache | llama.h | src/llama-kv-cache.cpp |

### State Management

| Function | Purpose | Header | Implementation |
|----------|---------|--------|---------------|
| `llama_state_seq_save_file` | Saves a sequence to a file | llama.h | src/llama-context.cpp |
| `llama_state_seq_load_file` | Loads a sequence from a file | llama.h | src/llama-context.cpp |

## API Usage in Server

The server.cpp file uses these APIs to implement a web server that provides access to llama.cpp functionality. The server handles requests for text generation, embeddings, and other operations, and uses the llama.cpp API to perform these operations on the loaded model.

Key usage patterns include:
1. Loading a model and initializing a context
2. Processing batches of tokens for text generation
3. Managing the KV cache for efficient generation
4. Extracting embeddings for embedding requests
5. Saving and loading state for persistence

## Request-to-Response Flow

This section details the flow from the moment a request arrives at the server until a response is produced.

### General Flow

1. **Request Reception**: The server receives an HTTP request at one of its endpoints (e.g., `/completions`, `/chat/completions`, `/embeddings`).
2. **Request Parsing**: The server parses the JSON request body and validates the parameters.
3. **Task Creation**: The server creates a task based on the request type (completion, embedding, reranking, etc.).
4. **Task Queuing**: The task is added to the server's task queue.
5. **Slot Allocation**: The server allocates a slot for the task when one becomes available.
6. **Task Processing**: The server processes the task using the llama.cpp API:
   - For text generation, it tokenizes the prompt, processes the tokens through the model, and generates new tokens.
   - For embeddings, it tokenizes the input text and extracts embeddings from the model.
   - For reranking, it processes each document and calculates relevance scores.
7. **Response Generation**: The server generates a response based on the task results.
8. **Response Delivery**: The server sends the response back to the client.

### Streaming vs Non-Streaming

#### Non-Streaming Flow

In non-streaming mode (`stream=false`):
1. The server processes the entire request and generates all tokens before sending any response.
2. Once all tokens are generated, the server constructs a complete response with all generated text.
3. The response is sent to the client in a single HTTP response.
4. The connection is closed after the response is sent.

#### Streaming Flow

In streaming mode (`stream=true`):
1. The server sets up a chunked HTTP response with content type `text/event-stream`.
2. As each token is generated, the server immediately sends it to the client as a Server-Sent Event (SSE).
3. Each event contains a partial response with the newly generated token.
4. The client receives and processes these events incrementally, updating the UI as new tokens arrive.
5. When generation is complete, the server sends a final event with a completion indicator.
6. The connection is closed after all events are sent.

Streaming provides a better user experience for longer generations, as users can see the text being generated in real-time rather than waiting for the entire response.

### Function Calling Handling

The server supports function calling (also known as tool use) in chat completions:

1. **Function Definition**: The client includes function definitions in the request.
2. **Model Generation**: The model generates text that may include function calls.
3. **Function Call Detection**: The server detects when the model has generated a function call.
4. **Response Formatting**: The server formats the function call as a structured JSON object in the response.
5. **Response Delivery**:
   - In non-streaming mode, the function call is included in the complete response.
   - In streaming mode, the function call is sent as a special event.

Function calls are represented in the response as:
```json
{
  "tool_calls": [
    {
      "id": "call_abc123",
      "type": "function",
      "function": {
        "name": "function_name",
        "arguments": "{\"arg1\":\"value1\",\"arg2\":\"value2\"}"
      }
    }
  ]
}
```

The client is responsible for executing the function and sending the result back to the server in a follow-up request.

## Endpoint Analysis

This section provides a detailed analysis of each endpoint in the server, from the moment a request is received until a response is produced.

### Server Status Endpoints

#### `/health` Endpoint

**HTTP Method**: GET

**Purpose**: Check if the server is running and healthy.

**Request-to-Response Flow**:
1. The server receives a GET request to `/health`.
2. The server returns a simple JSON response with status "ok".
3. No authentication is required for this endpoint.

#### `/metrics` Endpoint

**HTTP Method**: GET

**Purpose**: Get server metrics and statistics.

**Request-to-Response Flow**:
1. The server receives a GET request to `/metrics`.
2. The server collects metrics about its operation (slots, tasks, etc.).
3. The metrics are formatted as JSON.
4. A single HTTP response is sent with the metrics.

#### `/props` Endpoint

**HTTP Method**: GET

**Purpose**: Get server properties and configuration.

**Request-to-Response Flow**:
1. The server receives a GET request to `/props`.
2. The server collects its current properties and configuration.
3. The properties are formatted as JSON.
4. A single HTTP response is sent with the properties.

#### `/props` Endpoint (Update)

**HTTP Method**: POST

**Purpose**: Update server properties and configuration.

**Request Parameters**:
- Various properties to update (depends on the server configuration)

**Request-to-Response Flow**:
1. The server receives a POST request to `/props`.
2. The request body is parsed as JSON.
3. The server updates its properties based on the request.
4. A response is sent with the updated properties.

### Model Information Endpoints

#### `/models` and `/v1/models` Endpoints

**HTTP Method**: GET

**Purpose**: Get information about available models.

**Request-to-Response Flow**:
1. The server receives a GET request to `/models` or `/v1/models`.
2. The server collects information about the currently loaded model.
3. The model information is formatted as JSON.
4. A single HTTP response is sent with the model information.
5. No authentication is required for this endpoint.

#### `/api/show` Endpoint

**HTTP Method**: POST

**Purpose**: Show API information and capabilities.

**Request-to-Response Flow**:
1. The server receives a POST request to `/api/show`.
2. The server collects information about its API capabilities.
3. The API information is formatted as JSON.
4. A single HTTP response is sent with the API information.

### Text Generation Endpoints

#### `/completions` and `/v1/completions` Endpoints

**HTTP Method**: POST

**Purpose**: Generate text completions for a given prompt.

**Request Parameters**:
- `prompt`: The text prompt to complete (string or array of strings)
- `model`: The model to use for completion (optional)
- `max_tokens`: Maximum number of tokens to generate (optional)
- `temperature`: Sampling temperature (optional)
- `top_p`: Nucleus sampling parameter (optional)
- `n`: Number of completions to generate (optional)
- `stream`: Whether to stream the response (optional, default: false)
- `stop`: Stop sequences to end generation (optional)
- `frequency_penalty`: Penalty for repeated tokens (optional)
- `presence_penalty`: Penalty for tokens already in the prompt (optional)
- `logit_bias`: Bias for specific tokens (optional)

**Request-to-Response Flow**:
1. The server receives a POST request to `/completions` or `/v1/completions`.
2. The request body is parsed as JSON.
3. For OpenAI-compatible endpoints (`/v1/completions`), the parameters are converted to the internal format.
4. The server checks if completions are supported (not in embeddings-only mode).
5. The server generates a completion ID.
6. The prompt is tokenized.
7. A server task is created for each prompt with the appropriate parameters.
8. The tasks are added to the queue for processing.
9. If `stream=false` (non-streaming):
   - The server waits for all tasks to complete.
   - The results are formatted as JSON.
   - A single HTTP response is sent with all generated text.
10. If `stream=true` (streaming):
    - The server sets up a chunked HTTP response with content type `text/event-stream`.
    - As each token is generated, it's immediately sent to the client as a Server-Sent Event (SSE).
    - When generation is complete, a final "[DONE]" event is sent for OpenAI compatibility.
    - The connection is closed.

#### `/chat/completions` and `/v1/chat/completions` Endpoints

**HTTP Method**: POST

**Purpose**: Generate chat completions for a given conversation.

**Request Parameters**:
- `messages`: Array of message objects with `role` and `content` (required)
- `model`: The model to use for completion (optional)
- `max_tokens`: Maximum number of tokens to generate (optional)
- `temperature`: Sampling temperature (optional)
- `top_p`: Nucleus sampling parameter (optional)
- `n`: Number of completions to generate (optional)
- `stream`: Whether to stream the response (optional, default: false)
- `stop`: Stop sequences to end generation (optional)
- `frequency_penalty`: Penalty for repeated tokens (optional)
- `presence_penalty`: Penalty for tokens already in the prompt (optional)
- `logit_bias`: Bias for specific tokens (optional)
- `tools`: Array of tool definitions for function calling (optional)
- `tool_choice`: How to choose which tool to use (optional)

**Request-to-Response Flow**:
1. The server receives a POST request to `/chat/completions` or `/v1/chat/completions`.
2. The request body is parsed as JSON.
3. The server checks if completions are supported (not in embeddings-only mode).
4. The server generates a completion ID.
5. The messages are processed and formatted according to the chat template.
6. The formatted prompt is tokenized.
7. A server task is created with the appropriate parameters.
8. The task is added to the queue for processing.
9. If `stream=false` (non-streaming):
   - The server waits for the task to complete.
   - The results are formatted as JSON, including any function calls.
   - A single HTTP response is sent with all generated text.
10. If `stream=true` (streaming):
    - The server sets up a chunked HTTP response with content type `text/event-stream`.
    - As each token is generated, it's immediately sent to the client as a Server-Sent Event (SSE).
    - Function calls are detected and sent as special events.
    - When generation is complete, a final "[DONE]" event is sent for OpenAI compatibility.
    - The connection is closed.

### Vector Representation Endpoints

#### `/embeddings` and `/v1/embeddings` Endpoints

**HTTP Method**: POST

**Purpose**: Generate embeddings for a given text.

**Request Parameters**:
- `input`: The text to embed (string or array of strings) (required)
- `model`: The model to use for embeddings (optional)
- `encoding_format`: Format to return embeddings in (`float` or `base64`, optional)

**Request-to-Response Flow**:
1. The server receives a POST request to `/embeddings` or `/v1/embeddings`.
2. The request body is parsed as JSON.
3. The server checks if the pooling type is compatible with the requested format.
4. The input text is extracted from either the "input" or "content" field.
5. The encoding format preference is checked (float or base64).
6. The input text is tokenized.
7. Embedding tasks are created for each input.
8. The tasks are added to the queue for processing.
9. The server waits for all tasks to complete.
10. The embeddings are extracted from the results.
11. The response is formatted based on OpenAI compatibility settings.
12. A single HTTP response is sent with all embeddings.

#### `/rerank` and `/v1/rerank` Endpoints

**HTTP Method**: POST

**Purpose**: Rerank a list of documents based on their relevance to a query.

**Request Parameters**:
- `query`: The query text (required)
- `documents`: Array of documents to rerank (required)
- `model`: The model to use for reranking (optional)
- `top_n`: Number of top documents to return (optional)

**Request-to-Response Flow**:
1. The server receives a POST request to `/rerank` or `/v1/rerank`.
2. The request body is parsed as JSON.
3. The server checks if reranking is supported.
4. The query and documents are extracted from the request.
5. The query and each document are tokenized.
6. Reranking tasks are created for each document.
7. The tasks are added to the queue for processing.
8. The server waits for all tasks to complete.
9. The relevance scores are extracted from the results.
10. The documents are sorted by relevance score.
11. The top N documents are selected.
12. A single HTTP response is sent with the reranked documents.

### Token Management Endpoints

#### `/tokenize` Endpoint

**HTTP Method**: POST

**Purpose**: Tokenize a given text.

**Request Parameters**:
- `content`: The text to tokenize (required)
- `add_bos`: Whether to add a beginning-of-sentence token (optional)
- `add_eos`: Whether to add an end-of-sentence token (optional)
- `parse_special`: Whether to parse special tokens (optional)

**Request-to-Response Flow**:
1. The server receives a POST request to `/tokenize`.
2. The request body is parsed as JSON.
3. The text content is extracted from the request.
4. The text is tokenized using the model's vocabulary.
5. The tokens are formatted as a JSON array.
6. A single HTTP response is sent with the tokens.

#### `/detokenize` Endpoint

**HTTP Method**: POST

**Purpose**: Convert tokens back to text.

**Request Parameters**:
- `tokens`: Array of token IDs to convert (required)

**Request-to-Response Flow**:
1. The server receives a POST request to `/detokenize`.
2. The request body is parsed as JSON.
3. The token IDs are extracted from the request.
4. The tokens are converted to text using the model's vocabulary.
5. The text is formatted as a JSON response.
6. A single HTTP response is sent with the text.

#### `/infill` Endpoint

**HTTP Method**: POST

**Purpose**: Fill in missing text between a prefix and a suffix.

**Request Parameters**:
- `prompt`: The text with a special marker indicating where to infill
- `model`: The model to use for infilling (optional)
- Other parameters similar to `/completions`

**Request-to-Response Flow**:
1. The server receives a POST request to `/infill`.
2. The request body is parsed as JSON.
3. The server checks if the model supports infilling (has the necessary special tokens).
4. The prompt is processed to extract the prefix and suffix.
5. The prompt is formatted with special tokens for infilling.
6. The formatted prompt is tokenized.
7. A server task is created with the appropriate parameters.
8. The task is added to the queue for processing.
9. The server processes the request similar to completions, generating text to fill the gap.
10. The response is formatted and sent to the client.

#### `/apply-template` Endpoint

**HTTP Method**: POST

**Purpose**: Apply a chat template to a set of messages without generating a response.

**Request Parameters**:
- `messages`: Array of message objects with `role` and `content` (required)
- `template`: The template to apply (optional)
- `add_assistant`: Whether to add an assistant marker at the end (optional)

**Request-to-Response Flow**:
1. The server receives a POST request to `/apply-template`.
2. The request body is parsed as JSON.
3. The messages are extracted from the request.
4. The chat template is applied to the messages.
5. The formatted prompt is returned without generating any text.
6. A single HTTP response is sent with the formatted prompt.

### Model Adaptation Endpoints

#### `/lora-adapters` Endpoint (List)

**HTTP Method**: GET

**Purpose**: List available LoRA adapters.

**Request-to-Response Flow**:
1. The server receives a GET request to `/lora-adapters`.
2. The server collects information about available LoRA adapters.
3. The adapter information is formatted as JSON.
4. A single HTTP response is sent with the adapter information.

#### `/lora-adapters` Endpoint (Apply)

**HTTP Method**: POST

**Purpose**: Apply a LoRA adapter to the model.

**Request Parameters**:
- `adapter_name`: Name of the adapter to apply (required)
- `scale`: Scaling factor for the adapter (optional)

**Request-to-Response Flow**:
1. The server receives a POST request to `/lora-adapters`.
2. The request body is parsed as JSON.
3. The adapter name and scale are extracted from the request.
4. The server loads and applies the specified LoRA adapter.
5. A response is sent with the result of the operation.

### State Management Endpoints

#### `/slots` Endpoint

**HTTP Method**: GET

**Purpose**: List available slots for saving/restoring model state.

**Request-to-Response Flow**:
1. The server receives a GET request to `/slots`.
2. The server collects information about available slots.
3. The slot information is formatted as JSON.
4. A single HTTP response is sent with the slot information.

#### `/slots/:id_slot` Endpoint

**HTTP Method**: POST

**Purpose**: Perform actions on a specific slot (save, restore, erase).

**Request Parameters**:
- `action`: The action to perform (`save`, `restore`, or `erase`) (required)

**Request-to-Response Flow**:
1. The server receives a POST request to `/slots/:id_slot`.
2. The request body is parsed as JSON.
3. The action is extracted from the request.
4. The server performs the requested action on the specified slot:
   - For `save`, the current model state is saved to the slot.
   - For `restore`, the model state is restored from the slot.
   - For `erase`, the slot is cleared.
5. A response is sent with the result of the operation.

## Implementation Details

The llama.cpp API is implemented across multiple source files, each focusing on a specific aspect of the API:

- **llama.cpp**: Main implementation file with core API functions
- **llama-model.cpp**: Model-related functions
- **llama-context.cpp**: Context-related functions
- **llama-vocab.cpp**: Vocabulary-related functions
- **llama-kv-cache.cpp**: KV cache management functions
- **llama-batch.cpp**: Batch processing functions
- **llama-sampling.cpp**: Sampling-related functions

The implementation uses a modular approach, with each file focusing on a specific aspect of the API. This makes the codebase more maintainable and easier to understand.
