#include <iostream>
#include <string>

int main(int argc, char* argv[]) {
    std::cout << "CMake Plugin Test Program" << std::endl;
    std::cout << "=========================" << std::endl;

    std::cout << "This program demonstrates that the CMake Gradle plugin is working correctly." << std::endl;

    // Print command line arguments if any
    if (argc > 1) {
        std::cout << "\nCommand line arguments:" << std::endl;
        for (int i = 1; i < argc; ++i) {
            std::cout << "  " << i << ": " << argv[i] << std::endl;
        }
    }

    std::cout << "\nBuild completed successfully!" << std::endl;
    return 0;
}
