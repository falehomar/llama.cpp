package io.github.llama.impl.foreign;

import io.github.llama.impl.foreign.jextract.Llama;

import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for loading and interacting with the native llama.cpp library using the Java Foreign and Native Memory API.
 *
 * The native library is loaded using the following strategy:
 * 1. Check if a custom path is specified via the system property "llama.library.path".
 *    This can be either a direct path to the library file or a directory containing the library.
 * 2. If not found, check the default path at "/Users/e168693/TeamCompose/submodules/llama.cpp/build/bin".
 * 3. If still not found, try to extract the library from the resources.
 *
 * To specify a custom path, use: -Dllama.library.path=/path/to/library
 *
 * This implementation uses jextract-generated bindings for the llama.h header file.
 */
public class NativeLibrary {
    private static final String LIBRARY_NAME = "llama";
    private static final String DEFAULT_LIBRARY_PATH = "/Users/e168693/TeamCompose/submodules/llama.cpp/build/bin";
    private static final String LIBRARY_PATH_PROPERTY = "llama.library.path";
    private static boolean initialized = false;

    /**
     * Initializes the native library.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            System.out.println("Initializing native library...");

            // Try to find the native library in the default path first
            Path libraryPath = findNativeLibrary();
            if (libraryPath != null) {
                System.out.println("Found native library at: " + libraryPath);
            } else {
                // If not found, extract the native library from resources to a temporary file
                libraryPath = extractNativeLibrary();
                System.out.println("Extracted native library to: " + libraryPath);
            }

            // Load the native library
            System.out.println("Loading native library...");
            System.load(libraryPath.toString());
            System.out.println("Native library loaded successfully");

            // Create a symbol lookup for the library
            System.out.println("Creating symbol lookup...");
            libraryLookup = SymbolLookup.loaderLookup();
            System.out.println("Symbol lookup created successfully");

            // Create a linker for the current platform
            System.out.println("Creating linker...");
            linker = Linker.nativeLinker();
            System.out.println("Linker created successfully");

            // Mark as initialized before calling any native methods
            initialized = true;
            System.out.println("Native library marked as initialized");

            // Initialize the llama backend
            System.out.println("Initializing llama backend...");
            llamaBackendInit();
            System.out.println("Llama backend initialized successfully");

            System.out.println("Native library initialization complete");
        } catch (Exception e) {
            // Reset initialized flag if initialization fails
            initialized = false;

            System.err.println("Failed to initialize native library: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize native library", e);
        }
    }

    /**
     * Finds the native library in the configured paths.
     *
     * @return Path to the native library, or null if not found
     */
    private static Path findNativeLibrary() {
        String libraryFileName = System.mapLibraryName(LIBRARY_NAME);
        System.out.println("Looking for library file: " + libraryFileName);

        // First check if a custom path is specified via system property
        String customPath = System.getProperty(LIBRARY_PATH_PROPERTY);
        if (customPath != null && !customPath.isEmpty()) {
            // Check if the custom path is a direct path to the library file
            Path directPath = Paths.get(customPath);
            if (Files.exists(directPath) && !Files.isDirectory(directPath)) {
                System.out.println("Using direct library path: " + directPath);
                return directPath;
            }

            // Otherwise, treat it as a directory and append the library filename
            Path customLibraryPath = Paths.get(customPath, libraryFileName);
            System.out.println("Checking custom path: " + customLibraryPath);

            if (Files.exists(customLibraryPath)) {
                return customLibraryPath;
            }

            System.out.println("Library not found at custom path");
        }

        // Then check the default path
        Path defaultLibraryPath = Paths.get(DEFAULT_LIBRARY_PATH, libraryFileName);
        System.out.println("Checking default path: " + defaultLibraryPath);

        if (Files.exists(defaultLibraryPath)) {
            return defaultLibraryPath;
        }

        System.out.println("Library not found at default path, will try to extract from resources");
        return null;
    }

