package io.nanovc.indexing.repo.ranges;

/**
 * An unbounded range that does not have a min and a max value.
 * A value is always considered in this range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record UnBoundedRange<TUnit>() implements Range<TUnit>
{
}
