# JExtract gradle plugin

## jextract command options
```term
jextract --help
Usage: jextract <options> <header file> [<header file>] [...]

Option                             Description
------                             -----------
-?, -h, --help                     print help
-D --define-macro <macro>=<value>  define <macro> to <value> (or 1 if <value> omitted)
-I, --include-dir <dir>            add directory to the end of the list of include search paths
--dump-includes <file>             dump included symbols into specified file
--header-class-name <name>         name of the generated header class. If this option is not
specified, then header class name is derived from the header
file name. For example, class "foo_h" for header "foo.h".
--include-function <name>          name of function to include
--include-constant <name>          name of macro or enum constant to include
--include-struct <name>            name of struct definition to include
--include-typedef <name>           name of type definition to include
--include-union <name>             name of union definition to include
--include-var <name>               name of global variable to include
-l, --library <libspec>            specify a shared library that should be loaded by the
generated header class. If <libspec> starts with :, then
what follows is interpreted as a library path. Otherwise,
<libspec> denotes a library name. Examples:
-l GL
-l :libGL.so.1
-l :/usr/lib/libGL.so.1
--use-system-load-library          libraries specified using -l are loaded in the loader symbol
lookup (using either System::loadLibrary, or System::load).
Useful if the libraries must be loaded from one of the paths
in java.library.path.
--output <path>                    specify the directory to place generated files. If this
option is not specified, then current directory is used.
-t, --target-package <package>     target package name for the generated classes. If this option
is not specified, then unnamed package is used.
--symbols-class-name <name>        override the name of the root header class
--version                          print version information and exit

macOS platform options for running jextract (available only when running on macOS):
-F <dir>            specify the framework directory
--framework <framework>                     specify framework library. --framework libGL is equivalent to
-l :/System/Library/Frameworks/libGL.framework/libGL

```

## Jextract Plugin Support Matrix

The following table shows how each jextract option is supported by the Gradle plugin:

| Jextract Option | Plugin Property | Default Value | Notes |
|-----------------|----------------|---------------|-------|
| `<header file>` | `headerFile` | None (Required) | Specified as a file path in the build script |
| `-I, --include-dir <dir>` | `includePaths` | None | List of include directories |
| `--header-class-name <name>` | `headerClassName` | None | Name of the generated header class |
| `--include-function <name>` | `includeFunctions` | None | List of function patterns to include |
| `--include-constant <name>` | `includeConstants` | None | List of constant patterns to include |
| `--include-struct <name>` | `includeStructs` | None | List of struct patterns to include |
| `--include-typedef <name>` | `includeTypedefs` | None | List of typedef patterns to include |
| `--include-union <name>` | `includeUnions` | None | List of union patterns to include |
| `--include-var <name>` | `includeVars` | None | List of variable patterns to include |
| `--output <path>` | `outputDir` | `build/generated/jextract` | Output directory for generated code |
| `-t, --target-package <package>` | `targetPackage` | None (Required) | Target package for generated classes |
| `--symbols-class-name <name>` | `symbolsClassName` | None | Override the name of the root header class |
| `-D --define-macro <macro>=<value>` | `defineMacros` | None | Map of macro definitions (key-value pairs) |
| `--dump-includes <file>` | `dumpIncludesFile` | Derived from header file name | File to dump included symbols into. If not specified, it will be derived from the header file name and placed in the build directory |
| `-l, --library <libspec>` | `libraries` | None | List of libraries to load |
| `--use-system-load-library` | `useSystemLoadLibrary` | `false` | Whether to use System.loadLibrary/System.load for loading libraries |
| `--version` | Not supported | N/A | Not applicable for the plugin |
| `-F <dir>` (macOS only) | `frameworkDirs` | None | List of framework directories (macOS only) |
| `--framework <framework>` (macOS only) | `frameworks` | None | List of frameworks to load (macOS only) |

### Example Usage

```gradle
plugins {
    id 'java'
    id 'io.github.llama.jextract'
}

jextract {
    jextractPath = '/path/to/jextract'
    headerFile = file('src/main/headers/example.h')
    targetPackage = 'com.example.bindings'
    headerClassName = 'ExampleHeader'
    symbolsClassName = 'ExampleSymbols'
    includePaths = ['src/main/headers', '/usr/include']
    includeFunctions = ['example_*', 'test_function']
    includeStructs = ['Example_*']
    includeConstants = ['EXAMPLE_*']
    includeVars = ['global_*']
    includeUnions = ['Union_*']
    includeTypedefs = ['Type_*']
    outputDir = layout.buildDirectory.dir('generated/jextract-custom')

    // Define macros
    defineMacros = [
        'DEBUG': '1',
        'FEATURE_ENABLED': '',  // Defined without value
        'VERSION': '1.0.0'
    ]

    // Dump includes to a file
    dumpIncludesFile = file('build/example-includes.txt')

    // Libraries to load
    libraries = ['mylib', ':path/to/libcustom.so']
    useSystemLoadLibrary = true

    // macOS specific options
    frameworkDirs = ['/Library/Frameworks']
    frameworks = ['CoreFoundation']
}
```
## Required enhancements
1. all options must be supported.

## Using the dump-includes task

The plugin provides a dedicated task called `dump-includes` that can be used to generate a dump of all symbols encountered in a header file. This dump can be manipulated and then used as an argument file for filtering symbols when generating bindings.

### Example Usage

```gradle
// Configure the jextract extension with the header file
jextract {
    headerFile = file('src/main/headers/example.h')
    // dumpIncludesFile is optional - if not specified, it will be derived from the header file name
    // and placed in the build directory (e.g., build/example.includes)
    dumpIncludesFile = file('build/custom-includes.txt')  // Optional
}

// Run the dump-includes task to generate a dump of all symbols
./gradlew dump-includes

// Edit the dump file to keep only the symbols you want

// Use the dump file as an argument file when running jextract
./gradlew jextract -Pjextract.args=@build/example-includes.txt
```

The dump file contains one symbol per line, with the symbol type and name. You can edit this file to remove symbols you don't want to include in the generated bindings.

When using the dump file as an argument file, prefix it with `@` to tell jextract to read arguments from the file. This follows the standard JDK tools convention for argument files.

If you don't specify a `dumpIncludesFile` in your build.gradle, the task will automatically generate a file in the build directory with a name derived from the header file. For example, if your header file is named `example.h`, the dump file will be `build/example.includes`.

### Automatic Args File Support

The jextract task automatically looks for an args file in the `src/main/resources/jextract` directory with a name derived from the header file. For example, if your header file is named `example.h`, the task will look for `src/main/resources/jextract/example.includes`.

If this file exists, it will be passed to the jextract command using the `@argfile` syntax. This allows you to:

1. Generate a dump of all symbols using the `dump-includes` task
2. Edit the dump file to keep only the symbols you want
3. Place the edited file in `src/main/resources/jextract` with the appropriate name
4. Run the jextract task, which will automatically use the args file

This workflow is particularly useful when you want to filter the symbols included in the generated bindings without having to specify them all in the build script.

## Testing

The plugin includes comprehensive tests for all functionality, including the `dump-includes` task. The tests verify that:

1. The `dump-includes` task works correctly with a specified `dumpIncludesFile`
2. The `dump-includes` task works correctly when `dumpIncludesFile` is not specified, deriving the file name from the header file
3. The `dump-includes` task correctly handles include paths, macro definitions, and other options

These tests ensure that the plugin behaves as expected and that changes to the code don't break existing functionality.
