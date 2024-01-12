package io.nanovc.indexing.repo.ranges;

/**
 * A range with an inclusive minimum value.
 * This means that we include the minimum value as being in the range.
 * Values that are greater than or equal to the {@link #min()} value are considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MinInclusiveRange<TUnit>(TUnit min) implements Range<TUnit>
{
}
