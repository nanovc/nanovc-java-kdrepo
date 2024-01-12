package io.nanovc.indexing.repo.ranges;

import java.util.Comparator;

/**
 * Performs calculations on ranges of values.
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public class RangeCalculator<TUnit>
{
    /**
     * This is used for comparing units of the dimension.
     */
    private final Comparator<TUnit> unitComparator;

    /**
     * @param unitComparator The comparator used for comparing units of the dimension.
     */
    public RangeCalculator(Comparator<TUnit> unitComparator)
    {
        this.unitComparator = unitComparator;
    }

    /**
     * Checks whether the given value is in the range.
     * @param value The value to check whether it is in the range.
     * @param range The range that we want to check.
     * @return True if the value is in the range. False if not.
     */
    public boolean isInRange(TUnit value, Range<TUnit> range)
    {
        return switch (range)
        {
            case UnBoundedRange<TUnit>                r -> true;
            case NeverInRange<TUnit>                  r -> false;
            case SingleValueRange<TUnit>              r -> this.unitComparator.compare(r.value(), value) == 0;
            case NotSingleValueRange<TUnit>           r -> this.unitComparator.compare(r.value(), value) != 0;
            case MultiValueRange<TUnit>               r -> r.values().contains(value);
            case NotMultiValueRange<TUnit>            r -> !r.values().contains(value);
            case MinInclusiveRange<TUnit>             r -> this.unitComparator.compare(r.min(), value) <= 0;
            case MinExclusiveRange<TUnit>             r -> this.unitComparator.compare(r.min(), value) <  0;
            case MaxInclusiveRange<TUnit>             r -> this.unitComparator.compare(value, r.max()) <= 0;
            case MaxExclusiveRange<TUnit>             r -> this.unitComparator.compare(value, r.max()) <  0;
            case MinInclusiveMaxInclusiveRange<TUnit> r -> this.unitComparator.compare(r.min(), value) <= 0 && this.unitComparator.compare(value, r.max()) <= 0;
            case MinInclusiveMaxExclusiveRange<TUnit> r -> this.unitComparator.compare(r.min(), value) <= 0 && this.unitComparator.compare(value, r.max()) <  0;
            case MinExclusiveMaxInclusiveRange<TUnit> r -> this.unitComparator.compare(r.min(), value) <  0 && this.unitComparator.compare(value, r.max()) <= 0;
            case MinExclusiveMaxExclusiveRange<TUnit> r -> this.unitComparator.compare(r.min(), value) <  0 && this.unitComparator.compare(value, r.max()) <  0;
        };
    }

}
