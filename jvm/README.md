# llama.cpp Java API

This directory contains two Java APIs for the llama.cpp library:

1. **Java-centric API**: A clean, modular API designed to be Java-centric, using generic terms without direct references to the underlying implementation.
2. **SWIG Bindings**: Direct bindings to the llama.cpp library using SWIG.

## Building

### Java-centric API

To build the Java-centric API, you need:

- Java 24 or later
- Gradle 7.0 or later

Run the following command from the `jvm` directory:

```bash
./gradlew build
```

#### Native Library

The Java-centric API uses the Java Foreign and Native Memory API to interact with the native llama.cpp library. The library is loaded using the following strategy:

1. Check if a custom path is specified via the system property "llama.library.path"
2. If not found, check the default path at "/Users/e168693/TeamCompose/submodules/llama.cpp/build/bin"
3. If still not found, try to extract the library from the resources

To specify a custom path, use:

```bash
java -Dllama.library.path=/path/to/library -jar your-app.jar
```

**Important:** Building the native library using CMake is required to ensure compatibility with the Java code's expectations. The Java Foreign and Native Memory API requires precise memory layout and function signatures that match the compiled library. The build process will automatically build the native library if CMake is available.

**Why CMake is Needed:**
- The Java FFM API makes assumptions about the memory layout of structs and function signatures
- These assumptions must match the actual compiled library
- Building with CMake ensures that the library is compiled with the correct options and flags
- Using a pre-built library from another source may cause memory alignment issues or other incompatibilities

If you have a compatible pre-built library (built with the same version of llama.cpp and the same compiler options), you can specify its path using the system property, but this is not recommended unless you're certain of compatibility.

### SWIG Bindings

To build the SWIG bindings, run the following command from the root directory:

```bash
cd jvm
./build.sh
```

This will generate the Java bindings and compile them into a JAR file.

## Usage

### Java-centric API

The Java-centric API provides a clean, modular interface for working with large language models. Here's a simple example:

```java
import io.github.llama.api.LLM;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;
import io.github.llama.api.tokenization.SpecialToken;
import io.github.llama.api.tokenization.Tokenizer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Example {
    public static void main(String[] args) {
        // Get the model path
        Path modelPath = Paths.get("path/to/model.gguf");

        try {
            // Create model parameters
            ModelParams modelParams = ModelParams.builder()
                .useMemoryMapping(true)
                .gpuLayerCount(0)
                .build();

            // Load a model using the service loader mechanism
            try (LLM llm = LLMFactoryRegistry.getInstance().createLLM(modelPath, modelParams)) {
                // Create context parameters
                ContextParams contextParams = ContextParams.builder()
                    .contextSize(2048)
                    .threadCount(4)
                    .build();

                // Create a context
                try (Context context = llm.createContext(contextParams)) {
                    // Get the tokenizer
                    Tokenizer tokenizer = llm.getTokenizer();

                    // Tokenize input text
                    String inputText = "Hello, how are you?";
                    int[] tokens = tokenizer.tokenize(inputText, true, false);

                    // Create a batch
                    try (Batch batch = context.createBatch(tokens.length)) {
                        // Add tokens to the batch
                        batch.addTokens(tokens);

                        // Process the batch
                        BatchResult result = context.process(batch);
                        if (!result.isSuccess()) {
                            System.err.println("Batch processing failed: " + result.getErrorMessage());
                            return;
                        }

                        // Create a sampler
                        SamplerParams samplerParams = SamplerParams.builder()
                            .temperature(0.7f)
                            .topP(0.9f)
                            .maxTokens(100)
                            .build();

                        // Sample tokens
                        try (Sampler sampler = context.createSampler(samplerParams)) {
                            StringBuilder output = new StringBuilder();
                            for (int i = 0; i < samplerParams.getMaxTokens(); i++) {
                                int tokenId = sampler.sample(context.getLogits());
                                if (tokenId == tokenizer.getSpecialToken(SpecialToken.EOS)) {
                                    break;
                                }

                                String tokenText = tokenizer.getTokenText(tokenId);
                                output.append(tokenText);

                                // Add the sampled token to the batch for the next iteration
                                batch.clear();
                                batch.addToken(tokenId);
                                result = context.process(batch);
                                if (!result.isSuccess()) {
                                    System.err.println("Batch processing failed: " + result.getErrorMessage());
                                    break;
                                }
                            }

                            System.out.println("Generated text: " + output.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### SWIG Bindings

The SWIG bindings provide direct access to the llama.cpp API from Java. Here's a simple example:

```java
import com.llama.*;

