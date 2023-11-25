package io.nanovc.indexing.hierarchicalgrid;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;
import io.nanovc.indexing.grid.GridIndex1DBase;

import java.util.Comparator;

/**
 * A base class for a one dimensional {@link HierarchicalGridIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 */
public abstract class HierarchicalGridIndex1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>
    >
    extends GridIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder>
    implements HierarchicalGridIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder>
{
    public HierarchicalGridIndex1DBase(TItem minRange, TItem maxRange, int divisions, TMeasurer measurer, TDistanceComparator distanceComparator, TRangeSplitter rangeSplitter, TRangeFinder rangeFinder)
    {
        super(minRange, maxRange, divisions, measurer, distanceComparator, rangeSplitter, rangeFinder);
    }
}
