package io.nanovc.indexing.binarytree;

import io.nanovc.indexing.Index1D;
import io.nanovc.indexing.Measurer;

import java.util.Comparator;

/**
 * A one dimensional {@link Index1D} that uses a binary tree for its implementation.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TItemComparator>     The comparator that compares items.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public interface BinaryTreeIndex1D<
    TItem,
    TItemComparator extends Comparator<TItem>,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends Index1D<TItem>
{
}
