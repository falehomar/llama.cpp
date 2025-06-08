# llama.cpp Java API Tools

This document provides an overview of the available tools in the llama.cpp Java API and includes examples of how to implement them using the Java interface.

## Table of Contents
1. [Tokenization](#tokenization)
2. [Model Loading](#model-loading)
3. [Context Management](#context-management)
4. [Batch Processing](#batch-processing)
5. [Sampling](#sampling)
6. [Embeddings](#embeddings)
7. [KV Cache Management](#kv-cache-management)
8. [Server Integration](#server-integration)
9. [Performance Benchmarking](#performance-benchmarking)
10. [Text-to-Speech](#text-to-speech)

## Tokenization

The Tokenizer tool allows you to convert between text and token IDs.

### Java Implementation

```java
import io.github.llama.api.model.Model;
import io.github.llama.api.tokenization.Tokenizer;
import io.github.llama.api.tokenization.SpecialToken;

// Assuming you have a loaded model
Model model = /* loaded model */;

// Get the tokenizer
Tokenizer tokenizer = model.getTokenizer();

// Tokenize text
int[] tokens = tokenizer.tokenize("Hello, world!");

// Tokenize with options
int[] tokensWithOptions = tokenizer.tokenize("Hello, world!", true, false); // with BOS, without EOS

// Detokenize tokens
String text = tokenizer.detokenize(tokens);

// Get vocabulary size
int vocabSize = tokenizer.getVocabularySize();

// Get special token ID
int bosTokenId = tokenizer.getSpecialToken(SpecialToken.BOS);
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_tokenize()` | `Tokenizer.tokenize(String text)` | Tokenize text into token IDs |
| `llama_detokenize()` | `Tokenizer.detokenize(int[] tokens)` | Convert token IDs back to text |
| `llama_token_to_piece()` | `Tokenizer.getTokenText(int token)` | Get the text for a single token |
| `llama_token_get_text()` | `Tokenizer.getTokenText(int token)` | Get the text for a token |
| `llama_token_get_id()` | `Tokenizer.getTokenId(String text)` | Get the ID for a token text |
| `llama_token_get_type()` | `Tokenizer.getTokenType(int token)` | Get the type of a token |
| `llama_token_is_bos()` | `SpecialToken.BOS` + comparison | Check if a token is BOS |
| `llama_token_is_eos()` | `SpecialToken.EOS` + comparison | Check if a token is EOS |
| `llama_token_is_special()` | `Tokenizer.isSpecial(int token)` | Check if a token is special |
| `llama_vocab_n_tokens()` | `Tokenizer.getVocabularySize()` | Get the vocabulary size |

## Model Loading

The Model Loading tool allows you to load models from files.

### Java Implementation

```java
import io.github.llama.api.LLMFactoryRegistry;
import io.github.llama.api.spi.LLMFactory;
import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.model.ModelInfo;

import java.nio.file.Path;
import java.nio.file.Paths;

// Get the registry
LLMFactoryRegistry registry = LLMFactoryRegistry.getInstance();

// Get a factory
LLMFactory factory = registry.getFactories().get(0);

// Create parameters
ModelParams params = new ModelParams.Builder()
    .withModelPath(Paths.get("/path/to/model.gguf"))
    .withContextSize(2048)
    .withGpuLayers(32)
    .build();

// Load the model
Model model = factory.createModel(params);

// Get model information
ModelInfo info = model.getModelInfo();
System.out.println("Model name: " + info.getName());
System.out.println("Embedding size: " + info.getEmbeddingSize());

// Close the model when done
model.close();
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_model_default_params()` | `new ModelParams.Builder()` | Create default model parameters |
| `llama_model_load_from_file()` | `LLMFactory.createModel(ModelParams)` | Load a model from a file |
| `llama_model_free()` | `Model.close()` | Free model resources |
| `llama_model_n_ctx_train()` | `ModelInfo.getTrainingContextSize()` | Get the context size used during training |
| `llama_model_n_embd()` | `ModelInfo.getEmbeddingSize()` | Get the embedding size |
| `llama_model_n_layer()` | `ModelInfo.getLayerCount()` | Get the number of layers |
| `llama_model_n_head()` | `ModelInfo.getHeadCount()` | Get the number of attention heads |
| `llama_model_n_head_kv()` | `ModelInfo.getKeyValueHeadCount()` | Get the number of KV heads |
| `llama_model_meta_val_str()` | `ModelInfo.getMetadataValue(String key)` | Get a metadata value |
| `llama_model_meta_count()` | `ModelInfo.getMetadataCount()` | Get number of metadata items |
| `llama_model_desc()` | `ModelInfo.getDescription()` | Get model description |

## Context Management

The Context Management tool allows you to create and manage contexts for inference.

### Java Implementation

```java
import io.github.llama.api.LLM;
import io.github.llama.api.context.Context;
import io.github.llama.api.context.ContextParams;

// Assuming you have a loaded LLM
LLM llm = /* loaded LLM */;

// Create context parameters
ContextParams params = new ContextParams.Builder()
    .withBatchSize(512)
    .withThreadCount(4)
    .build();

// Create a context
Context context = llm.createContext(params);

// Use the context for inference (see batch processing)

// Close the context when done
context.close();
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_context_default_params()` | `new ContextParams.Builder()` | Create default context parameters |
| `llama_init_from_model()` | `LLM.createContext(ContextParams)` | Initialize a context from a model |
| `llama_free()` | `Context.close()` | Free context resources |
| `llama_n_ctx()` | `Context.getContextSize()` | Get context size |
| `llama_n_batch()` | `Context.getBatchSize()` | Get batch size |
| `llama_n_threads()` | `Context.getThreadCount()` | Get thread count |
| `llama_set_rng_seed()` | `Context.setRandomSeed(long seed)` | Set random seed |
| `llama_get_model()` | `Context.getModel()` | Get the model associated with a context |
| `llama_set_embeddings()` | `Context.setEmbeddingsEnabled(boolean enabled)` | Enable/disable embedding output |
| `llama_pooling_type()` | `Context.getPoolingType()` | Get pooling type |

## Batch Processing

The Batch Processing tool allows you to process batches of tokens for inference.

### Java Implementation

```java
import io.github.llama.api.context.Context;
import io.github.llama.api.batch.Batch;
import io.github.llama.api.batch.BatchResult;
import io.github.llama.api.tokenization.Tokenizer;

// Assuming you have a context and tokenizer
Context context = /* created context */;
Tokenizer tokenizer = /* model tokenizer */;

// Tokenize input text
int[] tokens = tokenizer.tokenize("Once upon a time", true, false);

// Create a batch
Batch batch = context.createBatch(tokens.length);

// Add tokens to the batch
for (int i = 0; i < tokens.length; i++) {
    batch.addToken(tokens[i], i, 0, i == tokens.length - 1);
}

// Process the batch
BatchResult result = context.process(batch);

// Get logits from the result
float[] logits = context.getLogits();
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_batch_init()` | `Context.createBatch(int maxTokens)` | Initialize a new batch |
| `llama_batch_free()` | `Batch` auto-managed by JVM | Free a batch (handled by JVM garbage collection) |
| `llama_batch_clear()` | `Batch.clear()` | Clear a batch for reuse |
| `llama_batch_add()` | `Batch.addToken(int token, int pos, int seq, boolean isLast)` | Add a token to a batch |
| `llama_encode()` | `Context.process(Batch)` | Process a batch (encoding phase) |
| `llama_decode()` | `Context.process(Batch)` | Process a batch (decoding phase) |
| `llama_get_logits()` | `Context.getLogits()` | Get logits from the last batch |
| `llama_get_logits_ith()` | `Context.getLogits(int index)` | Get logits for a specific position |

## Sampling

The Sampling tool allows you to generate text from model outputs.

### Java Implementation

```java
import io.github.llama.api.context.Context;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;

// Assuming you have processed a batch and have logits
Context context = /* context after processing */;

// Create sampler parameters
SamplerParams params = new SamplerParams.Builder()
    .withTemperature(0.8f)
    .withTopK(40)
    .withTopP(0.95f)
    .withPresencePenalty(1.0f)
    .withFrequencyPenalty(0.0f)
    .build();

// Create a sampler
Sampler sampler = context.createSampler(params);

// Sample next token
int nextToken = sampler.sample();

// Get the probability of the selected token
float probability = sampler.getProbability(nextToken);

// Close the sampler when done
sampler.close();
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_sampling_params` | `SamplerParams.Builder` | Parameters for sampling |
| `llama_sampling_init()` | `Context.createSampler(SamplerParams)` | Initialize a sampler |
| `llama_sampling_free()` | `Sampler.close()` | Free a sampler |
| `llama_sampling_sample()` | `Sampler.sample()` | Sample a token |
| `llama_sampling_accept()` | Handled internally | Accept a token |
| `llama_sampling_get_last_token()` | `Sampler.getLastToken()` | Get the last sampled token |
| `llama_sampling_get_probs()` | `Sampler.getProbabilities()` | Get probabilities for all tokens |
| `llama_sampling_get_p()` | `Sampler.getProbability(int token)` | Get probability for a specific token |
| `llama_sampling_reset()` | `Sampler.reset()` | Reset the sampler |
| `llama_sampling_set_top_k()` | `SamplerParams.Builder.withTopK()` | Set top-k value |
| `llama_sampling_set_top_p()` | `SamplerParams.Builder.withTopP()` | Set top-p value |
| `llama_sampling_set_min_p()` | `SamplerParams.Builder.withMinP()` | Set min-p value |
| `llama_sampling_set_temp()` | `SamplerParams.Builder.withTemperature()` | Set temperature |

## Embeddings

The Embeddings tool allows you to extract embeddings from processed tokens.

### Java Implementation

```java
import io.github.llama.api.context.Context;
import io.github.llama.api.embeddings.Embeddings;

// Assuming you have processed a batch
Context context = /* context after processing */;

// Get embeddings for the last token
float[] embeddings = context.getEmbeddings();

// Use the embeddings for downstream tasks
// For example, compute cosine similarity with another embedding
float similarity = computeCosineSimilarity(embeddings, otherEmbeddings);

// Helper function for cosine similarity
private static float computeCosineSimilarity(float[] a, float[] b) {
    float dotProduct = 0.0f;
    float normA = 0.0f;
    float normB = 0.0f;

    for (int i = 0; i < a.length; i++) {
        dotProduct += a[i] * b[i];
        normA += a[i] * a[i];
        normB += b[i] * b[i];
    }

    return dotProduct / (float)(Math.sqrt(normA) * Math.sqrt(normB));
}
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_set_embeddings()` | `Context.setEmbeddingsEnabled(boolean enabled)` | Enable/disable embedding extraction |
| `llama_get_embeddings()` | `Context.getEmbeddings()` | Get embeddings for the last token |
| `llama_get_embeddings_ith()` | `Context.getEmbeddings(int index)` | Get embeddings for a specific token |
| `llama_get_embeddings_seq()` | `Context.getSequenceEmbeddings(int sequence)` | Get embeddings for a sequence |
| `llama_embedding_normalize()` | Custom implementation | Normalize an embedding vector |
| `llama_embedding_distance()` | Custom implementation | Calculate distance between embeddings |

## KV Cache Management

The KV Cache Management tool allows you to manage the key-value cache for efficient inference.

### Java Implementation

```java
import io.github.llama.api.context.Context;
import io.github.llama.api.cache.KVCache;

// Assuming you have a context
Context context = /* created context */;

// Get the KV cache
KVCache kvCache = context.getKVCache();

// Clear the cache
kvCache.clear();

// Remove tokens from the cache (e.g., for context window management)
kvCache.removeSequence(0); // Remove sequence 0

// Add tokens to the cache (e.g., for continuous batching)
kvCache.addSequence(0, 100, 150); // Add sequence 0, positions 100-150
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_kv_self_clear()` | `KVCache.clear()` | Clear the KV cache |
| `llama_kv_self_seq_rm()` | `KVCache.removeSequence(int seq)` | Remove a sequence from the cache |
| `llama_kv_self_seq_add()` | `KVCache.addSequence(int seq, int posStart, int posEnd)` | Add sequence positions to the cache |
| `llama_kv_self_seq_pos_min()` | `KVCache.getMinPosition(int seq)` | Get minimum position in the cache for a sequence |
| `llama_kv_self_seq_pos_max()` | `KVCache.getMaxPosition(int seq)` | Get maximum position in the cache for a sequence |
| `llama_kv_self_can_shift()` | `KVCache.canShift()` | Check if the KV cache can be shifted |
| `llama_kv_self_update()` | `KVCache.update()` | Update the KV cache |
| `llama_kv_self_size()` | `KVCache.getSize()` | Get the size of the KV cache |
| `llama_kv_self_seq_size()` | `KVCache.getSequenceSize(int seq)` | Get the size of a sequence in the cache |

## Server Integration

The Server Integration tool allows you to integrate the Java API with a web server using Netty as the underlying HTTP server.

### Java Implementation

```java
import io.github.llama.api.LLM;
import io.github.llama.api.server.NettyServer;
import io.github.llama.api.server.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;

// Assuming you have a loaded LLM
LLM llm = /* loaded LLM */;

// Create server configuration
ServerConfig config = new ServerConfig.Builder()
    .withAddress(new InetSocketAddress("localhost", 8080))
    .withConcurrency(4)
    .withCorsOrigin("*")
    .build();

// Create and start the Netty-based server
NettyServer server = new NettyServer(llm, config);
server.start();

// For more advanced Netty configuration, you can use the lower-level API
public class LlamaNettyServer {
    private final LLM llm;
    private final ServerConfig config;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public LlamaNettyServer(LLM llm, ServerConfig config) {
        this.llm = llm;
        this.config = config;
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(config.getConcurrency());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                            new HttpServerCodec(),
                            new HttpObjectAggregator(65536),
                            new ChunkedWriteHandler(),
                            new LlamaHttpHandler(llm, config)
                        );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections
            ChannelFuture future = bootstrap.bind(config.getAddress()).sync();

            // Wait until the server socket is closed
            future.channel().closeFuture().sync();
        } finally {
            stop();
        }
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}

// Stop the server when done
server.stop();
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `server_context` | `NettyServer` class | Server context using Netty |
| `server_params` | `ServerConfig.Builder` | Server configuration parameters |
| `server_init()` | `new NettyServer(LLM, ServerConfig)` | Initialize a Netty-based server |
| `server_start()` | `NettyServer.start()` | Start the Netty server |
| `server_stop()` | `NettyServer.stop()` | Stop the Netty server |
| `server_running()` | `NettyServer.isRunning()` | Check if the Netty server is running |
| `server_completion()` | `NettyServer.completePrompt(String prompt, CompletionParams)` | Complete a prompt |
| `server_chat()` | `NettyServer.chat(List<ChatMessage>, ChatParams)` | Process a chat conversation |
| `server_get_embeddings()` | `NettyServer.getEmbeddings(String text, EmbeddingParams)` | Get embeddings for text |

### Netty-specific API Mapping

| Netty Component | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `EventLoopGroup` | `NioEventLoopGroup` | Netty event loop for handling I/O operations |
| `ServerBootstrap` | `ServerBootstrap` | Netty server bootstrap configuration |
| `ChannelHandler` | `LlamaHttpHandler` | Custom HTTP handler for llama.cpp requests |
| `ChannelPipeline` | `ch.pipeline()` | Pipeline for processing HTTP requests |
| N/A | `HttpServerCodec` | HTTP request decoder and response encoder |
| N/A | `HttpObjectAggregator` | Aggregates HTTP message fragments |
| N/A | `ChunkedWriteHandler` | Writes chunked responses for streaming generation |

## Performance Benchmarking

The Performance Benchmarking tool allows you to benchmark the performance of your models.

### Java Implementation

```java
import io.github.llama.api.LLM;
import io.github.llama.api.benchmark.Benchmark;
import io.github.llama.api.benchmark.BenchmarkResult;
import io.github.llama.api.benchmark.BenchmarkConfig;

// Assuming you have a loaded LLM
LLM llm = /* loaded LLM */;

// Create benchmark configuration
BenchmarkConfig config = new BenchmarkConfig.Builder()
    .withPrompt("Once upon a time")
    .withTokenCount(50)
    .withIterations(10)
    .withWarmupIterations(2)
    .build();

// Run the benchmark
Benchmark benchmark = new Benchmark(llm, config);
BenchmarkResult result = benchmark.run();

// Print results
System.out.println("Tokens per second: " + result.getTokensPerSecond());
System.out.println("Latency per token (ms): " + result.getLatencyPerToken());
System.out.println("Total time (ms): " + result.getTotalTimeMillis());
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_bench_params` | `BenchmarkConfig.Builder` | Benchmark configuration parameters |
| `llama_bench_init()` | `new Benchmark(LLM, BenchmarkConfig)` | Initialize a benchmark |
| `llama_bench_run()` | `Benchmark.run()` | Run the benchmark |
| `llama_bench_free()` | `Benchmark` auto-managed by JVM | Free benchmark resources (handled by JVM) |
| `llama_bench_get_results()` | `BenchmarkResult` from `run()` | Get benchmark results |
| `llama_time_us()` | `System.nanoTime() / 1000` | Get current time in microseconds |

## Text-to-Speech

The Text-to-Speech tool allows you to convert text to speech using appropriate models.

### Java Implementation

```java
import io.github.llama.api.LLMFactoryRegistry;
import io.github.llama.api.spi.LLMFactory;
import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.tts.TTSParams;
import io.github.llama.api.tts.TTSResult;

import java.nio.file.Path;
import java.nio.file.Paths;

// Get the registry
LLMFactoryRegistry registry = LLMFactoryRegistry.getInstance();

// Get a factory
LLMFactory factory = registry.getFactories().get(0);

// Load a TTS model
ModelParams modelParams = new ModelParams.Builder()
    .withModelPath(Paths.get("/path/to/tts/model.gguf"))
    .build();
Model ttsModel = factory.createModel(modelParams);

// Create TTS parameters
TTSParams ttsParams = new TTSParams.Builder()
    .withText("Hello, this is a test of text to speech conversion.")
    .withSpeakerId(0)
    .withSpeed(1.0f)
    .build();

// Generate speech
TTSResult result = ttsModel.generateSpeech(ttsParams);

// Save audio to file
result.saveToFile(Paths.get("output.wav"));

// Clean up
ttsModel.close();
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_tts_params` | `TTSParams.Builder` | TTS parameters |
| `llama_tts_params_default()` | `new TTSParams.Builder()` | Create default TTS parameters |
| `llama_tts_generate()` | `Model.generateSpeech(TTSParams)` | Generate speech from text |
| `llama_tts_inject_prompt()` | `TTSParams.Builder.withPrompt(String)` | Set custom TTS prompt |
| `llama_tts_generate_with_timestamps()` | `Model.generateSpeechWithTimestamps(TTSParams)` | Generate speech with word timestamps |
| `llama_tts_results_free()` | `TTSResult.close()` | Free TTS results |
| `llama_tts_results_get_audio_data()` | `TTSResult.getAudioData()` | Get audio data as float array |
| `llama_tts_results_get_audio_n()` | `TTSResult.getAudioLength()` | Get audio data length |
| `llama_tts_results_get_timestamps()` | `TTSResult.getTimestamps()` | Get word timestamps |

## Additional Tools

## Quantization

The Quantization tool allows you to reduce the size and memory footprint of models by quantizing them to lower precision.

### Java Implementation

```java
import io.github.llama.api.model.Model;
import io.github.llama.api.model.QuantizeParams;
import io.github.llama.api.model.ModelManager;

import java.nio.file.Path;
import java.nio.file.Paths;

// Create quantization parameters
QuantizeParams params = new QuantizeParams.Builder()
    .withInputPath(Paths.get("/path/to/input/model.gguf"))
    .withOutputPath(Paths.get("/path/to/output/model-q4_k.gguf"))
    .withQuantizationType("q4_k") // Common types: q4_0, q4_1, q5_0, q5_1, q8_0
    .build();

// Quantize the model
ModelManager.quantize(params);

// Load the quantized model
// (similar to Model Loading example)
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_model_quantize_params` | `QuantizeParams.Builder` | Quantization parameters |
| `llama_model_quantize_params_default()` | `new QuantizeParams.Builder()` | Create default quantization parameters |
| `llama_model_quantize()` | `ModelManager.quantize(QuantizeParams)` | Quantize a model |
| `llama_model_quantize_get_type_name()` | `QuantizeParams.getQuantizationType()` | Get quantization type name |
| `llama_model_quantize_set_params()` | `QuantizeParams.Builder` methods | Set quantization parameters |
| `llama_model_tensor_type_name()` | Static mapping in `QuantizationType` enum | Get tensor type name |

## Grammar-Based Sampling

The Grammar-Based Sampling tool allows you to constrain the model's output to follow a specific grammar, which is useful for generating structured outputs like JSON.

### Java Implementation

```java
import io.github.llama.api.context.Context;
import io.github.llama.api.sampling.Sampler;
import io.github.llama.api.sampling.SamplerParams;
import io.github.llama.api.grammar.Grammar;
import io.github.llama.api.grammar.GrammarParser;

// Assuming you have a context after processing
Context context = /* context after processing */;

// Create a grammar from a BNF-style grammar definition
String grammarDefinition = """
    root ::= object
    object ::= "{" ws pairs? ws "}"
    pairs ::= pair (ws "," ws pair)*
    pair ::= string ws ":" ws value
    ...
    """;

Grammar grammar = GrammarParser.parse(grammarDefinition);

// Create sampler parameters with the grammar
SamplerParams params = new SamplerParams.Builder()
    .withTemperature(0.8f)
    .withGrammar(grammar)
    .build();

// Create a sampler that respects the grammar
Sampler sampler = context.createSampler(params);

// Sample tokens until end of generation
StringBuilder output = new StringBuilder();
int token;
while ((token = sampler.sample()) != tokenizer.getSpecialToken(SpecialToken.EOS)) {
    output.append(tokenizer.detokenize(new int[]{token}));
}

// The resulting output will conform to the grammar
System.out.println("Grammar-constrained output: " + output.toString());

// Clean up
sampler.close();
grammar.close();
```

### C/C++ to Java API Mapping

| C/C++ API Function | Java API Equivalent | Description |
|-------------------|---------------------|-------------|
| `llama_grammar_init()` | `GrammarParser.parse(String grammarDefinition)` | Initialize a grammar from a definition |
| `llama_grammar_free()` | `Grammar.close()` | Free a grammar |
| `llama_grammar_copy()` | `Grammar.copy()` | Make a copy of a grammar |
| `llama_grammar_accept_token()` | Handled internally in `Sampler` | Check if a token is accepted by the grammar |
| `llama_grammar_get_rule()` | `Grammar.getRule(int index)` | Get a rule from a grammar |
| `llama_grammar_get_rule_count()` | `Grammar.getRuleCount()` | Get number of rules in the grammar |
| `llama_grammar_gen_json_schema()` | `Grammar.fromJsonSchema(String schema)` | Create a grammar from a JSON schema |
| `llama_grammar_gen_pydantic_model()` | `Grammar.fromPydanticModel(String model)` | Create a grammar from a Pydantic model |

## Multimodal Input Processing

The Multimodal Input Processing tool allows you to process inputs that combine multiple modalities like text and images.

### Java Implementation

```java
import io.github.llama.api.LLMFactoryRegistry;
import io.github.llama.api.spi.LLMFactory;
import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.context.Context;
import io.github.llama.api.multimodal.ImageProcessor;
import io.github.llama.api.multimodal.Image;
import io.github.llama.api.batch.Batch;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

// Load a multimodal model (e.g., LLaVA)
ModelParams params = new ModelParams.Builder()
    .withModelPath(Paths.get("/path/to/multimodal/model.gguf"))
    .build();

LLMFactory factory = LLMFactoryRegistry.getInstance().getFactories().get(0);
Model model = factory.createModel(params);

// Create a context
Context context = model.createContext(new ContextParams.Builder().build());

// Load an image
BufferedImage bufferedImage = ImageIO.read(new File("/path/to/image.jpg"));
Image image = ImageProcessor.createFromBufferedImage(bufferedImage);

// Tokenize text prompt and create batch
int[] tokens = model.getTokenizer().tokenize("Describe what you see in this image:", true, false);
Batch batch = context.createBatch(tokens.length + 1); // +1 for the image token

// Add tokens to batch
for (int i = 0; i < tokens.length; i++) {
    batch.addToken(tokens[i], i, 0, false);
}

// Add image to batch
batch.addImage(image, tokens.length, 0, true);

// Process batch and generate response
// ...continue with processing and sampling as in previous examples

// Clean up
image.close();
context.close();
model.close();
```

## Prompt Templates

The Prompt Templates tool allows you to use predefined templates for common prompt formats.

### Java Implementation

```java
import io.github.llama.api.model.Model;
import io.github.llama.api.template.PromptTemplate;
import io.github.llama.api.template.ChatMessage;
import io.github.llama.api.template.ChatRole;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

// Assuming you have a loaded model
Model model = /* loaded model */;

// Get the model's default chat template or specify a custom one
PromptTemplate template = model.getDefaultChatTemplate();

// Create a conversation
List<ChatMessage> messages = Arrays.asList(
    new ChatMessage(ChatRole.SYSTEM, "You are a helpful assistant."),
    new ChatMessage(ChatRole.USER, "Hi, can you help me with a math problem?"),
    new ChatMessage(ChatRole.ASSISTANT, "Of course! I'd be happy to help. What's the math problem?"),
    new ChatMessage(ChatRole.USER, "What is the derivative of f(x) = x^2 + 3x + 2?")
);

// Format the conversation using the template
String formattedPrompt = template.format(messages);

// Use the formatted prompt for tokenization and processing
// ...continue with tokenization and processing as in previous examples

// You can also create a custom prompt template
String customTemplateStr = "{{#system}}{{content}}{{/system}}\n\n{{#user}}{{content}}{{/user}}\n\n{{#assistant}}{{content}}{{/assistant}}";
PromptTemplate customTemplate = new PromptTemplate(customTemplateStr);

// Format with custom parameters
Map<String, String> params = new HashMap<>();
params.put("instruction", "Translate to French:");
params.put("input", "Hello, world!");

String customPrompt = customTemplate.format(params);
```

## Function Calling

The Function Calling tool allows you to implement structured function calling capabilities.

### Java Implementation

```java
import io.github.llama.api.model.Model;
import io.github.llama.api.context.Context;
import io.github.llama.api.function.FunctionRegistry;
import io.github.llama.api.function.FunctionDefinition;
import io.github.llama.api.function.FunctionParameter;
import io.github.llama.api.function.FunctionCallDetector;
import io.github.llama.api.function.FunctionCall;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Create function definitions
FunctionDefinition getWeather = new FunctionDefinition.Builder()
    .withName("get_weather")
    .withDescription("Get the current weather for a location")
    .withParameter(new FunctionParameter.Builder()
        .withName("location")
        .withType("string")
        .withDescription("The city and state, e.g., San Francisco, CA")
        .withRequired(true)
        .build())
    .withParameter(new FunctionParameter.Builder()
        .withName("unit")
        .withType("string")
        .withDescription("The temperature unit to use: 'celsius' or 'fahrenheit'")
        .withRequired(false)
        .withEnumValues(Arrays.asList("celsius", "fahrenheit"))
        .withDefaultValue("celsius")
        .build())
    .build();

// Register functions with the function registry
FunctionRegistry registry = new FunctionRegistry();
registry.register(getWeather);

// Create a function call detector
FunctionCallDetector detector = new FunctionCallDetector(registry);

// After generating text with the model
String modelOutput = /* model-generated text */;

// Detect and parse function calls
List<FunctionCall> functionCalls = detector.detect(modelOutput);

// Process function calls
for (FunctionCall call : functionCalls) {
    if (call.getName().equals("get_weather")) {
        Map<String, Object> args = call.getArguments();
        String location = (String) args.get("location");
        String unit = args.getOrDefault("unit", "celsius").toString();

        // Call the actual implementation
        String weatherResult = getWeatherImplementation(location, unit);

        // Use the result in the next model prompt
        // ...continue conversation with the weather information
    }
}

// Example implementation (would be replaced with actual API calls)
private static String getWeatherImplementation(String location, String unit) {
    return "The current weather in " + location + " is 22 degrees " + unit +
           " with partly cloudy skies.";
}
```

## State Management

The State Management tool allows you to save and load the internal state of the model, which is useful for continuing generation later.

### Java Implementation

```java
import io.github.llama.api.context.Context;
import io.github.llama.api.state.ContextState;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

// Assuming you have a context after processing some tokens
Context context = /* context after processing */;

// Save the context state to a file
Path statePath = Paths.get("/path/to/saved/state.bin");
try {
    ContextState state = context.saveState();
    state.saveToFile(statePath);
    state.close();
} catch (IOException e) {
    System.err.println("Failed to save state: " + e.getMessage());
}

// Later, load the state
try {
    ContextState state = ContextState.loadFromFile(statePath);
    context.loadState(state);
    state.close();

    // Continue generation from the loaded state
    // ...continue with processing and sampling
} catch (IOException e) {
    System.err.println("Failed to load state: " + e.getMessage());
}
```

## Interactive CLI Tool

The Interactive CLI Tool allows you to build command-line interfaces for interacting with the model.

### Java Implementation

```java
import io.github.llama.api.LLMFactoryRegistry;
import io.github.llama.api.spi.LLMFactory;
import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.cli.CommandLineInterface;
import io.github.llama.api.cli.CLIConfig;

import java.nio.file.Paths;
import java.io.IOException;

public class InteractiveCLI {
    public static void main(String[] args) {
        try {
            // Load a model
            LLMFactory factory = LLMFactoryRegistry.getInstance().getFactories().get(0);
            ModelParams params = new ModelParams.Builder()
                .withModelPath(Paths.get(args[0]))
                .build();

            Model model = factory.createModel(params);

            // Configure CLI options
            CLIConfig config = new CLIConfig.Builder()
                .withModel(model)
                .withPrompt("### User: ")
                .withResponsePrefix("### Assistant: ")
                .withTemperature(0.7f)
                .withHistorySize(10)
                .build();

            // Create and run the CLI
            CommandLineInterface cli = new CommandLineInterface(config);
            cli.run();

            // Clean up
            cli.close();
            model.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

## Distributed Inference

The Distributed Inference tool allows you to run inference across multiple machines.

### Java Implementation

```java
import io.github.llama.api.LLM;
import io.github.llama.api.distributed.DistributedConfig;
import io.github.llama.api.distributed.DistributedInference;
import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.LLMFactoryRegistry;

import java.util.List;
import java.util.Arrays;
import java.nio.file.Paths;

// Create distributed configuration
DistributedConfig config = new DistributedConfig.Builder()
    .withWorkers(Arrays.asList(
        "worker1.example.com:8000",
        "worker2.example.com:8000",
        "worker3.example.com:8000"
    ))
    .withTimeout(30000)  // 30 seconds in milliseconds
    .build();

// Create the distributed inference engine
DistributedInference inference = new DistributedInference(config);

// Load the model on all workers
ModelParams params = new ModelParams.Builder()
    .withModelPath(Paths.get("/path/to/model.gguf"))
    .build();

inference.loadModel(params);

// Run inference
String prompt = "Translate the following English text to French: 'Hello, world!'";
String result = inference.generate(prompt);
System.out.println("Result: " + result);

// Clean up
inference.close();
```

## Model Export and Format Conversion

The Model Export and Format Conversion tool allows you to convert between different model formats.

### Java Implementation

```java
import io.github.llama.api.model.ModelConverter;
import io.github.llama.api.model.ConversionParams;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class ModelFormatConversion {
    public static void main(String[] args) {
        try {
            // Define conversion parameters
            Path inputPath = Paths.get("/path/to/input/model.bin");
            Path outputPath = Paths.get("/path/to/output/model.gguf");

            ConversionParams params = new ConversionParams.Builder()
                .withInputPath(inputPath)
                .withOutputPath(outputPath)
                .withInputFormat("huggingface")  // or "pytorch", "safetensors", etc.
                .withOutputFormat("gguf")
                .withVerbose(true)
                .build();

            // Perform the conversion
            ModelConverter.convert(params);

            System.out.println("Model conversion completed successfully.");

        } catch (IOException e) {
            System.err.println("Conversion error: " + e.getMessage());
        }
    }
}
```

## Additional Resources

- [llama.cpp API Documentation](/docs/llama_api_documentation.md)
- [llama.cpp C++ API Documentation](/llama_cpp_api_documentation.md)
- [API Design Document](/docs/api-design-document.md)
