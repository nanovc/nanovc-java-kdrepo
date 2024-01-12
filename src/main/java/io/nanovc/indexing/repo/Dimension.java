package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;

/**
 * A dimension that we are indexing.
 *
 * @param name           The descriptive name of this dimension.
 *                       If no name is provided, then the {@link #dimensionIndex} is used instead.
 * @param dimensionIndex The index of the dimension.
 *                       Zero based.
 * @param range          The range of values for this dimension.
 * @param <TUnit>        The data type unit of this dimension.
 */
public record Dimension<TUnit>(
    String name,
    int dimensionIndex,
    Range<TUnit> range
    )
{
}
