package io.nanovc.indexing.repo.ranges;

/**
 * A range with an exclusive maximum value.
 * This means that we exclude the maximum value as being in the range.
 * Values that are less than the {@link #max()} value are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MaxExclusiveRange<TUnit>(TUnit max) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new MinInclusiveRange<>(this.max());
    }

    @Override public String toString()
    {
        return "<" + max();
    }
}
