package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;

import java.util.Comparator;

/**
 * A dimension that we are indexing.
 *
 * @param name           The descriptive name of this dimension.
 *                       If no name is provided, then the {@link #dimensionIndex} is used instead.
 * @param dimensionIndex The index of the dimension.
 *                       Zero based.
 * @param range          The range of values for this dimension.
 * @param unitComparator The logic to compare units of this dimension.
 * @param rangeCalculator The range calculator to use for performing computations on ranges in this dimension.
 * @param <TUnit>        The data type of the units for this dimension.
 */
public record Dimension<TUnit>(
    String name,
    int dimensionIndex,
    Range<TUnit> range,
    Comparator<TUnit> unitComparator,
    RangeCalculator<TUnit> rangeCalculator
    )
{
    /**
     * Checks whether the given value is in range for the dimension.
     * @param value The value to check whether it is in range for this dimension.
     * @return True if the value is in range for the dimension. False if it is out of range.
     */
    public boolean isInRange(TUnit value)
    {
        return rangeCalculator().isInRange(value, range());
    }
}
