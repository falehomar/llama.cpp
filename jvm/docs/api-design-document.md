# Java API for Large Language Models

## Overview

This document outlines the design for a Java API that provides access to large language model functionality. The API is designed to be Java-centric, using generic terms without direct references to the underlying implementation (llama.cpp). The API is organized into modules, each with its own package, and uses factories with the Java service loading mechanism to create implementation classes.

## Design Principles

1. **Java-centric**: The API uses Java conventions and idioms, avoiding direct references to the underlying C/C++ implementation.
2. **Modular**: Functionality is organized into logical modules, each with its own package.
3. **Factory-based**: Implementation classes are created through factories, allowing for different implementations to be swapped without changing client code.
4. **Service Loading**: The factories use the Java Service Provider Interface (SPI) to discover and load implementations.
5. **Clean Abstraction**: The API provides a clean abstraction over the underlying implementation, hiding implementation details.
6. **Thread Safety**: The API is designed to be thread-safe where appropriate.
7. **Resource Management**: The API provides mechanisms for proper resource management, including automatic cleanup.

## Package Structure

The API is organized into the following packages:

1. `io.github.llama.api`: Root package containing common interfaces and classes.
2. `io.github.llama.api.model`: Classes related to model loading and management.
3. `io.github.llama.api.context`: Classes related to context creation and management.
4. `io.github.llama.api.tokenization`: Classes related to tokenization and vocabulary.
5. `io.github.llama.api.batch`: Classes related to batch processing.
6. `io.github.llama.api.cache`: Classes related to key-value cache management.
7. `io.github.llama.api.state`: Classes related to state management.
8. `io.github.llama.api.decoding`: Classes related to decoding.
9. `io.github.llama.api.sampling`: Classes related to sampling.
10. `io.github.llama.api.adapter`: Classes related to adapter management.
11. `io.github.llama.api.spi`: Service provider interfaces for factory implementations.

## Core Interfaces

### `io.github.llama.api.LLM`

The main interface representing a large language model.

```java
public interface LLM extends AutoCloseable {
    /**
     * Gets information about the model.
     *
     * @return Model information
     */
    ModelInfo getModelInfo();

    /**
     * Creates a new context for inference.
     *
     * @param params Context parameters
     * @return A new context
     */
    Context createContext(ContextParams params);

    /**
     * Gets the tokenizer for this model.
     *
     * @return The tokenizer
     */
    Tokenizer getTokenizer();

    /**
     * Closes the model and releases resources.
     */
    @Override
    void close();
}
```

### `io.github.llama.api.model.ModelInfo`

Interface representing information about a model.

```java
public interface ModelInfo {
    /**
     * Gets the number of parameters in the model.
     *
     * @return Number of parameters
     */
    long getParameterCount();

    /**
     * Gets the context size used during training.
     *
     * @return Context size
     */
    int getContextSize();

    /**
     * Gets the embedding size.
     *
     * @return Embedding size
     */
    int getEmbeddingSize();

    /**
     * Gets the number of layers.
     *
     * @return Number of layers
     */
    int getLayerCount();

    /**
     * Gets the number of attention heads.
     *
     * @return Number of attention heads
     */
    int getHeadCount();

    /**
     * Gets a metadata value as a string.
     *
     * @param key Metadata key
     * @return Metadata value, or null if not found
     */
    String getMetadata(String key);

    /**
     * Gets all metadata keys.
     *
     * @return Set of metadata keys
     */
    Set<String> getMetadataKeys();

    /**
     * Gets a description of the model.
     *
     * @return Model description
     */
    String getDescription();
}
```

### `io.github.llama.api.context.Context`

Interface representing a context for inference.

```java
public interface Context extends AutoCloseable {
    /**
     * Gets the model associated with this context.
     *
     * @return The model
     */
    LLM getModel();

    /**
     * Creates a new batch for processing.
     *
     * @param maxTokens Maximum number of tokens in the batch
     * @return A new batch
     */
    Batch createBatch(int maxTokens);

    /**
     * Processes a batch of tokens.
     *
     * @param batch The batch to process
     * @return Result of processing the batch
     */
    BatchResult process(Batch batch);

    /**
     * Gets the logits from the last processed batch.
     *
     * @return Array of logits
     */
    float[] getLogits();

    /**
     * Creates a sampler for generating tokens.
     *
     * @param params Sampler parameters
     * @return A new sampler
     */
    Sampler createSampler(SamplerParams params);

    /**
     * Closes the context and releases resources.
     */
    @Override
    void close();
}
```

