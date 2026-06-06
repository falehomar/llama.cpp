#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Define color codes for pretty console output (ASCII only)
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== llama.cpp Local Tuned Build Script ===${NC}"

# Detect system details
OS_NAME=$(uname -s)
CPU_ARCH=$(uname -m)
CPU_CORES=$(sysctl -n hw.ncpu 2>/dev/null || sysctl -n hw.logicalcpu 2>/dev/null || echo "4")
CPU_BRAND=$(sysctl -n machdep.cpu.brand_string 2>/dev/null || sysctl -n hw.model 2>/dev/null || echo "Unknown CPU")

echo -e "System OS:        ${GREEN}${OS_NAME}${NC}"
echo -e "CPU Architecture: ${GREEN}${CPU_ARCH}${NC}"
echo -e "CPU Model:        ${GREEN}${CPU_BRAND}${NC}"
echo -e "Detected Cores:   ${GREEN}${CPU_CORES}${NC}"

# Default build parameters
BUILD_DIR="build-local"
BUILD_TYPE="Release"
CLEAN_BUILD=false
USE_METAL=true
USE_ACCELERATE=true
USE_NATIVE=true
USE_LTO=false
GENERATOR="Ninja"

# Display usage information
show_usage() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  --clean           Remove the existing build directory before compiling"
    echo "  --debug           Build in Debug mode instead of Release"
    echo "  --no-metal        Disable Metal GPU acceleration"
    echo "  --no-accelerate   Disable Apple Accelerate BLAS framework"
    echo "  --no-native       Disable compiler CPU-native microarchitecture tuning"
    echo "  --lto             Enable Link Time Optimization (LTO) for slightly faster execution but longer build times"
    echo "  --make            Use Unix Makefiles instead of Ninja generator"
    echo "  --help            Show this help message"
}

# Parse optional command-line arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --clean) CLEAN_BUILD=true ;;
        --debug) BUILD_TYPE="Debug" ;;
        --no-metal) USE_METAL=false ;;
        --no-accelerate) USE_ACCELERATE=false ;;
        --no-native) USE_NATIVE=false ;;
        --lto) USE_LTO=true ;;
        --make) GENERATOR="Unix Makefiles" ;;
        --help|-h) show_usage; exit 0 ;;
        *) echo "Unknown parameter: $1"; show_usage; exit 1 ;;
    esac
    shift
done

# Ensure CMake is installed
if ! command -v cmake &> /dev/null; then
    echo -e "${RED}Error: cmake is required but not installed.${NC}"
    exit 1
fi

# Fall back to Makefiles if Ninja is requested but not found
if [[ "$GENERATOR" == "Ninja" ]] && ! command -v ninja &> /dev/null; then
    echo -e "Ninja not found. Falling back to Unix Makefiles."
    GENERATOR="Unix Makefiles"
fi

# Perform clean build if requested
if [ "$CLEAN_BUILD" = true ] && [ -d "$BUILD_DIR" ]; then
    echo -e "${BLUE}Cleaning build directory '$BUILD_DIR'...${NC}"
    rm -rf "$BUILD_DIR"
fi

# Construct CMake flags based on options
CMAKE_FLAGS=(
    "-DCMAKE_BUILD_TYPE=${BUILD_TYPE}"
    "-DLLAMA_BUILD_COMMON=ON"
    "-DLLAMA_BUILD_SERVER=ON"
    "-DLLAMA_BUILD_TOOLS=ON"
    "-DLLAMA_BUILD_EXAMPLES=ON"
    "-DLLAMA_BUILD_UI=ON"
)

# Apply performance tuning options specific to Apple Silicon macOS
if [ "$OS_NAME" = "Darwin" ] && [ "$CPU_ARCH" = "arm64" ]; then
    echo -e "${BLUE}Tuning configurations for Apple Silicon macOS...${NC}"
    
    if [ "$USE_METAL" = true ]; then
        CMAKE_FLAGS+=("-DGGML_METAL=ON")
        CMAKE_FLAGS+=("-DGGML_METAL_EMBED_LIBRARY=ON")
        echo -e "  - Metal acceleration:       ${GREEN}ENABLED${NC}"
    else
        CMAKE_FLAGS+=("-DGGML_METAL=OFF")
        echo -e "  - Metal acceleration:       ${RED}DISABLED${NC}"
    fi

    if [ "$USE_ACCELERATE" = true ]; then
        CMAKE_FLAGS+=("-DGGML_ACCELERATE=ON")
        CMAKE_FLAGS+=("-DGGML_BLAS_VENDOR=Apple")
        echo -e "  - Apple Accelerate (BLAS):   ${GREEN}ENABLED${NC}"
    else
        CMAKE_FLAGS+=("-DGGML_ACCELERATE=OFF")
        echo -e "  - Apple Accelerate (BLAS):   ${RED}DISABLED${NC}"
    fi
    
    if [ "$USE_NATIVE" = true ]; then
        CMAKE_FLAGS+=("-DGGML_NATIVE=ON")
        echo -e "  - CPU-native tuning:        ${GREEN}ENABLED${NC}"
    else
        CMAKE_FLAGS+=("-DGGML_NATIVE=OFF")
        echo -e "  - CPU-native tuning:        ${RED}DISABLED${NC}"
    fi
else
    echo -e "${BLUE}Tuning configurations for generic target...${NC}"
    if [ "$USE_NATIVE" = true ]; then
        CMAKE_FLAGS+=("-DGGML_NATIVE=ON")
    fi
fi

if [ "$USE_LTO" = true ]; then
    CMAKE_FLAGS+=("-DGGML_LTO=ON")
    echo -e "  - Link Time Optimization:   ${GREEN}ENABLED${NC}"
else
    CMAKE_FLAGS+=("-DGGML_LTO=OFF")
    echo -e "  - Link Time Optimization:   ${RED}DISABLED${NC}"
fi

# Add the generator option
CMAKE_FLAGS+=("-G" "$GENERATOR")
echo -e "Generator:                   ${GREEN}${GENERATOR}${NC}"
echo -e "Build Type:                  ${GREEN}${BUILD_TYPE}${NC}"
echo -e "Build Directory:             ${GREEN}${BUILD_DIR}${NC}"
echo -e "Features Enabled:            ${GREEN}Server (with UI), All Tools (Control Vectors, Multimodal/mtmd)${NC}"

# Configure build with CMake
echo -e "\n${BLUE}Configuring build...${NC}"
cmake -B "$BUILD_DIR" "${CMAKE_FLAGS[@]}"

# Build the project using all available CPU cores
echo -e "\n${BLUE}Building with ${CPU_CORES} jobs...${NC}"
cmake --build "$BUILD_DIR" --config "$BUILD_TYPE" -j "$CPU_CORES"

echo -e "\n${GREEN}Build completed successfully!${NC}"
echo -e "You can find binaries in: ${GREEN}${BUILD_DIR}/bin/${NC}"
