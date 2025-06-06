#!/bin/bash

# Define default values
MODEL_PATH="/Users/e168693/.ollama/models/blobs/sha256-4ad960d180b16f56024f5b704697e5dd5b0837167c2e515ef0569abfc599743c"
CONTEXT_SIZE=2048
GPU_LAYERS=24
HOST="127.0.0.1"
PORT=8080

# Help message
show_help() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Run the llama.cpp server with WebUI"
    echo ""
    echo "Options:"
    echo "  -m, --model PATH      Path to model file (default: $MODEL_PATH)"
    echo "  -c, --context SIZE    Context size (default: $CONTEXT_SIZE)"
    echo "  -g, --gpu-layers N    Number of layers to offload to GPU (default: $GPU_LAYERS)"
    echo "  -h, --host HOST       Host to bind (default: $HOST)"
    echo "  -p, --port PORT       Port to listen on (default: $PORT)"
    echo "  --help                Display this help message and exit"
    echo ""
}

# Parse command line options
while [[ $# -gt 0 ]]; do
    case $1 in
        -m|--model)
            MODEL_PATH="$2"
            shift 2
            ;;
        -c|--context)
            CONTEXT_SIZE="$2"
            shift 2
            ;;
        -g|--gpu-layers)
            GPU_LAYERS="$2"
            shift 2
            ;;
        -h|--host)
            HOST="$2"
            shift 2
            ;;
        -p|--port)
            PORT="$2"
            shift 2
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Find the build directory and server binary
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
SERVER_BIN="${REPO_ROOT}/build/bin/llama-server"

if [ ! -f "$SERVER_BIN" ]; then
    # Try alternate build directory
    SERVER_BIN="${REPO_ROOT}/build-test/bin/llama-server"
    
    if [ ! -f "$SERVER_BIN" ]; then
        echo "Error: Could not find llama-server binary in ${REPO_ROOT}/build/bin/ or ${REPO_ROOT}/build-test/bin/"
        echo "Please build the server first with 'make -j' in the project root directory"
        exit 1
    fi
fi

# Check if model exists
if [ ! -f "$MODEL_PATH" ]; then
    echo "Error: Model file not found at $MODEL_PATH"
    exit 1
fi

# Run the server
echo "Starting llama.cpp server with WebUI..."
echo "Model: $MODEL_PATH"
echo "Server URL: http://$HOST:$PORT"

cd "$REPO_ROOT"
"$SERVER_BIN" \
    -m "$MODEL_PATH" \
    -c "$CONTEXT_SIZE" \
    --n-gpu-layers "$GPU_LAYERS" \
    --host "$HOST" \
    --port "$PORT"
