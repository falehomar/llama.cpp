package cpp.llama

import cpp.llama.config.LlamaProperties
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
abstract class BaseApiSpec extends Specification {

    @Inject
    LlamaProperties llamaProperties

    String getModelPath() {
        return llamaProperties.model.path
    }
}
