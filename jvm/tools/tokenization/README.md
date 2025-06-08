# llama.cpp Java API - Tokenization Tool

This microservice provides a REST API for tokenizing and detokenizing text using the llama.cpp Java API.

## Features

- Tokenize text to token IDs
- Detokenize token IDs back to text
- Get tokenizer information (vocabulary size, special tokens)
- OpenAPI/Swagger documentation

## Technologies

- Java 21
- Micronaut 4.8.2
- Netty for high-performance async I/O
- llama.cpp Java API

## Prerequisites

- Java 21 or higher
- A GGUF model file for tokenization

## Configuration

Edit the `application.yml` file to configure the tool:

```yaml
llama:
  model:
    path: /path/to/your/model.gguf
```

You can also set the model path using an environment variable:

```bash
export LLAMA_MODEL_PATH=/path/to/your/model.gguf
```

## Building

```bash
./gradlew build
```

## Running

```bash
./gradlew run
```

Or run the built JAR:

```bash
java -jar build/libs/tokenization-0.1-all.jar
```

## API Endpoints

### Tokenize Text

```
POST /api/tokenize
```

Request body:
```json
{
  "text": "Hello, world!",
  "addBos": true,
  "addEos": false
}
```

Response:
```json
{
  "tokenIds": [1, 15043, 29889, 29991, 2]
}
```

### Detokenize

```
POST /api/tokenize/detokenize
```

Request body:
```json
{
  "tokenIds": [1, 15043, 29889, 29991, 2]
}
```

Response:
```json
{
  "text": "Hello, world!"
}
```

### Tokenizer Info

```
GET /api/tokenize/info
```

Response:
```json
{
  "vocabularySize": 32000,
  "bosToken": 1,
  "eosToken": 2
}
```

## API Documentation

When the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui
```

## Test-Driven Development (TDD)

This project follows strict TDD principles:

1. Write a test that defines desired functionality
2. Run the test and watch it fail
3. Write minimal code to make the test pass
4. Run the test and watch it pass
5. Refactor code as necessary
6. Repeat

## License

This project is licensed under the MIT License - see the LICENSE file for details.


