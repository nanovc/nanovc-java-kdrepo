package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.arithmetic.Arithmetic;
import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;

/**
 * A dimension that we are indexing.
 *
 * @param <TUnit> The data type of the units for this dimension.
 */
public class Dimension<TUnit>
{
    /**
     * The descriptive name of this dimension.
     */
    private final String name;

    /**
     * The index of the dimension.
     * Zero based.
     */
    private final int dimensionIndex;

    /**
     * The range of values for this dimension.
     */
    private final Range<TUnit> range;

    /**
     * The logic for arithmetic in this dimension.
     */
    private final Arithmetic<TUnit> arithmetic;

    /**
     * The range calculator to use for performing computations on ranges in this dimension.
     */
    private final RangeCalculator<TUnit> rangeCalculator;

    public Dimension(
        String name,
        int index,
        Range<TUnit> range,
        Arithmetic<TUnit> arithmetic,
        RangeCalculator<TUnit> calculator
    )
    {
        this.name = name;
        dimensionIndex = index;
        this.range = range;
        this.arithmetic = arithmetic;
        rangeCalculator = calculator;
    }

    /**
     * Checks whether the given value is in range for the dimension.
     *
     * @param value The value to check whether it is in range for this dimension.
     * @return True if the value is in range for the dimension. False if it is out of range.
     */
    public boolean isInRange(TUnit value)
    {
        return getRangeCalculator().isInRange(value, getRange());
    }

    /**
     * Creates a range between the given values and bounds it by the range of this dimension.
     *
     * @param minInclusive The minimum value for the range. Inclusive.
     * @param maxInclusive The maximum value for the range. Exclusive.
     * @return The bounded range between the values.
     */
    public Range<TUnit> rangeBetween(TUnit minInclusive, TUnit maxInclusive)
    {
        return this.getRangeCalculator().createBoundedRangeBetween(minInclusive, maxInclusive, this.getRange());
    }

    /**
     * Gets the descriptive name of this dimension.
     *
     * @return The descriptive name of this dimension.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the index of the dimension.
     * Zero based.
     *
     * @return The index of the dimension.
     */
    public int getDimensionIndex()
    {
        return dimensionIndex;
    }

    /**
     * Gets the range of values for this dimension.
     *
     * @return The range of values for this dimension.
     */
    public Range<TUnit> getRange()
    {
        return range;
    }

    /**
     * Gets the logic for arithmetic in this dimension.
     *
     * @return The logic for arithmetic in this dimension.
     */
    public Arithmetic<TUnit> getArithmetic()
    {
        return arithmetic;
    }

    /**
     * Gets the range calculator to use for performing computations on ranges in this dimension.
     *
     * @return The range calculator to use for performing computations on ranges in this dimension.
     */
    public RangeCalculator<TUnit> getRangeCalculator()
    {
        return rangeCalculator;
    }

    @Override public String toString()
    {
        return "Dimension{" +
               "name='" + name + '\'' +
               ", dimensionIndex=" + dimensionIndex +
               ", range=" + range +
               '}';
    }

    /**
     * Gets the midpoint of the dimensions range.
     *
     * @return The midpoint of the dimensions range.
     */
    public TUnit getRangeMidPoint()
    {
        return this.rangeCalculator.midPoint(this.range);
    }
}
