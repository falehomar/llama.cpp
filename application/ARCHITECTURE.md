# Application Architecture

## Table of Contents
1. [Overview](#overview)
2. [Core Components](#core-components)
3. [Model Management](#model-management)
4. [Text Generation and Inference](#text-generation-and-inference)
5. [Features and Capabilities](#features-and-capabilities)
6. [Public Interfaces](#public-interfaces)
    - [6.1 Model Management Interface](#61-model-management-interface)
    - [6.2 Inference Engine Interface](#62-inference-engine-interface)
    - [6.3 Hardware Acceleration Interface](#63-hardware-acceleration-interface)
    - [6.4 Grammar and Formatting Interface](#64-grammar-and-formatting-interface)
    - [6.5 Chat Interface](#65-chat-interface)
    - [6.6 Performance Monitoring Interface](#66-performance-monitoring-interface)
    - [6.7 State Management Interface](#67-state-management-interface)
    - [6.8 REST API Interface](#68-rest-api-interface)
    - [6.9 WebSocket Interface](#69-websocket-interface)

## Overview
This document describes the architecture of the llama.cpp application layer, which provides high-level interfaces and abstractions over the core llama.cpp functionality.

## Core Components
1. Model Management
```cpp
// Load and initialize models
struct llama_model * llama_model_load_from_file(const char * path_model, struct llama_model_params params);
struct llama_model * llama_model_load_from_splits(const char ** paths, size_t n_paths, struct llama_model_params params);

// Model information
int32_t llama_model_n_ctx_train(const struct llama_model * model); // Training context size
int32_t llama_model_n_embd(const struct llama_model * model);      // Embedding dimension
uint64_t llama_model_size(const struct llama_model * model);       // Model size in bytes
uint64_t llama_model_n_params(const struct llama_model * model);   // Number of parameters

// Model capabilities
bool llama_model_has_encoder(const struct llama_model * model);
bool llama_model_has_decoder(const struct llama_model * model);
```

2. Text Generation/Inference
```cpp
// Core inference functions
int32_t llama_decode(struct llama_context * ctx, struct llama_batch batch);
int32_t llama_encode(struct llama_context * ctx, struct llama_batch batch);

// Generation parameters
struct GenerationParams {
    int maxTokens;          // Maximum tokens to generate
    float temperature;      // Sampling temperature (0.0-1.0)
    float topP;            // Top-p sampling
    float presencePenalty; // Presence penalty
    float frequencyPenalty;// Frequency penalty
    float repeatPenalty;   // Repetition penalty
    List<String> stopSequences;
    boolean streamResponse;
}
```

3. Tokenization
```cpp
int32_t llama_tokenize(
    const struct llama_vocab * vocab,
    const char * text,
    int32_t text_len,
    llama_token * tokens,
    int32_t n_tokens_max,
    bool add_special,
    bool parse_special
);

// Special tokens
llama_token llama_vocab_bos(const struct llama_vocab * vocab); // Beginning of sentence
llama_token llama_vocab_eos(const struct llama_vocab * vocab); // End of sentence
llama_token llama_vocab_nl(const struct llama_vocab * vocab);  // Newline
```

4. State Management
```cpp
// Save/load model state
size_t llama_state_get_size(struct llama_context * ctx);
size_t llama_state_get_data(struct llama_context * ctx, uint8_t * dst, size_t size);
size_t llama_state_set_data(struct llama_context * ctx, const uint8_t * src, size_t size);

// Session management
bool llama_state_load_file(struct llama_context * ctx, const char * path_session, ...);
bool llama_state_save_file(struct llama_context * ctx, const char * path_session, ...);
```

5. KV Cache Management
```cpp
// KV cache operations
void llama_kv_self_clear(struct llama_context * ctx);
void llama_kv_self_seq_clear(struct llama_context * ctx, llama_seq_id seq_id);
void llama_kv_self_defrag(struct llama_context * ctx);
bool llama_kv_self_can_shift(const struct llama_context * ctx);
```

6. LoRA Adapter Support
```cpp
// LoRA functions
struct llama_adapter_lora * llama_adapter_lora_init(struct llama_model * model, const char * path_lora);
int32_t llama_set_adapter_lora(struct llama_context * ctx, struct llama_adapter_lora * adapter, float scale);
void llama_clear_adapter_lora(struct llama_context * ctx);
```

7. Sampling
```cpp
struct llama_sampler * llama_sampler_init_greedy(void);  // Greedy sampling
struct llama_sampler * llama_sampler_init_dist(uint32_t seed);  // Distribution sampling
void llama_sampler_set_temperature(struct llama_sampler * sampler, float temperature);
void llama_sampler_set_top_p(struct llama_sampler * sampler, float top_p);
```

8. Performance Monitoring
```cpp
struct llama_perf_context_data {
    double t_start_ms;
    double t_load_ms;
    double t_p_eval_ms;
    double t_eval_ms;
    int32_t n_p_eval;
    int32_t n_eval;
};

// Performance monitoring functions
void llama_perf_context_print(const struct llama_context * ctx);
void llama_perf_context_reset(struct llama_context * ctx);
```

9. Embeddings Generation
```cpp
float * llama_get_embeddings(struct llama_context * ctx);
float * llama_get_embeddings_ith(struct llama_context * ctx, int32_t i);
float * llama_get_embeddings_seq(struct llama_context * ctx, llama_seq_id seq_id);
```

10. Model Quantization
```cpp
struct llama_model_quantize_params {
    int32_t nthread;
    enum llama_ftype ftype;
    enum ggml_type output_tensor_type;
    bool allow_requantize;
    bool quantize_output_tensor;
    bool only_copy;
    bool pure;
};

uint32_t llama_model_quantize(const char * fname_inp, const char * fname_out, const llama_model_quantize_params * params);
```

## Features and Capabilities
The interfaces provide comprehensive functionality for:
- Loading and managing large language models
- Text generation and inference
- Tokenization and vocabulary management
- State and session handling
- Advanced features like LoRA fine-tuning
- Performance optimization through KV cache management
- Model quantization for reduced memory usage
- Embedding generation for text representations
- Flexible sampling strategies for text generation
- Performance monitoring and diagnostics

## Public Interfaces

### 6.1 Model Management Interface
```java
public interface ModelManager {
    /**
     * Load a model from file with specified parameters
     * @param params Model loading parameters
     * @return Loaded model instance
     */
    Model loadModel(ModelParams params);

    /**
     * Convert model between formats
     * @param source Source model path
     * @param target Target model path
     * @param format Target format (GGUF/GGML)
     */
    void convertModel(Path source, Path target, ModelFormat format);

    /**
     * Quantize model to reduce size/memory usage
     * @param model Model to quantize
     * @param options Quantization options
     */
    void quantizeModel(Model model, QuantizationOptions options);
}

public record ModelParams(
    Path modelPath,           // Path to model file
    ModelType type,          // Model type (7B, 13B, 33B, 65B)
    int contextLength,       // Context window size (default: 2048)
    int gpuLayers,          // Number of layers to offload to GPU
    boolean useMMAPLoad,     // Use memory mapping for loading
    String... extraParams    // Additional model-specific parameters
) {}

public record QuantizationOptions(
    QuantizationType type,   // Q4_0, Q4_1, Q5_0, Q5_1, Q8_0
    boolean lowMemory,      // Use low memory mode
    int threads,           // Number of threads to use
    float threshold       // Quantization threshold
) {}
```

### 6.2 Inference Engine Interface
```java
public interface InferenceEngine {
    /**
     * Generate text completion
     * @param prompt Input prompt
     * @param params Generation parameters
     * @return Generated completion
     */
    String generate(String prompt, GenerationParams params);

    /**
     * Stream text completion
     * @param prompt Input prompt
     * @param params Generation parameters
     * @return Observable stream of tokens
     */
    Observable<String> streamGenerate(String prompt, GenerationParams params);

    /**
     * Generate embeddings for input text
     * @param text Input text
     * @return Float array of embeddings
     */
    float[] generateEmbeddings(String text);
}

public record GenerationParams(
    int maxTokens,          // Maximum tokens to generate
    float temperature,      // Sampling temperature (0.0-1.0)
    float topP,             // Top-p sampling (0.0-1.0)
    float presencePenalty,  // Presence penalty (0.0-2.0)
    float frequencyPenalty, // Frequency penalty (0.0-2.0)
    float repeatPenalty,    // Repetition penalty (1.0+)
    List<String> stopSequences, // Sequences to stop generation
    boolean streamResponse   // Stream tokens vs complete response
) {}
```

### 6.3 Hardware Acceleration Interface
```java
public interface HardwareAcceleration {
    /**
     * Configure hardware acceleration
     * @param config Hardware configuration
     */
    void configure(HardwareConfig config);

    /**
     * Get available hardware capabilities
     * @return Hardware capabilities
     */
    HardwareCapabilities getCapabilities();
}

public record HardwareConfig(
    AccelerationType type,   // CPU, CUDA, Metal, OpenCL
    int deviceId,           // GPU device ID
    int threads,            // Number of CPU threads
    int batchSize,          // Batch size for processing
    MemoryConfig memory    // Memory configuration
) {}

public record MemoryConfig(
    long maxRAM,           // Maximum RAM usage
    long maxVRAM,          // Maximum VRAM usage
    boolean lowMemMode     // Enable low memory mode
) {}
```

### 6.4 Grammar and Formatting Interface
```java
public interface GrammarManager {
    /**
     * Load grammar from JSON Schema
     * @param schema JSON Schema definition
     * @return Compiled grammar
     */
    Grammar loadFromJsonSchema(String schema);

    /**
     * Create grammar from regular expression
     * @param regex Regular expression pattern
     * @return Compiled grammar
     */
    Grammar createFromRegex(String regex);

    /**
     * Apply grammar constraints to generation
     * @param grammar Compiled grammar
     * @param generationParams Generation parameters
     */
    void applyGrammar(Grammar grammar, GenerationParams generationParams);
}
```

### 6.5 Chat Interface
```java
public interface ChatManager {
    /**
     * Initialize chat session
     * @param config Chat configuration
     * @return Session ID
     */
    String initSession(ChatConfig config);

    /**
     * Send message in chat session
     * @param sessionId Session ID
     * @param message User message
     * @return Assistant's response
     */
    Observable<ChatMessage> sendMessage(String sessionId, String message);

    /**
     * Configure system prompt
     * @param sessionId Session ID
     * @param systemPrompt System prompt text
     */
    void setSystemPrompt(String sessionId, String systemPrompt);
}

public record ChatConfig(
    String modelId,          // Model identifier
    String systemPrompt,     // Initial system prompt
    GenerationParams params, // Generation parameters
    int maxHistory,         // Maximum history to maintain
    boolean streamResponse  // Stream response tokens
) {}

public record ChatMessage(
    String role,            // "user" or "assistant"
    String content,         // Message content
    long timestamp,         // Message timestamp
    Map<String, Object> metadata // Additional message metadata
) {}
```

### 6.6 Performance Monitoring Interface
```java
public interface PerformanceMonitor {
    /**
     * Get current performance metrics
     * @return Performance metrics
     */
    PerformanceMetrics getMetrics();

    /**
     * Subscribe to performance updates
     * @param interval Update interval in milliseconds
     * @return Observable stream of metrics
     */
    Observable<PerformanceMetrics> subscribeToMetrics(long interval);
}

public record PerformanceMetrics(
    float tokensPerSecond,   // Generation speed
    float promptProcessingMs, // Prompt processing time
    float gpuUtilization,    // GPU utilization percentage
    float cpuUtilization,    // CPU utilization percentage
    long ramUsage,          // RAM usage in bytes
    long vramUsage,         // VRAM usage in bytes
    Map<String, Float> modelMetrics // Model-specific metrics
) {}
```

### 6.7 State Management Interface
```java
public interface StateManager {
    /**
     * Save model state
     * @param model Model instance
     * @param path Save path
     */
    void saveState(Model model, Path path);

    /**
     * Load model state
     * @param path State path
     * @return Model instance
     */
    Model loadState(Path path);

    /**
     * Export chat history
     * @param sessionId Session ID
     * @param format Export format
     * @param path Export path
     */
    void exportHistory(String sessionId, ExportFormat format, Path path);
}

public enum ExportFormat {
    JSON,
    MARKDOWN,
    TEXT,
    HTML
}
```

### 6.8 REST API Interface

#### Model Management Endpoints
```http
# Load model
POST /v1/models/load
{
    "model_path": string,
    "model_type": string,
    "context_length": integer,
    "gpu_layers": integer,
    "use_mmap": boolean
}

# List available models
GET /v1/models

# Get model info
GET /v1/models/{model_id}

# Unload model
DELETE /v1/models/{model_id}
```

#### Inference Endpoints
```http
# Generate completion
POST /v1/completions
{
    "model": string,
    "prompt": string,
    "max_tokens": integer,
    "temperature": float,
    "top_p": float,
    "presence_penalty": float,
    "frequency_penalty": float,
    "stop": [string],
    "stream": boolean
}

# Generate chat completion
POST /v1/chat/completions
{
    "model": string,
    "messages": [
        {
            "role": string,
            "content": string
        }
    ],
    "temperature": float,
    "max_tokens": integer,
    "stream": boolean
}

# Generate embeddings
POST /v1/embeddings
{
    "model": string,
    "input": string
}
```

#### System Management Endpoints
```http
# Get system status
GET /v1/system/status

# Get performance metrics
GET /v1/system/metrics

# Update system configuration
PUT /v1/system/config
{
    "threads": integer,
    "batch_size": integer,
    "max_ram": integer,
    "max_vram": integer
}
```

### 6.9 WebSocket Interface
```typescript
// Connect to WebSocket
ws://localhost:8080/v1/stream

// Message Types
interface WebSocketMessage {
    type: 'generate' | 'chat' | 'status' | 'error';
    data: any;
}

// Generation Request
interface GenerationRequest {
    type: 'generate';
    data: {
        prompt: string;
        params: GenerationParams;
    };
}

// Chat Request
interface ChatRequest {
    type: 'chat';
    data: {
        session_id: string;
        message: string;
    };
}

// Server Response
interface ServerResponse {
    type: 'token' | 'complete' | 'error';
    data: {
        token?: string;
        complete?: boolean;
        error?: string;
    };
}
```
