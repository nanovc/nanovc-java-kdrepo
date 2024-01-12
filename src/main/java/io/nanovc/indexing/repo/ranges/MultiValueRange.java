package io.nanovc.indexing.repo.ranges;

import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

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

    @Override public String toString()
    {
        Set<TUnit> values = this.values();
        if (values == null || values.size() == 0) return "[]";
        else
        {
            StringJoiner joiner = new StringJoiner(",");
            for (TUnit value : values)
            {
                joiner.add(Objects.toString(value));
            }
            return "[" + joiner.toString() + "]";
        }
    }
}
