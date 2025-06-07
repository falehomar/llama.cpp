# CMake Plugin Test

This project demonstrates the use of the CMake Gradle plugin to build a simple C++ program.

## Project Structure

- `src/main/cpp/test_program.cpp`: A simple C++ program that prints a message
- `src/main/cpp/CMakeLists.txt`: CMake configuration for building the program
- `build.gradle`: Gradle build file that uses the CMake plugin

## Building Manually

If you encounter issues with the Gradle CMake plugin, you can build the project manually using the following commands:

```bash
# Create the build directory
mkdir -p cmake-plugin-test/build/cmake

# Generate the build files
cd cmake-plugin-test/build/cmake
cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_STANDARD=11 -DCMAKE_INSTALL_PREFIX=../install ../../src/main/cpp

# Build the project
make

# Install the executable
make install

# Run the program
../install/bin/test_program
```

## Building with Gradle

The project can also be built using Gradle with the CMake plugin:

```bash
# Generate and build
./gradlew cmake-plugin-test:cmakeBuild

# Install
./gradlew cmake-plugin-test:installTest

# Run
./gradlew cmake-plugin-test:runTest
```

Note: If you encounter issues with the Gradle build, try the manual build process instead.

## CMake Plugin Configuration

The `build.gradle` file configures the CMake plugin with the following settings:

- Generator: "Unix Makefiles" (instead of the default "Ninja")
- Source directory: Points to the src/main/cpp directory
- Build directory: Set to build/cmake
- Build type: Release
- C++ standard: 11
- Install prefix: build/install

## Troubleshooting

If you encounter issues with the CMake plugin, check the following:

1. Make sure CMake is installed and in your PATH
2. Try running the CMake commands manually
3. Check the CMake version with `cmake --version`
4. Ensure the source directory exists and contains a valid CMakeLists.txt file
