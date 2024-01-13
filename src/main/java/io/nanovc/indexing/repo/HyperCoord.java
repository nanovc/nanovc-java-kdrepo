package io.nanovc.indexing.repo;

/**
 * A coordinate in a multidimensional {@link HyperCube hyper cube}.
 * @param values The values in this {@link HyperCoord hyper coordinate}.
 */
public record HyperCoord(Object... values)
{

    /**
     * Gets the value for a specific dimension.
     * @param dimension The dimension that we want to get.
     * @return The value for that dimension.
     */
    public Object getValue(Dimension<?> dimension)
    {
        return getValue(dimension.getDimensionIndex());
    }

    /**
     * Gets the value for a specific dimension.
     * @param dimensionIndex The index of the dimension that we want to get.
     * @return The value for that dimension.
     */
    public Object getValue(int dimensionIndex)
    {
        return values()[dimensionIndex];
    }
}
