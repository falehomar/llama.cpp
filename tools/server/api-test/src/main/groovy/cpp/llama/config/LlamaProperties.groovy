package cpp.llama.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("llama")
class LlamaProperties {
    ModelConfig model = new ModelConfig()

    static class ModelConfig {
        String path
    }
}
