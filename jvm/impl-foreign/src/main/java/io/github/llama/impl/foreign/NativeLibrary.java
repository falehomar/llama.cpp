package io.github.llama.impl.foreign;

import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for loading and interacting with the native llama.cpp library using the Java Foreign and Native Memory API.
 */
public class NativeLibrary {
    private static final String LIBRARY_NAME = "llama";
    private static boolean initialized = false;
    private static SymbolLookup libraryLookup;
    private static Linker linker;

    /**
     * Initializes the native library.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Extract the native library to a temporary file
            Path libraryPath = extractNativeLibrary();

            // Load the native library
            System.load(libraryPath.toString());

            // Create a symbol lookup for the library
            libraryLookup = SymbolLookup.loaderLookup();

            // Create a linker for the current platform
            linker = Linker.nativeLinker();

            // Initialize the llama backend
            llamaBackendInit();

            initialized = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize native library", e);
        }
    }

    /**
     * Extracts the native library from the resources to a temporary file.
     *
     * @return Path to the extracted library
     * @throws IOException If the library cannot be extracted
     */
    private static Path extractNativeLibrary() throws IOException {
        String libraryFileName = System.mapLibraryName(LIBRARY_NAME);
        Path tempFile = Files.createTempFile(LIBRARY_NAME, null);

        try (var inputStream = NativeLibrary.class.getResourceAsStream("/native/" + libraryFileName)) {
            if (inputStream == null) {
                throw new IOException("Native library not found in resources: " + libraryFileName);
            }

            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
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
     * Gets a method handle for a native function.
     *
     * @param name Function name
     * @param descriptor Function descriptor
     * @return Method handle
     */
    private static MethodHandle getMethodHandle(String name, FunctionDescriptor descriptor) {
        MemorySegment symbol = libraryLookup.find(name)
            .orElseThrow(() -> new RuntimeException("Native function not found: " + name));

        return linker.downcallHandle(symbol, descriptor);
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
