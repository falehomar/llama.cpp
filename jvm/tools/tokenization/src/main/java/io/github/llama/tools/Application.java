package io.github.llama.tools;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

/**
 * Main application class for the llama.cpp JVM tokenization tool.
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Llama.cpp Tokenization Tool",
        version = "1.0.0",
        description = "REST API for tokenizing and detokenizing text using the llama.cpp Java API",
        license = @License(name = "MIT", url = "https://github.com/ggerganov/llama.cpp/blob/master/LICENSE"),
        contact = @Contact(name = "llama.cpp team", url = "https://github.com/ggerganov/llama.cpp")
    )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
