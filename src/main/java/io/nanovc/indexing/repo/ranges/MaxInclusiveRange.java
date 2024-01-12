package io.nanovc.indexing.repo.ranges;


/**
 * A range with an inclusive maximum value.
 * This means that we include the maximum value as being in the range.
 * Values that are less than or equal to the {@link #max()} value are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MaxInclusiveRange<TUnit>(TUnit max) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new MinExclusiveRange<>(this.max());
    }

    @Override public String toString()
    {
        return "<=" + max();
    }
}
