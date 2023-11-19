package io.nanovc.indexing.repo;

import io.nanovc.indexing.Measurer;

import java.util.Comparator;

/**
 * A one dimensional {@link RepoIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public class RepoIndex1DImplementation<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    > extends RepoIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator>
{
    public RepoIndex1DImplementation(TMeasurer measurer, TDistanceComparator comparator)
    {
        super(measurer, comparator);
    }
}
