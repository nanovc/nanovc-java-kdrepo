package io.nanovc.indexing;

import java.util.List;

/**
 * This splits the range given by the two items into the given number of divisions.
 *
 * @param <T> The type of item that we want to split.
 */
@FunctionalInterface
public interface RangeSplitter<T>
{
    /**
     * This splits the range given by the two items into the given number of divisions.
     *
     * @param o1                         The first item to measure the distance from.
     * @param o2                         The second item to measure the distance to.
     * @param divisions                  The number of divisions to split the range into.
     * @param includeExtraRightMostSplit True to include an extra item in the list (1 more than the requested number of divisions) to represent the right most edge of the range. If this is false then we only have the left edges, leading up to but not including the right part of the range.
     * @param splitsToAddTo              The collection of splits to add to while we split the range.
     */
    void splitRange(T o1, T o2, int divisions, boolean includeExtraRightMostSplit, List<T> splitsToAddTo);
}
