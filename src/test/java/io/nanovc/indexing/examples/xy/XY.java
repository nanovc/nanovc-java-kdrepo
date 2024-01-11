package io.nanovc.indexing.examples.xy;

import io.nanovc.indexing.examples.x.X;

import java.util.List;

/**
 * A record for an X and Y coordinate.
 *
 * @param x The x coordinate.
 * @param y The y coordinate.
 */
public record XY(double x, double y)
{
    /**
     * Extracts the coordinate for the given dimension.
     *
     * @param dimension The dimension to extract.
     * @return The coordinate for the given dimension.
     */
    public static double extractCoordinate(XY item, Integer dimension)
    {
        return switch (dimension)
        {
            case 0 -> item.x();
            case 1 -> item.y();
            default -> throw new IllegalStateException("Unexpected value: " + dimension);
        };
    }

    /**
     * The name relates to the distance a taxi has to drive in a rectangular street grid (like that of the New York borough of Manhattan) to get from the origin to the point x.
     * The set of vectors whose 1-norm is a given constant forms the surface of a cross polytope, which has dimension equal to the dimension of the vector space minus 1.
     * The Taxicab norm is also called the l1 (\ell ^{1}) norm.
     * The distance derived from this norm is called the Manhattan distance or
     * l1 (\ell ^{1}) distance.
     * <p>
     * The 1-norm is simply the sum of the absolute values of the columns.
     * <p>
     * https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm
     */
    public static double measureDistanceL1NormManhattanTaxicabNorm(XY item1, XY item2)
    {
        double diffX = item2.x() - item1.x();
        double diffY = item2.y() - item1.y();
        return Math.abs(diffX) + Math.abs(diffY);
    }

    /**
     * In mathematics, the Euclidean distance between two points in Euclidean space is the length of the line segment between them.
     * It can be calculated from the Cartesian coordinates of the points using the Pythagorean theorem,
     * and therefore is occasionally called the Pythagorean distance.
     * <p>
     * The 2-norm is square root of the sum of the square of the columns.
     * <p>
     * https://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm
     * https://en.wikipedia.org/wiki/Euclidean_distance
     */
    public static double measureDistanceL2NormEuclidean(XY item1, XY item2)
    {
        double diffX = item2.x() - item1.x();
        double diffY = item2.y() - item1.y();
        return Math.sqrt( (diffX * diffX) + (diffY * diffY) );
    }

    /**
     * In many applications, and in particular when comparing distances,
     * it may be more convenient to omit the final square root in the calculation of Euclidean distances,
     * as the square root does not change the order
     * The value resulting from this omission is the square of the Euclidean distance,
     * and is called the squared Euclidean distance.
     * <p>
     * The 2-norm squared is the sum of the square of the columns.
     * <p>
     * https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance
     * https://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm
     * https://en.wikipedia.org/wiki/Euclidean_distance
     */
    public static double measureDistanceL2NormEuclideanSquared(XY item1, XY item2)
    {
        double diffX = item2.x() - item1.x();
        double diffY = item2.y() - item1.y();
        return (diffX * diffX) + (diffY * diffY);
    }

    /**
     * In many applications, and in particular when comparing distances,
     * it may be more convenient to omit the final square root in the calculation of Euclidean distances,
     * as the square root does not change the order
     * The value resulting from this omission is the square of the Euclidean distance,
     * and is called the squared Euclidean distance.
     * <p>
     * The 2-norm squared is the sum of the square of the columns.
     * <p>
     * https://en.wikipedia.org/wiki/Norm_(mathematics)#Maximum_norm_(special_case_of:_infinity_norm,_uniform_norm,_or_supremum_norm)
     * https://en.wikipedia.org/wiki/Norm_(mathematics)#p-norm
     */
    public static double measureDistanceLInfNormMaximumInfinity(XY item1, XY item2)
    {
        double diffX = item2.x() - item1.x();
        double diffY = item2.y() - item1.y();
        return Math.max(Math.abs(diffX), Math.abs(diffY));
    }

    /**
     * This splits the range given by the two items into the given number of divisions.
     *
     * @param o1                         The first item to measure the distance from.
     * @param o2                         The second item to measure the distance to.
     * @param divisions                  The number of divisions to split the range into.
     * @param includeExtraRightMostSplit True to include an extra item in the list (1 more than the requested number of divisions) to represent the right most edge of the range. If this is false then we only have the left edges, leading up to but not including the right part of the range.
     * @param splitsToAddTo              The collection of splits to add to while we split the range.
     */
    public static void splitRange(XY o1, XY o2, int divisions, boolean includeExtraRightMostSplit, List<XY> splitsToAddTo)
    {
        final int dimensions = 2;
        double[] startingValues = new double[dimensions];
        double[] endingValues = new double[dimensions];
        double[] ranges = new double[dimensions];
        double[] steps = new double[dimensions];

        for (int i = 0; i < dimensions; i++)
        {
            // Get the range:
            startingValues[i] = extractCoordinate(o1, i);
            endingValues[i] = extractCoordinate(o2, i);
            ranges[i] = endingValues[i] - startingValues[i];

            // Get the step:
            steps[i] = ranges[i] / divisions;

            // Make sure the step is at least one unit:
            if (steps[i] == 0.0) steps[i] = 1.0;
        }

        // Create the splits:
        XY currentValue = new XY(startingValues[0], startingValues[1]);
        for (int i = 0;i < divisions;i++)
        {
            // Add the item to the splits:
            splitsToAddTo.add(currentValue);

            // Create the new next:
            currentValue = new XY(currentValue.x() + steps[0], currentValue.y() + steps[1]);
        }

        // Add the ending item if desired (NOTE: it means we might have more than the requested (divisions) items in the result, but this is useful to have an entry as the right most edge:
        if (includeExtraRightMostSplit) splitsToAddTo.add(o2);
    }

    /**
     * This finds the division index for an item in a range that is defined by two other items.
     *
     * @param o1                The first item to measure the distance from.
     * @param o2                The second item to measure the distance to.
     * @param divisions         The number of divisions to split the range into.
     * @param itemToFindInRange The item to find the index of for the given range.
     * @param dimension         The index of the dimension to perform the range split in. Zero based.
     * @return The index of the division of the given item in the range specified.
     */
    public static int findIndexInRange(XY o1, XY o2, int divisions, XY itemToFindInRange, int dimension)
    {
        // Get the range:
        double startingValue = extractCoordinate(o1, dimension);
        double endingValue = extractCoordinate(o2, dimension);
        double range = endingValue - startingValue;

        // Get the step:
        double step = range / divisions;

        // Make sure the step is at least one unit:
        if (step == 0.0) step = 1.0;

        // Get the value for the item to find:
        double itemToFindValueInDimension = extractCoordinate(itemToFindInRange, dimension);

        // Get the index of the item:
        int index = (int) ((itemToFindValueInDimension - startingValue) / step);

        // Clamp the value in range:
        index = Math.max(0, Math.min(index, divisions - 1));

        return index;
    }
}
