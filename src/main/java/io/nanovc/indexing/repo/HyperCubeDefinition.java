package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;

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
     * @param unitComparator The logic to compare units of this dimension.
     * @param unitAdder      The logic to add two units in this dimension.
     * @param unitSubtractor The logic to subtract two units in this dimension.
     * @param name           The name of the dimension. If this is null or empty then the dimension index is used as the name.
     * @param range          The range for the dimension.
     * @param <TUnit>        The data type of the units for this dimension.
     * @return The dimension that was added.
     */
    public <TUnit> Dimension<TUnit> addDimension(
        Comparator<TUnit> unitComparator,
        BiFunction<TUnit, TUnit, TUnit> unitAdder,
        BiFunction<TUnit, TUnit, TUnit> unitSubtractor,
        String name,
        Range<TUnit> range
    )
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
            unitAdder,
            unitSubtractor,
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
}
