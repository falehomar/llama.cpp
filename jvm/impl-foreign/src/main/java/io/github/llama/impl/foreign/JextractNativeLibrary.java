package io.github.llama.impl.foreign;

import io.github.llama.impl.foreign.jextract.Llama;
import java.io.IOException;
import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for loading and interacting with the native llama.cpp library using jextract-generated bindings.
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
public class JextractNativeLibrary {
    private static final String LIBRARY_NAME = "llama";
    private static final String DEFAULT_LIBRARY_PATH = "/Users/e168693/TeamCompose/submodules/llama.cpp/build/bin";
    private static final String LIBRARY_PATH_PROPERTY = "llama.library.path";
    private static boolean initialized = false;

    /**
     * Checks if the native library is initialized.
     *
     * @return Whether the native library is initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Initializes the native library.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            System.out.println("Initializing native library with jextract...");

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

        try (var inputStream = JextractNativeLibrary.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Native library not found in resources: " + resourcePath);

                // List available resources in the /native directory
                System.out.println("Available resources in /native directory:");
                var urls = JextractNativeLibrary.class.getClassLoader().getResources("native");
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
            // Use the jextract-generated binding
            Llama.llama_backend_init();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to initialize llama backend", e);
        }
    }

    /**
     * Frees the llama backend.
     */
    public static void llamaBackendFree() {
        try {
            // Use the jextract-generated binding
            io.github.llama.impl.foreign.jextract.llama_h.llama_backend_free();
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_model_default_params();
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

                // Use the jextract-generated binding
                return io.github.llama.impl.foreign.jextract.llama_h.llama_model_load_from_file(pathSegment, params);
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
            // Use the jextract-generated binding
            io.github.llama.impl.foreign.jextract.llama_h.llama_model_free(model);
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_model_n_params(model);
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_model_n_ctx_train(model);
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_model_n_embd(model);
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_model_n_layer(model);
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_model_n_head(model);
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

                // Use the jextract-generated binding
                int length = io.github.llama.impl.foreign.jextract.llama_h.llama_model_desc(model, buffer, 1024);

                if (length <= 0) {
                    return "";
                }

                // Convert the bytes to a string using StandardCharsets.UTF_8
                byte[] bytes = buffer.toArray(ValueLayout.JAVA_BYTE);
                return new String(bytes, 0, length, StandardCharsets.UTF_8);
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_model_get_vocab(model);
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_context_default_params();
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
            // Use the jextract-generated binding
            return io.github.llama.impl.foreign.jextract.llama_h.llama_init_from_model(model, params);
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
            // Use the jextract-generated binding
            io.github.llama.impl.foreign.jextract.llama_h.llama_free(context);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to free context", e);
        }
    }
}
