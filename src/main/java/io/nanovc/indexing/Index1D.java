package io.nanovc.indexing;

import java.util.Comparator;

/**
 * A one-dimensional index.
 * This is used for searching for items efficiently.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public interface Index1D<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
{
    /**
     * Adds the given item to the index.
     *
     * @param item The item to add to the index.
     */
    void add(TItem item);

    /**
     * This finds the nearest item in the index to the given item.
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    TItem searchNearest(TItem item);
}
