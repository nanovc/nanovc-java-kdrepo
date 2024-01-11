package io.nanovc.indexing.linear;

import io.nanovc.indexing.Index2D;
import io.nanovc.indexing.Measurer;

import java.util.Comparator;

/**
 * A two dimensional {@link Index2D} that uses a linear list for its implementation.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public interface LinearIndex2D<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends LinearIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator>
{
}
