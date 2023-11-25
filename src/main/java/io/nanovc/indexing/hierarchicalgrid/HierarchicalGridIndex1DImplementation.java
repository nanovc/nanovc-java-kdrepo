package io.nanovc.indexing.hierarchicalgrid;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;

import java.util.Comparator;

/**
 * A one dimensional {@link HierarchicalGridIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 * @param <TSubGrid>            The type of sub-grid item. Normally it refers back to itself.
 */
public class HierarchicalGridIndex1DImplementation<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>,
    TSubGrid extends HierarchicalGridIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid>
    >
    extends HierarchicalGridIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid>
{
    public HierarchicalGridIndex1DImplementation(
        TItem minRange, TItem maxRange,
        int divisions,
        TMeasurer measurer, TDistanceComparator comparator, TRangeSplitter rangeSplitter, TRangeFinder rangeFinder,
        SubGridSupplier<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid> subGridSupplier,
        int maxItemThreshold,
        TDistance smallestSplittingDistance
    )
    {
        super(minRange, maxRange, divisions, measurer, comparator, rangeSplitter, rangeFinder, subGridSupplier, maxItemThreshold, smallestSplittingDistance);
    }

}
