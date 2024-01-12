package io.nanovc.indexing.repo.ranges;

import java.util.Set;

/**
 * A range that matches for many specific values.
 * A value is always considered in this range if it matches any of the given values exactly.
 * If it doesn't match exactly then it is considered out of range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record MultiValueRange<TUnit>(Set<TUnit> values) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new NotMultiValueRange<>(this.values());
    }
}