### `io.github.llama.api.tokenization.Tokenizer`

Interface for tokenizing and detokenizing text.

```java
public interface Tokenizer {
    /**
     * Tokenizes text into an array of token IDs.
     *
     * @param text Text to tokenize
     * @return Array of token IDs
     */
    int[] tokenize(String text);

    /**
     * Tokenizes text into an array of token IDs with options.
     *
     * @param text Text to tokenize
     * @param addBos Whether to add a beginning-of-sequence token
     * @param addEos Whether to add an end-of-sequence token
     * @return Array of token IDs
     */
    int[] tokenize(String text, boolean addBos, boolean addEos);

    /**
     * Detokenizes an array of token IDs into text.
     *
     * @param tokens Array of token IDs
     * @return Detokenized text
     */
    String detokenize(int[] tokens);

    /**
     * Gets the size of the vocabulary.
     *
     * @return Vocabulary size
     */
    int getVocabularySize();

    /**
     * Gets the token ID for a special token.
     *
     * @param token Special token type
     * @return Token ID
     */
    int getSpecialToken(SpecialToken token);

    /**
     * Gets the text for a token ID.
     *
     * @param tokenId Token ID
     * @return Token text
     */
    String getTokenText(int tokenId);
}
```

### `io.github.llama.api.batch.Batch`

Interface representing a batch of tokens for processing.

```java
public interface Batch extends AutoCloseable {
    /**
     * Adds a token to the batch.
     *
     * @param tokenId Token ID
     * @return This batch for chaining
     */
    Batch addToken(int tokenId);

    /**
     * Adds multiple tokens to the batch.
     *
     * @param tokenIds Array of token IDs
     * @return This batch for chaining
     */
    Batch addTokens(int[] tokenIds);

    /**
     * Gets the number of tokens in the batch.
     *
     * @return Number of tokens
     */
    int getTokenCount();

    /**
     * Gets the maximum number of tokens the batch can hold.
     *
     * @return Maximum token count
     */
    int getMaxTokenCount();

    /**
     * Clears the batch.
     *
     * @return This batch for chaining
     */
    Batch clear();

    /**
     * Closes the batch and releases resources.
     */
    @Override
    void close();
}
```

### `io.github.llama.api.sampling.Sampler`

Interface for sampling tokens from logits.

```java
public interface Sampler extends AutoCloseable {
    /**
     * Samples a token from logits.
     *
     * @param logits Array of logits
     * @return Sampled token ID
     */
    int sample(float[] logits);

    /**
     * Closes the sampler and releases resources.
     */
    @Override
    void close();
}
```

## Factory Interfaces

### `io.github.llama.api.spi.LLMFactory`

Interface for creating LLM instances.

```java
public interface LLMFactory {
    /**
     * Gets the name of this factory.
     *
     * @return Factory name
     */
    String getName();

    /**
     * Creates an LLM from a model file.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @return A new LLM instance
     * @throws IOException If the model cannot be loaded
     */
    LLM createLLM(Path modelPath, ModelParams params) throws IOException;

    /**
     * Checks if this factory supports the given model file.
     *
     * @param modelPath Path to the model file
     * @return True if the factory supports the model, false otherwise
     */
    boolean supportsModel(Path modelPath);
}
```

### `io.github.llama.api.LLMFactoryRegistry`

Class for discovering and managing LLM factories.

