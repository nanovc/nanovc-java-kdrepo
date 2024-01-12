package io.nanovc.indexing.repo.ranges;

/**
 * A range that matches for all except exactly one value.
 * A value is always considered out of this range if it matches the given value exactly.
 * If it doesn't match exactly then it is considered in range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record NotSingleValueRange<TUnit>(TUnit value) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new SingleValueRange<>(this.value());
    }
}
