package io.nanovc.indexing.linear;

import io.nanovc.indexing.Measurer;

import java.util.Comparator;

/**
 * A one dimensional {@link LinearIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public class LinearIndex1DImplementation<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends LinearIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator>
{
    public LinearIndex1DImplementation(TMeasurer measurer, TDistanceComparator comparator)
    {
        super(measurer, comparator);
    }
}
