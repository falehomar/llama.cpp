package io.github.llama.api;

import io.github.llama.api.model.ModelParams;
import io.github.llama.api.spi.LLMFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Registry for LLM factories.
 * This class uses the Java Service Provider Interface (SPI) to discover and manage LLM factories.
 */
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
