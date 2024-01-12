package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.arithmetic.Arithmetic;
import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A multidimensional hyper cube definition which describes the dimensions that we are indexing.
 * This allows us to have different data types for each dimension.
 */
public class HyperCubeDefinition
{
    /**
     * The dimensions in this {@link HyperCubeDefinition} indexed by their name.
     */
    private final LinkedHashMap<String, Dimension<?>> dimensionsByName = new LinkedHashMap<>();

    /**
     * The dimensions in this {@link HyperCubeDefinition}.
     */
    private final ArrayList<Dimension<?>> dimensions = new ArrayList<>();

    /**
     * A factory method to create a new dimension for this {@link HyperCubeDefinition}.
     *
     * @param arithmetic     The logic for performing arithmetic in this dimension.
     * @param name           The name of the dimension. If this is null or empty then the dimension index is used as the name.
     * @param range          The range for the dimension.
     * @param <TUnit>        The data type of the units for this dimension.
     * @return The dimension that was added.
     */
    public <TUnit> Dimension<TUnit> addDimension(
        Arithmetic<TUnit> arithmetic,
        String name,
        Range<TUnit> range
    )
    {
        // Create the range calculator for the dimension:
        RangeCalculator<TUnit> rangeCalculator = new RangeCalculator<>(arithmetic);

        // Get the dimension index that we are going to use:
        int dimensionIndex = this.dimensions.size();

        // Make sure the dimension has a name:
        String dimensionNameToUse = (name == null || name.isEmpty() ? Integer.toString(dimensionIndex) : name);

        // Create the dimension:
        Dimension<TUnit> dimension = new Dimension<>(
            dimensionNameToUse,
            dimensionIndex,
            range,
            arithmetic,
            rangeCalculator
        );

        // Add the dimension to this hyper cube:
        this.dimensions.add(dimension);
        this.dimensionsByName.put(dimensionNameToUse, dimension);

        return dimension;
    }

    /**
     * Gets the dimension with the given name.
     *
     * @param dimensionName The name of the dimension to get.
     * @return The dimension with the given name.
     */
    public <TUnit> Dimension<TUnit> getDimension(String dimensionName)
    {
        //noinspection unchecked
        return (Dimension<TUnit>) this.dimensionsByName.get(dimensionName);
    }

    /**
     * Gets the dimension with the given index.
     *
     * @param dimensionIndex The index of the dimension to get.
     * @return The dimension with that index.
     */
    public <TUnit> Dimension<TUnit> getDimension(int dimensionIndex)
    {
        //noinspection unchecked
        return (Dimension<TUnit>) this.dimensions.get(dimensionIndex);
    }

    /**
     * Creates a hyper cube that covers the range of this definition.
     *
     * @return A hyper cube that covers the range of this definition.
     */
    public HyperCube createHyperCube()
    {
        // Get the ranges for this hyper cube:
        Range<?>[] ranges = new Range<?>[this.getDimensionCount()];
        for (int dimIndex = 0; dimIndex < this.getDimensionCount(); dimIndex++)
        {
            // Get the dimension:
            Dimension<Object> dimension = getDimension(dimIndex);

            // Get the range:
            ranges[dimIndex] = dimension.getRange();
        }

        // Create the hyper cube:
        HyperCube cube = new HyperCube(this, ranges);

        return cube;
    }

    /**
     * @return The number of dimensions in this {@link HyperCube}.
     */
    public int getDimensionCount()
    {
        return this.dimensions.size();
    }

    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder();
        int dimensionCount = this.getDimensionCount();
        for (int dimensionIndex = 0; dimensionIndex < dimensionCount; dimensionIndex++)
        {
            if (dimensionIndex != 0) sb.append(",");
            Dimension<?> dimension = this.getDimension(dimensionIndex);
            sb.append(dimension.getName());
        }
        return sb.toString();
    }

}
