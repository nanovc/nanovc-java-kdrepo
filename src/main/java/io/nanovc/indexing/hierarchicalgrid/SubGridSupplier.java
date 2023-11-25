package io.nanovc.indexing.hierarchicalgrid;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;

import java.util.Comparator;

/**
 * An interface for a method that supplies an instance of a sub-grid.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 * @param <TSubGrid>            The type of sub-grid item. Normally it refers back to itself.
 */
public interface SubGridSupplier
    <TItem,
        TDistance,
        TMeasurer extends Measurer<TItem, TDistance>,
        TDistanceComparator extends Comparator<TDistance>,
        TRangeSplitter extends RangeSplitter<TItem>,
        TRangeFinder extends RangeFinder<TItem>,
        TSubGrid extends HierarchicalGridIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid>
        >
{
    /**
     * A factory method to create a new sub-grid for the given range.
     *
     * @return A new sub-grid for the given range.
     */
    TSubGrid createSubGrid(TItem minRange, TItem maxRange, int divisions, TMeasurer measurer, TDistanceComparator distanceComparator, TRangeSplitter rangeSplitter, TRangeFinder rangeFinder, int maxItemThreshold, TDistance smallestSplittingDistance);

}
