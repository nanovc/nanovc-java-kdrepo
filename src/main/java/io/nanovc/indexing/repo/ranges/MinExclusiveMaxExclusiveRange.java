package io.nanovc.indexing.repo.ranges;

/**
 * A range with an exclusive minimum and exclusive maximum value.
 * This means that we exclude the minimum and maximum values as being in the range.
 * Values that are greater than the {@link #min()} value
 * and values that are less than the {@link #max()} value
 * are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MinExclusiveMaxExclusiveRange<TUnit>(TUnit min, TUnit max) implements Range<TUnit>
{
}
