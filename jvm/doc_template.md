# Function Documentation Template

This document describes the format to be used for documenting functions in the llama.cpp API. The template is based on the documentation for `llama_backend_init` and should be applied consistently to all functions.

**Important**: Each function section should be linked to from the summary tables in the main documentation. This allows users to easily navigate from the function listing to its detailed documentation.

## Template Structure

### Function Name

#### Function Signature

```c
LLAMA_API return_type function_name(parameter_type parameter_name, ...);
```

#### Parameters

| Parameter | Type | Description | Possible Values |
|-----------|------|-------------|----------------|
| `parameter_name` | `parameter_type` | Brief description of the parameter | List of possible values if applicable |
| ... | ... | ... | ... |

*Note: If the function has no parameters, include the table with a single row stating "None".*

#### Description

A detailed explanation of what the function does, its purpose, and how it fits into the overall API. This should be 2-4 sentences that clearly explain the function's role and importance.

#### Usage Examples

**Basic Usage Pattern:**

```c
// A simple, minimal example showing how to use the function
```

**Example from Real Code:**

```c
// An example taken from one of the example applications
// showing the function in a realistic context
```

**Additional Example (if needed):**

```c
// Another example showing a different use case or context
```

#### Important Notes

1. Key point about using the function correctly
2. Common pitfalls or mistakes to avoid
3. Performance considerations
4. Any other critical information users should know

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| `related_function_name` | Brief description | How this function relates to the main function |
| ... | ... | ... |

## Example: llama_backend_init

The documentation for `llama_backend_init` follows this template:

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

#### Important Notes

1. `llama_backend_init()` should be called only once at the beginning of your program.
2. Always call `llama_backend_free()` at the end of your program to properly clean up resources.
3. The initialization must happen before loading any models or creating any contexts.
4. For NUMA systems, you can optionally call `llama_numa_init()` after `llama_backend_init()` to optimize for NUMA architectures.

#### Related Functions

| Function | Description | Relationship |
|----------|-------------|--------------|
| `llama_backend_free` | Free resources used by the backend | Cleanup function that should be called at the end of the program |
| `llama_numa_init` | Initialize NUMA optimization strategy | Optional function to call after initialization for NUMA systems |
| `llama_print_system_info` | Print system information | Useful for debugging after initialization |
| `llama_log_set` | Set logging callback | Configure logging before using other functions |
