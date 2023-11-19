package io.nanovc.indexing.repo;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.binarytree.BinaryTreeIndex1D;
import io.nanovc.indexing.linear.LinearIndex1DBase;

import java.util.Comparator;

/**
 * A one dimensional {@link BinaryTreeIndex1D}.
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
    > extends LinearIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator>
{
    public RepoIndex1DImplementation(TMeasurer measurer, TDistanceComparator comparator)
    {
        super(measurer, comparator);
    }
}