    /**
     * Extracts the native library from the resources to a temporary file.
     *
     * @return Path to the extracted library
     * @throws IOException If the library cannot be extracted
     */
    private static Path extractNativeLibrary() throws IOException {
        String libraryFileName = System.mapLibraryName(LIBRARY_NAME);
        System.out.println("Library file name: " + libraryFileName);

        Path tempFile = Files.createTempFile(LIBRARY_NAME, null);
        System.out.println("Created temp file: " + tempFile);

        String resourcePath = "/native/" + libraryFileName;
        System.out.println("Looking for resource: " + resourcePath);

        try (var inputStream = NativeLibrary.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Native library not found in resources: " + resourcePath);

                // List available resources in the /native directory
                System.out.println("Available resources in /native directory:");
                var urls = NativeLibrary.class.getClassLoader().getResources("native");
                while (urls.hasMoreElements()) {
                    System.out.println("  " + urls.nextElement());
                }

                throw new IOException("Native library not found in resources: " + resourcePath);
            }

            System.out.println("Found native library in resources, copying to temp file...");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Native library copied to temp file: " + tempFile);
        }

        return tempFile;
    }

    /**
     * Initializes the llama backend.
     */
    public static void llamaBackendInit() {
        try {
            MethodHandle handle = getMethodHandle("llama_backend_init", FunctionDescriptor.ofVoid());
            handle.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to initialize llama backend", e);
        }
    }

    /**
     * Frees the llama backend.
     */
    public static void llamaBackendFree() {
        try {
            MethodHandle handle = getMethodHandle("llama_backend_free", FunctionDescriptor.ofVoid());
            handle.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to free llama backend", e);
        }
    }

    /**
     * Gets default model parameters.
     *
     * @return Memory segment containing the model parameters
     */
    public static MemorySegment llamaModelDefaultParams() {
        try {
            MethodHandle handle = getMethodHandle("llama_model_default_params",
                FunctionDescriptor.of(ValueLayout.ADDRESS));
            return (MemorySegment) handle.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get default model parameters", e);
        }
    }

    /**
     * Loads a model from a file.
     *
     * @param path Path to the model file
     * @param params Model parameters
     * @return Memory segment containing the model handle
     */
    public static MemorySegment llamaModelLoadFromFile(String path, MemorySegment params) {
        try {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment pathSegment = arena.allocateFrom(path);

                MethodHandle handle = getMethodHandle("llama_model_load_from_file",
                    FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

                return (MemorySegment) handle.invokeExact(pathSegment, params);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to load model from file: " + path, e);
        }
    }

    /**
     * Frees a model.
     *
     * @param model Memory segment containing the model handle
     */
    public static void llamaModelFree(MemorySegment model) {
        try {
            MethodHandle handle = getMethodHandle("llama_model_free",
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
            handle.invokeExact(model);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to free model", e);
        }
    }

    /**
     * Gets the number of parameters in the model.
     *
     * @param model Memory segment containing the model handle
     * @return Number of parameters
     */
    public static long llamaModelNParams(MemorySegment model) {
        try {
            MethodHandle handle = getMethodHandle("llama_model_n_params",
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));
            return (long) handle.invokeExact(model);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get model parameter count", e);
        }
    }

    /**
     * Gets the context size used during training.
     *
     * @param model Memory segment containing the model handle
     * @return Context size
     */
    public static int llamaModelNCtxTrain(MemorySegment model) {
        try {
            MethodHandle handle = getMethodHandle("llama_model_n_ctx_train",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
            return (int) handle.invokeExact(model);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get model context size", e);
        }
    }

    /**
     * Gets the embedding size.
     *
     * @param model Memory segment containing the model handle
     * @return Embedding size
     */
    public static int llamaModelNEmbd(MemorySegment model) {
        try {
            MethodHandle handle = getMethodHandle("llama_model_n_embd",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
            return (int) handle.invokeExact(model);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get model embedding size", e);
        }
    }

    /**
     * Gets the number of layers.
     *
     * @param model Memory segment containing the model handle
     * @return Number of layers
     */
    public static int llamaModelNLayer(MemorySegment model) {
        try {
            MethodHandle handle = getMethodHandle("llama_model_n_layer",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
            return (int) handle.invokeExact(model);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get model layer count", e);
        }
    }

    /**
     * Gets the number of attention heads.
     *
     * @param model Memory segment containing the model handle
     * @return Number of attention heads
     */
    public static int llamaModelNHead(MemorySegment model) {
        try {
            MethodHandle handle = getMethodHandle("llama_model_n_head",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
            return (int) handle.invokeExact(model);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get model head count", e);
        }
    }

    /**
     * Gets a string describing the model type.
     *
     * @param model Memory segment containing the model handle
     * @return Model description
     */
    public static String llamaModelDesc(MemorySegment model) {
        try {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment buffer = arena.allocate(1024); // Allocate a buffer for the description

                MethodHandle handle = getMethodHandle("llama_model_desc",
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG));

                int length = (int) handle.invokeExact(model, buffer, 1024L);

                if (length <= 0) {
                    return "";
                }

                return buffer.getString(0);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get model description", e);
        }
    }

    /**
     * Gets the vocabulary from a model.
     *
     * @param model Memory segment containing the model handle
     * @return Memory segment containing the vocabulary handle
     */
    public static MemorySegment llamaModelGetVocab(MemorySegment model) {
        try {
            MethodHandle handle = getMethodHandle("llama_model_get_vocab",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
            return (MemorySegment) handle.invokeExact(model);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get model vocabulary", e);
        }
    }

    /**
     * Gets default context parameters.
     *
     * @return Memory segment containing the context parameters
     */
    public static MemorySegment llamaContextDefaultParams() {
        try {
            MethodHandle handle = getMethodHandle("llama_context_default_params",
                FunctionDescriptor.of(ValueLayout.ADDRESS));
            return (MemorySegment) handle.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get default context parameters", e);
        }
    }

    /**
     * Creates a context from a model.
     *
     * @param model Memory segment containing the model handle
     * @param params Context parameters
     * @return Memory segment containing the context handle
     */
    public static MemorySegment llamaInitFromModel(MemorySegment model, MemorySegment params) {
        try {
            MethodHandle handle = getMethodHandle("llama_init_from_model",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));
            return (MemorySegment) handle.invokeExact(model, params);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create context from model", e);
        }
    }

    /**
     * Frees a context.
     *
     * @param context Memory segment containing the context handle
     */
    public static void llamaFree(MemorySegment context) {
        try {
            MethodHandle handle = getMethodHandle("llama_free",
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
            handle.invokeExact(context);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to free context", e);
        }
    }

    /**
     * Tokenizes text into an array of token IDs.
     *
     * @param vocab Memory segment containing the vocabulary handle
     * @param text Text to tokenize
     * @param addBos Whether to add a beginning-of-sequence token
     * @param addEos Whether to add an end-of-sequence token
     * @return Array of token IDs
     */
    public static int[] llamaTokenize(MemorySegment vocab, String text, boolean addBos, boolean addEos) {
        try {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment textSegment = arena.allocateFrom(text);
                int textLen = text.length();

                // Allocate a buffer for the tokens (assuming max 4 tokens per character)
                int maxTokens = textLen * 4;
                MemorySegment tokensSegment = arena.allocate(maxTokens * 4); // 4 bytes per token

                MethodHandle handle = getMethodHandle("llama_tokenize",
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
                                         ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                                         ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN));

                int tokenCount = (int) handle.invokeExact(vocab, textSegment, textLen, tokensSegment, maxTokens, addBos, addEos);

                if (tokenCount < 0) {
                    // If tokenCount is negative, it means the buffer was too small
                    // Allocate a larger buffer and try again
                    maxTokens = -tokenCount;
                    tokensSegment = arena.allocate(maxTokens * 4);

                    tokenCount = (int) handle.invokeExact(vocab, textSegment, textLen, tokensSegment, maxTokens, addBos, addEos);
                }

                // Copy the tokens to a Java array
                int[] tokens = new int[tokenCount];
                for (int i = 0; i < tokenCount; i++) {
                    tokens[i] = tokensSegment.getAtIndex(ValueLayout.JAVA_INT, i);
                }

                return tokens;
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to tokenize text: " + text, e);
        }
    }

    /**
     * Gets the text for a token ID.
     *
     * @param vocab Memory segment containing the vocabulary handle
     * @param tokenId Token ID
     * @return Token text
     */
    public static String llamaTokenToText(MemorySegment vocab, int tokenId) {
        try {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment buffer = arena.allocate(256); // Allocate a buffer for the token text

                MethodHandle handle = getMethodHandle("llama_token_to_piece",
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                                         ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
                                         ValueLayout.JAVA_BOOLEAN));

                int length = (int) handle.invokeExact(vocab, tokenId, buffer, 256, 0, false);

                if (length <= 0) {
                    return "";
                }

                // Convert the buffer to a Java string
                byte[] bytes = new byte[length];
                for (int i = 0; i < length; i++) {
                    bytes[i] = buffer.get(ValueLayout.JAVA_BYTE, i);
                }

                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get token text for token ID: " + tokenId, e);
        }
    }

    /**
     * Gets the beginning-of-sequence token.
     *
     * @param vocab Memory segment containing the vocabulary handle
     * @return BOS token ID
     */
    public static int llamaVocabBos(MemorySegment vocab) {
        try {
            MethodHandle handle = getMethodHandle("llama_vocab_bos",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
            return (int) handle.invokeExact(vocab);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get BOS token", e);
        }
    }

    /**
     * Gets the end-of-sequence token.
     *
     * @param vocab Memory segment containing the vocabulary handle
     * @return EOS token ID
     */
    public static int llamaVocabEos(MemorySegment vocab) {
        try {
            MethodHandle handle = getMethodHandle("llama_vocab_eos",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
            return (int) handle.invokeExact(vocab);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get EOS token", e);
        }
    }

    /**
     * Creates a batch of tokens.
     *
     * @param maxTokens Maximum number of tokens in the batch
     * @return Memory segment containing the batch handle
     */
    public static MemorySegment llamaBatchInit(int maxTokens) {
        try {
            MethodHandle handle = getMethodHandle("llama_batch_init",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            return (MemorySegment) handle.invokeExact(maxTokens, 0, 1);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create batch", e);
        }
    }

    /**
     * Frees a batch.
     *
     * @param batch Memory segment containing the batch handle
     */
    public static void llamaBatchFree(MemorySegment batch) {
        try {
            MethodHandle handle = getMethodHandle("llama_batch_free",
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
            handle.invokeExact(batch);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to free batch", e);
        }
    }

    /**
     * Processes a batch of tokens.
     *
     * @param context Memory segment containing the context handle
     * @param batch Memory segment containing the batch handle
     * @return 0 on success, non-zero on failure
     */
    public static int llamaDecode(MemorySegment context, MemorySegment batch) {
        try {
            MethodHandle handle = getMethodHandle("llama_decode",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS));
            return (int) handle.invokeExact(context, batch);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to process batch", e);
        }
    }

    /**
     * Gets the logits from the last processed batch.
     *
     * @param context Memory segment containing the context handle
     * @return Memory segment containing the logits
     */
    public static MemorySegment llamaGetLogits(MemorySegment context) {
        try {
            MethodHandle handle = getMethodHandle("llama_get_logits",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
            return (MemorySegment) handle.invokeExact(context);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get logits", e);
        }
    }

    /**
     * Creates a sampler for generating tokens.
     *
     * @param temperature Temperature parameter
     * @return Memory segment containing the sampler handle
     */
    public static MemorySegment llamaSamplerInitTemp(float temperature) {
        try {
            MethodHandle handle = getMethodHandle("llama_sampler_init_temp",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_FLOAT));
            return (MemorySegment) handle.invokeExact(temperature);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create temperature sampler", e);
        }
    }

    /**
     * Creates a top-p sampler.
     *
     * @param p Top-p value
     * @param minKeep Minimum number of tokens to keep
     * @return Memory segment containing the sampler handle
     */
    public static MemorySegment llamaSamplerInitTopP(float p, int minKeep) {
        try {
            MethodHandle handle = getMethodHandle("llama_sampler_init_top_p",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_LONG));
            return (MemorySegment) handle.invokeExact(p, (long) minKeep);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create top-p sampler", e);
        }
    }

    /**
     * Creates a distribution sampler.
     *
     * @param seed Random seed
     * @return Memory segment containing the sampler handle
     */
    public static MemorySegment llamaSamplerInitDist(int seed) {
        try {
            MethodHandle handle = getMethodHandle("llama_sampler_init_dist",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
            return (MemorySegment) handle.invokeExact(seed);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create distribution sampler", e);
        }
    }

    /**
     * Creates a sampler chain.
     *
     * @return Memory segment containing the sampler chain handle
     */
    public static MemorySegment llamaSamplerChainInit() {
        try {
            MethodHandle paramsHandle = getMethodHandle("llama_sampler_chain_default_params",
                FunctionDescriptor.of(ValueLayout.ADDRESS));
            MemorySegment params = (MemorySegment) paramsHandle.invokeExact();

            MethodHandle handle = getMethodHandle("llama_sampler_chain_init",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
            return (MemorySegment) handle.invokeExact(params);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create sampler chain", e);
        }
    }

    /**
     * Adds a sampler to a sampler chain.
     *
     * @param chain Memory segment containing the sampler chain handle
     * @param sampler Memory segment containing the sampler handle
     */
    public static void llamaSamplerChainAdd(MemorySegment chain, MemorySegment sampler) {
        try {
            MethodHandle handle = getMethodHandle("llama_sampler_chain_add",
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
            handle.invokeExact(chain, sampler);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to add sampler to chain", e);
        }
    }

    /**
     * Samples a token from logits.
     *
     * @param sampler Memory segment containing the sampler handle
     * @param context Memory segment containing the context handle
     * @return Sampled token ID
     */
    public static int llamaSamplerSample(MemorySegment sampler, MemorySegment context) {
        try {
            MethodHandle handle = getMethodHandle("llama_sampler_sample",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
            return (int) handle.invokeExact(sampler, context, -1);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to sample token", e);
        }
    }

    /**
     * Frees a sampler.
     *
     * @param sampler Memory segment containing the sampler handle
     */
    public static void llamaSamplerFree(MemorySegment sampler) {
        try {
            MethodHandle handle = getMethodHandle("llama_sampler_free",
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
            handle.invokeExact(sampler);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to free sampler", e);
        }
    }

    /**
     * Gets a method handle for a native function.
     *
     * @param name Function name
     * @param descriptor Function descriptor
     * @return Method handle
     */
    private static MethodHandle getMethodHandle(String name, FunctionDescriptor descriptor) {
        System.out.println("Getting method handle for: " + name);

        if (!initialized) {
            System.err.println("Native library not initialized");
            throw new IllegalStateException("Native library not initialized");
        }

        try {
            // Look up the symbol
            System.out.println("Looking up symbol: " + name);
            var symbolOpt = libraryLookup.find(name);

            if (symbolOpt.isEmpty()) {
                System.err.println("Symbol not found: " + name);
                throw new RuntimeException("Native function not found: " + name);
            }

            MemorySegment symbol = symbolOpt.get();
            System.out.println("Symbol found: " + name + " at address: " + symbol.address());

            // Create a method handle for the function
            System.out.println("Creating method handle for: " + name);
            MethodHandle handle = linker.downcallHandle(symbol, descriptor);
            System.out.println("Method handle created successfully for: " + name);

            return handle;
        } catch (Exception e) {
            System.err.println("Error getting method handle for " + name + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get method handle for: " + name, e);
        }
    }

    /**
     * Checks if the native library is initialized.
     *
     * @return True if the native library is initialized, false otherwise
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
