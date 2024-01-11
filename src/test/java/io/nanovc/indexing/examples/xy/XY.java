package io.nanovc.indexing.examples.xy;

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
}
