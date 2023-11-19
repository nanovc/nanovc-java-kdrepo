package io.nanovc.indexing.binarytree;

import io.nanovc.indexing.Index1D;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.repo.RepoIndexKD;

import java.util.Comparator;

/**
 * A one dimensional {@link RepoIndexKD}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public interface BinaryTreeIndex1D<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends Index1D<TItem, TDistance, TMeasurer, TDistanceComparator>
{
}
