package io.nanovc.indexing.repo.ranges;


/**
 * A range with an exclusive minimum value.
 * This means that we exclude the minimum value as being in the range.
 * Values that are greater than the {@link #min()} value are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MinExclusiveRange<TUnit>(TUnit min) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new MaxInclusiveRange<>(this.min());
    }
}
