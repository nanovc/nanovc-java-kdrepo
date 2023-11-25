package io.nanovc.indexing.grid;

import io.nanovc.indexing.Index1D;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;

import java.util.Comparator;

/**
 * A one dimensional {@link Index1D} that divides the search space into a grid.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 */
public interface GridIndex1D<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>
    >
    extends Index1D<TItem>
{

    /**
     * Gets the minimum range of this index.
     * @return The minimum range of this index.
     */
    TItem getMinRange();

    /**
     * Gets the maximum range of this index.
     * @return The maximum range of this index.
     */
    TItem getMaxRange();

    /**
     * Gets the number of divisions to use for this grid index.
     * @return The number of divisions to use for this grid index.
     */
    int getDivisions();

    /**
     * Gets the measurer that measures distances between items.
     * @return The measurer that measures distances between items.
     */
    TMeasurer getMeasurer();

    /**
     * Gets the comparator to use for comparing distances of items.
     * @return The comparator to use for comparing distances of items.
     */
    TDistanceComparator getDistanceComparator();

    /**
     * Gets the range splitter that divides the range into a set of divisions.
     * @return The range splitter that divides the range into a set of divisions.
     */
    TRangeSplitter getRangeSplitter();

    /**
     * Gets the range finder that gets the index of an item in the divisions of a range.
     * @return The range finder that gets the index of an item in the divisions of a range.
     */
    TRangeFinder getRangeFinder();
}
