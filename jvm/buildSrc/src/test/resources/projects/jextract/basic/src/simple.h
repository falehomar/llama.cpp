#ifndef SIMPLE_H
#define SIMPLE_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * A simple structure representing a point in 2D space.
 */
typedef struct {
    int x;
    int y;
} Point;

/**
 * Adds two integers.
 */
int add(int a, int b);

/**
 * Calculates the distance between two points.
 */
double distance(Point p1, Point p2);

/**
 * Maximum number of points allowed.
 */
#define MAX_POINTS 100

#ifdef __cplusplus
}
#endif

#endif // SIMPLE_H
