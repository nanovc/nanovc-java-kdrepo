package io.nanovc.indexing.repo.ranges;

/**
 * This logically AND's two ranges together.
 * This allows us to intersect ranges.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record AndRange<TUnit>(Range<TUnit> range, Range<TUnit> other) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new NotRange<>(this);
    }

    @Override public String toString()
    {
        return range() + " && " + other();
    }
}
