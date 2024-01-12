package io.nanovc.indexing.repo.ranges;

/**
 * This describes a range of a specific dimension.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public sealed interface Range<TUnit>
    permits
    NotRange, AndRange, OrRange,
    UnBoundedRange, NeverInRange,
    SingleValueRange, NotSingleValueRange,
    MultiValueRange, NotMultiValueRange,
    MinInclusiveRange, MinExclusiveRange,
    MaxInclusiveRange, MaxExclusiveRange,
    MinInclusiveMaxInclusiveRange, MinInclusiveMaxExclusiveRange,
    MinExclusiveMaxInclusiveRange, MinExclusiveMaxExclusiveRange
{
    /**
     * Gets the inverse of this range.
     * @return The inverse of this range.
     */
    Range<TUnit> inverse();
}
