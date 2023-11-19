package io.nanovc.indexing.grid;

import io.nanovc.indexing.Index1DBase;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeSplitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A base class for a one dimensional {@link GridIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public abstract class GridIndex1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>
    >
    extends Index1DBase<TItem>
    implements GridIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter>
{

    /**
     * The minimum range of this index.
     */
    private final TItem minRange;

    /**
     * The maximum range of this index.
     */
    private final TItem maxRange;

    /**
     * The number of divisions to use for this grid index.
     */
    private final int divisions;

    /**
     * The measurer that measures distances between items.
     */
    private final TMeasurer measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final TDistanceComparator distanceComparator;

    /**
     * The range splitter that divides the range into a set of divisions.
     */
    private final TRangeSplitter rangeSplitter;

    /**
     * The splits for the range.
     */
    private final List<TItem> rangeSplits;

    /**
     * The items in this index for dimension one.
     */
    private final List<List<TItem>> items;

    public GridIndex1DBase(TItem minRange, TItem maxRange, int divisions, TMeasurer measurer, TDistanceComparator distanceComparator, TRangeSplitter rangeSplitter)
    {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.divisions = divisions;
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
        this.rangeSplitter = rangeSplitter;

        // Split the range:
        this.rangeSplits = new ArrayList<>(divisions);
        this.rangeSplitter.splitRange(minRange, maxRange, divisions, this.rangeSplits);

        // Initialise the grid:
        this.items = new ArrayList<>(divisions);
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
