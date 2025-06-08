package io.github.llama.tools.tokenization;

import io.github.llama.api.LLMFactoryRegistry;
import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelParams;
import io.github.llama.api.spi.LLMFactory;
import io.github.llama.api.tokenization.Tokenizer;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Factory for creating tokenizer-related beans.
 */
@Factory
public class TokenizerFactory {

    @Value("${llama.model.path}")
    private String modelPath;

    /**
     * Creates a Tokenizer bean.
     *
     * @return A tokenizer
     * @throws Exception If tokenizer creation fails
     */
    @Bean(preDestroy = "close")
    @Singleton
    public Model createModel() throws Exception {
        // Get the default LLM factory
        LLMFactoryRegistry registry = LLMFactoryRegistry.getInstance();
        if (registry.getFactories().isEmpty()) {
            throw new IllegalStateException("No LLM factories found");
        }

        LLMFactory factory = registry.getFactories().get(0);

        // Create model parameters
        Path path = Paths.get(modelPath);
        ModelParams params = new ModelParams.Builder()
            .withModelPath(path)
            .build();

        // Load the model
        return factory.createModel(params);
    }

    /**
     * Creates a Tokenizer bean.
     *
     * @param model The model to get the tokenizer from
     * @return A tokenizer
     * @throws Exception If tokenizer creation fails
     */
    @Bean
    @Singleton
    public Tokenizer createTokenizer(Model model) throws Exception {
        return model.getTokenizer();
    }
}
