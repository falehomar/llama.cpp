# llama.cpp API Test Project Guidelines

This document provides essential information for developers working on the llama.cpp API test project.

## Build/Configuration Instructions

### Prerequisites
- Java 21 or higher
- Gradle (or use the included Gradle wrapper)
- A running llama.cpp server instance

### Configuration
The project is configured using the `application.yaml` file located in `src/main/resources/`. Key configuration properties:

```yaml
llama:
  model:
    path: /path/to/your/model/file
```

You can customize the model path by modifying this file or by setting environment variables.

### Building the Project
To build the project, use the Gradle wrapper:

```bash
./gradlew build
```

For a clean build:

```bash
./gradlew clean build
```

### Running the Application
To run the application:

```bash
./gradlew run
```

## Testing Information

### Prerequisites for Testing
Before running tests, ensure:
1. The llama.cpp server is running on http://localhost:8080 (default)
2. The server has loaded a model matching the path in your configuration

You can start the server using:
```bash
cd /path/to/llama.cpp/tools/server
./run-webui.sh
```

### Running Tests
To run all tests:

```bash
./gradlew test
```

To run a specific test:

```bash
./gradlew test --tests "cpp.llama.HealthApiSpec"
```

Alternatively, use the provided script:

```bash
./run-tests.sh
```

This script checks if the server is running before executing tests.

### Test Reports
Test reports are generated at:
```
build/reports/tests/test/index.html
```

### Adding New Tests

To add a new test for an API endpoint:

1. Create a new Spock test class extending `BaseApiSpec`
2. Inject the `LlamaApiClient` interface
3. Implement test methods using Spock's given/when/then blocks

Example test for a new endpoint:

```groovy
package cpp.llama

import cpp.llama.client.LlamaApiClient
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject

class NewEndpointApiSpec extends BaseApiSpec {

    @Inject
    LlamaApiClient client

    def "new endpoint should return expected response"() {
        given:
        def request = new LlamaApiClient.SomeRequest(
            parameter1: "value1",
            parameter2: "value2"
        )

        when:
        def response = client.callNewEndpoint(request)

        then:
        response.status() == HttpStatus.OK
        response.body().someProperty == expectedValue
    }
}
```

To add a new API client method:

1. Add the method to the `LlamaApiClient` interface
2. Create request/response DTOs as needed
3. Annotate with appropriate HTTP method and path

## Additional Development Information

### Project Structure
- `src/main/groovy/cpp/llama/` - Main application code
- `src/main/groovy/cpp/llama/client/` - API client interfaces
- `src/main/groovy/cpp/llama/config/` - Configuration classes
- `src/test/groovy/cpp/llama/` - Test specifications

### Code Style
- The project uses Groovy with @CompileStatic for better performance
- Tests follow the Spock framework's given/when/then structure
- DTOs use Micronaut's Serdeable annotation for serialization/deserialization

### Debugging Tips
- Set the log level to DEBUG in `logback.xml` for more detailed logs
- Use Micronaut's built-in tracing for HTTP requests
- For test failures, check the test reports for detailed error information

### API Client
The project uses Micronaut's declarative HTTP client. Key features:
- Automatic JSON serialization/deserialization
- Reactive programming support with RxJava3
- Configurable base URL and connection parameters

### GraalVM Native Image Support
The project includes configuration for GraalVM native image compilation:

```bash
./gradlew nativeCompile
```

This creates a standalone executable that doesn't require a JVM.
