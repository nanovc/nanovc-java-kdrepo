package io.nanovc.indexing.repo.ranges;

import io.nanovc.indexing.repo.arithmetic.Arithmetic;

/**
 * Performs calculations on ranges of values.
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public class RangeCalculator<TUnit>
{
    /**
     * The logic for arithmetic in this dimension.
     */
    private final Arithmetic<TUnit> arithmetic;

    public RangeCalculator(Arithmetic<TUnit> arithmetic)
    {
        this.arithmetic = arithmetic;
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
            case NotRange<TUnit>                      r -> !isInRange(value, r.innerRange());
            case OrRange<TUnit>                       r -> isInRange(value, r.range()) || isInRange(value, r.other());
            case AndRange<TUnit>                      r -> isInRange(value, r.range()) && isInRange(value, r.other());
            case UnBoundedRange<TUnit>                r -> true;
            case NeverInRange<TUnit>                  r -> false;
            case SingleValueRange<TUnit>              r -> this.arithmetic.compare(r.value(), value) == 0;
            case NotSingleValueRange<TUnit>           r -> this.arithmetic.compare(r.value(), value) != 0;
            case MultiValueRange<TUnit>               r -> r.values().contains(value);
            case NotMultiValueRange<TUnit>            r -> !r.values().contains(value);
            case MinInclusiveRange<TUnit>             r -> this.arithmetic.compare(r.min(), value) <= 0;
            case MinExclusiveRange<TUnit>             r -> this.arithmetic.compare(r.min(), value) <  0;
            case MaxInclusiveRange<TUnit>             r -> this.arithmetic.compare(value, r.max()) <= 0;
            case MaxExclusiveRange<TUnit>             r -> this.arithmetic.compare(value, r.max()) <  0;
            case MinInclusiveMaxInclusiveRange<TUnit> r -> this.arithmetic.compare(r.min(), value) <= 0 && this.arithmetic.compare(value, r.max()) <= 0;
            case MinInclusiveMaxExclusiveRange<TUnit> r -> this.arithmetic.compare(r.min(), value) <= 0 && this.arithmetic.compare(value, r.max()) <  0;
            case MinExclusiveMaxInclusiveRange<TUnit> r -> this.arithmetic.compare(r.min(), value) <  0 && this.arithmetic.compare(value, r.max()) <= 0;
            case MinExclusiveMaxExclusiveRange<TUnit> r -> this.arithmetic.compare(r.min(), value) <  0 && this.arithmetic.compare(value, r.max()) <  0;
        };
    }

    /**
     * Splits the range at the given point.
     * @param range The range to split.
     * @param splitValue The value to divide the range at. It is assumed to be in the middle of the range.
     * @param inclusion  The way in which to include the splitValue in the split range result. In the lower, higher or both range splits.
     * @return The split range.
     */
    public RangeSplit<TUnit> splitRange(Range<TUnit> range, TUnit splitValue, RangeSplitInclusion inclusion)
    {
        return switch (range)
        {
            //case NotRange<TUnit>                      r -> null;
            //case OrRange<TUnit>                       r -> null;
            //case AndRange<TUnit>                      r -> null;
            //case UnBoundedRange<TUnit>                r -> null;
            //case NeverInRange<TUnit>                  r -> null;
            //case SingleValueRange<TUnit>              r -> null;
            //case NotSingleValueRange<TUnit>           r -> null;
            //case MultiValueRange<TUnit>               r -> null;
            //case NotMultiValueRange<TUnit>            r -> null;

            case MinInclusiveRange<TUnit>             r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveRange<>(splitValue));
                case Higher -> new RangeSplit<>(new MinInclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
                case Both   -> new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
            };
            case MinExclusiveRange<TUnit>             r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveRange<>(splitValue));
                case Higher -> new RangeSplit<>(new MinExclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
                case Both   -> new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
            };
            case MaxInclusiveRange<TUnit>             r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinExclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Higher -> new RangeSplit<>(new MaxExclusiveRange<>(splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Both   -> new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
            };
            case MaxExclusiveRange<TUnit>             r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinExclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Higher -> new RangeSplit<>(new MaxExclusiveRange<>(splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Both   -> new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
            };


            case MinInclusiveMaxInclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Higher -> new RangeSplit<>(new MinInclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Both   -> new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
            };
            case MinInclusiveMaxExclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Higher -> new RangeSplit<>(new MinInclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Both   -> new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
            };
            case MinExclusiveMaxInclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Higher -> new RangeSplit<>(new MinExclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Both   -> new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
            };
            case MinExclusiveMaxExclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower  -> new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Higher -> new RangeSplit<>(new MinExclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Both   -> new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
            };

            default -> throw new UnsupportedOperationException("Cannot split the given range");
        };
    }

    public Range<TUnit> createRangeLessThanOrEqualTo(TUnit value)
    {
        return new MaxInclusiveRange<>(value);
    }

    public Range<TUnit> createRangeLessThan(TUnit value)
    {
        return new MaxExclusiveRange<>(value);
    }

    public Range<TUnit> createRangeEqualTo(TUnit value)
    {
        return new SingleValueRange<>(value);
    }

    public Range<TUnit> createRangeGreaterThan(TUnit value)
    {
        return new MinExclusiveRange<>(value);
    }

    public Range<TUnit> createRangeGreaterThanOrEqualTo(TUnit value)
    {
        return new MinInclusiveRange<>(value);
    }


    public Range<TUnit> createBoundedRangeLessThanOrEqualTo(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MaxInclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeLessThan(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MaxExclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeEqualTo(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new SingleValueRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeGreaterThan(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MinExclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeGreaterThanOrEqualTo(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MinInclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeBetween(TUnit minInclusive, TUnit maxInclusive, Range<TUnit> bounds)
    {
        return new AndRange<>(new MinInclusiveMaxInclusiveRange<>(minInclusive, maxInclusive), bounds);
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
}
