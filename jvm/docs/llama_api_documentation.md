# llama.cpp API Documentation

## Table of Contents
1. [Introduction](#introduction)
2. [Initialization and Backend Management](#initialization-and-backend-management)
3. [Model Management](#model-management)
4. [Context Management](#context-management)
5. [Tokenization and Vocabulary](#tokenization-and-vocabulary)
6. [Batch Processing](#batch-processing)
7. [KV Cache Management](#kv-cache-management)
8. [State Management](#state-management)
9. [Decoding](#decoding)
10. [Sampling](#sampling)
11. [Adapter Management](#adapter-management)

## Introduction

This document provides a comprehensive overview of the llama.cpp API as defined in `llama.h`. The API is organized into modules, each responsible for a specific aspect of the library's functionality.

## Initialization and Backend Management

This module handles the initialization and cleanup of the llama.cpp backend.

### Dependencies
- ggml

### Functions

| Function | Description |
|----------|-------------|
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend |
| [`llama_backend_free`](#llama_backend_free) | Free resources used by the backend |
| [`llama_numa_init`](#llama_numa_init) | Initialize NUMA optimization strategy |
| [`llama_time_us`](#llama_time_us) | Get current time in microseconds |
| [`llama_max_devices`](#llama_max_devices) | Get maximum number of supported devices |
| [`llama_supports_mmap`](#llama_supports_mmap) | Check if memory mapping is supported |
| [`llama_supports_mlock`](#llama_supports_mlock) | Check if memory locking is supported |
| [`llama_supports_gpu_offload`](#llama_supports_gpu_offload) | Check if GPU offloading is supported |
| [`llama_supports_rpc`](#llama_supports_rpc) | Check if RPC is supported |
| [`llama_print_system_info`](#llama_print_system_info) | Print system information |
| [`llama_log_set`](#llama_log_set) | Set logging callback |

### Structs

None specific to this module.

## Model Management

This module handles loading, saving, and managing models.

### Dependencies
- Initialization and Backend Management
- ggml

### Functions

| Function | Description |
|----------|-------------|
| [`llama_model_default_params`](#llama_model_default_params) | Get default model parameters |
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file |
| [`llama_model_load_from_splits`](#llama_model_load_from_splits) | Load a model from multiple splits |
| [`llama_model_save_to_file`](#llama_model_save_to_file) | Save a model to a file |
| [`llama_model_free`](#llama_model_free) | Free a model |
| [`llama_model_get_vocab`](#llama_model_get_vocab) | Get the vocabulary from a model |
| [`llama_model_n_ctx_train`](#llama_model_n_ctx_train) | Get the context size used during training |
| [`llama_model_n_embd`](#llama_model_n_embd) | Get the embedding size |
| [`llama_model_n_layer`](#llama_model_n_layer) | Get the number of layers |
| [`llama_model_n_head`](#llama_model_n_head) | Get the number of attention heads |
| [`llama_model_n_head_kv`](#llama_model_n_head_kv) | Get the number of key-value heads |
| [`llama_model_rope_freq_scale_train`](#llama_model_rope_freq_scale_train) | Get the RoPE frequency scaling factor |
| [`llama_model_rope_type`](#llama_model_rope_type) | Get the RoPE type |
| [`llama_model_meta_val_str`](#llama_model_meta_val_str) | Get metadata value as a string by key name |
| [`llama_model_meta_count`](#llama_model_meta_count) | Get the number of metadata key/value pairs |
| [`llama_model_meta_key_by_index`](#llama_model_meta_key_by_index) | Get metadata key name by index |
| [`llama_model_meta_val_str_by_index`](#llama_model_meta_val_str_by_index) | Get metadata value as a string by index |
| [`llama_model_desc`](#llama_model_desc) | Get a string describing the model type |
| [`llama_model_size`](#llama_model_size) | Get the total size of all tensors in the model |
| [`llama_model_chat_template`](#llama_model_chat_template) | Get the default chat template |
| [`llama_model_n_params`](#llama_model_n_params) | Get the total number of parameters in the model |
| [`llama_model_has_encoder`](#llama_model_has_encoder) | Check if the model contains an encoder |
| [`llama_model_has_decoder`](#llama_model_has_decoder) | Check if the model contains a decoder |
| [`llama_model_decoder_start_token`](#llama_model_decoder_start_token) | Get the decoder start token |
| [`llama_model_is_recurrent`](#llama_model_is_recurrent) | Check if the model is recurrent |
| [`llama_model_quantize`](#llama_model_quantize) | Quantize a model |

### Structs

| Struct | Description |
|--------|-------------|
| `llama_model` | Opaque structure representing a model |
| `llama_model_params` | Parameters for model loading |
| `llama_model_kv_override` | Key-value override for model metadata |
| `llama_model_tensor_buft_override` | Buffer type override for model tensors |
| `llama_model_quantize_params` | Parameters for model quantization |

## Context Management

This module handles creating and managing contexts for inference.

### Dependencies
- Model Management
- ggml

### Functions

| Function | Description |
|----------|-------------|
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters |
| [`llama_init_from_model`](#llama_init_from_model) | Initialize a context from a model |
| [`llama_free`](#llama_free) | Free a context |
| [`llama_n_ctx`](#llama_n_ctx) | Get the context size |
| [`llama_n_batch`](#llama_n_batch) | Get the batch size |
| [`llama_n_ubatch`](#llama_n_ubatch) | Get the micro batch size |
| [`llama_n_seq_max`](#llama_n_seq_max) | Get the maximum number of sequences |
| [`llama_get_model`](#llama_get_model) | Get the model from a context |
| [`llama_get_kv_self`](#llama_get_kv_self) | Get the KV cache from a context |
| [`llama_pooling_type`](#llama_pooling_type) | Get the pooling type |
| [`llama_set_n_threads`](#llama_set_n_threads) | Set the number of threads |
| [`llama_n_threads`](#llama_n_threads) | Get the number of threads for generation |
| [`llama_n_threads_batch`](#llama_n_threads_batch) | Get the number of threads for batch processing |
| [`llama_set_embeddings`](#llama_set_embeddings) | Set whether to return embeddings |
| [`llama_set_causal_attn`](#llama_set_causal_attn) | Set whether to use causal attention |
| [`llama_set_warmup`](#llama_set_warmup) | Set whether the model is in warmup mode |
| [`llama_set_abort_callback`](#llama_set_abort_callback) | Set abort callback |
| [`llama_synchronize`](#llama_synchronize) | Wait until all computations are finished |
| [`llama_get_logits`](#llama_get_logits) | Get token logits from the last decode |
| [`llama_get_logits_ith`](#llama_get_logits_ith) | Get logits for a specific token |
| [`llama_get_embeddings`](#llama_get_embeddings) | Get all output token embeddings |
| [`llama_get_embeddings_ith`](#llama_get_embeddings_ith) | Get embeddings for a specific token |
| [`llama_get_embeddings_seq`](#llama_get_embeddings_seq) | Get embeddings for a sequence |
| [`llama_attach_threadpool`](#llama_attach_threadpool) | Attach a threadpool to a context |
| [`llama_detach_threadpool`](#llama_detach_threadpool) | Detach a threadpool from a context |

### Structs

| Struct | Description |
|--------|-------------|
| `llama_context` | Opaque structure representing a context |
| `llama_context_params` | Parameters for context initialization |

### llama_context_default_params

#### Function Signature

```c
LLAMA_API struct llama_context_params llama_context_default_params(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns a `llama_context_params` struct initialized with default values. This function should be called to get a starting point for creating a context configuration, which can then be modified as needed before passing to `llama_init_from_model`.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Get default parameters for context creation
    struct llama_context_params params = llama_context_default_params();

    // Modify parameters as needed
    params.n_ctx = 2048;  // Set context size to 2048 tokens
    params.n_threads = 4; // Use 4 threads for inference

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", params);
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Use the context...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from server.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse command line arguments
    gpt_params params;
    if (!gpt_params_parse(argc, argv, params)) {
        return 1;
    }

    // Get default context parameters
    llama_context_params llama_params = llama_context_default_params();

    // Customize parameters based on user input
    llama_params.n_ctx = params.n_ctx;
    llama_params.n_batch = params.n_batch;
    llama_params.n_threads = params.n_threads;
    llama_params.n_threads_batch = params.n_threads_batch;

    // Load model and create context
    llama_model * model = llama_model_load_from_file(params.model.c_str(), llama_params);
    llama_context * ctx = llama_init_from_model(model, llama_params);

    // Use the context for the server...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);

    return 0;
}
```

#### Important Notes

1. The default parameters are designed to work for most basic use cases but may not be optimal for all scenarios.
2. Key default values include:
   - Context size (`n_ctx`): 512 tokens
   - Batch size (`n_batch`): 2048 tokens
   - Micro batch size (`n_ubatch`): 512 tokens
   - Maximum sequences (`n_seq_max`): 1
   - KV cache data types: `GGML_TYPE_F16` for both keys and values
3. Always review and adjust the default parameters based on your specific use case, especially for context size and thread count.
4. Some parameters like `rope_scaling_type` and `pooling_type` are set to `UNSPECIFIED` by default, which means the model's default values will be used.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_init_from_model`](#llama_init_from_model) | Initialize a context from a model | Uses the parameters returned by this function |
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Often used before context initialization |

### llama_init_from_model

#### Function Signature

```c
LLAMA_API struct llama_context * llama_init_from_model(
                 struct llama_model * model,
        struct llama_context_params   params);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `struct llama_model *` | Pointer to a loaded model | A valid model loaded with `llama_model_load_from_file` |
| `params` | `struct llama_context_params` | Parameters for context initialization | Typically obtained from `llama_context_default_params` and then modified |

#### Description

Creates and initializes a new inference context using the provided model and parameters. This function allocates memory for the context, including the KV cache, and prepares the model for inference operations.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Get default parameters
    struct llama_context_params params = llama_context_default_params();

    // Modify parameters as needed
    params.n_ctx = 2048;  // Set context size to 2048 tokens

    // Load the model
    struct llama_model * model = llama_model_load_from_file("model.gguf", params);
    if (!model) {
        fprintf(stderr, "Failed to load model\n");
        return 1;
    }

    // Initialize the context
    struct llama_context * ctx = llama_init_from_model(model, params);
    if (!ctx) {
        fprintf(stderr, "Failed to create context\n");
        llama_model_free(model);
        return 1;
    }

    // Use the context...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from embedding.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse parameters
    common_params params;
    if (!common_params_parse(argc, argv, params, LLAMA_EXAMPLE_EMBEDDING)) {
        return 1;
    }

    // Initialize backend
    llama_backend_init();
    llama_numa_init(params.numa);

    // Load the model
    llama_model_params model_params = llama_model_default_params();
    model_params.n_gpu_layers = params.n_gpu_layers;
    llama_model * model = llama_model_load_from_file(params.model.c_str(), model_params);

    // Initialize the context
    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = params.n_ctx;
    ctx_params.n_batch = params.n_batch;
    ctx_params.embeddings = true;  // We want embeddings for this example

    llama_context * ctx = llama_init_from_model(model, ctx_params);

    // Use the context for embeddings...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

#### Important Notes

1. The function returns `NULL` if initialization fails, so always check the return value.
2. Common reasons for failure include:
   - Invalid model pointer
   - Both `n_batch` and `n_ubatch` set to zero
   - Both `n_ctx` and `model->hparams.n_ctx_train` set to zero
   - Incompatible parameter combinations (e.g., V cache quantization without flash attention)
3. The context must be freed with `llama_free()` when no longer needed.
4. The model must remain valid for the lifetime of the context.
5. The context size (`n_ctx`) determines how many tokens can be kept in the KV cache, affecting memory usage.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides initial parameters for this function |
| [`llama_free`](#llama_free) | Free a context | Cleanup function for contexts created by this function |
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Provides the model required by this function |

### llama_free

#### Function Signature

```c
LLAMA_API void llama_free(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context to be freed | A valid context created with `llama_init_from_model` |

#### Description

Frees all resources associated with a context, including memory allocated for the KV cache and other internal structures. This function should be called when the context is no longer needed to prevent memory leaks.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Use the context...

    // Free the context when done
    llama_free(ctx);

    // Free the model and backend
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from main.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse parameters
    gpt_params params;
    if (!gpt_params_parse(argc, argv, params)) {
        return 1;
    }

    // Initialize backend
    llama_backend_init();

    // Load model
    llama_model * model = llama_model_load_from_file(params.model.c_str(), llama_model_default_params());

    // Create context
    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = params.n_ctx;
    llama_context * ctx = llama_init_from_model(model, ctx_params);

    // Use the context for text generation...

    // Clean up in reverse order of creation
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

#### Important Notes

1. Always call `llama_free()` for every context created with `llama_init_from_model()` to avoid memory leaks.
2. It's safe to pass `NULL` to `llama_free()`, though this has no effect.
3. After calling `llama_free()`, the context pointer should not be used again.
4. Free resources in the reverse order of creation: first contexts, then models, then the backend.
5. If you have attached a threadpool to the context with `llama_attach_threadpool()`, it will be automatically detached when the context is freed.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_init_from_model`](#llama_init_from_model) | Initialize a context from a model | Creates contexts that should be freed with this function |
| [`llama_model_free`](#llama_model_free) | Free a model | Should be called after freeing all contexts that use the model |

### llama_n_ctx

#### Function Signature

```c
LLAMA_API uint32_t llama_n_ctx(const struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `const struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns the context size (maximum number of tokens that can be kept in the KV cache) for the given context. This is the value that was specified in the `llama_context_params` when the context was created, or the model's default if none was specified.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.n_ctx = 2048;  // Set context size to 2048 tokens
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Get and print the context size
    uint32_t ctx_size = llama_n_ctx(ctx);
    printf("Context size: %u tokens\n", ctx_size);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from server.cpp:**

```c
// Function to check if a new conversation would fit in the context
bool would_exceed_context(llama_context * ctx, int new_tokens) {
    // Get the current context size
    uint32_t max_ctx = llama_n_ctx(ctx);

    // Get the current number of tokens in the KV cache
    llama_kv_cache * kv_cache = llama_get_kv_self(ctx);
    llama_pos max_pos = llama_kv_self_seq_pos_max(ctx, 0);

    // Check if adding new tokens would exceed the context size
    return (max_pos + new_tokens) > max_ctx;
}
```

#### Important Notes

1. The context size determines how many tokens can be processed in a single forward pass and affects memory usage.
2. If you need to process longer sequences, you'll need to create a context with a larger `n_ctx` value.
3. Increasing the context size significantly increases memory usage, especially for larger models.
4. The maximum effective context size is limited by the model's training context length, even if you set a larger value.
5. Some models may support context extension techniques like RoPE scaling, which can be configured in the context parameters.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides the default context size |
| [`llama_init_from_model`](#llama_init_from_model) | Initialize a context from a model | Creates a context with the specified context size |
| [`llama_kv_self_seq_pos_max`](#llama_kv_self_seq_pos_max) | Get the largest position in the KV cache | Can be used to determine how much of the context is currently used |

### llama_n_batch

#### Function Signature

```c
LLAMA_API uint32_t llama_n_batch(const struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `const struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns the logical maximum batch size for the given context. This is the maximum number of tokens that can be submitted to `llama_decode()` in a single batch, as specified in the `n_batch` parameter when the context was created.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.n_batch = 512;  // Set batch size to 512 tokens
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Get and print the batch size
    uint32_t batch_size = llama_n_batch(ctx);
    printf("Batch size: %u tokens\n", batch_size);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for batch processing:**

```c
// Function to process a batch of tokens efficiently
void process_batch(llama_context * ctx, const llama_token * tokens, size_t n_tokens) {
    // Get the maximum batch size
    uint32_t batch_size = llama_n_batch(ctx);

    // Process tokens in batches
    for (size_t i = 0; i < n_tokens; i += batch_size) {
        size_t n_process = (i + batch_size <= n_tokens) ? batch_size : (n_tokens - i);

        // Create a batch for this chunk of tokens
        llama_batch batch = llama_batch_get_one(&tokens[i], n_process);

        // Process the batch
        if (llama_decode(ctx, batch) != 0) {
            fprintf(stderr, "Failed to decode batch\n");
            break;
        }
    }
}
```

#### Important Notes

1. The batch size affects both performance and memory usage:
   - Larger batch sizes can improve performance by processing more tokens in parallel
   - However, larger batch sizes also require more memory
2. The default batch size (from `llama_context_default_params()`) is 2048 tokens.
3. The batch size is a logical limit - the actual number of tokens processed at once may be limited by the micro batch size (`n_ubatch`).
4. When creating batches for `llama_decode()`, ensure they don't exceed the batch size returned by this function.
5. If both `n_batch` and `n_ubatch` are set to zero when creating the context, an error will occur.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_n_ubatch`](#llama_n_ubatch) | Get the micro batch size | Complementary function that returns the physical batch size |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides the default batch size |
| [`llama_batch_get_one`](#llama_batch_get_one) | Get a batch for a single sequence of tokens | Creates batches that should respect the batch size limit |
| [`llama_decode`](#llama_decode) | Process a batch of tokens | The function that uses the batch size limit |

### llama_n_ubatch

#### Function Signature

```c
LLAMA_API uint32_t llama_n_ubatch(const struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `const struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns the physical maximum micro batch size for the given context. This is the maximum number of tokens that will be processed at once internally during decoding, as specified in the `n_ubatch` parameter when the context was created.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.n_batch = 1024;   // Logical batch size
    params.n_ubatch = 256;   // Physical micro batch size
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Get and print the micro batch size
    uint32_t ubatch_size = llama_n_ubatch(ctx);
    printf("Micro batch size: %u tokens\n", ubatch_size);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for optimizing batch processing:**

```c
// Function to optimize batch processing based on hardware capabilities
void optimize_batch_settings(llama_context * ctx) {
    // Get current batch settings
    uint32_t batch_size = llama_n_batch(ctx);
    uint32_t ubatch_size = llama_n_ubatch(ctx);

    printf("Current settings:\n");
    printf("  Logical batch size: %u tokens\n", batch_size);
    printf("  Physical micro batch size: %u tokens\n", ubatch_size);

    // Analyze if the settings are optimal for the hardware
    // (This would depend on specific hardware characteristics)
    if (ubatch_size < 128 && available_memory_gb > 16) {
        printf("Consider increasing micro batch size for better performance\n");
    } else if (ubatch_size > 512 && available_memory_gb < 8) {
        printf("Consider decreasing micro batch size to reduce memory usage\n");
    }
}
```

#### Important Notes

1. The micro batch size (`n_ubatch`) determines how many tokens are actually processed at once internally:
   - Smaller values use less memory but may be slower
   - Larger values can improve performance but require more memory
2. The default micro batch size (from `llama_context_default_params()`) is 512 tokens.
3. The micro batch size must be less than or equal to the logical batch size (`n_batch`).
4. If the micro batch size is smaller than the logical batch size, the decoding will be done in multiple internal steps.
5. For optimal performance, the micro batch size should be tuned based on the available hardware resources.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_n_batch`](#llama_n_batch) | Get the logical batch size | Complementary function that returns the logical batch size |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides the default micro batch size |
| [`llama_decode`](#llama_decode) | Process a batch of tokens | The function that uses the micro batch size internally |

### llama_n_seq_max

#### Function Signature

```c
LLAMA_API uint32_t llama_n_seq_max(const struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `const struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns the maximum number of sequences that can be managed in the given context. This is the value that was specified in the `n_seq_max` parameter when the context was created, which determines how many distinct conversation sequences can be tracked in the KV cache.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.n_seq_max = 4;  // Support up to 4 concurrent sequences
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Get and print the maximum number of sequences
    uint32_t max_sequences = llama_n_seq_max(ctx);
    printf("Maximum sequences: %u\n", max_sequences);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for multi-user chat application:**

```c
// Function to check if a new user can be added to the chat
bool can_add_user(llama_context * ctx, int current_users) {
    // Get the maximum number of sequences
    uint32_t max_sequences = llama_n_seq_max(ctx);

    // Check if adding a new user would exceed the limit
    if (current_users + 1 > max_sequences) {
        printf("Cannot add more users: maximum of %u concurrent conversations supported\n", max_sequences);
        return false;
    }

    return true;
}
```

#### Important Notes

1. The maximum number of sequences determines how many separate conversation threads can be managed in the KV cache.
2. The default value (from `llama_context_default_params()`) is 1, which is suitable for single-conversation applications.
3. For multi-user applications or applications that need to manage multiple conversation contexts, this value should be increased.
4. Each sequence requires additional memory for tracking in the KV cache, so increasing this value will increase memory usage.
5. When using multiple sequences, each token in a batch must be assigned to a specific sequence ID using the `seq_id` field in the `llama_batch` structure.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides the default maximum number of sequences |
| [`llama_kv_self_seq_rm`](#llama_kv_self_seq_rm) | Remove tokens from a sequence | Used to manage sequences in the KV cache |
| [`llama_kv_self_seq_cp`](#llama_kv_self_seq_cp) | Copy tokens between sequences | Used to duplicate or fork sequences |

### llama_get_model

#### Function Signature

```c
LLAMA_API const struct llama_model * llama_get_model(const struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `const struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns a pointer to the model associated with the given context. This function allows access to the model that was used to create the context, which can be useful for retrieving model-specific information such as vocabulary, embedding size, or other model parameters.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Get the model from the context
    const struct llama_model * model_from_ctx = llama_get_model(ctx);

    // Use the model to get information
    int n_embd = llama_model_n_embd(model_from_ctx);
    printf("Model embedding size: %d\n", n_embd);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for accessing vocabulary:**

```c
// Function to print information about a token using the model's vocabulary
void print_token_info(llama_context * ctx, llama_token token) {
    // Get the model from the context
    const struct llama_model * model = llama_get_model(ctx);

    // Get the vocabulary from the model
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    // Get and print token information
    const char * token_text = llama_vocab_get_text(vocab, token);
    float token_score = llama_vocab_get_score(vocab, token);

    printf("Token %d: text='%s', score=%f\n", token, token_text, token_score);
}
```

#### Important Notes

1. The returned model pointer is const, indicating that you should not modify the model through this pointer.
2. The model remains owned by the original code that loaded it - do not free the model obtained through this function.
3. The model pointer remains valid as long as the original model is not freed.
4. This function is useful for accessing model-specific information without having to keep track of the original model pointer.
5. Common uses include accessing the model's vocabulary, embedding size, and other architectural parameters.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_init_from_model`](#llama_init_from_model) | Initialize a context from a model | Creates the context from which the model can be retrieved |
| [`llama_model_get_vocab`](#llama_model_get_vocab) | Get the vocabulary from a model | Often used with the model retrieved by this function |
| [`llama_model_n_embd`](#llama_model_n_embd) | Get the embedding size of a model | Often used with the model retrieved by this function |

### llama_get_kv_self

#### Function Signature

```c
LLAMA_API struct llama_kv_cache * llama_get_kv_self(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns a pointer to the KV (Key-Value) cache associated with the given context. The KV cache stores the attention keys and values for previously processed tokens, which is essential for efficient autoregressive generation and managing conversation state.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Get the KV cache
    struct llama_kv_cache * kv_cache = llama_get_kv_self(ctx);

    // Use the KV cache (e.g., to check its state)
    llama_pos max_pos = llama_kv_self_seq_pos_max(ctx, 0);
    printf("Current maximum position in KV cache: %d\n", max_pos);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for managing KV cache:**

```c
// Function to check and optionally clear the KV cache if it's getting full
void manage_kv_cache(llama_context * ctx, float threshold) {
    // Get the KV cache
    struct llama_kv_cache * kv_cache = llama_get_kv_self(ctx);

    // Get the context size and current position
    uint32_t ctx_size = llama_n_ctx(ctx);
    llama_pos max_pos = llama_kv_self_seq_pos_max(ctx, 0);

    // Calculate how full the cache is
    float fill_ratio = (float)max_pos / ctx_size;

    // If the cache is getting full, clear it
    if (fill_ratio > threshold) {
        printf("KV cache is %0.1f%% full, clearing...\n", fill_ratio * 100);
        llama_kv_self_clear(ctx);
    }
}
```

#### Important Notes

1. The KV cache is a critical component for efficient text generation, as it avoids recomputing attention for tokens that have already been processed.
2. The size of the KV cache is determined by the context size (`n_ctx`) specified when creating the context.
3. The KV cache consumes a significant amount of memory, especially for large context sizes and models.
4. For long-running applications, you may need to manage the KV cache by:
   - Clearing it with `llama_kv_self_clear()`
   - Removing specific tokens with `llama_kv_self_seq_rm()`
   - Defragmenting it with `llama_kv_self_defrag()`
5. When using multiple sequences, each sequence has its own section in the KV cache.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_kv_self_clear`](#llama_kv_self_clear) | Clear the KV cache | Operates on the KV cache returned by this function |
| [`llama_kv_self_seq_rm`](#llama_kv_self_seq_rm) | Remove tokens from the KV cache | Operates on the KV cache returned by this function |
| [`llama_kv_self_seq_pos_max`](#llama_kv_self_seq_pos_max) | Get the largest position in the KV cache | Provides information about the KV cache |

### llama_pooling_type

#### Function Signature

```c
LLAMA_API enum llama_pooling_type llama_pooling_type(const struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `const struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns the pooling type used by the given context. The pooling type determines how token embeddings are combined when generating sequence-level embeddings, which is particularly important for tasks like text classification, semantic search, and other applications that require fixed-size vector representations of text.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    // Create context with specific pooling type
    struct llama_context_params params = llama_context_default_params();
    params.pooling_type = LLAMA_POOLING_TYPE_MEAN;  // Use mean pooling
    params.embeddings = true;  // Enable embeddings
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Get and print the pooling type
    enum llama_pooling_type pool_type = llama_pooling_type(ctx);

    printf("Pooling type: ");
    switch (pool_type) {
        case LLAMA_POOLING_TYPE_NONE:
            printf("None\n");
            break;
        case LLAMA_POOLING_TYPE_MEAN:
            printf("Mean\n");
            break;
        case LLAMA_POOLING_TYPE_CLS:
            printf("CLS token\n");
            break;
        case LLAMA_POOLING_TYPE_LAST:
            printf("Last token\n");
            break;
        default:
            printf("Unknown (%d)\n", pool_type);
    }

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for embedding generation:**

```c
// Function to generate and use embeddings based on pooling type
void generate_embeddings(llama_context * ctx, const char * text) {
    // Tokenize the input text
    llama_token tokens[256];
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    int n_tokens = llama_tokenize(vocab, text, strlen(text), tokens, 256, true, false);

    // Create a batch with the tokens
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);

    // Process the batch
    llama_decode(ctx, batch);

    // Get the pooling type
    enum llama_pooling_type pool_type = llama_pooling_type(ctx);

    // Get embeddings based on pooling type
    float * embeddings = NULL;
    if (pool_type == LLAMA_POOLING_TYPE_NONE) {
        // No pooling - get embeddings for each token
        embeddings = llama_get_embeddings(ctx);
        printf("Generated individual token embeddings\n");
    } else {
        // Pooled embeddings - get sequence embedding
        embeddings = llama_get_embeddings_seq(ctx, 0);
        printf("Generated pooled sequence embedding\n");
    }

    // Use the embeddings...

    // Free the batch
    llama_batch_free(batch);
}
```

#### Important Notes

1. The pooling type affects how sequence-level embeddings are generated:
   - `LLAMA_POOLING_TYPE_NONE` (0): No pooling, each token has its own embedding
   - `LLAMA_POOLING_TYPE_MEAN` (1): Average the embeddings of all tokens in the sequence
   - `LLAMA_POOLING_TYPE_CLS` (2): Use the embedding of the classification (CLS) token
   - `LLAMA_POOLING_TYPE_LAST` (3): Use the embedding of the last token in the sequence
   - `LLAMA_POOLING_TYPE_RANK` (4): Used by reranking models to attach the classification head
2. The default pooling type (from `llama_context_default_params()`) is `LLAMA_POOLING_TYPE_UNSPECIFIED` (-1), which means the model's default will be used.
3. Pooling is only relevant when generating embeddings, so the `embeddings` parameter should be set to `true` in the context parameters.
4. Different pooling types may be better suited for different tasks:
   - Mean pooling often works well for semantic similarity tasks
   - CLS token pooling is common in BERT-style models
   - Last token pooling can be effective for autoregressive models
5. The choice of pooling type can significantly affect the quality of embeddings for downstream tasks.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_set_embeddings`](#llama_set_embeddings) | Set whether to return embeddings | Controls whether embeddings are generated |
| [`llama_get_embeddings`](#llama_get_embeddings) | Get all output token embeddings | Returns token embeddings when pooling is NONE |
| [`llama_get_embeddings_seq`](#llama_get_embeddings_seq) | Get embeddings for a sequence | Returns sequence embeddings based on the pooling type |

### llama_set_n_threads

#### Function Signature

```c
LLAMA_API void llama_set_n_threads(struct llama_context * ctx, int32_t n_threads, int32_t n_threads_batch);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `n_threads` | `int32_t` | Number of threads to use for generation (single token) | Positive integer, typically 1-16 depending on CPU cores |
| `n_threads_batch` | `int32_t` | Number of threads to use for batch processing (multiple tokens) | Positive integer, typically 1-16 depending on CPU cores |

#### Description

Sets the number of CPU threads used for computation in the given context. This function allows dynamic adjustment of thread count for both single-token generation and batch processing, which can help optimize performance based on the current workload and available system resources.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Set thread counts
    llama_set_n_threads(ctx, 4, 8);  // 4 threads for generation, 8 for batch processing

    // Verify the thread counts
    printf("Threads for generation: %d\n", llama_n_threads(ctx));
    printf("Threads for batch processing: %d\n", llama_n_threads_batch(ctx));

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for dynamic thread adjustment:**

```c
// Function to adjust thread count based on workload
void optimize_threads(llama_context * ctx, size_t batch_size) {
    int32_t cpu_cores = get_available_cpu_cores();  // Hypothetical function

    if (batch_size > 32) {
        // For large batches, use more threads for batch processing
        llama_set_n_threads(ctx, cpu_cores / 4, cpu_cores);
        printf("Large batch: optimized for batch processing with %d threads\n", cpu_cores);
    } else {
        // For small batches or single token generation, use more threads for generation
        llama_set_n_threads(ctx, cpu_cores, cpu_cores / 2);
        printf("Small batch: optimized for generation with %d threads\n", cpu_cores);
    }
}
```

#### Important Notes

1. The number of threads directly affects performance:
   - More threads generally improve performance up to a point
   - Too many threads can lead to diminishing returns or even decreased performance due to overhead
2. The optimal thread count depends on:
   - The number of available CPU cores
   - The model size and complexity
   - The batch size being processed
   - Other system load
3. `n_threads` is used for generating a single token (typically in the decoding phase)
4. `n_threads_batch` is used for processing multiple tokens at once (typically in the prompt processing phase)
5. The default thread count (from `llama_context_default_params()`) is based on the system's available cores.
6. Thread count can be adjusted dynamically during runtime to optimize for different phases of processing.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_n_threads`](#llama_n_threads) | Get the number of threads for generation | Returns the value set by this function |
| [`llama_n_threads_batch`](#llama_n_threads_batch) | Get the number of threads for batch processing | Returns the value set by this function |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides the default thread counts |

### llama_n_threads

#### Function Signature

```c
LLAMA_API int32_t llama_n_threads(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns the number of threads currently being used for single-token generation in the given context. This is the value that was either specified in the `n_threads` parameter when the context was created or set later using `llama_set_n_threads()`.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context with specific thread count
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.n_threads = 4;  // Set to use 4 threads for generation
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Get and print the thread count
    int32_t threads = llama_n_threads(ctx);
    printf("Using %d threads for generation\n", threads);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for performance monitoring:**

```c
// Function to log performance metrics
void log_performance_metrics(llama_context * ctx, double generation_time_ms) {
    // Get the thread count
    int32_t threads = llama_n_threads(ctx);

    // Calculate tokens per second
    double tokens_per_second = 1000.0 / generation_time_ms;

    // Log the metrics
    printf("Performance metrics:\n");
    printf("  Threads: %d\n", threads);
    printf("  Generation time: %.2f ms\n", generation_time_ms);
    printf("  Tokens per second: %.2f\n", tokens_per_second);

    // Suggest optimization if performance is low
    if (tokens_per_second < 5.0 && threads < get_available_cpu_cores()) {
        printf("  Suggestion: Consider increasing thread count for better performance\n");
    }
}
```

#### Important Notes

1. The thread count returned by this function affects single-token generation performance.
2. This value may be different from the batch processing thread count (`n_threads_batch`).
3. The default value (from `llama_context_default_params()`) is typically based on the system's available CPU cores.
4. Increasing the thread count generally improves performance up to a point, after which diminishing returns or even decreased performance may occur.
5. For optimal performance, this value should be tuned based on:
   - The specific model being used
   - The hardware capabilities
   - The current system load
   - The desired balance between speed and resource usage

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_set_n_threads`](#llama_set_n_threads) | Set the number of threads | Sets the value returned by this function |
| [`llama_n_threads_batch`](#llama_n_threads_batch) | Get the number of threads for batch processing | Complementary function for batch processing threads |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides the default thread count |

### llama_n_threads_batch

#### Function Signature

```c
LLAMA_API int32_t llama_n_threads_batch(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns the number of threads currently being used for batch processing (multiple tokens) in the given context. This is the value that was either specified in the `n_threads_batch` parameter when the context was created or set later using `llama_set_n_threads()`.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context with specific thread counts
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.n_threads = 4;        // For single token generation
    params.n_threads_batch = 8;  // For batch processing
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Get and print the batch thread count
    int32_t batch_threads = llama_n_threads_batch(ctx);
    printf("Using %d threads for batch processing\n", batch_threads);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for prompt processing optimization:**

```c
// Function to process a prompt efficiently
void process_prompt(llama_context * ctx, const char * prompt) {
    // Get the current batch thread count
    int32_t batch_threads = llama_n_threads_batch(ctx);
    printf("Processing prompt with %d threads\n", batch_threads);

    // Tokenize the prompt
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    llama_token tokens[1024];
    int n_tokens = llama_tokenize(vocab, prompt, strlen(prompt), tokens, 1024, true, false);

    // Create and process the batch
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);

    // Process the batch (this will use n_threads_batch internally)
    llama_decode(ctx, batch);

    // Free the batch
    llama_batch_free(batch);
}
```

#### Important Notes

1. The batch thread count affects performance when processing multiple tokens at once, such as during prompt processing.
2. This value may be different from the single-token generation thread count (`n_threads`).
3. The default value (from `llama_context_default_params()`) is typically based on the system's available CPU cores.
4. For prompt processing, which is often more parallelizable than token generation, a higher thread count can be beneficial.
5. Considerations for setting the optimal batch thread count include:
   - The length of prompts being processed
   - The available CPU cores and memory
   - The desired balance between prompt processing speed and resource usage
   - The need to reserve resources for other system tasks

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_set_n_threads`](#llama_set_n_threads) | Set the number of threads | Sets the value returned by this function |
| [`llama_n_threads`](#llama_n_threads) | Get the number of threads for generation | Complementary function for generation threads |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Provides the default batch thread count |
| [`llama_decode`](#llama_decode) | Process a batch of tokens | Uses the batch thread count internally |

### llama_set_embeddings

#### Function Signature

```c
LLAMA_API void llama_set_embeddings(struct llama_context * ctx, bool embeddings);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `embeddings` | `bool` | Whether to return embeddings | `true` to enable embeddings, `false` to disable |

#### Description

Sets whether the context should return embeddings (vector representations of tokens) during decoding. When enabled, embeddings can be retrieved using functions like `llama_get_embeddings()`, which is useful for tasks such as semantic search, clustering, and other applications that require vector representations of text.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Enable embeddings
    llama_set_embeddings(ctx, true);

    // Tokenize and process some text
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Hello, world!", 13, tokens, 256, true, false);

    // Create and process a batch
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    llama_decode(ctx, batch);

    // Get the embeddings
    float * embeddings = llama_get_embeddings(ctx);
    if (embeddings) {
        printf("Successfully retrieved embeddings\n");
        // Use the embeddings...
    } else {
        printf("Failed to retrieve embeddings\n");
    }

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for semantic search:**

```c
// Function to compute similarity between two texts using embeddings
float compute_similarity(llama_context * ctx, const char * text1, const char * text2) {
    // Enable embeddings
    llama_set_embeddings(ctx, true);

    // Set pooling type to mean for sequence-level embeddings
    struct llama_context_params params;
    params.pooling_type = LLAMA_POOLING_TYPE_MEAN;

    // Get embeddings for first text
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    // Process first text
    llama_token tokens1[256];
    int n_tokens1 = llama_tokenize(vocab, text1, strlen(text1), tokens1, 256, true, false);
    llama_batch batch1 = llama_batch_get_one(tokens1, n_tokens1);
    llama_decode(ctx, batch1);
    float * embd1 = llama_get_embeddings_seq(ctx, 0);

    // Process second text
    llama_token tokens2[256];
    int n_tokens2 = llama_tokenize(vocab, text2, strlen(text2), tokens2, 256, true, false);
    llama_batch batch2 = llama_batch_get_one(tokens2, n_tokens2);
    llama_decode(ctx, batch2);
    float * embd2 = llama_get_embeddings_seq(ctx, 0);

    // Compute cosine similarity
    int n_embd = llama_model_n_embd(model);
    float dot_product = 0.0f;
    float norm1 = 0.0f;
    float norm2 = 0.0f;

    for (int i = 0; i < n_embd; i++) {
        dot_product += embd1[i] * embd2[i];
        norm1 += embd1[i] * embd1[i];
        norm2 += embd2[i] * embd2[i];
    }

    float similarity = dot_product / (sqrt(norm1) * sqrt(norm2));

    // Clean up
    llama_batch_free(batch1);
    llama_batch_free(batch2);

    return similarity;
}
```

#### Important Notes

1. By default, embeddings are disabled (`false`) to save memory and computation.
2. When embeddings are enabled, the context will compute and store embeddings for tokens processed by `llama_decode()`.
3. Enabling embeddings increases memory usage and may slightly reduce performance.
4. Embeddings can be retrieved at different levels:
   - Token-level embeddings with `llama_get_embeddings()` or `llama_get_embeddings_ith()`
   - Sequence-level embeddings with `llama_get_embeddings_seq()`
5. The dimensionality of embeddings is determined by the model's embedding size (`llama_model_n_embd()`).
6. For sequence-level embeddings, the pooling type (`llama_pooling_type()`) determines how token embeddings are combined.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_get_embeddings`](#llama_get_embeddings) | Get all output token embeddings | Returns embeddings when enabled by this function |
| [`llama_get_embeddings_ith`](#llama_get_embeddings_ith) | Get embeddings for a specific token | Returns embeddings when enabled by this function |
| [`llama_get_embeddings_seq`](#llama_get_embeddings_seq) | Get embeddings for a sequence | Returns sequence embeddings when enabled by this function |
| [`llama_pooling_type`](#llama_pooling_type) | Get the pooling type | Affects how sequence embeddings are generated |

### llama_set_causal_attn

#### Function Signature

```c
LLAMA_API void llama_set_causal_attn(struct llama_context * ctx, bool causal_attn);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `causal_attn` | `bool` | Whether to use causal attention | `true` for causal attention, `false` for bidirectional attention |

#### Description

Sets whether the context should use causal attention during decoding. Causal attention means that tokens can only attend to previous tokens (and themselves), which is the standard behavior for autoregressive language models. Disabling causal attention allows bidirectional attention, where tokens can attend to both previous and future tokens, which can be useful for certain tasks like text classification or feature extraction.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Disable causal attention for bidirectional processing
    llama_set_causal_attn(ctx, false);

    // Process text with bidirectional attention
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Analyze this sentence for sentiment.", 34, tokens, 256, true, false);

    // Create and process a batch
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    llama_decode(ctx, batch);

    // Get the logits or embeddings for analysis
    // ...

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for text classification:**

```c
// Function to classify text using bidirectional attention
void classify_text(llama_context * ctx, const char * text) {
    // Set bidirectional attention for better classification
    llama_set_causal_attn(ctx, false);

    // Enable embeddings to get the final representation
    llama_set_embeddings(ctx, true);

    // Set pooling type to mean for sequence-level embeddings
    struct llama_context_params params;
    params.pooling_type = LLAMA_POOLING_TYPE_MEAN;

    // Tokenize the text
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    llama_token tokens[512];
    int n_tokens = llama_tokenize(vocab, text, strlen(text), tokens, 512, true, false);

    // Create and process a batch
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    llama_decode(ctx, batch);

    // Get the sequence-level embedding
    float * embedding = llama_get_embeddings_seq(ctx, 0);

    // Use the embedding for classification
    // (e.g., pass it to a classifier or compare with class prototypes)
    printf("Generated text embedding with bidirectional attention\n");

    // Clean up
    llama_batch_free(batch);
}
```

#### Important Notes

1. By default, causal attention is enabled (`true`) for autoregressive language models.
2. Causal attention is essential for proper text generation, as it prevents the model from "seeing the future."
3. Disabling causal attention (setting to `false`) allows bidirectional attention, which can be useful for:
   - Text classification
   - Sentiment analysis
   - Feature extraction
   - Other tasks where the entire input is available at once
4. Changing the attention type may significantly affect the model's behavior and output quality.
5. Some models may not be trained for bidirectional attention and might produce unexpected results when causal attention is disabled.
6. This setting affects all subsequent calls to `llama_decode()` until changed again.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_decode`](#llama_decode) | Process a batch of tokens | Uses the attention type set by this function |
| [`llama_set_embeddings`](#llama_set_embeddings) | Set whether to return embeddings | Often used together with this function for feature extraction |
| [`llama_get_embeddings_seq`](#llama_get_embeddings_seq) | Get embeddings for a sequence | Often used with bidirectional attention for better sequence representations |

### llama_set_warmup

#### Function Signature

```c
LLAMA_API void llama_set_warmup(struct llama_context * ctx, bool warmup);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `warmup` | `bool` | Whether the model is in warmup mode | `true` to enable warmup mode, `false` to disable |

#### Description

Sets whether the model is in warmup mode. When warmup mode is enabled, all model tensors are activated during `llama_decode()` to load and cache their weights. This is useful for ensuring that all model weights are loaded into memory or GPU before actual inference, which can help avoid stuttering or delays during the first real inference pass.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Enable warmup mode
    llama_set_warmup(ctx, true);

    // Create a small batch for warmup
    llama_token dummy_tokens[] = {0, 1, 2, 3};  // Dummy tokens for warmup
    llama_batch batch = llama_batch_get_one(dummy_tokens, 4);

    // Process the batch in warmup mode
    printf("Warming up model...\n");
    llama_decode(ctx, batch);

    // Disable warmup mode for actual inference
    llama_set_warmup(ctx, false);
    printf("Warmup complete, ready for inference\n");

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for server initialization:**

```c
// Function to initialize a model for serving
void initialize_model_for_serving(llama_context * ctx) {
    printf("Initializing model for serving...\n");

    // Enable warmup mode
    llama_set_warmup(ctx, true);

    // Create a batch with a few tokens for warmup
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    // Use a short, generic prompt for warmup
    llama_token tokens[16];
    int n_tokens = llama_tokenize(vocab, "Hello, how are you?", 18, tokens, 16, true, false);

    // Create and process a batch
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);

    // Process the batch in warmup mode
    int64_t start_time = llama_time_us();
    llama_decode(ctx, batch);
    int64_t end_time = llama_time_us();

    // Disable warmup mode
    llama_set_warmup(ctx, false);

    printf("Model warmup complete in %.2f ms\n", (end_time - start_time) / 1000.0);
    printf("Model is now ready for serving\n");

    // Clean up
    llama_batch_free(batch);
}
```

#### Important Notes

1. Warmup mode is primarily useful for:
   - Server initialization to ensure smooth first requests
   - Applications where consistent latency is important
   - Systems with GPU acceleration to preload weights into GPU memory
2. During warmup, all model tensors are activated, which may temporarily increase memory usage.
3. The warmup process only needs to be done once after loading the model.
4. A typical warmup process involves:
   - Enabling warmup mode with `llama_set_warmup(ctx, true)`
   - Processing a small batch of tokens with `llama_decode()`
   - Disabling warmup mode with `llama_set_warmup(ctx, false)`
5. The tokens used for warmup don't need to be meaningful - they're just used to activate all parts of the model.
6. Warmup is especially important for models with GPU offloading, as it ensures all necessary tensors are loaded into GPU memory.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_decode`](#llama_decode) | Process a batch of tokens | Activates all model tensors when in warmup mode |
| [`llama_batch_get_one`](#llama_batch_get_one) | Get a batch for a single sequence of tokens | Useful for creating simple batches for warmup |
| [`llama_time_us`](#llama_time_us) | Get current time in microseconds | Useful for measuring warmup duration |

### llama_set_abort_callback

#### Function Signature

```c
LLAMA_API void llama_set_abort_callback(struct llama_context * ctx, ggml_abort_callback abort_callback, void * abort_callback_data);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `abort_callback` | `ggml_abort_callback` | Callback function that determines whether to abort execution | A function pointer that returns `true` to abort, `false` to continue |
| `abort_callback_data` | `void *` | User data passed to the abort callback | Any pointer that the callback can use |

#### Description

Sets a callback function that will be called during the execution of `llama_decode()` to determine whether the operation should be aborted. This is useful for implementing timeout mechanisms, user interruption handling, or other forms of execution control. If the callback returns `true`, the execution of `llama_decode()` will be aborted.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>
#include <time.h>

// Abort callback function that implements a timeout
bool timeout_callback(void * user_data) {
    time_t * start_time = (time_t *)user_data;
    time_t current_time = time(NULL);

    // Abort if more than 5 seconds have passed
    if (difftime(current_time, *start_time) > 5.0) {
        printf("Execution timed out after 5 seconds\n");
        return true;  // Abort execution
    }

    return false;  // Continue execution
}

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Set up timeout
    time_t start_time = time(NULL);
    llama_set_abort_callback(ctx, timeout_callback, &start_time);

    // Process a large batch that might take a long time
    // ...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for user interruption:**

```c
// Global flag for user interruption
volatile bool user_interrupt = false;

// Signal handler for Ctrl+C
void handle_sigint(int sig) {
    user_interrupt = true;
}

// Abort callback that checks for user interruption
bool interrupt_callback(void * user_data) {
    return user_interrupt;  // Abort if user has interrupted
}

// Function to run inference with interruption support
void run_inference_with_interrupt(llama_context * ctx, const char * prompt) {
    // Set up signal handler
    signal(SIGINT, handle_sigint);
    user_interrupt = false;

    // Set abort callback
    llama_set_abort_callback(ctx, interrupt_callback, NULL);

    // Tokenize the prompt
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    llama_token tokens[1024];
    int n_tokens = llama_tokenize(vocab, prompt, strlen(prompt), tokens, 1024, true, false);

    // Create batch
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);

    // Process batch (can be aborted by user with Ctrl+C)
    printf("Processing prompt (press Ctrl+C to abort)...\n");
    int result = llama_decode(ctx, batch);

    if (result == 2) {
        printf("Inference aborted by user\n");
    } else if (result != 0) {
        printf("Inference failed with error code %d\n", result);
    } else {
        printf("Inference completed successfully\n");
    }

    // Clean up
    llama_batch_free(batch);
}
```

#### Important Notes

1. The abort callback is primarily useful for:
   - Implementing timeouts to prevent excessive computation
   - Handling user interruptions (e.g., Ctrl+C)
   - Resource management in multi-user environments
   - Preventing runaway processes
2. The callback should be lightweight and fast, as it may be called frequently during decoding.
3. Currently, the abort callback works only with CPU execution, not with GPU acceleration.
4. If the callback returns `true`, `llama_decode()` will return with a result code of 2.
5. The callback data pointer (`abort_callback_data`) can be used to pass any information needed by the callback function.
6. Setting `abort_callback` to `NULL` disables the abort mechanism.
7. The abort callback is checked periodically during computation, not after every single operation.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_decode`](#llama_decode) | Process a batch of tokens | The function that can be aborted by the callback |
| [`llama_synchronize`](#llama_synchronize) | Wait until all computations are finished | Can be used after an abort to ensure all operations have stopped |

### llama_synchronize

#### Function Signature

```c
LLAMA_API void llama_synchronize(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Waits until all computations in the given context are finished. This function is useful when working with asynchronous operations or when you need to ensure that all pending computations have completed before accessing their results. It's automatically called by functions that obtain computation results, so it's not necessary to call it explicitly in most cases.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Process a batch of tokens
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Hello, world!", 13, tokens, 256, true, false);

    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    llama_decode(ctx, batch);

    // Ensure all computations are finished
    llama_synchronize(ctx);

    // Now it's safe to access the results
    float * logits = llama_get_logits(ctx);

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for custom processing:**

```c
// Function to perform custom processing on model outputs
void process_model_outputs(llama_context * ctx) {
    // Start a computation
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Process this text", 16, tokens, 256, true, false);

    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    llama_decode(ctx, batch);

    // Do some other work while computation is running
    printf("Doing other work while model is computing...\n");

    // Now ensure computation is finished before accessing results
    printf("Waiting for computation to finish...\n");
    llama_synchronize(ctx);

    // Access the results
    printf("Computation finished, accessing results\n");
    float * logits = llama_get_logits(ctx);

    // Process the logits
    // ...

    // Clean up
    llama_batch_free(batch);
}
```

#### Important Notes

1. This function is primarily useful when:
   - Working with asynchronous operations
   - Implementing custom processing of model outputs
   - Measuring precise timing of model computations
   - Ensuring resource availability before starting new computations
2. In most cases, you don't need to call `llama_synchronize()` explicitly, as it's automatically called by functions that access computation results, such as:
   - `llama_get_logits()`
   - `llama_get_embeddings()`
   - `llama_get_logits_ith()`
   - `llama_get_embeddings_ith()`
   - `llama_get_embeddings_seq()`
3. Calling this function will block until all pending computations are complete.
4. For GPU-accelerated computations, this ensures that all GPU operations have finished and their results are available in host memory.
5. This function has no effect if there are no pending computations.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_decode`](#llama_decode) | Process a batch of tokens | The main function that initiates computations |
| [`llama_get_logits`](#llama_get_logits) | Get token logits from the last decode | Automatically calls `llama_synchronize()` internally |
| [`llama_get_embeddings`](#llama_get_embeddings) | Get all output token embeddings | Automatically calls `llama_synchronize()` internally |

### llama_get_logits

#### Function Signature

```c
LLAMA_API float * llama_get_logits(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns a pointer to the logits (unnormalized probabilities) obtained from the last call to `llama_decode()`. The logits for which `llama_batch.logits[i] != 0` are stored contiguously in the order they appeared in the batch. This function is essential for accessing the model's raw predictions, which can then be used for token sampling, probability analysis, or other post-processing.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>
#include <math.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Process a batch of tokens
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Hello", 5, tokens, 256, true, false);

    // Create a batch with logits enabled for the last token
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    batch.logits[n_tokens - 1] = 1;  // Request logits for the last token

    // Process the batch
    llama_decode(ctx, batch);

    // Get the logits
    float * logits = llama_get_logits(ctx);

    // Get vocabulary size to know how many logits we have
    int n_vocab = llama_vocab_n_tokens(vocab);

    // Find the token with the highest probability
    int max_token = 0;
    float max_logit = logits[0];

    for (int i = 1; i < n_vocab; i++) {
        if (logits[i] > max_logit) {
            max_logit = logits[i];
            max_token = i;
        }
    }

    // Convert logits to probabilities using softmax
    float sum_exp = 0.0f;
    for (int i = 0; i < n_vocab; i++) {
        sum_exp += expf(logits[i]);
    }

    float probability = expf(max_logit) / sum_exp;

    // Print the most likely next token
    char token_text[32];
    llama_token_to_piece(vocab, max_token, token_text, sizeof(token_text), 0, true);
    printf("Most likely next token: '%s' (probability: %.2f%%)\n", token_text, probability * 100);

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for token sampling:**

```c
// Function to sample a token based on logits
llama_token sample_token(llama_context * ctx, float temperature) {
    // Get the model and vocabulary
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    int n_vocab = llama_vocab_n_tokens(vocab);

    // Get the logits
    float * logits = llama_get_logits(ctx);

    // Apply temperature
    if (temperature > 0.0f) {
        for (int i = 0; i < n_vocab; i++) {
            logits[i] /= temperature;
        }
    }

    // Convert logits to probabilities using softmax
    float sum_exp = 0.0f;
    float probs[n_vocab];

    for (int i = 0; i < n_vocab; i++) {
        probs[i] = expf(logits[i]);
        sum_exp += probs[i];
    }

    for (int i = 0; i < n_vocab; i++) {
        probs[i] /= sum_exp;
    }

    // Sample a token based on probabilities
    float r = (float)rand() / RAND_MAX;
    float cdf = 0.0f;

    for (int i = 0; i < n_vocab; i++) {
        cdf += probs[i];
        if (r < cdf) {
            return i;  // Return the sampled token ID
        }
    }

    // Fallback to the most probable token
    int max_token = 0;
    float max_prob = probs[0];

    for (int i = 1; i < n_vocab; i++) {
        if (probs[i] > max_prob) {
            max_prob = probs[i];
            max_token = i;
        }
    }

    return max_token;
}
```

#### Important Notes

1. This function returns `NULL` if no logits were requested in the last batch or if an error occurred.
2. The logits are stored in row-major order, with each row corresponding to a token for which logits were requested.
3. The number of rows equals the number of tokens for which `llama_batch.logits[i] != 0`.
4. Each row has `n_vocab` elements, where `n_vocab` is the vocabulary size of the model.
5. The logits are unnormalized log-probabilities - to convert to probabilities, you need to apply the softmax function.
6. The returned pointer is owned by the context and remains valid until the next call to `llama_decode()` or until the context is freed.
7. This function automatically calls `llama_synchronize()` to ensure all computations are finished.
8. For more advanced sampling, consider using the sampling API functions like `llama_sampler_init_*()` and `llama_sampler_sample()`.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_get_logits_ith`](#llama_get_logits_ith) | Get logits for a specific token | Alternative way to access logits for a specific position |
| [`llama_decode`](#llama_decode) | Process a batch of tokens | Generates the logits that can be accessed with this function |
| [`llama_synchronize`](#llama_synchronize) | Wait until all computations are finished | Automatically called by this function |
| [`llama_sampler_sample`](#llama_sampler_sample) | Sample a token based on logits | Higher-level alternative to manual sampling |

### llama_get_logits_ith

#### Function Signature

```c
LLAMA_API float * llama_get_logits_ith(struct llama_context * ctx, int32_t i);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `i` | `int32_t` | Index of the token for which to get logits | Positive index for forward access, negative for reverse access |

#### Description

Returns a pointer to the logits (unnormalized probabilities) for the i-th token from the last call to `llama_decode()`. For positive indices, this is equivalent to `llama_get_logits(ctx) + i * n_vocab`. Negative indices can be used to access logits in reverse order, with -1 referring to the last token's logits. This function provides a convenient way to access logits for a specific token position.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>
#include <math.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Process a batch of tokens
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Hello world", 11, tokens, 256, true, false);

    // Create a batch with logits enabled for multiple tokens
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    for (int i = 0; i < n_tokens; i++) {
        batch.logits[i] = 1;  // Request logits for all tokens
    }

    // Process the batch
    llama_decode(ctx, batch);

    // Get logits for the last token using negative indexing
    float * last_token_logits = llama_get_logits_ith(ctx, -1);

    // Get vocabulary size
    int n_vocab = llama_vocab_n_tokens(vocab);

    // Find the token with the highest probability
    int max_token = 0;
    float max_logit = last_token_logits[0];

    for (int i = 1; i < n_vocab; i++) {
        if (last_token_logits[i] > max_logit) {
            max_logit = last_token_logits[i];
            max_token = i;
        }
    }

    // Print the most likely next token
    char token_text[32];
    llama_token_to_piece(vocab, max_token, token_text, sizeof(token_text), 0, true);
    printf("Most likely next token after 'world': '%s'\n", token_text);

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for comparing token predictions:**

```c
// Function to compare predictions at different positions
void compare_predictions(llama_context * ctx, const char * text) {
    // Tokenize the input text
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, text, strlen(text), tokens, 256, true, false);

    // Create a batch with logits enabled for all tokens
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    for (int i = 0; i < n_tokens; i++) {
        batch.logits[i] = 1;
    }

    // Process the batch
    llama_decode(ctx, batch);

    // Compare predictions at different positions
    printf("Comparing predictions for text: '%s'\n", text);

    for (int pos = 0; pos < n_tokens; pos++) {
        // Get logits for this position
        float * pos_logits = llama_get_logits_ith(ctx, pos);

        // Find the top 3 most likely next tokens
        int top_tokens[3] = {0};
        float top_logits[3] = {pos_logits[0], -INFINITY, -INFINITY};

        int n_vocab = llama_vocab_n_tokens(vocab);
        for (int i = 1; i < n_vocab; i++) {
            if (pos_logits[i] > top_logits[0]) {
                top_logits[2] = top_logits[1];
                top_tokens[2] = top_tokens[1];
                top_logits[1] = top_logits[0];
                top_tokens[1] = top_tokens[0];
                top_logits[0] = pos_logits[i];
                top_tokens[0] = i;
            } else if (pos_logits[i] > top_logits[1]) {
                top_logits[2] = top_logits[1];
                top_tokens[2] = top_tokens[1];
                top_logits[1] = pos_logits[i];
                top_tokens[1] = i;
            } else if (pos_logits[i] > top_logits[2]) {
                top_logits[2] = pos_logits[i];
                top_tokens[2] = i;
            }
        }

        // Print the current token and top predictions
        char current_token[32];
        llama_token_to_piece(vocab, tokens[pos], current_token, sizeof(current_token), 0, true);

        printf("Position %d (token: '%s'):\n", pos, current_token);

        for (int j = 0; j < 3; j++) {
            char pred_token[32];
            llama_token_to_piece(vocab, top_tokens[j], pred_token, sizeof(pred_token), 0, true);

            // Convert logit to probability
            float sum_exp = 0.0f;
            for (int i = 0; i < n_vocab; i++) {
                sum_exp += expf(pos_logits[i]);
            }
            float probability = expf(top_logits[j]) / sum_exp;

            printf("  #%d: '%s' (%.2f%%)\n", j+1, pred_token, probability * 100);
        }
        printf("\n");
    }

    // Clean up
    llama_batch_free(batch);
}
```

#### Important Notes

1. This function returns `NULL` for invalid indices or if no logits were requested for the specified position.
2. Positive indices access logits in forward order (0 is the first token with logits).
3. Negative indices access logits in reverse order (-1 is the last token with logits).
4. The returned pointer points to `n_vocab` consecutive float values, where `n_vocab` is the vocabulary size.
5. The logits are unnormalized log-probabilities - to convert to probabilities, apply the softmax function.
6. The returned pointer is owned by the context and remains valid until the next call to `llama_decode()` or until the context is freed.
7. This function automatically calls `llama_synchronize()` to ensure all computations are finished.
8. For a token to have logits available, its corresponding `llama_batch.logits[i]` value must have been set to non-zero when calling `llama_decode()`.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_get_logits`](#llama_get_logits) | Get token logits from the last decode | Returns all logits, while this function returns logits for a specific position |
| [`llama_decode`](#llama_decode) | Process a batch of tokens | Generates the logits that can be accessed with this function |
| [`llama_synchronize`](#llama_synchronize) | Wait until all computations are finished | Automatically called by this function |
| [`llama_sampler_sample`](#llama_sampler_sample) | Sample a token based on logits | Higher-level alternative to manual sampling |

### llama_get_embeddings

#### Function Signature

```c
LLAMA_API float * llama_get_embeddings(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Returns a pointer to all output token embeddings from the last call to `llama_decode()`. When using a generative model or when pooling type is `LLAMA_POOLING_TYPE_NONE`, the embeddings for which `llama_batch.logits[i] != 0` are stored contiguously in the order they appeared in the batch. This function is essential for accessing the model's internal vector representations of tokens, which can be used for semantic search, clustering, and other embedding-based applications.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    // Create context with embeddings enabled
    struct llama_context_params params = llama_context_default_params();
    params.embeddings = true;  // Enable embeddings
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Process a batch of tokens
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Hello, world!", 13, tokens, 256, true, false);

    // Create a batch with logits enabled for all tokens
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    for (int i = 0; i < n_tokens; i++) {
        batch.logits[i] = 1;  // Request embeddings for all tokens
    }

    // Process the batch
    llama_decode(ctx, batch);

    // Get the embeddings
    float * embeddings = llama_get_embeddings(ctx);

    // Get embedding size
    int n_embd = llama_model_n_embd(model);

    // Print the first few values of the first token's embedding
    printf("First token embedding (first 5 values):\n");
    for (int i = 0; i < 5; i++) {
        printf("  [%d]: %f\n", i, embeddings[i]);
    }

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for semantic similarity:**

```c
// Function to compute cosine similarity between two embeddings
float cosine_similarity(const float * embd1, const float * embd2, int n_embd) {
    float dot_product = 0.0f;
    float norm1 = 0.0f;
    float norm2 = 0.0f;

    for (int i = 0; i < n_embd; i++) {
        dot_product += embd1[i] * embd2[i];
        norm1 += embd1[i] * embd1[i];
        norm2 += embd2[i] * embd2[i];
    }

    return dot_product / (sqrtf(norm1) * sqrtf(norm2));
}

// Function to compare semantic similarity between words
void compare_word_similarity(llama_context * ctx, const char * word1, const char * word2) {
    // Ensure embeddings are enabled
    llama_set_embeddings(ctx, true);

    // Get model and vocabulary
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    int n_embd = llama_model_n_embd(model);

    // Tokenize the first word
    llama_token tokens1[16];
    int n_tokens1 = llama_tokenize(vocab, word1, strlen(word1), tokens1, 16, true, false);

    // Process the first word
    llama_batch batch1 = llama_batch_get_one(tokens1, n_tokens1);
    for (int i = 0; i < n_tokens1; i++) {
        batch1.logits[i] = 1;
    }
    llama_decode(ctx, batch1);

    // Get embeddings for the first word
    float * embeddings1 = llama_get_embeddings(ctx);

    // Make a copy of the embeddings (they will be overwritten by the next decode)
    float word1_embd[n_embd];
    for (int i = 0; i < n_embd; i++) {
        word1_embd[i] = embeddings1[i];
    }

    // Tokenize the second word
    llama_token tokens2[16];
    int n_tokens2 = llama_tokenize(vocab, word2, strlen(word2), tokens2, 16, true, false);

    // Process the second word
    llama_batch batch2 = llama_batch_get_one(tokens2, n_tokens2);
    for (int i = 0; i < n_tokens2; i++) {
        batch2.logits[i] = 1;
    }
    llama_decode(ctx, batch2);

    // Get embeddings for the second word
    float * embeddings2 = llama_get_embeddings(ctx);

    // Compute similarity
    float similarity = cosine_similarity(word1_embd, embeddings2, n_embd);

    printf("Semantic similarity between '%s' and '%s': %.4f\n", word1, word2, similarity);

    // Clean up
    llama_batch_free(batch1);
    llama_batch_free(batch2);
}
```

#### Important Notes

1. This function returns `NULL` if embeddings are not enabled or if no logits were requested in the last batch.
2. To enable embeddings, either:
   - Set `embeddings = true` in the context parameters when creating the context, or
   - Call `llama_set_embeddings(ctx, true)` on an existing context
3. The embeddings are stored in row-major order, with each row corresponding to a token for which logits were requested.
4. The number of rows equals the number of tokens for which `llama_batch.logits[i] != 0`.
5. Each row has `n_embd` elements, where `n_embd` is the embedding size of the model (obtained with `llama_model_n_embd()`).
6. The returned pointer is owned by the context and remains valid until the next call to `llama_decode()` or until the context is freed.
7. This function automatically calls `llama_synchronize()` to ensure all computations are finished.
8. For sequence-level embeddings, use `llama_get_embeddings_seq()` instead, which applies the pooling strategy specified by `pooling_type`.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_set_embeddings`](#llama_set_embeddings) | Set whether to return embeddings | Controls whether embeddings are generated |
| [`llama_get_embeddings_ith`](#llama_get_embeddings_ith) | Get embeddings for a specific token | Alternative way to access embeddings for a specific position |
| [`llama_get_embeddings_seq`](#llama_get_embeddings_seq) | Get embeddings for a sequence | Returns pooled sequence-level embeddings |
| [`llama_pooling_type`](#llama_pooling_type) | Get the pooling type | Affects how sequence embeddings are generated |

### llama_get_embeddings_ith

#### Function Signature

```c
LLAMA_API float * llama_get_embeddings_ith(struct llama_context * ctx, int32_t i);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `i` | `int32_t` | Index of the token for which to get embeddings | Positive index for forward access, negative for reverse access |

#### Description

Returns a pointer to the embeddings for the i-th token from the last call to `llama_decode()`. For positive indices, this is equivalent to `llama_get_embeddings(ctx) + i * n_embd`. Negative indices can be used to access embeddings in reverse order, with -1 referring to the last token's embeddings. This function provides a convenient way to access embeddings for a specific token position.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context with embeddings enabled
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.embeddings = true;  // Enable embeddings
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Process a batch of tokens
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Hello, world!", 13, tokens, 256, true, false);

    // Create a batch with logits enabled for all tokens
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    for (int i = 0; i < n_tokens; i++) {
        batch.logits[i] = 1;  // Request embeddings for all tokens
    }

    // Process the batch
    llama_decode(ctx, batch);

    // Get embeddings for the last token using negative indexing
    float * last_token_embd = llama_get_embeddings_ith(ctx, -1);

    // Get embedding size
    int n_embd = llama_model_n_embd(model);

    // Print the first few values of the last token's embedding
    printf("Last token embedding (first 5 values):\n");
    for (int i = 0; i < 5; i++) {
        printf("  [%d]: %f\n", i, last_token_embd[i]);
    }

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for token comparison:**

```c
// Function to compare embeddings of different tokens in a sentence
void compare_token_embeddings(llama_context * ctx, const char * text) {
    // Ensure embeddings are enabled
    llama_set_embeddings(ctx, true);

    // Get model and vocabulary
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    int n_embd = llama_model_n_embd(model);

    // Tokenize the text
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, text, strlen(text), tokens, 256, true, false);

    // Print the tokens
    printf("Tokens for text '%s':\n", text);
    for (int i = 0; i < n_tokens; i++) {
        char token_text[32];
        llama_token_to_piece(vocab, tokens[i], token_text, sizeof(token_text), 0, true);
        printf("  [%d]: '%s'\n", i, token_text);
    }

    // Create a batch with logits enabled for all tokens
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    for (int i = 0; i < n_tokens; i++) {
        batch.logits[i] = 1;
    }

    // Process the batch
    llama_decode(ctx, batch);

    // Compare embeddings of different tokens
    printf("\nComparing token embeddings:\n");

    for (int i = 0; i < n_tokens; i++) {
        for (int j = i + 1; j < n_tokens; j++) {
            // Get embeddings for tokens i and j
            float * embd_i = llama_get_embeddings_ith(ctx, i);
            float * embd_j = llama_get_embeddings_ith(ctx, j);

            // Compute cosine similarity
            float dot_product = 0.0f;
            float norm_i = 0.0f;
            float norm_j = 0.0f;

            for (int k = 0; k < n_embd; k++) {
                dot_product += embd_i[k] * embd_j[k];
                norm_i += embd_i[k] * embd_i[k];
                norm_j += embd_j[k] * embd_j[k];
            }

            float similarity = dot_product / (sqrtf(norm_i) * sqrtf(norm_j));

            // Get token text
            char token_i_text[32];
            char token_j_text[32];
            llama_token_to_piece(vocab, tokens[i], token_i_text, sizeof(token_i_text), 0, true);
            llama_token_to_piece(vocab, tokens[j], token_j_text, sizeof(token_j_text), 0, true);

            printf("  Similarity between '%s' and '%s': %.4f\n", token_i_text, token_j_text, similarity);
        }
    }

    // Clean up
    llama_batch_free(batch);
}
```

#### Important Notes

1. This function returns `NULL` for invalid indices, if embeddings are not enabled, or if no logits were requested for the specified position.
2. To enable embeddings, either:
   - Set `embeddings = true` in the context parameters when creating the context, or
   - Call `llama_set_embeddings(ctx, true)` on an existing context
3. Positive indices access embeddings in forward order (0 is the first token with embeddings).
4. Negative indices access embeddings in reverse order (-1 is the last token with embeddings).
5. The returned pointer points to `n_embd` consecutive float values, where `n_embd` is the embedding size of the model.
6. The returned pointer is owned by the context and remains valid until the next call to `llama_decode()` or until the context is freed.
7. This function automatically calls `llama_synchronize()` to ensure all computations are finished.
8. For a token to have embeddings available, its corresponding `llama_batch.logits[i]` value must have been set to non-zero when calling `llama_decode()`.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_set_embeddings`](#llama_set_embeddings) | Set whether to return embeddings | Controls whether embeddings are generated |
| [`llama_get_embeddings`](#llama_get_embeddings) | Get all output token embeddings | Returns all embeddings, while this function returns embeddings for a specific position |
| [`llama_get_embeddings_seq`](#llama_get_embeddings_seq) | Get embeddings for a sequence | Returns pooled sequence-level embeddings |
| [`llama_model_n_embd`](#llama_model_n_embd) | Get the embedding size of a model | Returns the dimensionality of the embeddings |

### llama_get_embeddings_seq

#### Function Signature

```c
LLAMA_API float * llama_get_embeddings_seq(struct llama_context * ctx, llama_seq_id seq_id);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `seq_id` | `llama_seq_id` | Sequence ID for which to get embeddings | A valid sequence ID used in the batch |

#### Description

Returns a pointer to the embeddings for a specific sequence from the last call to `llama_decode()`. The embeddings are pooled according to the context's pooling type. This function is particularly useful for obtaining fixed-size vector representations of sequences, which can be used for semantic search, classification, and other tasks that require sequence-level embeddings.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context with embeddings enabled and mean pooling
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());

    struct llama_context_params params = llama_context_default_params();
    params.embeddings = true;           // Enable embeddings
    params.pooling_type = LLAMA_POOLING_TYPE_MEAN;  // Use mean pooling
    struct llama_context * ctx = llama_init_from_model(model, params);

    // Process a batch of tokens
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    llama_token tokens[256];
    int n_tokens = llama_tokenize(vocab, "Hello, world!", 13, tokens, 256, true, false);

    // Create a batch with sequence ID 0
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);

    // Process the batch
    llama_decode(ctx, batch);

    // Get sequence embeddings
    float * seq_embd = llama_get_embeddings_seq(ctx, 0);

    // Get embedding size
    int n_embd = llama_model_n_embd(model);

    // Print the first few values of the sequence embedding
    printf("Sequence embedding (first 5 values):\n");
    for (int i = 0; i < 5; i++) {
        printf("  [%d]: %f\n", i, seq_embd[i]);
    }

    // Clean up
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example for document similarity:**

```c
// Function to compute similarity between documents
float document_similarity(llama_context * ctx, const char * doc1, const char * doc2) {
    // Ensure embeddings are enabled and set pooling type
    llama_set_embeddings(ctx, true);

    // Get model and vocabulary
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);
    int n_embd = llama_model_n_embd(model);

    // Check pooling type
    enum llama_pooling_type pool_type = llama_pooling_type(ctx);
    if (pool_type == LLAMA_POOLING_TYPE_NONE) {
        printf("Warning: No pooling type set, sequence embeddings may not be available\n");
        return -1.0f;
    }

    // Process first document
    llama_token tokens1[1024];
    int n_tokens1 = llama_tokenize(vocab, doc1, strlen(doc1), tokens1, 1024, true, false);

    // Create batch for first document with sequence ID 0
    llama_batch batch1 = llama_batch_get_one(tokens1, n_tokens1);
    llama_seq_id seq_id1 = 0;
    batch1.n_seq_id[0] = 1;
    batch1.seq_id[0] = &seq_id1;

    // Process the batch
    llama_decode(ctx, batch1);

    // Get sequence embeddings for first document
    float * embd1 = llama_get_embeddings_seq(ctx, 0);

    // Make a copy of the embeddings (they will be overwritten by the next decode)
    float doc1_embd[n_embd];
    for (int i = 0; i < n_embd; i++) {
        doc1_embd[i] = embd1[i];
    }

    // Process second document
    llama_token tokens2[1024];
    int n_tokens2 = llama_tokenize(vocab, doc2, strlen(doc2), tokens2, 1024, true, false);

    // Create batch for second document with sequence ID 1
    llama_batch batch2 = llama_batch_get_one(tokens2, n_tokens2);
    llama_seq_id seq_id2 = 1;
    batch2.n_seq_id[0] = 1;
    batch2.seq_id[0] = &seq_id2;

    // Process the batch
    llama_decode(ctx, batch2);

    // Get sequence embeddings for second document
    float * embd2 = llama_get_embeddings_seq(ctx, 1);

    // Compute cosine similarity
    float dot_product = 0.0f;
    float norm1 = 0.0f;
    float norm2 = 0.0f;

    for (int i = 0; i < n_embd; i++) {
        dot_product += doc1_embd[i] * embd2[i];
        norm1 += doc1_embd[i] * doc1_embd[i];
        norm2 += embd2[i] * embd2[i];
    }

    float similarity = dot_product / (sqrtf(norm1) * sqrtf(norm2));

    // Clean up
    llama_batch_free(batch1);
    llama_batch_free(batch2);

    return similarity;
}
```

#### Important Notes

1. This function returns `NULL` if:
   - Pooling type is `LLAMA_POOLING_TYPE_NONE`
   - Embeddings are not enabled
   - The specified sequence ID is not valid
2. The pooling type determines how token embeddings are combined:
   - `LLAMA_POOLING_TYPE_MEAN`: Average of all token embeddings in the sequence
   - `LLAMA_POOLING_TYPE_CLS`: Embedding of the classification (CLS) token
   - `LLAMA_POOLING_TYPE_LAST`: Embedding of the last token in the sequence
   - `LLAMA_POOLING_TYPE_RANK`: Used by reranking models, returns a float[1] with the rank
3. When pooling type is `LLAMA_POOLING_TYPE_RANK`, the function returns a single float value (the rank) instead of a full embedding.
4. The returned pointer points to `n_embd` consecutive float values (except for `POOLING_TYPE_RANK`), where `n_embd` is the embedding size of the model.
5. The returned pointer is owned by the context and remains valid until the next call to `llama_decode()` or until the context is freed.
6. This function automatically calls `llama_synchronize()` to ensure all computations are finished.
7. For optimal results, ensure that all tokens in the sequence have their `llama_batch.logits[i]` value set to non-zero when calling `llama_decode()`.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_set_embeddings`](#llama_set_embeddings) | Set whether to return embeddings | Controls whether embeddings are generated |
| [`llama_pooling_type`](#llama_pooling_type) | Get the pooling type | Returns the pooling strategy used by this function |
| [`llama_get_embeddings`](#llama_get_embeddings) | Get all output token embeddings | Returns token-level embeddings instead of sequence-level |
| [`llama_model_n_embd`](#llama_model_n_embd) | Get the embedding size of a model | Returns the dimensionality of the embeddings |

### llama_attach_threadpool

#### Function Signature

```c
LLAMA_API void llama_attach_threadpool(
        struct llama_context * ctx,
           ggml_threadpool_t   threadpool,
           ggml_threadpool_t   threadpool_batch);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |
| `threadpool` | `ggml_threadpool_t` | Threadpool for single-token generation | A valid threadpool created with `ggml_threadpool_create()` |
| `threadpool_batch` | `ggml_threadpool_t` | Threadpool for batch processing | A valid threadpool created with `ggml_threadpool_create()` |

#### Description

Attaches external threadpools to a context for CPU computation. This allows sharing threadpools across multiple contexts or using custom threadpools with specific configurations. By default, each context creates its own internal threadpools, but this function allows for more control over thread management.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include "ggml.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create custom threadpools
    int n_threads = 4;
    ggml_threadpool_t pool_gen = ggml_threadpool_create(n_threads);
    ggml_threadpool_t pool_batch = ggml_threadpool_create(n_threads * 2);

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Attach the threadpools to the context
    llama_attach_threadpool(ctx, pool_gen, pool_batch);

    printf("Attached custom threadpools: %d threads for generation, %d threads for batch processing\n",
           n_threads, n_threads * 2);

    // Use the context with custom threadpools
    // ...

    // Detach threadpools before freeing the context
    llama_detach_threadpool(ctx);

    // Clean up
    llama_free(ctx);
    llama_model_free(model);

    // Free the threadpools
    ggml_threadpool_free(pool_gen);
    ggml_threadpool_free(pool_batch);

    llama_backend_free();

    return 0;
}
```

**Example for sharing threadpools:**

```c
// Function to create multiple contexts sharing the same threadpools
void create_shared_contexts(const char * model_path, int n_contexts, int n_threads) {
    // Create shared threadpools
    ggml_threadpool_t pool_gen = ggml_threadpool_create(n_threads);
    ggml_threadpool_t pool_batch = ggml_threadpool_create(n_threads);

    printf("Created shared threadpools with %d threads\n", n_threads);

    // Load the model
    struct llama_model * model = llama_model_load_from_file(model_path, llama_model_default_params());

    // Create multiple contexts
    struct llama_context * contexts[n_contexts];

    for (int i = 0; i < n_contexts; i++) {
        // Create context
        contexts[i] = llama_init_from_model(model, llama_context_default_params());

        // Attach the shared threadpools
        llama_attach_threadpool(contexts[i], pool_gen, pool_batch);

        printf("Created context %d and attached shared threadpools\n", i);
    }

    // Use the contexts
    // ...

    // Clean up
    for (int i = 0; i < n_contexts; i++) {
        // Detach threadpools before freeing
        llama_detach_threadpool(contexts[i]);
        llama_free(contexts[i]);
    }

    llama_model_free(model);

    // Free the threadpools
    ggml_threadpool_free(pool_gen);
    ggml_threadpool_free(pool_batch);
}
```

#### Important Notes

1. This function is optional - by default, each context creates its own internal threadpools.
2. Attaching external threadpools is useful for:
   - Sharing threadpools across multiple contexts to reduce thread creation overhead
   - Using custom threadpool configurations
   - Controlling the total number of threads used by the application
3. The `threadpool` parameter is used for single-token generation (equivalent to `n_threads` in context parameters).
4. The `threadpool_batch` parameter is used for batch processing (equivalent to `n_threads_batch` in context parameters).
5. Either parameter can be `NULL`, in which case the context will continue to use its internal threadpool for that purpose.
6. You must call `llama_detach_threadpool()` before freeing the context if you've attached external threadpools.
7. The attached threadpools must remain valid for the lifetime of the context or until they are detached.
8. The context does not take ownership of the threadpools - you are responsible for freeing them after detaching.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_detach_threadpool`](#llama_detach_threadpool) | Detach a threadpool from a context | Must be called before freeing the context if threadpools were attached |
| [`llama_set_n_threads`](#llama_set_n_threads) | Set the number of threads | Alternative way to control thread count for internal threadpools |
| [`ggml_threadpool_create`](#ggml_threadpool_create) | Create a threadpool | Creates threadpools that can be attached with this function |
| [`ggml_threadpool_free`](#ggml_threadpool_free) | Free a threadpool | Frees threadpools after they are detached |

### llama_detach_threadpool

#### Function Signature

```c
LLAMA_API void llama_detach_threadpool(struct llama_context * ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `ctx` | `struct llama_context *` | Pointer to the context | A valid context created with `llama_init_from_model` |

#### Description

Detaches any external threadpools that were previously attached to the context with `llama_attach_threadpool()`. After calling this function, the context will revert to using its internal threadpools. This function must be called before freeing a context if external threadpools were attached to it.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include "ggml.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create custom threadpools
    int n_threads = 4;
    ggml_threadpool_t pool_gen = ggml_threadpool_create(n_threads);
    ggml_threadpool_t pool_batch = ggml_threadpool_create(n_threads * 2);

    // Load model and create context
    struct llama_model * model = llama_model_load_from_file("model.gguf", llama_model_default_params());
    struct llama_context * ctx = llama_init_from_model(model, llama_context_default_params());

    // Attach the threadpools to the context
    llama_attach_threadpool(ctx, pool_gen, pool_batch);

    // Use the context with custom threadpools
    // ...

    // Detach threadpools before freeing the context
    llama_detach_threadpool(ctx);
    printf("Detached custom threadpools, context now using internal threadpools\n");

    // Continue using the context with internal threadpools
    // ...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);

    // Free the threadpools
    ggml_threadpool_free(pool_gen);
    ggml_threadpool_free(pool_batch);

    llama_backend_free();

    return 0;
}
```

**Example for temporary threadpool attachment:**

```c
// Function to temporarily use custom threadpools for a specific task
void process_with_custom_threadpool(llama_context * ctx, const char * prompt, int n_threads) {
    // Create custom threadpools for this specific task
    ggml_threadpool_t custom_pool = ggml_threadpool_create(n_threads);

    printf("Created temporary threadpool with %d threads\n", n_threads);

    // Attach the custom threadpool
    llama_attach_threadpool(ctx, custom_pool, custom_pool);

    // Process the prompt with the custom threadpool
    const struct llama_model * model = llama_get_model(ctx);
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    llama_token tokens[1024];
    int n_tokens = llama_tokenize(vocab, prompt, strlen(prompt), tokens, 1024, true, false);

    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    printf("Processing prompt with custom threadpool: %s\n", prompt);
    llama_decode(ctx, batch);

    // Detach the custom threadpool
    llama_detach_threadpool(ctx);
    printf("Detached custom threadpool, reverting to default\n");

    // Free the custom threadpool
    ggml_threadpool_free(custom_pool);

    // Clean up
    llama_batch_free(batch);
}
```

#### Important Notes

1. This function must be called before freeing a context if external threadpools were attached to it.
2. After calling this function, the context will revert to using its internal threadpools.
3. The function does not free the external threadpools - you are responsible for freeing them separately.
4. It's safe to call this function even if no external threadpools were attached.
5. Common usage patterns include:
   - Attaching threadpools at context creation and detaching before context destruction
   - Temporarily attaching custom threadpools for specific tasks
   - Switching between different threadpool configurations during the lifetime of a context
6. The context's internal threadpools will use the thread counts specified in the context parameters or set with `llama_set_n_threads()`.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_attach_threadpool`](#llama_attach_threadpool) | Attach a threadpool to a context | The function that attaches threadpools that this function detaches |
| [`llama_set_n_threads`](#llama_set_n_threads) | Set the number of threads | Controls thread count for internal threadpools |
| [`ggml_threadpool_create`](#ggml_threadpool_create) | Create a threadpool | Creates threadpools that can be attached to contexts |
| [`ggml_threadpool_free`](#ggml_threadpool_free) | Free a threadpool | Frees threadpools after they are detached |

## Tokenization and Vocabulary

This module handles tokenization and vocabulary management.

### Dependencies
- Model Management

### Functions

| Function | Description |
|----------|-------------|
| `llama_vocab_type` | Get the vocabulary type |
| `llama_vocab_n_tokens` | Get the number of tokens in the vocabulary |
| `llama_vocab_get_text` | Get the text for a token |
| `llama_vocab_get_score` | Get the score for a token |
| `llama_vocab_get_attr` | Get the attributes for a token |
| `llama_vocab_is_eog` | Check if a token is an end-of-generation token |
| `llama_vocab_is_control` | Check if a token is a control token |
| `llama_vocab_bos` | Get the beginning-of-sentence token |
| `llama_vocab_eos` | Get the end-of-sentence token |
| `llama_vocab_eot` | Get the end-of-turn token |
| `llama_vocab_sep` | Get the sentence separator token |
| `llama_vocab_nl` | Get the next-line token |
| `llama_vocab_pad` | Get the padding token |
| `llama_vocab_get_add_bos` | Check if BOS token should be added |
| `llama_vocab_get_add_eos` | Check if EOS token should be added |
| `llama_vocab_fim_pre` | Get the FIM prefix token |
| `llama_vocab_fim_suf` | Get the FIM suffix token |
| `llama_vocab_fim_mid` | Get the FIM middle token |
| `llama_vocab_fim_pad` | Get the FIM padding token |
| `llama_vocab_fim_rep` | Get the FIM repeat token |
| `llama_vocab_fim_sep` | Get the FIM separator token |
| `llama_tokenize` | Convert text into tokens |
| `llama_token_to_piece` | Convert a token to a text piece |
| `llama_detokenize` | Convert tokens into text |
| `llama_chat_apply_template` | Apply a chat template to messages |
| `llama_chat_builtin_templates` | Get list of built-in chat templates |

### Structs

| Struct | Description |
|--------|-------------|
| `llama_vocab` | Opaque structure representing a vocabulary |
| `llama_chat_message` | Structure representing a chat message |

## Batch Processing

This module handles processing batches of tokens.

### Dependencies
- Context Management

### Functions

| Function | Description |
|----------|-------------|
| `llama_batch_init` | Initialize a batch |
| `llama_batch_free` | Free a batch |
| `llama_batch_get_one` | Get a batch for a single sequence of tokens |

### Structs

| Struct | Description |
|--------|-------------|
| `llama_batch` | Structure representing a batch of tokens |
| `llama_token_data` | Structure representing token data |
| `llama_token_data_array` | Structure representing an array of token data |

## KV Cache Management

This module handles managing the key-value cache.

### Dependencies
- Context Management

### Functions

| Function | Description |
|----------|-------------|
| `llama_kv_self_clear` | Clear the KV cache |
| `llama_kv_self_seq_rm` | Remove tokens from the KV cache |
| `llama_kv_self_seq_cp` | Copy tokens in the KV cache |
| `llama_kv_self_seq_keep` | Keep only tokens from a specific sequence |
| `llama_kv_self_seq_add` | Add relative position to tokens |
| `llama_kv_self_seq_div` | Divide positions by a factor |
| `llama_kv_self_seq_pos_min` | Get the smallest position in the KV cache |
| `llama_kv_self_seq_pos_max` | Get the largest position in the KV cache |
| `llama_kv_self_defrag` | Defragment the KV cache |
| `llama_kv_self_can_shift` | Check if the KV cache can be shifted |
| `llama_kv_self_update` | Apply KV cache updates |

### Structs

| Struct | Description |
|--------|-------------|
| `llama_kv_cache` | Opaque structure representing a KV cache |

## State Management

This module handles saving and loading model state.

### Dependencies
- Context Management

### Functions

| Function | Description |
|----------|-------------|
| `llama_state_get_size` | Get the size of the state |
| `llama_state_get_data` | Copy the state to a buffer |
| `llama_state_set_data` | Set the state from a buffer |
| `llama_state_load_file` | Load state from a file |
| `llama_state_save_file` | Save state to a file |
| `llama_state_seq_get_size` | Get the size needed to copy a sequence's KV cache |
| `llama_state_seq_get_data` | Copy a sequence's KV cache to a buffer |
| `llama_state_seq_set_data` | Copy data into a sequence's KV cache |
| `llama_state_seq_save_file` | Save a sequence's state to a file |
| `llama_state_seq_load_file` | Load a sequence's state from a file |

### Structs

None specific to this module.

## Decoding

This module handles decoding tokens.

### Dependencies
- Context Management
- Batch Processing

### Functions

| Function | Description |
|----------|-------------|
| `llama_encode` | Process a batch of tokens using the encoder |
| `llama_decode` | Process a batch of tokens using the decoder |

### Structs

None specific to this module.

## Sampling

This module handles sampling tokens from the model output.

### Dependencies
- Context Management
- Tokenization and Vocabulary

### Functions

| Function | Description |
|----------|-------------|
| `llama_sampler_chain_default_params` | Get default sampler chain parameters |
| `llama_sampler_init` | Initialize a sampler |
| `llama_sampler_name` | Get the name of a sampler |
| `llama_sampler_accept` | Accept a token |
| `llama_sampler_apply` | Apply a sampler to token data |
| `llama_sampler_reset` | Reset a sampler |
| `llama_sampler_clone` | Clone a sampler |
| `llama_sampler_free` | Free a sampler |
| `llama_sampler_chain_init` | Initialize a sampler chain |
| `llama_sampler_chain_add` | Add a sampler to a chain |
| `llama_sampler_chain_get` | Get a sampler from a chain |
| `llama_sampler_chain_n` | Get the number of samplers in a chain |
| `llama_sampler_chain_remove` | Remove a sampler from a chain |
| `llama_sampler_init_greedy` | Initialize a greedy sampler |
| `llama_sampler_init_dist` | Initialize a distribution sampler |
| `llama_sampler_init_top_k` | Initialize a top-k sampler |
| `llama_sampler_init_top_p` | Initialize a top-p sampler |
| `llama_sampler_init_min_p` | Initialize a min-p sampler |
| `llama_sampler_init_typical` | Initialize a typical sampler |
| `llama_sampler_init_temp` | Initialize a temperature sampler |
| `llama_sampler_init_temp_ext` | Initialize an extended temperature sampler |
| `llama_sampler_init_xtc` | Initialize an XTC sampler |
| `llama_sampler_init_top_n_sigma` | Initialize a top-n sigma sampler |
| `llama_sampler_init_mirostat` | Initialize a mirostat sampler |
| `llama_sampler_init_mirostat_v2` | Initialize a mirostat v2 sampler |
| `llama_sampler_init_grammar` | Initialize a grammar sampler |
| `llama_sampler_init_grammar_lazy_patterns` | Initialize a lazy grammar sampler |
| `llama_sampler_init_penalties` | Initialize a penalties sampler |
| `llama_sampler_init_dry` | Initialize a DRY sampler |
| `llama_sampler_init_logit_bias` | Initialize a logit bias sampler |
| `llama_sampler_init_infill` | Initialize an infill sampler |
| `llama_sampler_get_seed` | Get the seed used by a sampler |
| `llama_sampler_sample` | Sample a token |
| `llama_perf_sampler` | Get sampler performance data |
| `llama_perf_sampler_print` | Print sampler performance data |
| `llama_perf_sampler_reset` | Reset sampler performance data |

### Structs

| Struct | Description |
|--------|-------------|
| `llama_sampler` | Structure representing a sampler |
| `llama_sampler_i` | Interface for custom samplers |
| `llama_sampler_chain_params` | Parameters for sampler chains |
| `llama_logit_bias` | Structure representing a logit bias |
| `llama_perf_sampler_data` | Structure containing sampler performance data |

## Adapter Management

This module handles loading and applying adapters to models.

### Dependencies
- Model Management
- Context Management

### Functions

| Function | Description |
|----------|-------------|
| `llama_adapter_lora_init` | Load a LoRA adapter from file |
| `llama_adapter_lora_free` | Free a LoRA adapter |
| `llama_set_adapter_lora` | Add a LoRA adapter to a context |
| `llama_rm_adapter_lora` | Remove a LoRA adapter from a context |
| `llama_clear_adapter_lora` | Remove all LoRA adapters from a context |
| `llama_apply_adapter_cvec` | Apply a control vector to a context |

### Structs

| Struct | Description |
|--------|-------------|
| `llama_adapter_lora` | Opaque structure representing a LoRA adapter |

## Function Details

### llama_backend_init

#### Function Signature

```c
LLAMA_API void llama_backend_init(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Initializes the llama.cpp backend and the underlying GGML library. This function must be called once at the beginning of your program before using any other llama.cpp functions. It sets up the necessary resources and configurations for the library to function properly.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Use llama.cpp functionality...

    // Clean up at the end
    llama_backend_free();

    return 0;
}
```

**Example from embedding.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse parameters and initialize common components
    common_params params;
    if (!common_params_parse(argc, argv, params, LLAMA_EXAMPLE_EMBEDDING)) {
        return 1;
    }
    common_init();

    // Initialize the backend
    llama_backend_init();
    llama_numa_init(params.numa);

    // Load model and create context
    common_init_result llama_init = common_init_from_params(params);
    llama_model * model = llama_init.model.get();
    llama_context * ctx = llama_init.context.get();

    // Use the model for embeddings...

    // Clean up
    llama_batch_free(batch);
    llama_backend_free();

    return 0;
}
```

**Example from batched.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse parameters
    common_params params;
    if (!common_params_parse(argc, argv, params, LLAMA_EXAMPLE_COMMON, print_usage)) {
        return 1;
    }
    common_init();

    // Initialize the backend
    llama_backend_init();
    llama_numa_init(params.numa);

    // Load model and create context
    llama_model_params model_params = common_model_params_to_llama(params);
    llama_model * model = llama_model_load_from_file(params.model.path.c_str(), model_params);

    // Use the model...

    // Clean up
    llama_sampler_free(smpl);
    llama_free(ctx);
    llama_model_free(model);
    llama_batch_free(batch);
    llama_backend_free();

    return 0;
}
```

#### Important Notes

1. `llama_backend_init()` should be called only once at the beginning of your program.
2. Always call `llama_backend_free()` at the end of your program to properly clean up resources.
3. The initialization must happen before loading any models or creating any contexts.
4. For NUMA systems, you can optionally call `llama_numa_init()` after `llama_backend_init()` to optimize for NUMA architectures.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_backend_free`](#llama_backend_free) | Free resources used by the backend | Cleanup function that should be called at the end of the program |
| [`llama_numa_init`](#llama_numa_init) | Initialize NUMA optimization strategy | Optional function to call after initialization for NUMA systems |
| `llama_print_system_info` | Print system information | Useful for debugging after initialization |
| `llama_log_set` | Set logging callback | Configure logging before using other functions |
| `llama_supports_mmap` | Check if memory mapping is supported | Query capability of the initialized backend |
| `llama_supports_mlock` | Check if memory locking is supported | Query capability of the initialized backend |
| `llama_supports_gpu_offload` | Check if GPU offloading is supported | Query capability of the initialized backend |
| `llama_supports_rpc` | Check if RPC is supported | Query capability of the initialized backend |

### llama_backend_free

#### Function Signature

```c
LLAMA_API void llama_backend_free(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Frees resources used by the llama.cpp backend. This function should be called once at the end of your program after you're done using all llama.cpp functionality. It cleans up any resources allocated by `llama_backend_init`.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Use llama.cpp functionality...

    // Clean up at the end
    llama_backend_free();

    return 0;
}
```

**Example from embedding.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse parameters and initialize common components
    common_params params;
    if (!common_params_parse(argc, argv, params, LLAMA_EXAMPLE_EMBEDDING)) {
        return 1;
    }
    common_init();

    // Initialize the backend
    llama_backend_init();
    llama_numa_init(params.numa);

    // Use the model for embeddings...

    // Clean up
    llama_batch_free(batch);
    llama_backend_free();

    return 0;
}
```

**Example from batched.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse parameters
    common_params params;
    if (!common_params_parse(argc, argv, params, LLAMA_EXAMPLE_COMMON, print_usage)) {
        return 1;
    }
    common_init();

    // Initialize the backend
    llama_backend_init();
    llama_numa_init(params.numa);

    // Use the model...

    // Clean up
    llama_sampler_free(smpl);
    llama_free(ctx);
    llama_model_free(model);
    llama_batch_free(batch);
    llama_backend_free();

    return 0;
}
```

#### Important Notes

1. `llama_backend_free()` should be called only once at the end of your program.
2. Make sure to free all other resources (models, contexts, batches, etc.) before calling `llama_backend_free()`.
3. After calling `llama_backend_free()`, you cannot use any other llama.cpp functions unless you call `llama_backend_init()` again.
4. Failing to call `llama_backend_free()` may result in memory leaks.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend | Initialization function that must be called before using `llama_backend_free` |

### llama_numa_init

#### Function Signature

```c
LLAMA_API void llama_numa_init(enum ggml_numa_strategy numa);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `numa` | `enum ggml_numa_strategy` | NUMA optimization strategy | `GGML_NUMA_STRATEGY_DISABLED` (0)<br>`GGML_NUMA_STRATEGY_DISTRIBUTE` (1)<br>`GGML_NUMA_STRATEGY_ISOLATE` (2)<br>`GGML_NUMA_STRATEGY_NUMACTL` (3)<br>`GGML_NUMA_STRATEGY_MIRROR` (4) |

#### Description

Initializes NUMA (Non-Uniform Memory Access) optimization strategy for better performance on NUMA systems. This function should be called after `llama_backend_init()` and before loading models or creating contexts if you want to optimize for NUMA architectures.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Initialize NUMA optimization
    llama_numa_init(GGML_NUMA_STRATEGY_DISTRIBUTE);

    // Use llama.cpp functionality...

    // Clean up at the end
    llama_backend_free();

    return 0;
}
```

**Example from embedding.cpp:**

```c
int main(int argc, char ** argv) {
    // Parse parameters and initialize common components
    common_params params;
    if (!common_params_parse(argc, argv, params, LLAMA_EXAMPLE_EMBEDDING)) {
        return 1;
    }
    common_init();

    // Initialize the backend
    llama_backend_init();
    llama_numa_init(params.numa);

    // Load model and create context
    common_init_result llama_init = common_init_from_params(params);
    llama_model * model = llama_init.model.get();
    llama_context * ctx = llama_init.context.get();

    // Use the model...
}
```

**Example from batched.cpp:**

```c
int main(int argc, char ** argv) {
    common_params params;

    if (!common_params_parse(argc, argv, params, LLAMA_EXAMPLE_COMMON, print_usage)) {
        return 1;
    }
    common_init();

    // Initialize the backend
    llama_backend_init();
    llama_numa_init(params.numa);

    // Load model and create context
    llama_model_params model_params = common_model_params_to_llama(params);
    llama_model * model = llama_model_load_from_file(params.model.path.c_str(), model_params);

    // Use the model...
}
```

#### Important Notes

1. NUMA optimization is only relevant for systems with multiple CPU sockets or NUMA nodes.
2. The default strategy is `GGML_NUMA_STRATEGY_DISABLED` if this function is not called.
3. Different strategies have different performance implications:
   - `DISTRIBUTE`: Spread execution evenly over all NUMA nodes
   - `ISOLATE`: Only spawn threads on CPUs on the node that execution started on
   - `NUMACTL`: Use the CPU map provided by numactl
   - `MIRROR`: Mirror memory across NUMA nodes (can improve performance but uses more memory)
4. If you've run without NUMA optimization previously, it's recommended to drop the system page cache before using this.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend | Should be called before `llama_numa_init` |

### llama_time_us

#### Function Signature

```c
LLAMA_API int64_t llama_time_us(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns the current time in microseconds. This function is useful for measuring elapsed time and performance benchmarking in llama.cpp applications. It's a wrapper around the GGML library's time measurement function.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Get start time
    int64_t start_time = llama_time_us();

    // Perform some operations...

    // Get end time and calculate elapsed time
    int64_t end_time = llama_time_us();
    int64_t elapsed_us = end_time - start_time;

    printf("Operation took %lld microseconds\n", (long long)elapsed_us);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example from quantize.cpp:**

```c
// Record the start time
const int64_t t_main_start_us = llama_time_us();

// Perform initialization
const int64_t t_start_us = llama_time_us();

// Perform quantization
// ...

// Calculate quantization time
t_quantize_us = llama_time_us() - t_start_us;

// Calculate total time
const int64_t t_main_end_us = llama_time_us();
```

#### Important Notes

1. The time is measured in microseconds (1 second = 1,000,000 microseconds).
2. The function provides a monotonic time source, which means it's suitable for measuring elapsed time.
3. The absolute value returned by this function is not meaningful; only the difference between two calls is useful.
4. For accurate measurements, call this function immediately before and after the operation you want to time.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend | Should be called before using time measurement functions |

### llama_max_devices

#### Function Signature

```c
LLAMA_API size_t llama_max_devices(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns the maximum number of devices (typically GPUs) that can be used with llama.cpp. This function is useful for allocating arrays of the correct size for device-specific configurations and for checking if a requested number of devices is valid.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Get the maximum number of devices
    size_t max_devices = llama_max_devices();
    printf("Maximum number of supported devices: %zu\n", max_devices);

    // Allocate an array for device-specific configurations
    float* tensor_split = (float*)malloc(max_devices * sizeof(float));

    // Use the array...

    // Clean up
    free(tensor_split);
    llama_backend_free();

    return 0;
}
```

**Example from llama-bench.cpp:**

```c
// Check if the requested number of devices is valid
GGML_ASSERT(split_arg.size() <= llama_max_devices());

// Allocate an array for tensor split configuration
std::vector<float> tensor_split(llama_max_devices());
for (size_t i = 0; i < llama_max_devices(); ++i) {
    tensor_split[i] = 0.0f;
}

// Set values for the requested devices
for (size_t i = 0; i < split_arg.size(); ++i) {
    tensor_split[i] = split_arg[i];
}
```

#### Important Notes

1. The current implementation returns a hardcoded value of 16, indicating that llama.cpp supports a maximum of 16 devices.
2. This function is particularly useful when working with multi-GPU setups and when configuring tensor splits across devices.
3. When allocating arrays for device configurations, always use this function to determine the array size to ensure compatibility with future versions of llama.cpp.
4. The actual number of available devices may be less than the maximum supported; use `ggml_backend_dev_count()` to get the actual number of available devices.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_supports_gpu_offload`](#llama_supports_gpu_offload) | Check if GPU offloading is supported | Used to determine if GPU devices can be used |

### llama_supports_mmap

#### Function Signature

```c
LLAMA_API bool llama_supports_mmap(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns whether memory mapping is supported on the current platform. Memory mapping allows the model to be loaded directly from disk into memory without copying it, which can significantly reduce memory usage and improve loading times for large models.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Check if memory mapping is supported
    if (llama_supports_mmap()) {
        printf("Memory mapping is supported on this platform.\n");
    } else {
        printf("Memory mapping is not supported on this platform.\n");
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example in Model Loading:**

```c
// Set up model parameters
struct llama_model_params params = llama_model_default_params();

// Use memory mapping if supported
if (llama_supports_mmap()) {
    params.use_mmap = true;
    printf("Using memory mapping for model loading\n");
} else {
    params.use_mmap = false;
    printf("Memory mapping not supported, loading model into RAM\n");
}

// Load the model
struct llama_model * model = llama_model_load_from_file(model_path, params);
```

#### Important Notes

1. Memory mapping is platform-dependent and may not be available on all systems.
2. The result of this function is determined at compile time, not runtime.
3. Memory mapping can significantly reduce the memory footprint of large models, as only the parts of the model that are actively used are loaded into memory.
4. When memory mapping is not supported, the entire model must be loaded into RAM, which requires more memory.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Can use memory mapping if supported |
| [`llama_supports_mlock`](#llama_supports_mlock) | Check if memory locking is supported | Another memory management feature |

### llama_supports_mlock

#### Function Signature

```c
LLAMA_API bool llama_supports_mlock(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns whether memory locking is supported on the current platform. Memory locking allows the model to be locked in RAM, preventing it from being swapped out to disk, which can improve performance by avoiding page faults during inference.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Check if memory locking is supported
    if (llama_supports_mlock()) {
        printf("Memory locking is supported on this platform.\n");
    } else {
        printf("Memory locking is not supported on this platform.\n");
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example in Model Loading:**

```c
// Set up model parameters
struct llama_model_params params = llama_model_default_params();

// Use memory locking if supported
if (llama_supports_mlock()) {
    params.use_mlock = true;
    printf("Using memory locking for model loading\n");
} else {
    params.use_mlock = false;
    printf("Memory locking not supported\n");
}

// Load the model
struct llama_model * model = llama_model_load_from_file(model_path, params);
```

#### Important Notes

1. Memory locking is platform-dependent and may not be available on all systems.
2. The result of this function is determined at compile time, not runtime.
3. Memory locking requires appropriate permissions (e.g., on Linux, it may require increasing the `RLIMIT_MEMLOCK` limit).
4. Locking large models in memory can consume a significant amount of RAM and may affect other applications running on the system.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Can use memory locking if supported |
| [`llama_supports_mmap`](#llama_supports_mmap) | Check if memory mapping is supported | Another memory management feature |

### llama_supports_gpu_offload

#### Function Signature

```c
LLAMA_API bool llama_supports_gpu_offload(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns whether GPU offloading is supported. This function checks if there's a GPU backend available or if RPC is supported, which would allow offloading computations to a GPU or a remote server. GPU offloading can significantly accelerate model inference.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Check if GPU offloading is supported
    if (llama_supports_gpu_offload()) {
        printf("GPU offloading is supported.\n");
    } else {
        printf("GPU offloading is not supported.\n");
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example in Model Loading:**

```c
// Set up model parameters
struct llama_model_params params = llama_model_default_params();

// Use GPU offloading if supported
if (llama_supports_gpu_offload()) {
    params.n_gpu_layers = 32; // Offload 32 layers to GPU
    printf("Using GPU offloading for model inference\n");
} else {
    params.n_gpu_layers = 0;
    printf("GPU offloading not supported, using CPU only\n");
}

// Load the model
struct llama_model * model = llama_model_load_from_file(model_path, params);
```

#### Important Notes

1. GPU offloading requires a compatible GPU and the appropriate backend (CUDA, Metal, etc.) to be available.
2. The function returns true if either a GPU backend is available or if RPC is supported (which could allow offloading to a remote GPU).
3. Even if GPU offloading is supported, the actual performance benefit depends on the specific GPU, the model size, and other factors.
4. When GPU offloading is not supported, the model will run entirely on the CPU, which may be slower for large models.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_supports_rpc`](#llama_supports_rpc) | Check if RPC is supported | Another way to offload computations |
| [`llama_max_devices`](#llama_max_devices) | Get maximum number of supported devices | Used for multi-GPU configurations |

### llama_supports_rpc

#### Function Signature

```c
LLAMA_API bool llama_supports_rpc(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns whether Remote Procedure Call (RPC) support is available. RPC allows offloading computations to a remote server, which can be useful for distributing the workload or using more powerful hardware that's not directly attached to the local machine.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Check if RPC is supported
    if (llama_supports_rpc()) {
        printf("RPC support is available.\n");
    } else {
        printf("RPC support is not available.\n");
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example in Distributed Setup:**

```c
// Initialize the backend
llama_backend_init();

// Check if we can use remote servers
if (llama_supports_rpc()) {
    printf("Setting up distributed inference with RPC\n");

    // Set up RPC connections to remote servers
    // ...

    // Load the model with RPC configuration
    // ...
} else {
    printf("RPC not supported, using local resources only\n");

    // Load the model locally
    // ...
}
```

#### Important Notes

1. RPC support requires the RPC backend to be available, which may not be included in all builds of llama.cpp.
2. Using RPC introduces network latency, which can affect performance, especially for interactive applications.
3. RPC can be useful for distributing the workload across multiple machines or for offloading computations to more powerful hardware.
4. Security considerations should be taken into account when using RPC, as it involves network communication.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_supports_gpu_offload`](#llama_supports_gpu_offload) | Check if GPU offloading is supported | RPC can be used for remote GPU offloading |

### llama_print_system_info

#### Function Signature

```c
LLAMA_API const char * llama_print_system_info(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns a string containing information about the system and available backends with their features. This is useful for debugging and understanding what capabilities are available in the current build of llama.cpp. The returned string includes details about each registered backend and its features.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Get and print system information
    const char * system_info = llama_print_system_info();
    printf("System information:\n%s\n", system_info);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example in Debugging Context:**

```c
// When troubleshooting performance or compatibility issues
llama_backend_init();

// Log system capabilities for debugging
const char * system_info = llama_print_system_info();
fprintf(log_file, "System configuration:\n%s\n", system_info);

// Continue with application logic
// ...
```

#### Important Notes

1. This function should be called after `llama_backend_init()` to ensure all backends are properly initialized.
2. The returned string is statically allocated and will be overwritten on subsequent calls to this function.
3. The information provided is useful for debugging, logging, and understanding what hardware acceleration is available.
4. The string format may change between versions of llama.cpp, so don't rely on parsing it programmatically.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend | Must be called before `llama_print_system_info` |
| [`llama_supports_gpu_offload`](#llama_supports_gpu_offload) | Check if GPU offloading is supported | Complementary function for checking specific capabilities |

### llama_log_set

#### Function Signature

```c
LLAMA_API void llama_log_set(ggml_log_callback log_callback, void * user_data);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `log_callback` | `ggml_log_callback` | Function pointer to the callback that will handle log messages | Custom function or `NULL` to use default |
| `user_data` | `void *` | User-provided data that will be passed to the callback | Any pointer value |

#### Description

Sets the callback function for handling log messages from the llama.cpp library. This allows applications to customize how log messages are processed, displayed, or stored. If `log_callback` is `NULL`, the default callback will be used, which prints messages to stderr.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

// Custom log callback function
void my_log_callback(enum ggml_log_level level, const char * text, void * user_data) {
    FILE * log_file = (FILE *) user_data;
    fprintf(log_file, "[%d] %s", level, text);
    fflush(log_file);
}

int main() {
    // Open a log file
    FILE * log_file = fopen("llama.log", "w");

    // Set the custom log callback
    llama_log_set(my_log_callback, log_file);

    // Initialize the backend (will generate log messages)
    llama_backend_init();

    // Use llama.cpp functionality...

    // Clean up
    llama_backend_free();
    fclose(log_file);

    return 0;
}
```

**Example with Different Log Levels:**

```c
// Custom log callback that filters by log level
void filtered_log_callback(enum ggml_log_level level, const char * text, void * user_data) {
    // Only print warnings and errors
    if (level <= GGML_LOG_LEVEL_WARNING) {
        printf("[%s] %s",
               level == GGML_LOG_LEVEL_ERROR ? "ERROR" : "WARNING",
               text);
    }
}

// Set the filtered log callback
llama_log_set(filtered_log_callback, NULL);
```

#### Important Notes

1. The log callback should be set before calling other llama.cpp functions to ensure all log messages are captured.
2. The callback function must handle the log message quickly to avoid blocking the main processing.
3. The default callback simply prints messages to stderr.
4. The callback may be called from different threads, so it should be thread-safe.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend | Often generates log messages that will be processed by the callback |
| [`llama_print_system_info`](#llama_print_system_info) | Print system information | Can be used in conjunction with logging for debugging |

### llama_model_default_params

#### Function Signature

```c
LLAMA_API struct llama_model_params llama_model_default_params(void);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| None | | | |

#### Description

Returns a struct with default parameters for model loading. This function should be called before loading a model to get a default set of parameters, which can then be modified as needed before passing to `llama_model_load_from_file` or similar functions.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Get default model parameters
    struct llama_model_params params = llama_model_default_params();

    // Modify parameters as needed
    params.n_gpu_layers = 32; // Offload 32 layers to GPU

    // Load the model with the modified parameters
    struct llama_model * model = llama_model_load_from_file("path/to/model.gguf", params);

    // Use the model...

    // Clean up
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from simple.cpp:**

```c
// Load dynamic backends
ggml_backend_load_all();

// Initialize the model
llama_model_params model_params = llama_model_default_params();
model_params.n_gpu_layers = ngl;

llama_model * model = llama_model_load_from_file(model_path.c_str(), model_params);

if (model == NULL) {
    fprintf(stderr , "%s: error: unable to load model\n" , __func__);
    return 1;
}
```

**Example from batched.cpp:**

```c
// Initialize the backend
llama_backend_init();
llama_numa_init(params.numa);

// Initialize the model
llama_model_params model_params = common_model_params_to_llama(params);
llama_model * model = llama_model_load_from_file(params.model.path.c_str(), model_params);

if (model == NULL) {
    LOG_ERR("%s: error: unable to load model\n" , __func__);
    return 1;
}
```

#### Important Notes

1. The default parameters include:
   - `n_gpu_layers`: 0 (or 999 if Metal is available)
   - `split_mode`: LLAMA_SPLIT_MODE_LAYER
   - `main_gpu`: 0
   - `vocab_only`: false
   - `use_mmap`: true
   - `use_mlock`: false
   - `check_tensors`: false
2. You should always modify the parameters as needed for your specific use case before loading a model.
3. The function returns a struct by value, so you can modify it without affecting other code.
4. Some parameters, like `n_gpu_layers`, can significantly affect performance and memory usage.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Uses the parameters returned by `llama_model_default_params` |
| [`llama_model_load_from_splits`](#llama_model_load_from_splits) | Load a model from multiple splits | Uses the parameters returned by `llama_model_default_params` |
| [`llama_supports_gpu_offload`](#llama_supports_gpu_offload) | Check if GPU offloading is supported | Useful to check before setting `n_gpu_layers` |

### llama_model_load_from_file

#### Function Signature

```c
LLAMA_API struct llama_model * llama_model_load_from_file(
                         const char * path_model,
          struct llama_model_params   params);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `path_model` | `const char *` | Path to the model file | Path to a GGUF file |
| `params` | `struct llama_model_params` | Parameters for model loading | Struct returned by `llama_model_default_params` with optional modifications |

#### Description

Loads a model from a file. This function takes a path to a GGUF model file and a set of parameters, and returns a pointer to a `llama_model` struct that can be used for inference. If the file is split into multiple parts, the file name must follow a specific pattern: `<name>-%05d-of-%05d.gguf`. If the split file name does not follow this pattern, use `llama_model_load_from_splits` instead.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load dynamic backends
    ggml_backend_load_all();

    // Get default model parameters
    struct llama_model_params params = llama_model_default_params();

    // Modify parameters as needed
    params.n_gpu_layers = 32; // Offload 32 layers to GPU

    // Load the model
    struct llama_model * model = llama_model_load_from_file("path/to/model.gguf", params);

    if (model == NULL) {
        fprintf(stderr, "Failed to load model\n");
        return 1;
    }

    // Use the model...

    // Clean up
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from simple.cpp:**

```c
// Load dynamic backends
ggml_backend_load_all();

// Initialize the model
llama_model_params model_params = llama_model_default_params();
model_params.n_gpu_layers = ngl;

llama_model * model = llama_model_load_from_file(model_path.c_str(), model_params);

if (model == NULL) {
    fprintf(stderr , "%s: error: unable to load model\n" , __func__);
    return 1;
}

// Get the vocabulary from the model
const llama_vocab * vocab = llama_model_get_vocab(model);
```

**Example from batched.cpp:**

```c
// Initialize the backend
llama_backend_init();
llama_numa_init(params.numa);

// Initialize the model
llama_model_params model_params = common_model_params_to_llama(params);
llama_model * model = llama_model_load_from_file(params.model.path.c_str(), model_params);

if (model == NULL) {
    LOG_ERR("%s: error: unable to load model\n" , __func__);
    return 1;
}

const llama_vocab * vocab = llama_model_get_vocab(model);
```

#### Important Notes

1. You must call `llama_backend_init()` before loading a model.
2. For GPU acceleration, you should also call `ggml_backend_load_all()` to load all available backends.
3. The function returns `NULL` if the model cannot be loaded, so always check the return value.
4. The model must be freed with `llama_model_free()` when no longer needed.
5. For large models, loading can take a significant amount of time and memory.
6. The `n_gpu_layers` parameter in `llama_model_params` controls how many layers are offloaded to the GPU. Setting this to a higher value can improve performance but requires more VRAM.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_default_params`](#llama_model_default_params) | Get default model parameters | Provides default parameters for `llama_model_load_from_file` |
| [`llama_model_load_from_splits`](#llama_model_load_from_splits) | Load a model from multiple splits | Alternative to `llama_model_load_from_file` for split models |
| [`llama_model_free`](#llama_model_free) | Free a model | Must be called to free the model loaded by `llama_model_load_from_file` |
| [`llama_model_get_vocab`](#llama_model_get_vocab) | Get the vocabulary from a model | Used to get the vocabulary from a loaded model |
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend | Must be called before `llama_model_load_from_file` |

### llama_model_load_from_splits

#### Function Signature

```c
LLAMA_API struct llama_model * llama_model_load_from_splits(
                         const char ** paths,
                             size_t    n_paths,
          struct llama_model_params    params);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `paths` | `const char **` | Array of paths to the model split files | Array of paths to GGUF files |
| `n_paths` | `size_t` | Number of paths in the array | Number of split files |
| `params` | `struct llama_model_params` | Parameters for model loading | Struct returned by `llama_model_default_params` with optional modifications |

#### Description

Loads a model from multiple split files. This function is useful when a model has been split into multiple files that don't follow the standard naming pattern (`<name>-%05d-of-%05d.gguf`). It takes an array of paths to the split files, the number of paths, and a set of parameters, and returns a pointer to a `llama_model` struct that can be used for inference.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load dynamic backends
    ggml_backend_load_all();

    // Get default model parameters
    struct llama_model_params params = llama_model_default_params();

    // Modify parameters as needed
    params.n_gpu_layers = 32; // Offload 32 layers to GPU

    // Define paths to split files
    const char * paths[] = {
        "path/to/model-part1.gguf",
        "path/to/model-part2.gguf",
        "path/to/model-part3.gguf"
    };

    // Load the model from splits
    struct llama_model * model = llama_model_load_from_splits(paths, 3, params);

    if (model == NULL) {
        fprintf(stderr, "Failed to load model from splits\n");
        return 1;
    }

    // Use the model...

    // Clean up
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

#### Important Notes

1. You must call `llama_backend_init()` before loading a model.
2. For GPU acceleration, you should also call `ggml_backend_load_all()` to load all available backends.
3. The function returns `NULL` if the model cannot be loaded, so always check the return value.
4. The model must be freed with `llama_model_free()` when no longer needed.
5. The first path in the array is used as the main path for the model, and the other paths are used as splits.
6. This function is particularly useful when the split files don't follow the standard naming pattern that `llama_model_load_from_file` expects.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_default_params`](#llama_model_default_params) | Get default model parameters | Provides default parameters for `llama_model_load_from_splits` |
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Alternative to `llama_model_load_from_splits` for single files or standard split patterns |
| [`llama_model_free`](#llama_model_free) | Free a model | Must be called to free the model loaded by `llama_model_load_from_splits` |
| [`llama_backend_init`](#llama_backend_init) | Initialize the llama + ggml backend | Must be called before `llama_model_load_from_splits` |

### llama_model_save_to_file

#### Function Signature

```c
LLAMA_API void llama_model_save_to_file(
        const struct llama_model * model,
                    const char * path_model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model to save | Valid model pointer |
| `path_model` | `const char *` | Path where the model will be saved | Path to a GGUF file |

#### Description

Saves a model to a file. This function takes a pointer to a `llama_model` struct and a path, and saves the model to the specified file in GGUF format. This is useful for saving a model after fine-tuning or other modifications.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load dynamic backends
    ggml_backend_load_all();

    // Load a model
    struct llama_model_params params = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("path/to/model.gguf", params);

    if (model == NULL) {
        fprintf(stderr, "Failed to load model\n");
        return 1;
    }

    // Modify the model (e.g., fine-tune it)
    // ...

    // Save the modified model
    llama_model_save_to_file(model, "path/to/modified_model.gguf");

    // Clean up
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from finetune.cpp:**

```c
// Load the model and apply lora adapter, if any
common_init_result llama_init = common_init_from_params(params);
llama_model_ptr   & model = llama_init.model;
llama_context_ptr & ctx   = llama_init.context;

// Initialize optimizer and train the model
llama_opt_init(ctx.get(), model.get(), lopt_params);

// Train for multiple epochs
for (int epoch = 0; epoch < 2; ++epoch) {
    llama_opt_epoch(ctx.get(), dataset, result_train, result_eval, idata_split,
        ggml_opt_epoch_callback_progress_bar, ggml_opt_epoch_callback_progress_bar);
    // ...
}

// Save the finetuned model
llama_model_save_to_file(model.get(), "finetuned-model.gguf");
```

#### Important Notes

1. The model must be a valid `llama_model` pointer, typically obtained from `llama_model_load_from_file` or `llama_model_load_from_splits`.
2. The function will overwrite any existing file at the specified path.
3. The saved model will be in GGUF format, which is the standard format for llama.cpp models.
4. This function is particularly useful after fine-tuning a model, to save the updated weights.
5. The function does not return a value, so there's no direct way to check if the save was successful.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Can be used to load a model that was saved with `llama_model_save_to_file` |
| [`llama_model_load_from_splits`](#llama_model_load_from_splits) | Load a model from multiple splits | Can be used to load a model that was saved with `llama_model_save_to_file` and then split |
| [`llama_model_free`](#llama_model_free) | Free a model | Should be called after using the model, regardless of whether it was saved |

### llama_model_free

#### Function Signature

```c
LLAMA_API void llama_model_free(struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `struct llama_model *` | Pointer to the model to free | Valid model pointer |

#### Description

Frees the memory allocated for a model. This function should be called when a model is no longer needed to prevent memory leaks. It deallocates all resources associated with the model, including the model weights, vocabulary, and any other data structures.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params params = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("path/to/model.gguf", params);

    if (model == NULL) {
        fprintf(stderr, "Failed to load model\n");
        return 1;
    }

    // Use the model...

    // Free the model when done
    llama_model_free(model);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example from simple.cpp:**

```c
// Load the model
llama_model_params model_params = llama_model_default_params();
model_params.n_gpu_layers = ngl;

llama_model * model = llama_model_load_from_file(model_path.c_str(), model_params);

if (model == NULL) {
    fprintf(stderr , "%s: error: unable to load model\n" , __func__);
    return 1;
}

// Use the model...

// Free resources when done
llama_free(ctx);
llama_model_free(model);
llama_backend_free();
```

**Example from batched.cpp:**

```c
// Initialize the model
llama_model_params model_params = common_model_params_to_llama(params);
llama_model * model = llama_model_load_from_file(params.model.path.c_str(), model_params);

// Use the model...

// Clean up
llama_sampler_free(smpl);
llama_free(ctx);
llama_model_free(model);
llama_batch_free(batch);
llama_backend_free();
```

#### Important Notes

1. Always call `llama_model_free()` when you're done with a model to prevent memory leaks.
2. Make sure to free any contexts created from the model with `llama_free()` before freeing the model.
3. After calling `llama_model_free()`, the model pointer becomes invalid and should not be used.
4. This function is safe to call with a NULL pointer (it will do nothing in that case).
5. The function does not return a value, so there's no direct way to check if the free was successful.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_load_from_file`](#llama_model_load_from_file) | Load a model from a file | Creates a model that should be freed with `llama_model_free` |
| [`llama_model_load_from_splits`](#llama_model_load_from_splits) | Load a model from multiple splits | Creates a model that should be freed with `llama_model_free` |
| [`llama_free`](#llama_free) | Free a context | Should be called on any contexts created from the model before calling `llama_model_free` |
| [`llama_backend_free`](#llama_backend_free) | Free resources used by the backend | Should be called after freeing all models and contexts |

### llama_model_get_vocab

#### Function Signature

```c
LLAMA_API const struct llama_vocab * llama_model_get_vocab(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns a pointer to the vocabulary of the model. The vocabulary contains information about all tokens in the model, including their text representations, scores, and attributes. This function is typically used to access the vocabulary for tokenization, detokenization, and other operations that require knowledge of the model's tokens.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params params = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("path/to/model.gguf", params);

    if (model == NULL) {
        fprintf(stderr, "Failed to load model\n");
        return 1;
    }

    // Get the vocabulary
    const struct llama_vocab * vocab = llama_model_get_vocab(model);

    // Use the vocabulary for tokenization
    const char * text = "Hello, world!";
    std::vector<llama_token> tokens(32);
    int n_tokens = llama_tokenize(vocab, text, -1, tokens.data(), tokens.size(), true, false);

    // Print the tokens
    for (int i = 0; i < n_tokens; i++) {
        printf("Token %d: %s\n", i, llama_vocab_get_text(vocab, tokens[i]));
    }

    // Clean up
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example from simple.cpp:**

```c
// Load the model
llama_model * model = llama_model_load_from_file(model_path.c_str(), model_params);

if (model == NULL) {
    fprintf(stderr , "%s: error: unable to load model\n" , __func__);
    return 1;
}

// Get the vocabulary
const llama_vocab * vocab = llama_model_get_vocab(model);

// Tokenize the prompt
const int n_prompt = -llama_tokenize(vocab, prompt.c_str(), prompt.size(), NULL, 0, true, true);
std::vector<llama_token> prompt_tokens(n_prompt);
llama_tokenize(vocab, prompt.c_str(), prompt.size(), prompt_tokens.data(), prompt_tokens.size(), true, true);
```

**Example from embedding.cpp:**

```c
// Load the model
common_init_result llama_init = common_init_from_params(params);
llama_model * model = llama_init.model.get();
llama_context * ctx = llama_init.context.get();

// Get the vocabulary
const llama_vocab * vocab = llama_model_get_vocab(model);

// Check if the last token is SEP
for (auto & inp : inputs) {
    if (inp.empty() || inp.back() != llama_vocab_sep(vocab)) {
        LOG_WRN("%s: last token in the prompt is not SEP\n", __func__);
        LOG_WRN("%s: 'tokenizer.ggml.add_eos_token' should be set to 'true' in the GGUF header\n", __func__);
    }
}
```

#### Important Notes

1. The returned vocabulary pointer is owned by the model and should not be freed separately.
2. The vocabulary pointer becomes invalid when the model is freed with `llama_model_free()`.
3. The vocabulary is read-only and should not be modified.
4. This function is typically used in conjunction with tokenization functions like `llama_tokenize()` and vocabulary query functions like `llama_vocab_get_text()`.
5. The function returns a const pointer, emphasizing that the vocabulary should not be modified.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_tokenize`](#llama_tokenize) | Convert text into tokens | Uses the vocabulary returned by `llama_model_get_vocab` |
| [`llama_detokenize`](#llama_detokenize) | Convert tokens into text | Uses the vocabulary returned by `llama_model_get_vocab` |
| [`llama_vocab_get_text`](#llama_vocab_get_text) | Get the text for a token | Uses the vocabulary returned by `llama_model_get_vocab` |
| [`llama_vocab_get_score`](#llama_vocab_get_score) | Get the score for a token | Uses the vocabulary returned by `llama_model_get_vocab` |
| [`llama_vocab_get_attr`](#llama_vocab_get_attr) | Get the attributes for a token | Uses the vocabulary returned by `llama_model_get_vocab` |
| [`llama_vocab_is_eog`](#llama_vocab_is_eog) | Check if a token is an end-of-generation token | Uses the vocabulary returned by `llama_model_get_vocab` |

### llama_model_n_ctx_train

#### Function Signature

```c
LLAMA_API int32_t llama_model_n_ctx_train(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns the context size (number of tokens) that was used during the training of the model. This information is useful for understanding the model's capabilities and limitations, as it indicates the maximum sequence length the model was designed to handle during training.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    if (model) {
        // Get the context size used during training
        int32_t n_ctx_train = llama_model_n_ctx_train(model);
        printf("Model was trained with context size: %d\n", n_ctx_train);

        // Free the model
        llama_model_free(model);
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example for Setting Inference Context Size:**

```c
// Load the model
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

if (model) {
    // Get the training context size
    int32_t n_ctx_train = llama_model_n_ctx_train(model);

    // Set up context parameters based on training context size
    struct llama_context_params cparams = llama_context_default_params();

    // Use the training context size as a starting point
    // You might want to use a smaller value for efficiency or a larger value if needed
    cparams.n_ctx = n_ctx_train;

    // Create the context
    struct llama_context * ctx = llama_new_context_with_model(model, cparams);

    // Use the context for inference...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
}
```

#### Important Notes

1. The training context size is a property of the model and cannot be changed after training.
2. This value is often used as a reference when setting the inference context size (`n_ctx` parameter).
3. Using a context size larger than what the model was trained with may result in degraded performance for tokens beyond the training context size.
4. Some models can handle longer contexts than their training context size through techniques like position interpolation, but performance may vary.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_default_params`](#llama_model_default_params) | Get default model parameters | Used when loading a model before querying its properties |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Often uses `n_ctx_train` as a reference for setting `n_ctx` |

### llama_model_n_embd

#### Function Signature

```c
LLAMA_API int32_t llama_model_n_embd(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns the embedding size (dimension) of the model. This is the size of the hidden state vectors used throughout the model and is a fundamental property that determines the model's capacity and computational requirements.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    if (model) {
        // Get the embedding size
        int32_t n_embd = llama_model_n_embd(model);
        printf("Model embedding size: %d\n", n_embd);

        // Free the model
        llama_model_free(model);
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example for Memory Estimation:**

```c
// Load the model
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

if (model) {
    // Get model properties
    int32_t n_embd = llama_model_n_embd(model);
    int32_t n_layer = llama_model_n_layer(model);
    int32_t n_ctx = llama_model_n_ctx_train(model);

    // Rough estimation of memory requirements (simplified)
    size_t memory_per_token = n_embd * sizeof(float);
    size_t kv_cache_size = 2 * n_layer * n_ctx * memory_per_token;

    printf("Estimated KV cache size for context length %d: %.2f MB\n",
           n_ctx, kv_cache_size / (1024.0 * 1024.0));

    // Clean up
    llama_model_free(model);
}
```

#### Important Notes

1. The embedding size is a fixed property of the model architecture and cannot be changed after training.
2. Common embedding sizes range from 768 (for small models) to 8192 or more (for very large models).
3. The embedding size affects both the model's capacity and its computational requirements.
4. This value is useful for memory estimation, especially for calculating the size of the KV cache.
5. The embedding size is sometimes referred to as the "hidden size" or "model dimension" in academic literature.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_n_layer`](#llama_model_n_layer) | Get the number of layers | Often used together with `n_embd` for model size estimation |
| [`llama_model_n_head`](#llama_model_n_head) | Get the number of attention heads | Related to `n_embd` as typically `n_embd` is divisible by `n_head` |
| [`llama_model_n_params`](#llama_model_n_params) | Get the total number of parameters | Depends on `n_embd` among other factors |
| [`llama_get_embeddings`](#llama_get_embeddings) | Get token embeddings | Returns vectors of size `n_embd` |

### llama_model_n_layer

#### Function Signature

```c
LLAMA_API int32_t llama_model_n_layer(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns the number of layers (transformer blocks) in the model. The number of layers is a key architectural parameter that determines the depth of the model and significantly impacts its capacity, computational requirements, and performance.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    if (model) {
        // Get the number of layers
        int32_t n_layer = llama_model_n_layer(model);
        printf("Model has %d layers\n", n_layer);

        // Free the model
        llama_model_free(model);
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example for GPU Offloading Configuration:**

```c
// Load the model with GPU offloading
struct llama_model_params mparams = llama_model_default_params();

// Check if GPU offloading is supported
if (llama_supports_gpu_offload()) {
    // Get the model to check its properties first
    struct llama_model * model_check = llama_model_load_from_file("model.gguf", mparams);

    if (model_check) {
        // Get the number of layers
        int32_t n_layer = llama_model_n_layer(model_check);

        // Free the model used for checking
        llama_model_free(model_check);

        // Decide how many layers to offload to GPU
        // For example, offload half of the layers
        mparams.n_gpu_layers = n_layer / 2;

        // Now load the model with GPU offloading
        struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

        if (model) {
            printf("Model loaded with %d layers offloaded to GPU\n", mparams.n_gpu_layers);

            // Use the model...

            // Free the model
            llama_model_free(model);
        }
    }
}
```

#### Important Notes

1. The number of layers is a fixed property of the model architecture and cannot be changed after training.
2. Common layer counts range from 12 (for small models) to 80 or more (for very large models).
3. The computational complexity and memory requirements of the model generally scale linearly with the number of layers.
4. When using GPU offloading, the `n_gpu_layers` parameter determines how many layers are offloaded to the GPU.
5. The number of layers is a key factor in determining the model's inference speed and memory usage.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_n_embd`](#llama_model_n_embd) | Get the embedding size | Often used together with `n_layer` for model size estimation |
| [`llama_model_n_params`](#llama_model_n_params) | Get the total number of parameters | Depends on `n_layer` among other factors |
| [`llama_model_default_params`](#llama_model_default_params) | Get default model parameters | Used to set `n_gpu_layers` based on the model's layer count |
| [`llama_supports_gpu_offload`](#llama_supports_gpu_offload) | Check if GPU offloading is supported | Relevant when deciding whether to offload layers to GPU |

### llama_model_n_head

#### Function Signature

```c
LLAMA_API int32_t llama_model_n_head(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns the number of attention heads in the model. Attention heads are a key component of the transformer architecture, allowing the model to focus on different parts of the input simultaneously. The number of attention heads affects how the model processes information and its ability to capture different types of relationships in the data.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    if (model) {
        // Get the number of attention heads
        int32_t n_head = llama_model_n_head(model);
        printf("Model has %d attention heads\n", n_head);

        // Free the model
        llama_model_free(model);
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example for Model Architecture Analysis:**

```c
// Load the model
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

if (model) {
    // Get model architecture properties
    int32_t n_embd = llama_model_n_embd(model);
    int32_t n_head = llama_model_n_head(model);
    int32_t n_head_kv = llama_model_n_head_kv(model);

    // Calculate head dimensions
    int32_t head_dim = n_embd / n_head;

    printf("Model architecture:\n");
    printf("- Embedding size: %d\n", n_embd);
    printf("- Number of attention heads: %d\n", n_head);
    printf("- Number of key-value heads: %d\n", n_head_kv);
    printf("- Dimension per head: %d\n", head_dim);
    printf("- Multi-query attention: %s\n", (n_head > n_head_kv) ? "Yes" : "No");

    // Clean up
    llama_model_free(model);
}
```

#### Important Notes

1. The number of attention heads is a fixed property of the model architecture and cannot be changed after training.
2. Common values range from 12 (for small models) to 64 or more (for very large models).
3. The embedding size (`n_embd`) is typically divisible by the number of heads, with each head having dimension `n_embd / n_head`.
4. Modern architectures may use different numbers of heads for queries vs. keys/values (multi-query or grouped-query attention), which is why `n_head_kv` may differ from `n_head`.
5. The number of attention heads affects the model's ability to capture different types of relationships in parallel.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_n_embd`](#llama_model_n_embd) | Get the embedding size | Related to `n_head` as typically `n_embd` is divisible by `n_head` |
| [`llama_model_n_head_kv`](#llama_model_n_head_kv) | Get the number of key-value heads | May differ from `n_head` in models using multi-query attention |
| [`llama_model_n_layer`](#llama_model_n_layer) | Get the number of layers | Each layer contains attention heads |
| [`llama_model_rope_type`](#llama_model_rope_type) | Get the RoPE type | Affects how positional information is incorporated into attention |

### llama_model_n_head_kv

#### Function Signature

```c
LLAMA_API int32_t llama_model_n_head_kv(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns the number of key-value heads in the model. In traditional transformer architectures, the number of key-value heads is the same as the number of query heads. However, modern architectures like multi-query attention (MQA) or grouped-query attention (GQA) use fewer key-value heads than query heads to reduce memory usage and computational requirements while maintaining performance.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    if (model) {
        // Get the number of key-value heads
        int32_t n_head_kv = llama_model_n_head_kv(model);
        printf("Model has %d key-value heads\n", n_head_kv);

        // Free the model
        llama_model_free(model);
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example for Detecting Multi-Query Attention:**

```c
// Load the model
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

if (model) {
    // Get the number of query heads and key-value heads
    int32_t n_head = llama_model_n_head(model);
    int32_t n_head_kv = llama_model_n_head_kv(model);

    // Determine the attention type
    const char* attention_type;
    if (n_head == n_head_kv) {
        attention_type = "Standard Attention";
    } else if (n_head_kv == 1) {
        attention_type = "Multi-Query Attention (MQA)";
    } else {
        attention_type = "Grouped-Query Attention (GQA)";
    }

    printf("Attention configuration:\n");
    printf("- Query heads: %d\n", n_head);
    printf("- Key-Value heads: %d\n", n_head_kv);
    printf("- Attention type: %s\n", attention_type);
    printf("- KV grouping factor: %d\n", n_head / n_head_kv);

    // Clean up
    llama_model_free(model);
}
```

#### Important Notes

1. The number of key-value heads is a fixed property of the model architecture and cannot be changed after training.
2. In traditional transformer models, `n_head_kv` equals `n_head`.
3. In multi-query attention (MQA), `n_head_kv` is 1, meaning all query heads share the same key-value pairs.
4. In grouped-query attention (GQA), `n_head_kv` is less than `n_head`, with multiple query heads sharing each key-value head.
5. Models with fewer key-value heads than query heads typically use less memory for the KV cache, which can be significant for long context lengths.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_n_head`](#llama_model_n_head) | Get the number of attention heads | Compared with `n_head_kv` to determine the attention architecture |
| [`llama_model_n_embd`](#llama_model_n_embd) | Get the embedding size | Related to both `n_head` and `n_head_kv` for calculating head dimensions |
| [`llama_model_size`](#llama_model_size) | Get the total size of all tensors | KV head count affects model size and memory usage |

### llama_model_rope_freq_scale_train

#### Function Signature

```c
LLAMA_API float llama_model_rope_freq_scale_train(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns the RoPE (Rotary Positional Embedding) frequency scaling factor that was used during the training of the model. RoPE is a technique used in transformer models to encode positional information directly into the attention mechanism. The frequency scaling factor affects how positional information is encoded and can impact the model's ability to handle sequences of different lengths.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    if (model) {
        // Get the RoPE frequency scaling factor
        float rope_freq_scale = llama_model_rope_freq_scale_train(model);
        printf("Model RoPE frequency scaling factor: %f\n", rope_freq_scale);

        // Free the model
        llama_model_free(model);
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example for Context Length Extension:**

```c
// Load the model
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

if (model) {
    // Get model properties
    int32_t n_ctx_train = llama_model_n_ctx_train(model);
    float rope_freq_scale = llama_model_rope_freq_scale_train(model);

    // Set up context parameters for inference
    struct llama_context_params cparams = llama_context_default_params();

    // Example: Extend context length beyond training length
    int32_t target_ctx_len = 8192;  // Target context length

    if (target_ctx_len > n_ctx_train) {
        // Adjust RoPE frequency scaling to handle longer contexts
        // This is a simplified example - actual scaling methods may vary
        cparams.rope_freq_scale = rope_freq_scale * ((float)n_ctx_train / target_ctx_len);
        printf("Adjusting RoPE scaling from %f to %f for context extension\n",
               rope_freq_scale, cparams.rope_freq_scale);
    } else {
        // Use the original scaling
        cparams.rope_freq_scale = rope_freq_scale;
    }

    cparams.n_ctx = target_ctx_len;

    // Create the context with adjusted parameters
    struct llama_context * ctx = llama_init_from_model(model, cparams);

    // Use the context for inference...

    // Clean up
    llama_free(ctx);
    llama_model_free(model);
}
```

#### Important Notes

1. The RoPE frequency scaling factor is a property set during model training and stored in the model file.
2. A value of 1.0 indicates standard RoPE scaling as described in the original papers.
3. Some models use different scaling factors to better handle certain sequence lengths or to improve performance.
4. When extending context length beyond what the model was trained on, adjusting the RoPE frequency scaling can help maintain performance.
5. This value is used in conjunction with other RoPE parameters like `rope_freq_base` to determine the final positional encoding behavior.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_n_ctx_train`](#llama_model_n_ctx_train) | Get the context size used during training | Often used together with RoPE parameters for context length extension |
| [`llama_model_rope_type`](#llama_model_rope_type) | Get the RoPE type | Indicates which RoPE implementation is used by the model |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Contains RoPE parameters that can be adjusted based on the training values |

### llama_model_rope_type

#### Function Signature

```c
LLAMA_API enum llama_rope_type llama_model_rope_type(const struct llama_model * model);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `model` | `const struct llama_model *` | Pointer to the model | Valid model pointer |

#### Description

Returns the type of Rotary Positional Embedding (RoPE) used by the model. RoPE is a technique used in transformer models to encode positional information directly into the attention mechanism. Different implementations of RoPE exist, each with its own characteristics and performance implications.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load a model
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    if (model) {
        // Get the RoPE type
        enum llama_rope_type rope_type = llama_model_rope_type(model);

        // Convert to string for display
        const char* rope_type_str;
        switch (rope_type) {
            case LLAMA_ROPE_TYPE_NONE:
                rope_type_str = "None";
                break;
            case LLAMA_ROPE_TYPE_NORM:
                rope_type_str = "Normal";
                break;
            case LLAMA_ROPE_TYPE_NEOX:
                rope_type_str = "NeoX";
                break;
            case LLAMA_ROPE_TYPE_MROPE:
                rope_type_str = "MRoPE";
                break;
            case LLAMA_ROPE_TYPE_VISION:
                rope_type_str = "Vision";
                break;
            default:
                rope_type_str = "Unknown";
        }

        printf("Model uses RoPE type: %s\n", rope_type_str);

        // Free the model
        llama_model_free(model);
    }

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example for Model Architecture Analysis:**

```c
// Load the model
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

if (model) {
    // Get RoPE configuration
    enum llama_rope_type rope_type = llama_model_rope_type(model);
    float rope_freq_scale = llama_model_rope_freq_scale_train(model);

    printf("RoPE configuration:\n");

    // Print RoPE type
    printf("- Type: ");
    switch (rope_type) {
        case LLAMA_ROPE_TYPE_NONE:
            printf("None (No positional embeddings)\n");
            break;
        case LLAMA_ROPE_TYPE_NORM:
            printf("Normal (Standard RoPE implementation)\n");
            break;
        case LLAMA_ROPE_TYPE_NEOX:
            printf("NeoX (GPT-NeoX style implementation)\n");
            break;
        case LLAMA_ROPE_TYPE_MROPE:
            printf("MRoPE (Multi-resolution RoPE)\n");
            break;
        case LLAMA_ROPE_TYPE_VISION:
            printf("Vision (RoPE for vision models)\n");
            break;
        default:
            printf("Unknown (%d)\n", rope_type);
    }

    printf("- Frequency scale: %f\n", rope_freq_scale);

    // Clean up
    llama_model_free(model);
}
```

#### Important Notes

1. The RoPE type is a fixed property of the model architecture and cannot be changed after training.
2. Different RoPE implementations may have different performance characteristics and capabilities:
   - `LLAMA_ROPE_TYPE_NONE`: No rotary positional embeddings are used.
   - `LLAMA_ROPE_TYPE_NORM`: Standard RoPE implementation as described in the original paper.
   - `LLAMA_ROPE_TYPE_NEOX`: GPT-NeoX style implementation with slight differences from the standard.
   - `LLAMA_ROPE_TYPE_MROPE`: Multi-resolution RoPE, which can better handle longer contexts.
   - `LLAMA_ROPE_TYPE_VISION`: RoPE implementation designed for vision models.
3. The RoPE type affects how positional information is encoded and can impact the model's ability to handle sequences of different lengths.
4. When implementing context length extension techniques, it's important to consider the specific RoPE type used by the model.
5. The RoPE type is defined in the `llama_rope_type` enum in the llama.h header file.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_model_rope_freq_scale_train`](#llama_model_rope_freq_scale_train) | Get the RoPE frequency scaling factor | Used together with RoPE type to understand the model's positional encoding |
| [`llama_model_n_ctx_train`](#llama_model_n_ctx_train) | Get the context size used during training | Related to RoPE configuration for context length considerations |
| [`llama_context_default_params`](#llama_context_default_params) | Get default context parameters | Contains RoPE parameters that can be adjusted based on the model's RoPE type |

### llama_sampler_init

#### Function Signature

```c
LLAMA_API struct llama_sampler * llama_sampler_init(const struct llama_sampler_i * iface, llama_sampler_context_t ctx);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `iface` | `const struct llama_sampler_i *` | Interface containing function pointers for the sampler implementation | Custom implementation or one of the built-in samplers |
| `ctx` | `llama_sampler_context_t` | Context data for the sampler | Sampler-specific context data |

#### Description

Initializes a new sampler with the provided interface and context. This is a low-level function that is typically used to create custom samplers. For most use cases, you should use one of the specialized initialization functions like `llama_sampler_init_top_k`, `llama_sampler_init_top_p`, etc.

The `llama_sampler_i` interface defines the behavior of the sampler through function pointers. The `ctx` parameter provides the sampler-specific context data that will be used by these functions.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

// Define custom sampler interface
struct llama_sampler_i custom_sampler_iface = {
    .name = my_sampler_name,
    .accept = my_sampler_accept,
    .apply = my_sampler_apply,
    .reset = my_sampler_reset,
    .clone = my_sampler_clone,
    .free = my_sampler_free
};

// Create custom sampler context
my_sampler_context_t * my_ctx = my_sampler_context_create();

// Initialize the sampler
struct llama_sampler * sampler = llama_sampler_init(&custom_sampler_iface, my_ctx);

// Use the sampler
// ...

// Free the sampler
llama_sampler_free(sampler);
```

**Example with Built-in Samplers:**

```c
// It's more common to use the specialized initialization functions
struct llama_sampler * top_k_sampler = llama_sampler_init_top_k(40);
struct llama_sampler * top_p_sampler = llama_sampler_init_top_p(0.9, 1);
struct llama_sampler * temp_sampler = llama_sampler_init_temp(0.8);

// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add samplers to the chain
llama_sampler_chain_add(chain, top_k_sampler);
llama_sampler_chain_add(chain, top_p_sampler);
llama_sampler_chain_add(chain, temp_sampler);

// Use the sampler chain
// ...

// Free the sampler chain (this will free all added samplers)
llama_sampler_free(chain);
```

#### Important Notes

1. The `llama_sampler_i` interface must be fully initialized with valid function pointers before calling this function.
2. The `apply` function in the interface is required, while other functions can be NULL if not needed.
3. The sampler takes ownership of the context and will free it when `llama_sampler_free` is called, unless the context is NULL.
4. For most use cases, you should use one of the specialized initialization functions instead of this low-level function.
5. When adding a sampler to a chain with `llama_sampler_chain_add`, the chain takes ownership of the sampler and will free it when the chain is freed.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_free`](#llama_sampler_free) | Free a sampler | Cleanup function for samplers created with `llama_sampler_init` |
| [`llama_sampler_apply`](#llama_sampler_apply) | Apply a sampler to token data | Core function that implements the sampling logic |
| [`llama_sampler_chain_init`](#llama_sampler_chain_init) | Initialize a sampler chain | Used to create a chain of samplers |
| [`llama_sampler_chain_add`](#llama_sampler_chain_add) | Add a sampler to a chain | Used to add a sampler to a chain |

### llama_sampler_name

#### Function Signature

```c
LLAMA_API const char * llama_sampler_name(const struct llama_sampler * smpl);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `smpl` | `const struct llama_sampler *` | Pointer to the sampler | Valid sampler created with one of the `llama_sampler_init_*` functions |

#### Description

Returns the name of the sampler. This function is useful for debugging and logging purposes, as it allows you to identify which sampler is being used. The name is provided by the sampler's implementation through the `name` function in the `llama_sampler_i` interface.

If the sampler's interface does not provide a `name` function (i.e., the function pointer is NULL), this function will return NULL.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create some samplers
    struct llama_sampler * top_k = llama_sampler_init_top_k(40);
    struct llama_sampler * top_p = llama_sampler_init_top_p(0.9, 1);
    struct llama_sampler * temp = llama_sampler_init_temp(0.8);

    // Get and print the names of the samplers
    printf("Sampler 1: %s\n", llama_sampler_name(top_k));
    printf("Sampler 2: %s\n", llama_sampler_name(top_p));
    printf("Sampler 3: %s\n", llama_sampler_name(temp));

    // Free the samplers
    llama_sampler_free(top_k);
    llama_sampler_free(top_p);
    llama_sampler_free(temp);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example with Sampler Chain:**

```c
// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add samplers to the chain
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
llama_sampler_chain_add(chain, llama_sampler_init_temp(0.8));

// Get and print the name of the chain
printf("Chain name: %s\n", llama_sampler_name(chain));

// Get and print the names of the samplers in the chain
int n = llama_sampler_chain_n(chain);
for (int i = 0; i < n; i++) {
    struct llama_sampler * smpl = llama_sampler_chain_get(chain, i);
    printf("Sampler %d: %s\n", i, llama_sampler_name(smpl));
}

// Free the chain (this will free all added samplers)
llama_sampler_free(chain);
```

#### Important Notes

1. This function is primarily useful for debugging and logging purposes.
2. The returned string is owned by the sampler and should not be freed by the caller.
3. The function may return NULL if the sampler's interface does not provide a `name` function.
4. For sampler chains, the name will typically be "chain" or similar, and you can use `llama_sampler_chain_get` to access the individual samplers in the chain.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_init`](#llama_sampler_init) | Initialize a sampler | Creates a sampler that can be named |
| [`llama_sampler_chain_get`](#llama_sampler_chain_get) | Get a sampler from a chain | Used to access individual samplers in a chain |
| [`llama_sampler_chain_n`](#llama_sampler_chain_n) | Get the number of samplers in a chain | Used to iterate over samplers in a chain |

### llama_sampler_accept

#### Function Signature

```c
LLAMA_API void llama_sampler_accept(struct llama_sampler * smpl, llama_token token);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `smpl` | `struct llama_sampler *` | Pointer to the sampler | Valid sampler created with one of the `llama_sampler_init_*` functions |
| `token` | `llama_token` | The token that was selected | Any valid token ID |

#### Description

Informs the sampler that a specific token has been selected and accepted. This allows the sampler to update its internal state based on the selected token. For example, a repetition penalty sampler would use this information to track which tokens have been generated and apply penalties to them in future sampling operations.

This function is particularly important for samplers that maintain state across multiple sampling operations, such as repetition penalty, grammar-based sampling, or mirostat.

If the sampler's interface does not provide an `accept` function (i.e., the function pointer is NULL), this function does nothing.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    struct llama_context_params cparams = llama_context_default_params();
    struct llama_context * ctx = llama_init_from_model(model, cparams);

    // Create a sampler chain
    struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
    struct llama_sampler * chain = llama_sampler_chain_init(sparams);

    // Add samplers to the chain
    llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
    llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
    llama_sampler_chain_add(chain, llama_sampler_init_temp(0.8));
    llama_sampler_chain_add(chain, llama_sampler_init_penalties(64, 1.1, 0.0, 0.0));
    llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

    // Tokenize input
    const char * prompt = "Once upon a time";
    llama_token tokens[32];
    int n_tokens = llama_tokenize(llama_model_get_vocab(model), prompt, -1, tokens, 32, true, false);

    // Create a batch
    struct llama_batch batch = llama_batch_init(n_tokens, 0, 1);
    for (int i = 0; i < n_tokens; i++) {
        batch.token[i] = tokens[i];
        batch.pos[i] = i;
        batch.n_seq_id[i] = 1;
        batch.seq_id[i][0] = 0;
        batch.logits[i] = i == n_tokens - 1 ? 1 : 0;  // Only compute logits for the last token
    }

    // Process the batch
    llama_decode(ctx, batch);

    // Generate tokens
    for (int i = 0; i < 10; i++) {
        // Sample a token
        llama_token new_token = llama_sampler_sample(chain, ctx, -1);

        // Accept the token (update sampler state)
        llama_sampler_accept(chain, new_token);

        // Print the token text
        char token_text[32];
        llama_token_to_piece(llama_model_get_vocab(model), new_token, token_text, sizeof(token_text), 0, true);
        printf("%s", token_text);

        // Prepare batch for next token
        batch = llama_batch_init(1, 0, 1);
        batch.token[0] = new_token;
        batch.pos[0] = n_tokens + i;
        batch.n_seq_id[0] = 1;
        batch.seq_id[0][0] = 0;
        batch.logits[0] = 1;

        // Process the batch
        llama_decode(ctx, batch);

        // Free the batch
        llama_batch_free(batch);
    }

    // Clean up
    llama_sampler_free(chain);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example with Grammar Sampler:**

```c
// Create a grammar sampler
const char * grammar_str = "root ::= (\"a\" | \"b\" | \"c\")+";
struct llama_sampler * grammar = llama_sampler_init_grammar(llama_model_get_vocab(model), grammar_str, "root");

// Sample tokens
for (int i = 0; i < 10; i++) {
    llama_token token = llama_sampler_sample(grammar, ctx, -1);

    // Accept the token to update the grammar state
    llama_sampler_accept(grammar, token);

    // Process the token...
}

// Free the sampler
llama_sampler_free(grammar);
```

#### Important Notes

1. This function must be called after sampling a token to ensure that the sampler's internal state is updated correctly.
2. For samplers that don't maintain state (e.g., temperature, top-k, top-p), this function has no effect.
3. For sampler chains, calling this function on the chain will propagate the call to all samplers in the chain.
4. The `llama_sampler_sample` function already calls `llama_sampler_accept` internally, so you don't need to call it separately if you're using that function.
5. If you're manually applying samplers with `llama_sampler_apply`, you must call `llama_sampler_accept` separately.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_sample`](#llama_sampler_sample) | Sample a token | Calls `llama_sampler_accept` internally |
| [`llama_sampler_apply`](#llama_sampler_apply) | Apply a sampler to token data | Used before `llama_sampler_accept` when manually sampling |
| [`llama_sampler_reset`](#llama_sampler_reset) | Reset a sampler | Resets the state that would be updated by `llama_sampler_accept` |
| [`llama_sampler_init_penalties`](#llama_sampler_init_penalties) | Initialize a penalties sampler | Creates a sampler that uses `llama_sampler_accept` to track tokens |
| [`llama_sampler_init_grammar`](#llama_sampler_init_grammar) | Initialize a grammar sampler | Creates a sampler that uses `llama_sampler_accept` to update grammar state |

### llama_sampler_apply

#### Function Signature

```c
LLAMA_API void llama_sampler_apply(struct llama_sampler * smpl, llama_token_data_array * cur_p);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `smpl` | `struct llama_sampler *` | Pointer to the sampler | Valid sampler created with one of the `llama_sampler_init_*` functions |
| `cur_p` | `llama_token_data_array *` | Array of token data to be modified | Array containing token IDs, logits, and probabilities |

#### Description

Applies the sampler's logic to modify the token probabilities in the provided array. This is the core function that implements the sampling logic for each sampler type. Different samplers will modify the probabilities in different ways:

- Top-K: Keeps only the K tokens with the highest probabilities
- Top-P: Keeps the smallest set of tokens whose cumulative probability exceeds P
- Temperature: Adjusts the probability distribution by dividing logits by the temperature
- Repetition penalty: Reduces the probabilities of tokens that have appeared recently
- Grammar: Filters tokens based on a formal grammar

The function modifies the `cur_p` array in place, potentially changing the probabilities, sorting the array, and setting the `selected` field to indicate which token was chosen.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"
#include <stdio.h>

int main() {
    // Initialize the backend
    llama_backend_init();

    // Load model and create context
    struct llama_model_params mparams = llama_model_default_params();
    struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

    struct llama_context_params cparams = llama_context_default_params();
    struct llama_context * ctx = llama_init_from_model(model, cparams);

    // Create a sampler
    struct llama_sampler * top_k = llama_sampler_init_top_k(40);

    // Tokenize input
    const char * prompt = "Once upon a time";
    llama_token tokens[32];
    int n_tokens = llama_tokenize(llama_model_get_vocab(model), prompt, -1, tokens, 32, true, false);

    // Create a batch
    struct llama_batch batch = llama_batch_init(n_tokens, 0, 1);
    for (int i = 0; i < n_tokens; i++) {
        batch.token[i] = tokens[i];
        batch.pos[i] = i;
        batch.n_seq_id[i] = 1;
        batch.seq_id[i][0] = 0;
        batch.logits[i] = i == n_tokens - 1 ? 1 : 0;  // Only compute logits for the last token
    }

    // Process the batch
    llama_decode(ctx, batch);

    // Get the logits
    float * logits = llama_get_logits(ctx);

    // Create token data array
    int n_vocab = llama_vocab_n_tokens(llama_model_get_vocab(model));
    llama_token_data * candidates = malloc(n_vocab * sizeof(llama_token_data));

    for (int i = 0; i < n_vocab; i++) {
        candidates[i].id = i;
        candidates[i].logit = logits[i];
        candidates[i].p = 0.0f;  // Will be computed by the sampler
    }

    llama_token_data_array candidates_p = {
        .data = candidates,
        .size = n_vocab,
        .selected = -1,
        .sorted = false
    };

    // Apply the sampler
    llama_sampler_apply(top_k, &candidates_p);

    // Print the top tokens
    printf("Top tokens after top-k sampling:\n");
    for (int i = 0; i < 10 && i < candidates_p.size; i++) {
        char token_text[32];
        llama_token_to_piece(llama_model_get_vocab(model), candidates_p.data[i].id, token_text, sizeof(token_text), 0, true);
        printf("%d. %s (p=%.6f)\n", i+1, token_text, candidates_p.data[i].p);
    }

    // Clean up
    free(candidates);
    llama_sampler_free(top_k);
    llama_batch_free(batch);
    llama_free(ctx);
    llama_model_free(model);
    llama_backend_free();

    return 0;
}
```

**Example with Multiple Samplers:**

```c
// Create samplers
struct llama_sampler * top_k = llama_sampler_init_top_k(40);
struct llama_sampler * top_p = llama_sampler_init_top_p(0.9, 1);
struct llama_sampler * temp = llama_sampler_init_temp(0.8);
struct llama_sampler * dist = llama_sampler_init_dist(0);

// Get logits and create token data array
float * logits = llama_get_logits(ctx);
int n_vocab = llama_vocab_n_tokens(llama_model_get_vocab(model));
llama_token_data * candidates = malloc(n_vocab * sizeof(llama_token_data));

for (int i = 0; i < n_vocab; i++) {
    candidates[i].id = i;
    candidates[i].logit = logits[i];
    candidates[i].p = 0.0f;
}

llama_token_data_array candidates_p = {
    .data = candidates,
    .size = n_vocab,
    .selected = -1,
    .sorted = false
};

// Apply samplers in sequence
llama_sampler_apply(top_k, &candidates_p);
llama_sampler_apply(top_p, &candidates_p);
llama_sampler_apply(temp, &candidates_p);
llama_sampler_apply(dist, &candidates_p);

// Get the selected token
llama_token token = candidates_p.data[candidates_p.selected].id;

// Accept the token
llama_sampler_accept(top_k, token);
llama_sampler_accept(top_p, token);
llama_sampler_accept(temp, token);
llama_sampler_accept(dist, token);

// Clean up
free(candidates);
```

#### Important Notes

1. The `cur_p` array must be properly initialized with token IDs and logits before calling this function.
2. The `p` field in each `llama_token_data` entry will be computed by the sampler based on the logits.
3. The `sorted` field in `llama_token_data_array` indicates whether the array is sorted by probability (descending). Some samplers require a sorted array, while others will sort it themselves.
4. The `selected` field in `llama_token_data_array` will be set by some samplers (e.g., greedy, distribution) to indicate which token was chosen. For other samplers, it will remain unchanged.
5. For sampler chains, calling this function on the chain will apply all samplers in the chain in sequence.
6. After applying the sampler, you should call `llama_sampler_accept` with the selected token to update the sampler's internal state.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_accept`](#llama_sampler_accept) | Accept a token | Called after applying the sampler to update its state |
| [`llama_sampler_sample`](#llama_sampler_sample) | Sample a token | Combines `llama_sampler_apply` and `llama_sampler_accept` |
| [`llama_sampler_reset`](#llama_sampler_reset) | Reset a sampler | Resets the state that would be updated by `llama_sampler_apply` |
| [`llama_get_logits`](#llama_get_logits) | Get token logits | Used to get the logits needed for the `cur_p` array |

### llama_sampler_reset

#### Function Signature

```c
LLAMA_API void llama_sampler_reset(struct llama_sampler * smpl);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `smpl` | `struct llama_sampler *` | Pointer to the sampler | Valid sampler created with one of the `llama_sampler_init_*` functions |

#### Description

Resets the internal state of the sampler to its initial state. This is useful when you want to start a new generation sequence or when you want to reuse a sampler for a different context.

For stateful samplers like repetition penalty or grammar-based samplers, this function clears any accumulated state. For stateless samplers like temperature or top-k, this function has no effect.

If the sampler's interface does not provide a `reset` function (i.e., the function pointer is NULL), this function does nothing.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create a sampler that maintains state
    struct llama_sampler * penalties = llama_sampler_init_penalties(64, 1.1, 0.0, 0.0);

    // Use the sampler for a generation sequence
    // ...

    // Reset the sampler for a new generation sequence
    llama_sampler_reset(penalties);

    // Use the sampler for another generation sequence
    // ...

    // Clean up
    llama_sampler_free(penalties);
    llama_backend_free();

    return 0;
}
```

**Example with Multiple Generation Sequences:**

```c
// Load model and create context
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

struct llama_context_params cparams = llama_context_default_params();
struct llama_context * ctx = llama_init_from_model(model, cparams);

// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add samplers to the chain
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
llama_sampler_chain_add(chain, llama_sampler_init_penalties(64, 1.1, 0.0, 0.0));
llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

// Process multiple prompts
const char * prompts[] = {
    "Once upon a time",
    "In a galaxy far, far away",
    "It was a dark and stormy night"
};

for (int p = 0; p < 3; p++) {
    // Reset the sampler for each new prompt
    llama_sampler_reset(chain);

    // Tokenize the prompt
    llama_token tokens[32];
    int n_tokens = llama_tokenize(llama_model_get_vocab(model), prompts[p], -1, tokens, 32, true, false);

    // Process the prompt and generate tokens
    // ...

    printf("\n\n");  // Separate outputs
}

// Clean up
llama_sampler_free(chain);
llama_free(ctx);
llama_model_free(model);
```

**Example with Grammar Sampler:**

```c
// Create a grammar sampler
const char * grammar_str = "root ::= (\"a\" | \"b\" | \"c\")+";
struct llama_sampler * grammar = llama_sampler_init_grammar(llama_model_get_vocab(model), grammar_str, "root");

// Generate a sequence
for (int i = 0; i < 10; i++) {
    llama_token token = llama_sampler_sample(grammar, ctx, -1);
    // Process the token...
}

// Reset the grammar state to start a new sequence
llama_sampler_reset(grammar);

// Generate another sequence
for (int i = 0; i < 10; i++) {
    llama_token token = llama_sampler_sample(grammar, ctx, -1);
    // Process the token...
}

// Free the sampler
llama_sampler_free(grammar);
```

#### Important Notes

1. This function is particularly useful for stateful samplers like repetition penalty, grammar-based samplers, or mirostat.
2. For stateless samplers (e.g., temperature, top-k, top-p), this function has no effect.
3. For sampler chains, calling this function on the chain will propagate the call to all samplers in the chain.
4. It's good practice to reset samplers between different generation sequences to avoid any state from the previous sequence affecting the new one.
5. This function does not change the sampler's parameters (e.g., the k value for top-k), only its internal state.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_accept`](#llama_sampler_accept) | Accept a token | Updates the state that is reset by this function |
| [`llama_sampler_apply`](#llama_sampler_apply) | Apply a sampler to token data | Uses the state that is reset by this function |
| [`llama_sampler_init_penalties`](#llama_sampler_init_penalties) | Initialize a penalties sampler | Creates a stateful sampler that can be reset |
| [`llama_sampler_init_grammar`](#llama_sampler_init_grammar) | Initialize a grammar sampler | Creates a stateful sampler that can be reset |
| [`llama_sampler_clone`](#llama_sampler_clone) | Clone a sampler | Creates a new sampler with the same parameters but a fresh state |

### llama_sampler_clone

#### Function Signature

```c
LLAMA_API struct llama_sampler * llama_sampler_clone(const struct llama_sampler * smpl);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `smpl` | `const struct llama_sampler *` | Pointer to the sampler to clone | Valid sampler created with one of the `llama_sampler_init_*` functions |

#### Description

Creates a new sampler that is a clone of the provided sampler. The clone has the same parameters and configuration as the original sampler, but with a fresh internal state. This is useful when you want to use the same sampler configuration for multiple independent generation sequences.

The function calls the `clone` function in the sampler's interface to create the clone. If the sampler's interface does not provide a `clone` function (i.e., the function pointer is NULL), this function returns NULL.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create a sampler
    struct llama_sampler * original = llama_sampler_init_top_k(40);

    // Clone the sampler
    struct llama_sampler * clone = llama_sampler_clone(original);

    // Use the original and clone for different purposes
    // ...

    // Free both samplers
    llama_sampler_free(original);
    llama_sampler_free(clone);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example with Multiple Contexts:**

```c
// Load model
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

// Create two contexts for parallel processing
struct llama_context_params cparams = llama_context_default_params();
struct llama_context * ctx1 = llama_init_from_model(model, cparams);
struct llama_context * ctx2 = llama_init_from_model(model, cparams);

// Create a sampler chain with desired configuration
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain1 = llama_sampler_chain_init(sparams);
llama_sampler_chain_add(chain1, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain1, llama_sampler_init_top_p(0.9, 1));
llama_sampler_chain_add(chain1, llama_sampler_init_temp(0.8));
llama_sampler_chain_add(chain1, llama_sampler_init_penalties(64, 1.1, 0.0, 0.0));
llama_sampler_chain_add(chain1, llama_sampler_init_dist(0));

// Clone the chain for the second context
struct llama_sampler * chain2 = llama_sampler_clone(chain1);

// Process different prompts in parallel
// (In a real application, you would use threads for true parallelism)
const char * prompt1 = "Once upon a time";
const char * prompt2 = "In a galaxy far, far away";

// Process prompt1 with ctx1 and chain1
// ...

// Process prompt2 with ctx2 and chain2
// ...

// Clean up
llama_sampler_free(chain1);
llama_sampler_free(chain2);
llama_free(ctx1);
llama_free(ctx2);
llama_model_free(model);
```

**Example with Stateful Samplers:**

```c
// Create a grammar sampler
const char * grammar_str = "root ::= (\"a\" | \"b\" | \"c\")+";
struct llama_sampler * grammar1 = llama_sampler_init_grammar(llama_model_get_vocab(model), grammar_str, "root");

// Use the grammar sampler for a while
for (int i = 0; i < 5; i++) {
    llama_token token = llama_sampler_sample(grammar1, ctx, -1);
    // Process the token...
}

// Clone the grammar sampler with its current state
struct llama_sampler * grammar2 = llama_sampler_clone(grammar1);

// Continue using the original sampler
for (int i = 0; i < 5; i++) {
    llama_token token = llama_sampler_sample(grammar1, ctx, -1);
    // Process the token...
}

// Use the cloned sampler (it will have the same grammar but a fresh state)
for (int i = 0; i < 5; i++) {
    llama_token token = llama_sampler_sample(grammar2, ctx, -1);
    // Process the token...
}

// Free the samplers
llama_sampler_free(grammar1);
llama_sampler_free(grammar2);
```

#### Important Notes

1. The cloned sampler is completely independent from the original sampler. Changes to one will not affect the other.
2. The clone has the same parameters and configuration as the original, but with a fresh internal state.
3. Not all samplers support cloning. If the sampler's interface does not provide a `clone` function, this function will return NULL.
4. For sampler chains, cloning will create a new chain with clones of all the samplers in the original chain.
5. You must free the cloned sampler with `llama_sampler_free` when you're done with it.
6. Cloning a sampler is more efficient than creating a new one with the same parameters, especially for complex samplers like grammar-based ones.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_init`](#llama_sampler_init) | Initialize a sampler | Alternative to cloning when you need a fresh sampler |
| [`llama_sampler_free`](#llama_sampler_free) | Free a sampler | Must be called to free the cloned sampler |
| [`llama_sampler_reset`](#llama_sampler_reset) | Reset a sampler | Alternative to cloning when you only need to reset the state |
| [`llama_sampler_chain_init`](#llama_sampler_chain_init) | Initialize a sampler chain | Creates a chain that can be cloned |

### llama_sampler_free

#### Function Signature

```c
LLAMA_API void llama_sampler_free(struct llama_sampler * smpl);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `smpl` | `struct llama_sampler *` | Pointer to the sampler to free | Valid sampler created with one of the `llama_sampler_init_*` functions |

#### Description

Frees all resources associated with the sampler. This function should be called when you're done using a sampler to avoid memory leaks.

The function calls the `free` function in the sampler's interface to free any resources allocated by the sampler. If the sampler's interface does not provide a `free` function (i.e., the function pointer is NULL), only the sampler structure itself is freed.

For sampler chains, this function will free all samplers in the chain, so you don't need to free them individually.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create a sampler
    struct llama_sampler * sampler = llama_sampler_init_top_k(40);

    // Use the sampler
    // ...

    // Free the sampler when done
    llama_sampler_free(sampler);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example with Multiple Samplers:**

```c
// Create multiple samplers
struct llama_sampler * top_k = llama_sampler_init_top_k(40);
struct llama_sampler * top_p = llama_sampler_init_top_p(0.9, 1);
struct llama_sampler * temp = llama_sampler_init_temp(0.8);
struct llama_sampler * dist = llama_sampler_init_dist(0);

// Use the samplers
// ...

// Free each sampler individually
llama_sampler_free(top_k);
llama_sampler_free(top_p);
llama_sampler_free(temp);
llama_sampler_free(dist);
```

**Example with Sampler Chain:**

```c
// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add samplers to the chain
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
llama_sampler_chain_add(chain, llama_sampler_init_temp(0.8));
llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

// Use the chain
// ...

// Free the chain (this will free all added samplers)
llama_sampler_free(chain);
```

**Example with Removed Sampler:**

```c
// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add samplers to the chain
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
struct llama_sampler * temp = llama_sampler_init_temp(0.8);
llama_sampler_chain_add(chain, temp);
llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

// Use the chain
// ...

// Remove a sampler from the chain
struct llama_sampler * removed = llama_sampler_chain_remove(chain, 2);  // Remove the temperature sampler

// The removed sampler is no longer owned by the chain, so we need to free it separately
if (removed == temp) {
    llama_sampler_free(removed);
}

// Free the chain (this will free all remaining samplers)
llama_sampler_free(chain);
```

#### Important Notes

1. Always call this function when you're done with a sampler to avoid memory leaks.
2. Do not use the sampler after calling this function, as it will be freed and the pointer will be invalid.
3. For sampler chains, this function will free all samplers in the chain, so you don't need to free them individually.
4. If you remove a sampler from a chain using `llama_sampler_chain_remove`, you are responsible for freeing it separately.
5. Passing NULL to this function is safe and will have no effect.
6. This function is not thread-safe. Do not call it while other threads are using the sampler.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_init`](#llama_sampler_init) | Initialize a sampler | Creates a sampler that needs to be freed |
| [`llama_sampler_clone`](#llama_sampler_clone) | Clone a sampler | Creates a new sampler that needs to be freed |
| [`llama_sampler_chain_init`](#llama_sampler_chain_init) | Initialize a sampler chain | Creates a chain that needs to be freed |
| [`llama_sampler_chain_add`](#llama_sampler_chain_add) | Add a sampler to a chain | Transfers ownership of the sampler to the chain |
| [`llama_sampler_chain_remove`](#llama_sampler_chain_remove) | Remove a sampler from a chain | Transfers ownership of the sampler back to the caller |

### llama_sampler_chain_init

#### Function Signature

```c
LLAMA_API struct llama_sampler * llama_sampler_chain_init(struct llama_sampler_chain_params params);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `params` | `struct llama_sampler_chain_params` | Parameters for the sampler chain | Use `llama_sampler_chain_default_params()` for default values |

#### Description

Initializes a new sampler chain. A sampler chain is a special type of sampler that can contain multiple other samplers, which are applied in sequence. This allows you to combine different sampling strategies, such as applying top-k filtering, then top-p filtering, then temperature scaling, and finally sampling from the resulting distribution.

The sampler chain takes ownership of any samplers added to it with `llama_sampler_chain_add`, and will free them when the chain itself is freed with `llama_sampler_free`.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create a sampler chain with default parameters
    struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
    struct llama_sampler * chain = llama_sampler_chain_init(sparams);

    // Add samplers to the chain
    llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
    llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
    llama_sampler_chain_add(chain, llama_sampler_init_temp(0.8));
    llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

    // Use the chain
    // ...

    // Free the chain (this will free all added samplers)
    llama_sampler_free(chain);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example with Performance Measurement:**

```c
// Create a sampler chain with performance measurement disabled
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
sparams.no_perf = true;  // Disable performance measurement
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add samplers to the chain
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
llama_sampler_chain_add(chain, llama_sampler_init_temp(0.8));
llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

// Use the chain
// ...

// Free the chain
llama_sampler_free(chain);
```

**Example with Complete Generation Loop:**

```c
// Load model and create context
struct llama_model_params mparams = llama_model_default_params();
struct llama_model * model = llama_model_load_from_file("model.gguf", mparams);

struct llama_context_params cparams = llama_context_default_params();
struct llama_context * ctx = llama_init_from_model(model, cparams);

// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add samplers to the chain
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
llama_sampler_chain_add(chain, llama_sampler_init_temp(0.8));
llama_sampler_chain_add(chain, llama_sampler_init_penalties(64, 1.1, 0.0, 0.0));
llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

// Tokenize input
const char * prompt = "Once upon a time";
llama_token tokens[32];
int n_tokens = llama_tokenize(llama_model_get_vocab(model), prompt, -1, tokens, 32, true, false);

// Create a batch
struct llama_batch batch = llama_batch_init(n_tokens, 0, 1);
for (int i = 0; i < n_tokens; i++) {
    batch.token[i] = tokens[i];
    batch.pos[i] = i;
    batch.n_seq_id[i] = 1;
    batch.seq_id[i][0] = 0;
    batch.logits[i] = i == n_tokens - 1 ? 1 : 0;  // Only compute logits for the last token
}

// Process the batch
llama_decode(ctx, batch);
llama_batch_free(batch);

// Generate tokens
for (int i = 0; i < 100; i++) {
    // Sample a token using the chain
    llama_token new_token = llama_sampler_sample(chain, ctx, -1);

    // Check if we've reached the end of generation
    if (llama_vocab_is_eog(llama_model_get_vocab(model), new_token)) {
        break;
    }

    // Print the token text
    char token_text[32];
    llama_token_to_piece(llama_model_get_vocab(model), new_token, token_text, sizeof(token_text), 0, true);
    printf("%s", token_text);

    // Prepare batch for next token
    batch = llama_batch_init(1, 0, 1);
    batch.token[0] = new_token;
    batch.pos[0] = n_tokens + i;
    batch.n_seq_id[0] = 1;
    batch.seq_id[0][0] = 0;
    batch.logits[0] = 1;

    // Process the batch
    llama_decode(ctx, batch);
    llama_batch_free(batch);
}

// Clean up
llama_sampler_free(chain);
llama_free(ctx);
llama_model_free(model);
```

#### Important Notes

1. The sampler chain takes ownership of any samplers added to it with `llama_sampler_chain_add`, and will free them when the chain itself is freed.
2. Samplers in the chain are applied in the order they were added, which can significantly affect the results.
3. The last sampler in the chain should typically be a sampler that actually selects a token, such as `llama_sampler_init_greedy` or `llama_sampler_init_dist`.
4. The `no_perf` parameter in `llama_sampler_chain_params` controls whether performance measurements are collected. Set it to `true` to disable performance measurements.
5. You can access individual samplers in the chain using `llama_sampler_chain_get` and `llama_sampler_chain_n`.
6. You can remove samplers from the chain using `llama_sampler_chain_remove`, but you then become responsible for freeing them.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_chain_default_params`](#llama_sampler_chain_default_params) | Get default sampler chain parameters | Used to initialize the parameters for this function |
| [`llama_sampler_chain_add`](#llama_sampler_chain_add) | Add a sampler to a chain | Used to add samplers to the chain created by this function |
| [`llama_sampler_chain_get`](#llama_sampler_chain_get) | Get a sampler from a chain | Used to access individual samplers in the chain |
| [`llama_sampler_chain_n`](#llama_sampler_chain_n) | Get the number of samplers in a chain | Used to determine how many samplers are in the chain |
| [`llama_sampler_chain_remove`](#llama_sampler_chain_remove) | Remove a sampler from a chain | Used to remove a sampler from the chain |
| [`llama_sampler_free`](#llama_sampler_free) | Free a sampler | Used to free the chain when you're done with it |

### llama_sampler_chain_add

#### Function Signature

```c
LLAMA_API void llama_sampler_chain_add(struct llama_sampler * chain, struct llama_sampler * smpl);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `chain` | `struct llama_sampler *` | Pointer to the sampler chain | Valid sampler chain created with `llama_sampler_chain_init` |
| `smpl` | `struct llama_sampler *` | Pointer to the sampler to add | Valid sampler created with one of the `llama_sampler_init_*` functions |

#### Description

Adds a sampler to a sampler chain. The samplers in a chain are applied in the order they are added, with each sampler modifying the token probabilities before passing them to the next sampler in the chain.

This function transfers ownership of the sampler to the chain, meaning that the chain will be responsible for freeing the sampler when the chain itself is freed. You should not call `llama_sampler_free` on a sampler that has been added to a chain, unless you first remove it from the chain using `llama_sampler_chain_remove`.

#### Usage Examples

**Basic Usage Pattern:**

```c
#include "llama.h"

int main() {
    // Initialize the backend
    llama_backend_init();

    // Create a sampler chain
    struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
    struct llama_sampler * chain = llama_sampler_chain_init(sparams);

    // Create and add samplers to the chain
    struct llama_sampler * top_k = llama_sampler_init_top_k(40);
    struct llama_sampler * top_p = llama_sampler_init_top_p(0.9, 1);
    struct llama_sampler * temp = llama_sampler_init_temp(0.8);
    struct llama_sampler * dist = llama_sampler_init_dist(0);

    llama_sampler_chain_add(chain, top_k);
    llama_sampler_chain_add(chain, top_p);
    llama_sampler_chain_add(chain, temp);
    llama_sampler_chain_add(chain, dist);

    // Use the chain
    // ...

    // Free the chain (this will free all added samplers)
    llama_sampler_free(chain);

    // Clean up
    llama_backend_free();

    return 0;
}
```

**Example with Conditional Sampling:**

```c
// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add basic samplers
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));

// Conditionally add a temperature sampler based on a parameter
float temperature = 0.8;
if (temperature > 0.0f && temperature != 1.0f) {
    llama_sampler_chain_add(chain, llama_sampler_init_temp(temperature));
}

// Add repetition penalty if enabled
bool use_penalty = true;
if (use_penalty) {
    llama_sampler_chain_add(chain, llama_sampler_init_penalties(64, 1.1, 0.0, 0.0));
}

// Add a grammar sampler if a grammar is provided
const char * grammar_str = "root ::= (\"a\" | \"b\" | \"c\")+";
if (grammar_str != NULL) {
    struct llama_sampler * grammar = llama_sampler_init_grammar(
        llama_model_get_vocab(model), grammar_str, "root");
    if (grammar != NULL) {
        llama_sampler_chain_add(chain, grammar);
    }
}

// Always add a distribution sampler at the end
llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

// Use the chain
// ...

// Free the chain
llama_sampler_free(chain);
```

**Example with Dynamic Sampler Adjustment:**

```c
// Create a sampler chain
struct llama_sampler_chain_params sparams = llama_sampler_chain_default_params();
struct llama_sampler * chain = llama_sampler_chain_init(sparams);

// Add initial samplers
llama_sampler_chain_add(chain, llama_sampler_init_top_k(40));
llama_sampler_chain_add(chain, llama_sampler_init_top_p(0.9, 1));
llama_sampler_chain_add(chain, llama_sampler_init_temp(0.8));
llama_sampler_chain_add(chain, llama_sampler_init_dist(0));

// Use the chain for a while
// ...

// Remove the temperature sampler and replace it with a different one
int temp_index = 2;  // Index of the temperature sampler
struct llama_sampler * old_temp = llama_sampler_chain_remove(chain, temp_index);
llama_sampler_free(old_temp);  // Free the removed sampler

// Add a new temperature sampler
struct llama_sampler * new_temp = llama_sampler_init_temp(0.5);  // Lower temperature
llama_sampler_chain_add(chain, new_temp);

// Continue using the chain
// ...

// Free the chain
llama_sampler_free(chain);
```

#### Important Notes

1. The sampler chain takes ownership of any samplers added to it, and will free them when the chain itself is freed.
2. Samplers in the chain are applied in the order they are added, which can significantly affect the results.
3. The last sampler in the chain should typically be a sampler that actually selects a token, such as `llama_sampler_init_greedy` or `llama_sampler_init_dist`.
4. You can add as many samplers to a chain as needed, but adding too many may impact performance.
5. You can remove samplers from the chain using `llama_sampler_chain_remove`, but you then become responsible for freeing them.
6. Adding a NULL sampler to the chain is safe and will have no effect.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| [`llama_sampler_chain_init`](#llama_sampler_chain_init) | Initialize a sampler chain | Creates the chain to which samplers are added |
| [`llama_sampler_chain_get`](#llama_sampler_chain_get) | Get a sampler from a chain | Used to access individual samplers in the chain |
| [`llama_sampler_chain_n`](#llama_sampler_chain_n) | Get the number of samplers in a chain | Used to determine how many samplers are in the chain |
| [`llama_sampler_chain_remove`](#llama_sampler_chain_remove) | Remove a sampler from a chain | Used to remove a sampler that was previously added |
| [`llama_sampler_free`](#llama_sampler_free) | Free a sampler | Used to free the chain and all its samplers |
