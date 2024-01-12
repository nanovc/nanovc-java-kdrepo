package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;

import java.util.Comparator;
import java.util.function.BiFunction;

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
     * The logic to compare units of this dimension.
     */
    private final Comparator<TUnit> unitComparator;

    /**
     * The logic to add two units together in this dimension.
     */
    private final BiFunction<TUnit, TUnit, TUnit> unitAdder;

    /**
     * The logic to subtract two units in this dimension.
     * It corresponds to first - second.
     */
    private final BiFunction<TUnit, TUnit, TUnit> unitSubtractor;

    /**
     * The range calculator to use for performing computations on ranges in this dimension.
     */
    private final RangeCalculator<TUnit> rangeCalculator;

    public Dimension(
        String name,
        int index,
        Range<TUnit> range,
        Comparator<TUnit> comparator,
        BiFunction<TUnit, TUnit, TUnit> unitAdder,
        BiFunction<TUnit, TUnit, TUnit> unitSubtractor,
        RangeCalculator<TUnit> calculator
    )
    {
        this.name = name;
        dimensionIndex = index;
        this.range = range;
        unitComparator = comparator;
        this.unitAdder = unitAdder;
        this.unitSubtractor = unitSubtractor;
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
     * Adds the two values together.
     * result = left + right
     *
     * @param left  The left value to add.
     * @param right The right value to add.
     * @return The result after adding. result = left + right
     */
    public TUnit add(TUnit left, TUnit right)
    {
        return this.getUnitAdder().apply(left, right);
    }

    /**
     * Subtracts the two values.
     * result = left - right
     *
     * @param left  The left value to add.
     * @param right The right value to add.
     * @return The result after subtracting. result = left - right
     */
    public TUnit subtract(TUnit left, TUnit right)
    {
        return this.getUnitSubtractor().apply(left, right);
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
     * Gets the logic to compare units of this dimension.
     *
     * @return The logic to compare units of this dimension.
     */
    public Comparator<TUnit> getUnitComparator()
    {
        return unitComparator;
    }

    /**
     * Gets the logic to add two units together in this dimension.
     *
     * @return The logic to add two units together in this dimension.
     */
    public BiFunction<TUnit, TUnit, TUnit> getUnitAdder()
    {
        return unitAdder;
    }

    /**
     * Gets the logic to subtract two units in this dimension.
     * It corresponds to first - second.
     *
     * @return The logic to subtract two units in this dimension. It corresponds to first - second.
     */
    public BiFunction<TUnit, TUnit, TUnit> getUnitSubtractor()
    {
        return unitSubtractor;
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
}
