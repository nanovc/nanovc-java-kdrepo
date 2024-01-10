package io.nanovc.indexing.kdtree.bentley1990;

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
}
