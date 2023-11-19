package io.nanovc.indexing.linear;

import io.nanovc.indexing.Index1DBase;
import io.nanovc.indexing.Measurer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A base class for a one dimensional {@link LinearIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public abstract class LinearIndex1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends Index1DBase<TItem>
    implements LinearIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator>
{
    /**
     * The measurer that measures distances between items.
     */
    private final TMeasurer measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final TDistanceComparator distanceComparator;

    /**
     * The items in this index for dimension one.
     */
    private final List<TItem> items = new ArrayList<>();

    public LinearIndex1DBase(TMeasurer measurer, TDistanceComparator distanceComparator)
    {
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
    }

    /**
     * Adds the given item to the index.
     *
     * @param item The item to add to the index.
     */
    public void add(TItem item)
    {
        this.items.add(item);
    }

    /**
     * This finds the nearest item in the index to the given item.
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    public TItem searchNearest(TItem item)
    {
        // Start with an unspecified distance:
        TDistance shortestDistance = null;

        // Keep track of the closest item:
        TItem closestItem = null;

        // Cache dependencies:
        TMeasurer measurer = this.getMeasurer();
        TDistanceComparator distanceComparator = this.getDistanceComparator();

        // Go linearly through each item:
        for (TItem otherItem : this.items)
        {
            // Measure the distance to the item:
            TDistance distance = measurer.measureDistanceBetween(otherItem, item);

            // Check whether this is the first distance we have:
            if (shortestDistance == null)
            {
                // This is teh first distance we have measured.
                // This is the new shortest distance:
                shortestDistance = distance;
                closestItem = otherItem;
            }
            else
            {
                // This is not the first distance we have measured.

                // Check if this is the new shortest distance:
                if (distanceComparator.compare(distance, shortestDistance) < 0)
                {
                    // This item is closer than the previous one.

                    // This is the new shortest distance:
                    shortestDistance = distance;
                    closestItem = otherItem;
                }
            }
        }

        return closestItem;
    }


    /**
     * Gets the measurer that measures distances between items.
     * @return The measurer that measures distances between items.
     */
    public TMeasurer getMeasurer()
    {
        return measurer;
    }

    /**
     * Gets the comparator to use for comparing distances of items.
     * @return The comparator to use for comparing distances of items.
     */
    public TDistanceComparator getDistanceComparator()
    {
        return distanceComparator;
    }
}
