package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * A multidimensional hyper cube which describes the dimensions that we are indexing.
 * This allows us to have different data types for each dimension.
 */
public class HyperCube
{
    /**
     * The dimensions in this {@link HyperCube} indexed by their name.
     */
    private final LinkedHashMap<String, Dimension<?>> dimensionsByName = new LinkedHashMap<>();

    /**
     * The dimensions in this {@link HyperCube}.
     */
    private final ArrayList<Dimension<?>> dimensions = new ArrayList<>();

    /**
     * A factory method to create a new dimension for this {@link HyperCube}.
     *
     * @param unitComparator The logic to compare units of this dimension.
     * @param name           The name of the dimension. If this is null or empty then the dimension index is used as the name.
     * @param range          The range for the dimension.
     * @param <TUnit>        The data type of the units for this dimension.
     * @return The dimension that was added.
     */
    public <TUnit> Dimension<TUnit> addDimension(Comparator<TUnit> unitComparator, String name, Range<TUnit> range)
    {
        // Create the range calculator for the dimension:
        RangeCalculator<TUnit> rangeCalculator = new RangeCalculator<>(unitComparator);

        // Get the dimension index that we are going to use:
        int dimensionIndex = this.dimensions.size();

        // Make sure the dimension has a name:
        String dimensionNameToUse = (name == null || name.isEmpty() ? Integer.toString(dimensionIndex) : name);

        // Create the dimension:
        Dimension<TUnit> dimension = new Dimension<>(
            dimensionNameToUse,
            dimensionIndex,
            range,
            unitComparator,
            rangeCalculator
        );

        // Add the dimension to this hyper cube:
        this.dimensions.add(dimension);
        this.dimensionsByName.put(dimensionNameToUse, dimension);

        return dimension;
    }

    /**
     * Gets the dimension with the given name.
     * @param dimensionName The name of the dimension to get.
     * @return The dimension with the given name.
     */
    public <TUnit> Dimension<TUnit> getDimension(String dimensionName)
    {
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
        return (Dimension<TUnit>) this.dimensions.get(dimensionIndex);
    }
}
