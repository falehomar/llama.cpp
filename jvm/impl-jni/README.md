# Llama.cpp JNI Wrapper

This is a Java Native Interface (JNI) wrapper for the llama.cpp library, enabling Java applications to leverage llama.cpp's capabilities for running large language models.

## Overview

This wrapper provides a Java API for the llama.cpp C library, allowing you to:

- Load and run llama.cpp compatible models
- Create inference contexts
- Process tokens in batches
- Sample tokens using various sampling strategies
- Tokenize and detokenize text

## Requirements

- Java JDK 11 or later
- GCC or compatible C compiler
- llama.cpp library (built with shared library support)
- On macOS: Xcode Command Line Tools

## Building

### 1. Build llama.cpp

First, build the llama.cpp library with shared library support:

```bash
cd /path/to/llama.cpp
mkdir -p build
cd build
cmake .. -DBUILD_SHARED_LIBS=ON
make -j
```

### 2. Build the JNI wrapper

Use the provided build script:

```bash
./build-llamajni-full.sh
```

This script will:
- Compile the Java classes
- Generate JNI headers
- Compile the C implementation
- Link with the llama.cpp library
- Output the shared library to `build/native/libllama_jni.dylib` (on macOS)

## Usage

### Loading a model

```java
// Initialize backend
LlamaJniBackend.llama_backend_init();

// Get default parameters
ModelParams params = LlamaJniBackend.llama_get_model_default_params();

// Load model
LLM model = new LlamaJniModel("/path/to/model.gguf", params);

// Get model info
ModelInfo info = model.getModelInfo();
System.out.println("Model: " + info.getName());
System.out.println("Vocab size: " + info.getVocabSize());
```

### Creating a context and processing tokens

```java
// Create context
ContextParams contextParams = new ContextParams();
contextParams.setContextSize(2048);
contextParams.setThreadCount(4);
Context context = model.createContext(contextParams);

// Tokenize text
Tokenizer tokenizer = model.getTokenizer();
int[] tokens = tokenizer.encode("Hello, world!");

// Process tokens
Batch batch = context.createBatch(tokens.length);
for (int i = 0; i < tokens.length; i++) {
    batch.add(tokens[i], i);
}
BatchResult result = context.process(batch);
```

### Sampling tokens

```java
// Create sampler
SamplerParams samplerParams = new SamplerParams();
samplerParams.setTemperature(0.8f);
samplerParams.setTopP(0.9f);
samplerParams.setTopK(40);
Sampler sampler = context.createSampler(samplerParams);

// Sample next token
int nextToken = sampler.sample();
System.out.println("Next token: " + tokenizer.decode(new int[]{nextToken}));
```

### Cleanup

```java
// Clean up resources
context.close();
model.close();
LlamaJniBackend.llama_backend_free();
```

## Running the test

A simple test is provided to verify the functionality:

```bash
./run-llamajni-test.sh /path/to/model.gguf
```

## Platform Support

This implementation has been tested on:
- macOS (Apple Silicon and Intel)

Additional platform support is planned for:
- Linux
- Windows

## Advanced Configuration

### Memory Mapping and GPU Layers

The `ModelParams` class provides options for configuring memory mapping, GPU acceleration, and more:

```java
ModelParams params = LlamaJniBackend.llama_get_model_default_params();
params.setUseMemoryMapping(true);
params.setGpuLayerCount(20); // Move 20 layers to GPU
```

### Context Parameters

The `ContextParams` class allows configuration of the inference context:

```java
ContextParams params = new ContextParams();
params.setContextSize(4096);  // Set context window size
params.setBatchSize(512);     // Set batch size
params.setThreadCount(8);     // Set number of threads
```

## Architecture

The implementation follows this architecture:
- Java API interfaces (`LLM`, `Context`, etc.) define the contract
- JNI implementations (`LlamaJniModel`, `LlamaJniContext`, etc.) provide Java-side logic
- Native C code in `llama_jni.c` interfaces with the llama.cpp library
- `LlamaJniBackend` provides native method declarations that bridge Java and C

## Security Considerations

- The JNI implementation requires proper management of native resources
- Care is taken to handle memory allocation and deallocation properly
- Exception handling is used to ensure resources are freed in error cases

## License

This wrapper follows the same licensing as the llama.cpp project, as it directly interfaces with its API.
