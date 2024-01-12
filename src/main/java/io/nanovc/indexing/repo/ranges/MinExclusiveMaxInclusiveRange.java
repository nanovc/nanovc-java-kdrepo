package io.nanovc.indexing.repo.ranges;

/**
 * A range with an exclusive minimum and inclusive maximum value.
 * This means that we exclude the minimum value and include the maximum value as being in the range.
 * Values that are greater the {@link #min()} value
 * and values that are less than or equal to the {@link #max()} value
 * are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MinExclusiveMaxInclusiveRange<TUnit>(TUnit min, TUnit max) implements Range<TUnit>
{
}
