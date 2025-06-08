#!/bin/bash

# Example script to test the llama.cpp Java Tokenization Tool API
# This script demonstrates how to use curl to interact with the API

# Base URL for the API
BASE_URL="http://localhost:8080/api/tokenize"

# Check if the server is running
echo "Checking if the API server is running..."
if ! curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/info" | grep -q "200"; then
    echo "Error: API server not running. Please start the server first."
    echo "Run ./run.sh /path/to/model.gguf to start the server."
    exit 1
fi

echo "Server is running. Testing API endpoints..."

# Test 1: Get tokenizer info
echo -e "\n=== Test 1: Get Tokenizer Info ==="
curl -s "$BASE_URL/info" | jq .

# Test 2: Tokenize text
echo -e "\n=== Test 2: Tokenize Text ==="
TEXT="Hello, world!"
echo "Tokenizing: '$TEXT'"
curl -s -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d "{\"text\":\"$TEXT\"}" | jq .

# Test 3: Tokenize with options
echo -e "\n=== Test 3: Tokenize Text with Options ==="
TEXT="Hello, world!"
echo "Tokenizing with BOS=true and EOS=true: '$TEXT'"
curl -s -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d "{\"text\":\"$TEXT\", \"addBos\":true, \"addEos\":true}" | jq .

# Test 4: Detokenize
echo -e "\n=== Test 4: Detokenize ==="
# Note: Token IDs may vary based on your model
TOKENS="[1, 15043, 29889, 29991, 2]"
echo "Detokenizing tokens: $TOKENS"
curl -s -X POST "$BASE_URL/detokenize" \
    -H "Content-Type: application/json" \
    -d "{\"tokenIds\":$TOKENS}" | jq .

echo -e "\nAPI Tests completed!"
