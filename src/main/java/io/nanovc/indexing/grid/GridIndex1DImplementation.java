package io.nanovc.indexing.grid;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeSplitter;

import java.util.Comparator;

/**
 * A one dimensional {@link GridIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public class GridIndex1DImplementation<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>
    > extends GridIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter>
{
    public GridIndex1DImplementation(TItem minRange, TItem maxRange, int divisions, TMeasurer measurer, TDistanceComparator comparator, TRangeSplitter rangeSplitter)
    {
        super(minRange, maxRange, divisions, measurer, comparator, rangeSplitter);
    }
}
