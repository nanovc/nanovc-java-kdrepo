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
     * @param o1            The first item to measure the distance from.
     * @param o2            The second item to measure the distance to.
     * @param divisions     The number of divisions to split the range into.
     * @param splitsToAddTo The collection of splits to add to while we split the range.
     */
    void splitRange(T o1, T o2, int divisions, List<T> splitsToAddTo);
}
