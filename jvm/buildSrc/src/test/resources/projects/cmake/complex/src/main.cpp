#include <iostream>
#include "math_lib.h"

int main() {
    std::cout << "Hello, World from complex CMake test!" << std::endl;

    // Use the math library
    std::cout << "2 + 3 = " << add(2, 3) << std::endl;
    std::cout << "5 * 4 = " << multiply(5, 4) << std::endl;

    return 0;
}
