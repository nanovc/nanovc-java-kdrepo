package io.nanovc.indexing.repo.ranges;

/**
 * A range with an inclusive minimum and exclusive maximum value.
 * This means that we include the minimum value but exclude the maximum value as being in the range.
 * Values that are greater than or equal to the {@link #min()} value
 * and values that are less than the {@link #max()} value
 * are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MinInclusiveMaxExclusiveRange<TUnit>(TUnit min, TUnit max) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new NotRange<>(this);
    }
}
