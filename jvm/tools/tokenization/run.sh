#!/bin/bash

# Example script to run the llama.cpp Java Tokenization Tool
# This script demonstrates how to run the tool with a specified model path

# Check if a model path is provided as an argument
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <path_to_model.gguf>"
    echo "Example: $0 /path/to/models/llama-2-7b-chat.Q4_K_M.gguf"
    exit 1
fi

MODEL_PATH="$1"

# Check if the model file exists
if [ ! -f "$MODEL_PATH" ]; then
    echo "Error: Model file not found at $MODEL_PATH"
    exit 1
fi

echo "Starting llama.cpp Java Tokenization Tool with model: $MODEL_PATH"

# Set the environment variable for model path
export LLAMA_MODEL_PATH="$MODEL_PATH"

# Navigate to the tokenization tool directory
cd "$(dirname "$0")"

# Build the tool if not already built
echo "Building the tokenization tool..."
./gradlew build

# Run the tool with the specified model
echo "Running the tokenization tool..."
./gradlew run

# Note: When stopping the script with Ctrl+C, the Java process will be terminated
# To access the API documentation, open http://localhost:8080/swagger-ui in your browser