public class Example {
    public static void main(String[] args) {
        // Initialize the backend
        llama.llama_backend_init();

        // Create model parameters with default values
        llama_model_params params = llama.new_llama_model_params();

        // Load a model
        String modelPath = "path/to/model.gguf";
        llama_model model = llama.llama_model_load_from_file(modelPath, params);

        // Create context parameters with default values
        llama_context_params ctxParams = llama.new_llama_context_params();

        // Initialize a context from the model
        llama_context ctx = llama.llama_init_from_model(model, ctxParams);

        // Tokenize some text
        String text = "Hello, world!";
        int maxTokens = 32;

        // Create a token array
        llama_token[] tokens = new llama_token[maxTokens];
        for (int i = 0; i < maxTokens; i++) {
            tokens[i] = 0;
        }

        // Get the vocabulary from the model
        llama_vocab vocab = llama.llama_model_get_vocab(model);

        // Tokenize the text
        int numTokens = llama.llama_tokenize(vocab, text, text.length(), tokens, maxTokens, true, false);

        // Create a batch
        llama_batch batch = llama.new_llama_batch(numTokens, 0, 1);

        // Set the tokens in the batch
        for (int i = 0; i < numTokens; i++) {
            llama.set_llama_token_array(batch.token, i, tokens[i]);
        }

        // Decode the batch
        llama.llama_decode(ctx, batch);

        // Get the logits
        float[] logits = new float[llama.llama_model_n_vocab(model)];
        float[] logitsPtr = llama.llama_get_logits(ctx);

        // Clean up
        llama.delete_llama_batch(batch);
        llama.llama_free(ctx);
        llama.llama_model_free(model);
        llama.llama_backend_free();
    }
}
```

## API Documentation

### Java-centric API

The Java-centric API is organized into the following packages:

- `io.github.llama.api`: Root package containing common interfaces and classes
- `io.github.llama.api.model`: Classes related to model loading and management
- `io.github.llama.api.context`: Classes related to context creation and management
- `io.github.llama.api.tokenization`: Classes related to tokenization and vocabulary
- `io.github.llama.api.batch`: Classes related to batch processing
- `io.github.llama.api.sampling`: Classes related to sampling
- `io.github.llama.api.spi`: Service provider interfaces for factory implementations

The API provides access to the following functionality:

- Model loading and management
- Context creation and management
- Tokenization and detokenization
- Batch processing
- Sampling
- And more

For detailed API documentation, refer to the Java files in the `jvm/api/src/main/java/io/github/llama/api/` directory.

### SWIG Bindings

The SWIG bindings provide access to the following llama.cpp API functions:

- Model loading and management
- Context initialization and management
- Tokenization and detokenization
- Batch processing
- Sampling
- And more

For detailed API documentation, refer to the generated Java files in `src/main/java/com/llama/`.

## Implementation Details

### Java-centric API

The Java-centric API has two implementations:

1. `impl-foreign`: Uses the Java Foreign and Native Memory API (Java 21+) to interact with the native library
2. `impl-llamacpp`: Uses SWIG-generated bindings to interact with the native library

The implementation to use is determined at runtime using the Java Service Provider Interface (SPI) mechanism. The `LLMFactoryRegistry` class discovers and manages the available implementations.

The API is designed to be Java-centric, using generic terms without direct references to the underlying implementation. It provides a clean abstraction over the underlying implementation, hiding implementation details and providing a more natural Java experience.

### SWIG Bindings

The SWIG bindings are implemented using SWIG, which generates Java wrapper code for the C/C++ API. The SWIG interface file (`src/main/swig/llama.i`) defines how the C/C++ API is exposed to Java.

The bindings include:

- Type mappings for C/C++ types to Java types
- Handling of pointers and arrays
- Callback support
- Memory management helpers
- And more

## Testing

### Java-centric API

A test class is provided in `jvm/impl-foreign/src/test/java/io/github/llama/impl/foreign/ForeignLLMTest.java`. To run the test, build the API and then run:

```bash
java -cp jvm/impl-foreign/build/libs/impl-foreign-0.1.0.jar:jvm/api/build/libs/api-0.1.0.jar io.github.llama.impl.ffm.ForeignLLMTest path/to/model.gguf
```

### SWIG Bindings

A simple test is provided in `src/test/java/com/llama/LlamaTest.java`. To run the test, build the bindings and then run:

```bash
java -cp build/llama.jar:src/test/java com.llama.LlamaTest
```

## Known Issues and Limitations

### Java-centric API

- The API is still in development and may not cover all aspects of the llama.cpp API.
- The Foreign implementation requires Java 24 or later.
- The implementation is a proof of concept and may not be optimized for performance.

### SWIG Bindings

- The bindings are still in development and may not cover all aspects of the llama.cpp API.
- Memory management is manual; you must explicitly free resources when done with them.
- Array handling requires using helper functions to create, access, and free arrays.
- String handling may require additional care, especially for functions that return strings.

## Contributing

Contributions to improve the Java API and bindings are welcome. Please submit pull requests with improvements, bug fixes, or additional tests.
