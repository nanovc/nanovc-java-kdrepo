package io.nanovc.indexing.repo.ranges;

/**
 * This logically OR's two ranges together.
 * This allows us to compose ranges.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record OrRange<TUnit>(Range<TUnit> range, Range<TUnit> other) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new NotRange<>(this);
    }
}
