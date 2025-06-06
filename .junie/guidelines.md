# llama.cpp Development Guidelines

This document provides guidelines and information for developers working on the llama.cpp project.

## Build/Configuration Instructions

### Basic Build

The project uses CMake as its build system. The basic build process is:

```bash
# Configure the build
cmake -B build

# Build the project
cmake --build build --config Release
```

For faster compilation, you can use the `-j` flag to specify the number of parallel jobs:

```bash
cmake --build build --config Release -j 8
```

### Build Options

The project supports various build options that can be enabled or disabled using CMake flags:

- **Backend Options**:
  - `-DGGML_CUDA=ON`: Enable NVIDIA CUDA backend
  - `-DGGML_METAL=ON`: Enable Apple Metal backend (default on macOS)
  - `-DGGML_VULKAN=ON`: Enable Vulkan backend
  - `-DGGML_HIP=ON`: Enable AMD HIP backend
  - `-DGGML_SYCL=ON`: Enable Intel SYCL backend
  - `-DGGML_MUSA=ON`: Enable Moore Threads MUSA backend
  - `-DGGML_CANN=ON`: Enable Ascend NPU backend
  - `-DGGML_OPENCL=ON`: Enable OpenCL backend
  - `-DGGML_BLAS=ON`: Enable BLAS acceleration

- **Build Type Options**:
  - `-DCMAKE_BUILD_TYPE=Release`: Optimized build (default)
  - `-DCMAKE_BUILD_TYPE=Debug`: Debug build with symbols
  - `-DBUILD_SHARED_LIBS=OFF`: Build static libraries instead of shared

- **Other Options**:
  - `-DLLAMA_BUILD_TESTS=ON`: Build tests (default when building standalone)
  - `-DLLAMA_BUILD_EXAMPLES=ON`: Build examples (default when building standalone)
  - `-DLLAMA_BUILD_SERVER=ON`: Build server (default when building standalone)
  - `-DLLAMA_CURL=ON`: Use libcurl to download models from URLs (default)

### Platform-Specific Instructions

#### macOS

On macOS, Metal is enabled by default. To build:

```bash
cmake -B build
cmake --build build --config Release
```

#### Linux with CUDA

To build with CUDA support on Linux:

```bash
cmake -B build -DGGML_CUDA=ON
cmake --build build --config Release
```

#### Windows

For Windows, it's recommended to use Visual Studio or the MSVC compiler:

```bash
cmake -B build
cmake --build build --config Release
```

For more detailed build instructions, refer to the [build documentation](docs/build.md).

## Testing Information

### Test Framework

The project uses CTest for testing. Tests are located in the `tests/` directory and are organized by functionality (tokenizer, grammar, quantization, etc.).

### Running Tests

To run all tests:

```bash
cd build
ctest
```

To run a specific test:

```bash
cd build
ctest -R test-name
```

### Adding New Tests

To add a new test:

1. Create a new C/C++ file in the `tests/` directory (e.g., `test-feature.cpp`)
2. Use the `llama_build_and_test` function in the CMakeLists.txt to build and register the test:

```cmake
llama_build_and_test(test-feature.cpp)
```

### Test Example

Here's a simple test example:

```cpp
#include "log.h"

#include <cstdlib>
#include <cassert>

// A simple test to demonstrate how to write tests for llama.cpp
int main() {
    // Initialize logging
    LOG_INF("Starting simple test\n");

    // Test a simple assertion
    int test_value = 42;
    LOG_INF("Testing assertion: test_value == 42\n");
    assert(test_value == 42);

    // Test logging at different levels
    LOG_INF("This is an info message\n");
    LOG_WRN("This is a warning message\n");
    LOG_ERR("This is an error message\n");
    LOG_DBG("This is a debug message\n");

    LOG_INF("All tests passed successfully!\n");
    return 0;
}
```

To build and run this test:

```bash
# Add to CMakeLists.txt
llama_build_and_test(test-simple.cpp)

# Build and run
cmake -B build
cmake --build build --config Release
cd build
ctest -R test-simple
```

## Code Style and Development Guidelines

### Coding Style

- Use 4 spaces for indentation (no tabs)
- Use `snake_case` for function, variable, and type names
- Clean up trailing whitespaces
- Brackets on the same line
- Use `void * ptr` and `int & a` style for pointers and references
- Use sized integer types (e.g., `int32_t`) in the public API

### Naming Conventions

- Enum values are in upper case and prefixed with the enum name
- The general naming pattern is `<class>_<method>`, with `<method>` being `<action>_<noun>`
- Use `init`/`free` for constructor/destructor actions
- Use the `_t` suffix for opaque types
- C/C++ filenames are all lowercase with dashes
- Python filenames are all lowercase with underscores

### Development Guidelines

- Avoid adding third-party dependencies, extra files, extra headers, etc.
- Always consider cross-compatibility with other operating systems and architectures
- Avoid fancy-looking modern STL constructs, use basic `for` loops, avoid templates, keep it simple
- Vertical alignment makes things more readable and easier to batch edit
- Tensors store data in row-major order
- Matrix multiplication is unconventional: `C = ggml_mul_mat(ctx, A, B)` means C^T = A B^T ⟺ C = B A^T

### Performance Testing

Use the `llama-perplexity` and `llama-bench` tools to verify that your changes don't negatively impact performance:

```bash
# Test perplexity
./build/bin/llama-perplexity -m model.gguf -f file.txt

# Benchmark performance
./build/bin/llama-bench -m model.gguf
```

For more detailed development guidelines, refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file.
