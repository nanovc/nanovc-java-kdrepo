package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;

/**
 * A hyper cube that defines a region of a multidimensional space.
 */
public class HyperCube
{

    public HyperCube(HyperCubeDefinition definition, Range<?>... ranges)
    {
        this.definition = definition;
        this.ranges = ranges;
    }

    /**
     * The definition of the hyper cube.
     */
    private final HyperCubeDefinition definition;

    /**
     * The ranges for each dimension of the hyper cube.
     */
    private final Range<?>[] ranges;

    /**
     * Gets the definition of the hyper cube.
     *
     * @return The definition of the hyper cube.
     */
    public HyperCubeDefinition getDefinition()
    {
        return definition;
    }

    /**
     * Gets the ranges for each dimension of the hyper cube.
     *
     * @return The ranges for each dimension of the hyper cube.
     */
    public Range<?>[] getRanges()
    {
        return ranges;
    }

    /**
     * Tests whether the given {@link HyperCoord coordinate} is in this {@link HyperCube hyper cube}.
     *
     * @param coord The {@link HyperCoord coordinate} to test.
     * @return True if the coordinate is in this {@link HyperCube hyper cube}.
     */
    public boolean isCoordinateInRange(HyperCoord coord)
    {
        return isCoordinateInRange(coord.values());
    }

    /**
     * Tests whether the given {@link HyperCoord coordinate} is in this {@link HyperCube hyper cube}.
     *
     * @param coord The coordinate to test.
     * @return True if the coordinate is in this {@link HyperCube hyper cube}.
     */
    public boolean isCoordinateInRange(Object... coord)
    {
        // Get the ranges:
        Range<?>[] ranges = getRanges();

        // Get the definition of the hyper cube so that we can check the ranges:
        HyperCubeDefinition definition = getDefinition();

        // Make sure that the coordinate is in each of the ranges:
        for (int dimIndex = 0; dimIndex < coord.length; dimIndex++)
        {
            // Get the dimension:
            Dimension<Object> dimension = definition.getDimension(dimIndex);

            // Get the range for this coordinate:
            //noinspection unchecked
            Range<Object> range = (Range<Object>) ranges[dimIndex];

            // Get the value for this coordinate:
            Object value = coord[dimIndex];

            // Check whether this coordinate is in range:
            if (!dimension.getRangeCalculator().isInRange(value, range))
            {
                // This value is not within the coordinate range.
                return false;
            }
        }
        // If we get here then the coordinate is in this hyper cube.
        return true;
    }

    @Override public String toString()
    {
        HyperCubeDefinition definition = this.getDefinition();
        if (definition == null) return super.toString();

        Range<?>[] ranges = getRanges();
        if (ranges == null) return super.toString();

        StringBuilder sb = new StringBuilder();
        int dimensionCount = definition.getDimensionCount();
        for (int dimensionIndex = 0; dimensionIndex < dimensionCount; dimensionIndex++)
        {
            if (dimensionIndex != 0) sb.append("\n");
            Dimension<?> dimension = definition.getDimension(dimensionIndex);
            sb.append(dimension.getName());
            sb.append(":");
            sb.append(ranges[dimensionIndex]);
        }
        return sb.toString();
    }
}
