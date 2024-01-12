package io.nanovc.indexing.repo.ranges;


/**
 * A range with an inclusive minimum and inclusive maximum value.
 * This means that we include the minimum and maximum values as being in the range.
 * Values that are greater than or equal to the {@link #min()} value
 * and values that are less than or equal to the {@link #max()} value
 * are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MinInclusiveMaxInclusiveRange<TUnit>(TUnit min, TUnit max) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new NotRange<>(this);
    }
}