```java
public final class LLMFactoryRegistry {
    private static final LLMFactoryRegistry INSTANCE = new LLMFactoryRegistry();

    private final List<LLMFactory> factories;

    private LLMFactoryRegistry() {
        factories = new ArrayList<>();
        ServiceLoader<LLMFactory> loader = ServiceLoader.load(LLMFactory.class);
        for (LLMFactory factory : loader) {
            factories.add(factory);
        }
    }

    /**
     * Gets the singleton instance of the registry.
     *
     * @return The registry instance
     */
    public static LLMFactoryRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Gets all registered factories.
     *
     * @return List of factories
     */
    public List<LLMFactory> getFactories() {
        return Collections.unmodifiableList(factories);
    }

    /**
     * Gets a factory by name.
     *
     * @param name Factory name
     * @return The factory, or null if not found
     */
    public LLMFactory getFactory(String name) {
        for (LLMFactory factory : factories) {
            if (factory.getName().equals(name)) {
                return factory;
            }
        }
        return null;
    }

    /**
     * Creates an LLM from a model file using the first factory that supports it.
     *
     * @param modelPath Path to the model file
     * @param params Model parameters
     * @return A new LLM instance
     * @throws IOException If the model cannot be loaded
     * @throws IllegalArgumentException If no factory supports the model
     */
    public LLM createLLM(Path modelPath, ModelParams params) throws IOException {
        for (LLMFactory factory : factories) {
            if (factory.supportsModel(modelPath)) {
                return factory.createLLM(modelPath, params);
            }
        }
        throw new IllegalArgumentException("No factory supports the model: " + modelPath);
    }
}
```

## Parameter Classes

### `io.github.llama.api.model.ModelParams`

Class representing parameters for model loading.

```java
public class ModelParams {
    private boolean useMemoryMapping = true;
    private boolean useMemoryLocking = false;
    private int gpuLayerCount = 0;
    private boolean vocabOnly = false;
    private Map<String, String> metadataOverrides = new HashMap<>();

    // Getters and setters

    /**
     * Creates a new builder for ModelParams.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ModelParams.
     */
    public static class Builder {
        private final ModelParams params = new ModelParams();

        /**
         * Sets whether to use memory mapping.
         *
         * @param useMemoryMapping Whether to use memory mapping
         * @return This builder for chaining
         */
        public Builder useMemoryMapping(boolean useMemoryMapping) {
            params.setUseMemoryMapping(useMemoryMapping);
            return this;
        }

        // Other builder methods

        /**
         * Builds the ModelParams.
         *
         * @return The built ModelParams
         */
        public ModelParams build() {
            return params;
        }
    }
}
```

### `io.github.llama.api.context.ContextParams`

Class representing parameters for context creation.

```java
public class ContextParams {
    private int contextSize = 512;
    private int batchSize = 512;
    private int threadCount = 4;
    private boolean logitsAll = false;

    // Getters and setters

    /**
     * Creates a new builder for ContextParams.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ContextParams.
     */
    public static class Builder {
        private final ContextParams params = new ContextParams();

        /**
         * Sets the context size.
         *
         * @param contextSize Context size
         * @return This builder for chaining
         */
        public Builder contextSize(int contextSize) {
            params.setContextSize(contextSize);
            return this;
        }

        // Other builder methods

        /**
         * Builds the ContextParams.
         *
         * @return The built ContextParams
         */
        public ContextParams build() {
            return params;
        }
    }
}
```

### `io.github.llama.api.sampling.SamplerParams`

Class representing parameters for sampler creation.

```java
public class SamplerParams {
    private float temperature = 0.8f;
    private float topP = 0.95f;
    private int topK = 40;
    private float repetitionPenalty = 1.1f;
    private int maxTokens = 128;

    // Getters and setters

    /**
     * Creates a new builder for SamplerParams.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for SamplerParams.
     */
    public static class Builder {
        private final SamplerParams params = new SamplerParams();

        /**
         * Sets the temperature.
         *
         * @param temperature Temperature
         * @return This builder for chaining
         */
        public Builder temperature(float temperature) {
            params.setTemperature(temperature);
            return this;
        }

        // Other builder methods

        /**
         * Builds the SamplerParams.
         *
         * @return The built SamplerParams
         */
        public SamplerParams build() {
            return params;
        }
    }
}
```

## Enum Classes

### `io.github.llama.api.tokenization.SpecialToken`

Enum representing special tokens.

```java
public enum SpecialToken {
    BOS, // Beginning of sequence
    EOS, // End of sequence
    PAD, // Padding
    UNK  // Unknown
}
```

## Implementation Classes

The implementation classes will be in a separate package, e.g., `io.github.llama.impl.llamacpp.ffm`, and will not be directly exposed to users of the API. They will be loaded through the factory mechanism.

