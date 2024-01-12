package io.nanovc.indexing.repo.ranges;

/**
 * A split of a range.
 * @param lower The lower split of the range.
 * @param higher The higher split of the range.
 * @param <TUnit> The data type of the unit for the dimension that the range division is for.
 */
public record RangeSplit<TUnit>(Range<TUnit> lower, Range<TUnit> higher)
{
}
