package io.nanovc.indexing;

import java.util.Comparator;

/**
 * A base class for a one-dimensional index.
 * This is used for searching for items efficiently.
 */
public abstract class Index1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    implements Index1D<TItem, TDistance, TMeasurer, TDistanceComparator>
{
    /**
     * The measurer that measures distances between items.
     */
    private final TMeasurer measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final TDistanceComparator distanceComparator;

    protected Index1DBase(TMeasurer measurer, TDistanceComparator comparator)
    {
        this.measurer = measurer;
        this.distanceComparator = comparator;
    }

    /**
     * Gets the measurer that measures distances between items.
     * @return The measurer that measures distances between items.
     */
    public TMeasurer getMeasurer()
    {
        return measurer;
    }

    /**
     * Gets the comparator to use for comparing distances of items.
     * @return The comparator to use for comparing distances of items.
     */
    public TDistanceComparator getDistanceComparator()
    {
        return distanceComparator;
    }
}
