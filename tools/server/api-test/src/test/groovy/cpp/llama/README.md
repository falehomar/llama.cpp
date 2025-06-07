# llama.cpp Server API Tests

This directory contains Spock tests for the llama.cpp HTTP server's API endpoints.

## Overview

These tests verify the functionality of all the API endpoints provided by the llama.cpp server. They use Micronaut's HTTP client to send requests to the server and Spock's testing framework to verify the responses.

## Prerequisites

1. A running llama.cpp server instance
2. Java 21 or higher
3. Gradle

## Configuration

The test suite is configured using the `application.yaml` file, which contains:

- Model path configuration (pointing to `/Users/e168693/.ollama/models/blobs/sha256-4ad960d180b16f56024f5b704697e5dd5b0837167c2e515ef0569abfc599743c`)
- API URL configuration (defaults to `http://localhost:8080`)

You can customize these settings by modifying the `application.yaml` file or by setting environment variables.

## Running the Tests

Before running the tests, make sure your llama.cpp server is up and running with the configured model.

You can start the server using the provided `run-webui.sh` script:

```bash
cd ..
./run-webui.sh
```

Once the server is running, you can execute the tests with:

```bash
./gradlew test
```

## Test Coverage

The test suite covers the following endpoints:

1. Health API (`/health`)
2. Models API (`/models`)
3. Completion API (`/completion`)
4. Tokenize API (`/tokenize`)
5. Detokenize API (`/detokenize`)
6. Embedding API (`/embedding`)
7. Infill API (`/infill`)
8. Apply Template API (`/apply-template`)
9. Reranking API (`/rerank`)
10. Properties API (`/props`)
11. Slots API (`/slots`)
12. OpenAI Compatibility APIs:
    - Completions API (`/v1/completions`)
    - Chat Completions API (`/v1/chat/completions`)
    - Embeddings API (`/v1/embeddings`)

## Adding New Tests

To add tests for new endpoints or functionality:

1. Create a new Spock test class extending `BaseApiSpec`
2. Inject the appropriate client interface
3. Implement test methods with given/when/then blocks

## Troubleshooting

If the tests fail, please check:

1. The server is running and accessible at the configured URL
2. The model path exists and is valid
3. The server has enough resources to handle the requests
