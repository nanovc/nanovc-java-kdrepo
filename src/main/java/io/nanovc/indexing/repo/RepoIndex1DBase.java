package io.nanovc.indexing.repo;

import io.nanovc.indexing.Index1DBase;
import io.nanovc.indexing.Measurer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A base class for a one dimensional {@link RepoIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public abstract class RepoIndex1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends Index1DBase<TItem>
    implements RepoIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator>
{
    /**
     * The measurer that measures distances between items.
     */
    private final TMeasurer measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final TDistanceComparator distanceComparator;

    /**
     * The items in this index for dimension one.
     */
    private final List<TItem> items = new ArrayList<>();

    public RepoIndex1DBase(TMeasurer measurer, TDistanceComparator distanceComparator)
    {
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
    }

    /**
     * Adds the given item to the index.
     *
     * @param item The item to add to the index.
     */
    public void add(TItem item)
    {
    }

    /**
     * This finds the nearest item in the index to the given item.
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    public TItem searchNearest(TItem item)
    {
        return item;
    }
}