## Example Usage

```java
// Create model parameters
ModelParams modelParams = ModelParams.builder()
    .useMemoryMapping(true)
    .gpuLayerCount(32)
    .build();

// Load a model
Path modelPath = Paths.get("path/to/model.gguf");
LLM llm = LLMFactoryRegistry.getInstance().createLLM(modelPath, modelParams);

// Create context parameters
ContextParams contextParams = ContextParams.builder()
    .contextSize(2048)
    .threadCount(8)
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

        // Create a sampler
        SamplerParams samplerParams = SamplerParams.builder()
            .temperature(0.7f)
            .topP(0.9f)
            .maxTokens(100)
            .build();

        try (Sampler sampler = context.createSampler(samplerParams)) {
            // Sample tokens
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
            }

            System.out.println("Generated text: " + output.toString());
        }
    }
}
```

## Gradle Build System

The API will be built using Gradle, with the following structure:

```
jvm/
├── api/
│   ├── build.gradle
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── io/
│       │           └── github/
│       │               └── llama/
│       │                   └── api/
│       │                       ├── LLM.java
│       │                       ├── LLMFactoryRegistry.java
│       │                       ├── model/
│       │                       ├── context/
│       │                       ├── tokenization/
│       │                       ├── batch/
│       │                       ├── cache/
│       │                       ├── state/
│       │                       ├── decoding/
│       │                       ├── sampling/
│       │                       ├── adapter/
│       │                       └── spi/
│       └── test/
│           └── java/
│               └── io/
│                   └── github/
│                       └── llama/
│                           └── api/
│                               └── ...
├── impl-llamacpp/
│   ├── build.gradle
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── io/
│       │   │       └── github/
│       │   │           └── llama/
│       │   │               └── impl/
│       │   │                   └── llamacpp/
│       │   │                       ├── LlamaCppLLMFactory.java
│       │   │                       ├── LlamaCppLLM.java
│       │   │                       └── ...
│       │   └── resources/
│       │       └── META-INF/
│       │           └── services/
│       │               └── io.github.llama.api.spi.LLMFactory
│       └── test/
│           └── java/
│               └── io/
│                   └── github/
│                       └── llama/
│                           └── impl/
│                               └── llamacpp/
│                                   └── ...
└── build.gradle
```

### Root build.gradle

```groovy
plugins {
    id 'java-library'
    id 'maven-publish'
}

allprojects {
    group = 'io.github.llama'
    version = '0.1.0'

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply plugin: 'java-library'
    apply plugin: 'maven-publish'

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType(JavaCompile) {
        options.encoding = 'UTF-8'
    }

    test {
        useJUnitPlatform()
    }

    publishing {
        publications {
            maven(MavenPublication) {
                from components.java
            }
        }
    }
}
```

### API build.gradle

```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.2'
    testImplementation 'org.mockito:mockito-core:4.5.1'
}
```

### Implementation build.gradle

```groovy
dependencies {
    implementation project(':api')
    implementation 'net.java.dev.jna:jna:5.12.1'
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.2'
    testImplementation 'org.mockito:mockito-core:4.5.1'
}

// Custom task to build native library
task buildNative(type: Exec) {
    workingDir '../..'
    commandLine 'cmake', '-B', 'build', '-DBUILD_SHARED_LIBS=ON'
    doLast {
        exec {
            workingDir '../..'
            commandLine 'cmake', '--build', 'build', '--config', 'Release'
        }
    }
}

// Copy native library to resources
task copyNative(type: Copy, dependsOn: buildNative) {
    from '../../build/lib'
    into 'src/main/resources/native'
    include '*.so', '*.dll', '*.dylib'
}

// Make sure native library is built and copied before processing resources
processResources.dependsOn copyNative
```

## Conclusion

This design document outlines a Java-centric API for accessing large language model functionality. The API is organized into modules, each with its own package, and uses factories with the Java service loading mechanism to create implementation classes. The API provides a clean abstraction over the underlying implementation, hiding implementation details and providing a more natural Java experience.

The Gradle build system is used to build the API and its implementation, with separate modules for the API and the implementation. This allows for multiple implementations to be developed and used interchangeably.
