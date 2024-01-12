package io.nanovc.indexing.repo.ranges;

import java.util.Set;

/**
 * A range that doesn't match for many specific values.
 * A value is always considered out of this range if it matches any of the given values exactly.
 * If it doesn't match exactly then it is considered in the range.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public record NotMultiValueRange<TUnit>(Set<TUnit> values) implements Range<TUnit>
{
    @Override public Range<TUnit> inverse()
    {
        return new MultiValueRange<>(this.values());
    }
}
