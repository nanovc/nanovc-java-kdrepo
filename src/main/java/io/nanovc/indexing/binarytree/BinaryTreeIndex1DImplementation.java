package io.nanovc.indexing.binarytree;

import io.nanovc.indexing.Measurer;

import java.util.Comparator;

/**
 * A one dimensional {@link BinaryTreeIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TItemComparator>     The comparator that compares items.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public class BinaryTreeIndex1DImplementation<
    TItem,
    TItemComparator extends Comparator<TItem>,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    > extends BinaryTreeIndex1DBase<TItem, TItemComparator, TDistance, TMeasurer, TDistanceComparator>
{
    public BinaryTreeIndex1DImplementation(TItemComparator itemComparator, TMeasurer measurer, TDistanceComparator comparator)
    {
        super(itemComparator, measurer, comparator);
    }
}
