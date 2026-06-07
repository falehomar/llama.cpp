#!/bin/bash

# Define directories and model parameters
MODEL_DIR="/Users/falehomar/.lmstudio/models/mradermacher/Qwen3.5-9B-Claude-4.6-HighIQ-INSTRUCT-HERETIC-UNCENSORED-GGUF"
MODEL_PATH="${MODEL_DIR}/Qwen3.5-9B-Claude-4.6-HighIQ-INSTRUCT-HERETIC-UNCENSORED.Q8_0.gguf"
MMPROJ_PATH="${MODEL_DIR}/Qwen3.5-9B-Claude-4.6-HighIQ-INSTRUCT-HERETIC-UNCENSORED.mmproj-f16.gguf"

# Verify model files exist
if [ ! -f "$MODEL_PATH" ]; then
    echo "Error: Base model file not found at $MODEL_PATH"
    exit 1
fi

if [ ! -f "$MMPROJ_PATH" ]; then
    echo "Error: Multimodal projector file not found at $MMPROJ_PATH"
    exit 1
fi

# Locate llama-server executable
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVER_BIN="${SCRIPT_DIR}/build-local/bin/llama-server"

if [ ! -f "$SERVER_BIN" ]; then
    echo "Error: llama-server executable not found at $SERVER_BIN. Please run local-build.sh first."
    exit 1
fi

echo "Starting llama-server with Qwen3.5 Vision Model and Built-in Tools..."
exec "$SERVER_BIN" \
  --model "$MODEL_PATH" \
  --mmproj "$MMPROJ_PATH" \
  --gpu-layers 32 \
  --threads 12 \
  --ctx-size 1048576 \
  --batch-size 2048 \
  --ubatch-size 512 \
  --parallel 4 \
  --flash-attn on \
  --cache-type-k q8_0 \
  --cache-type-v q8_0 \
  --port 8080 \
  --tools all "$@"
