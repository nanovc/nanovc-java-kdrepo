package io.nanovc.indexing.repo.ranges;

/**
 * A range that never has values in it.
 * A value is never considered in this range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record NeverInRange<TUnit>() implements Range<TUnit>
{
}
