#ifndef COMPLEX_H
#define COMPLEX_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Error codes for the library.
 */
typedef enum {
    SUCCESS = 0,
    ERROR_INVALID_ARGUMENT = -1,
    ERROR_OUT_OF_MEMORY = -2,
    ERROR_OVERFLOW = -3
} ErrorCode;

/**
 * A structure representing a 3D vector.
 */
typedef struct {
    double x;
    double y;
    double z;
} Vector3D;

/**
 * A structure representing a 3D matrix.
 */
typedef struct {
    double m[3][3];
} Matrix3D;

/**
 * A union that can represent different types of data.
 */
typedef union {
    int i;
    float f;
    double d;
    char* s;
} Variant;

/**
 * Global configuration options.
 */
typedef struct {
    int maxIterations;
    double tolerance;
    int enableLogging;
} Config;

/**
 * Global configuration instance.
 */
extern Config globalConfig;

/**
 * Maximum number of vectors allowed.
 */
#define MAX_VECTORS 1000

/**
 * Default tolerance value.
 */
#define DEFAULT_TOLERANCE 1e-6

/**
 * Initializes the library.
 * @return Error code indicating success or failure.
 */
ErrorCode initialize();

/**
 * Cleans up resources used by the library.
 */
void cleanup();

/**
 * Adds two vectors.
 * @param v1 First vector.
 * @param v2 Second vector.
 * @param result Output parameter for the result.
 * @return Error code indicating success or failure.
 */
ErrorCode addVectors(const Vector3D* v1, const Vector3D* v2, Vector3D* result);

/**
 * Multiplies a vector by a matrix.
 * @param m Matrix.
 * @param v Vector.
 * @param result Output parameter for the result.
 * @return Error code indicating success or failure.
 */
ErrorCode multiplyMatrixVector(const Matrix3D* m, const Vector3D* v, Vector3D* result);

/**
 * Calculates the dot product of two vectors.
 * @param v1 First vector.
 * @param v2 Second vector.
 * @return The dot product.
 */
double dotProduct(const Vector3D* v1, const Vector3D* v2);

/**
 * Creates a variant containing an integer.
 * @param value Integer value.
 * @return Variant containing the integer.
 */
Variant createIntVariant(int value);

/**
 * Creates a variant containing a float.
 * @param value Float value.
 * @return Variant containing the float.
 */
Variant createFloatVariant(float value);

/**
 * Creates a variant containing a double.
 * @param value Double value.
 * @return Variant containing the double.
 */
Variant createDoubleVariant(double value);

/**
 * Creates a variant containing a string.
 * @param value String value.
 * @return Variant containing the string.
 */
Variant createStringVariant(const char* value);

#ifdef __cplusplus
}
#endif

#endif // COMPLEX_H
