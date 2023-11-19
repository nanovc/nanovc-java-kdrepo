package io.nanovc.indexing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A one dimensional {@link RepoIndexKD}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public class LinearIndex1D<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    > extends RepoIndexKD
{
    /**
     * The items in this index for dimension one.
     */
    private List<TItem> items = new ArrayList<>();

    /**
     * The comparator to use for comparing distances of items.
     */
    private TDistanceComparator distanceComparator;

    private final TMeasurer measurer;

    public LinearIndex1D(TMeasurer measurer, TDistanceComparator comparator)
    {
        this.measurer = measurer;
        distanceComparator = comparator;
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

        // Go linearly through each item:
        for (TItem otherItem : this.items)
        {
            // Measure the distance to the item:
            TDistance distance = this.measurer.measureDistanceBetween(otherItem, item);

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
                if (this.distanceComparator.compare(distance, shortestDistance) < 0)
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
}
