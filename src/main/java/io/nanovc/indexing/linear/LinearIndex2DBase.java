package io.nanovc.indexing.linear;

import io.nanovc.indexing.Measurer;

import java.util.Comparator;

/**
 * A base class for a two dimensional {@link LinearIndex2D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public abstract class LinearIndex2DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends LinearIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator>
    implements LinearIndex2D<TItem, TDistance, TMeasurer, TDistanceComparator>
{
    public LinearIndex2DBase(TMeasurer measurer, TDistanceComparator comparator)
    {
        super(measurer, comparator);
    }
}
