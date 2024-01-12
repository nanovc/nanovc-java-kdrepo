package io.nanovc.indexing.repo.ranges;

/**
 * This inverts the given range.
 * A value is considered the opposite of the inner range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record NotRange<TUnit>(Range<TUnit> innerRange) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        // Since we want to invert what we already are, an inverse, we can just return our inner range:
        return this.innerRange();
    }

    @Override public String toString()
    {
        return "NOT " + innerRange();
    }
}
