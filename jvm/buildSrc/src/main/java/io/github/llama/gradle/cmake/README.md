# CMake Gradle Plugin

A Gradle plugin for integrating CMake builds with Gradle projects.

## Features

- Configure CMake generator (default: Ninja)
- Generate CMake build files
- Build CMake projects
- Configure build parameters (build type, parallel jobs, etc.)

## Usage

### Apply the Plugin

Add the following to your `build.gradle` file:

```groovy
plugins {
    id 'io.github.llama.cmake'
}
```

### Basic Configuration

Configure the CMake build in your `build.gradle` file:

```groovy
cmake {
    // Path to CMake executable (default: "cmake")
    cmakePath = "cmake"

    // CMake generator to use (default: "Ninja")
    generator = "Ninja"

    // Source directory containing CMakeLists.txt (default: project directory)
    sourceDir = file("src/native")

    // Build directory for CMake output (default: build/cmake)
    buildDir = file("build/cmake")

    // Build type (default: "Release")
    buildType = "Debug"

    // Number of parallel jobs for build (default: number of processors)
    parallelJobs = 4

    // Additional CMake arguments
    arguments = ["-DBUILD_SHARED_LIBS=ON"]

    // CMake cache variables
    cacheVariables = [
        "CMAKE_CXX_STANDARD": "17",
        "MY_CUSTOM_VARIABLE": "value"
    ]
}
```

### Tasks

The plugin provides the following tasks:

- `cmakeGenerate`: Generates CMake build files
- `cmakeBuild`: Builds the CMake project (depends on `cmakeGenerate`)

### Example Usage

```groovy
// Apply the plugin
plugins {
    id 'io.github.llama.cmake'
}

// Configure CMake
cmake {
    sourceDir = file("src/native")
    buildType = "Debug"
    cacheVariables = [
        "CMAKE_CXX_STANDARD": "17"
    ]
}

// Add a dependency on the CMake build for the Java compile task
compileJava.dependsOn cmakeBuild

// Custom task to clean the CMake build directory
tasks.register("cleanCMake", Delete) {
    delete cmake.buildDir
}
```

## Advanced Configuration

### Using a Different Generator

```groovy
cmake {
    generator = "Unix Makefiles"
}
```

### Building Specific Targets

```groovy
tasks.named("cmakeBuild") {
    target = "my_target"
}
```

### Custom Build Arguments

```groovy
tasks.named("cmakeBuild") {
    arguments = ["--verbose"]
}
```
