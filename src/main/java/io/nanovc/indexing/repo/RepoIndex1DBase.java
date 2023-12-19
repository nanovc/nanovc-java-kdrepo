package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.Index1DBase;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;

import java.util.*;

/**
 * A base class for a one dimensional {@link RepoIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 * @param <TContent>            The specific type of content that the repo commits.
 * @param <TArea>               The specific type of content area that the repo commits.
 * @param <TCommit>             The specific type of commit that the repo creates.
 * @param <TRepoHandler>        The specific type of repo handler to use for this index.
 * @param <TSubGrid>            The type of sub-grid item. Normally it refers back to itself.
 */
public abstract class RepoIndex1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?,?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>,
    TSubGrid extends RepoIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TContent, TArea, TCommit, TRepoHandler, TSubGrid>
    >
    extends Index1DBase<TItem>
    implements RepoIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TContent, TArea, TCommit, TRepoHandler, TSubGrid>
{
    /**
     * The minimum range of this index.
     */
    private final TItem minRange;

    /**
     * The maximum range of this index.
     */
    private final TItem maxRange;

    /**
     * The number of divisions to use for this grid index.
     */
    private final int divisions;

    /**
     * The measurer that measures distances between items.
     */
    private final TMeasurer measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final TDistanceComparator distanceComparator;

    /**
     * The range splitter that divides the range into a set of divisions.
     */
    private final TRangeSplitter rangeSplitter;

    /**
     * The range finder that gets the index of an item in the divisions of a range.
     */
    private final TRangeFinder rangeFinder;

    /**
     * The splits for the range.
     */
    private final List<TItem> rangeSplits;

    /**
     * The items in this index for dimension one.
     */
    private final Map<Integer, List<TItem>> items;

    /**
     * This is the maximum number of items to keep in the grid before it splits the cell into a subgrid.
     */
    private final int maxItemThreshold;

    /**
     * The smallest distance that we do not split beyond.
     */
    private final TDistance smallestSplittingDistance;

    /**
     * A factory method to create a new sub-grid for the given range.
     */
    private final SubGridSupplier<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TContent, TArea, TCommit, TRepoHandler, TSubGrid> subGridSupplier;

    /**
     * The repo handler to use for this repo index.
     */
    private final TRepoHandler repoHandler;

    /**
     * The repo path to the root of this repo index where we update the index information.
     */
    private final RepoPath rootRepoPath;

    /**
     * These are the sub-grids that exist.
     */
    private final Map<Integer, TSubGrid> subGrids;

    public RepoIndex1DBase(
        TItem minRange, TItem maxRange, int divisions,
        TMeasurer measurer, TDistanceComparator distanceComparator,
        TRangeSplitter rangeSplitter, TRangeFinder rangeFinder,
        int maxItemThreshold, TDistance smallestSplittingDistance,
        SubGridSupplier<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TContent, TArea, TCommit, TRepoHandler, TSubGrid> subGridSupplier,
        TRepoHandler repoHandler, RepoPath rootRepoPath
        )
    {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.divisions = divisions;
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
        this.rangeSplitter = rangeSplitter;
        this.rangeFinder = rangeFinder;
        this.maxItemThreshold = maxItemThreshold;
        this.smallestSplittingDistance = smallestSplittingDistance;
        this.subGridSupplier = subGridSupplier;
        this.repoHandler = repoHandler;
        this.rootRepoPath = rootRepoPath;

        // Split the range:
        this.rangeSplits = new ArrayList<>(divisions);
        this.splitRange(minRange, maxRange, divisions, true, this.rangeSplits);

        // Initialise the grid:
        this.items = new HashMap<>();

        // Initialise the sub-grids:
        this.subGrids = new HashMap<>();
    }

    /**
     * This splits the range given by the two items into the given number of divisions.
     *
     * @param minRange                   The item at the minimum range to measure the distance from.
     * @param maxRange                   The item at the maximum range to measure the distance to.
     * @param divisions                  The number of divisions to split the range into.
     * @param includeExtraRightMostSplit True to include an extra item in the list (1 more than the requested number of divisions) to represent the right most edge of the range. If this is false then we only have the left edges, leading up to but not including the right part of the range.
     * @param splitsToAddTo              The collection of splits to add to while we split the range.
     */
    protected void splitRange(TItem minRange, TItem maxRange, int divisions, boolean includeExtraRightMostSplit, List<TItem> splitsToAddTo)
    {
        this.rangeSplitter.splitRange(minRange, maxRange, divisions, includeExtraRightMostSplit, splitsToAddTo);
    }

    /**
     * Gets the lower range of the given division.
     * @param divisionIndex The division that we want to get the lower range value of.
     * @return The lower range of the given division.
     */
    protected TItem getDivisionMinRange(int divisionIndex)
    {
        // Make sure we are within our bounds:
        divisionIndex = Math.max(0, Math.min(this.rangeSplits.size() - 1, divisionIndex));

        // Get the division:
        return this.rangeSplits.get(divisionIndex);
    }

    /**
     * Gets the upper range of the given division.
     * @param divisionIndex The division that we want to get the lower range value of.
     * @return The lower range of the given division.
     */
    protected TItem getDivisionMaxRange(int divisionIndex)
    {
        // Get the next division:
        int nextDivisionIndex = divisionIndex + 1;

        // Make sure we are within our bounds:
        nextDivisionIndex = Math.max(0, Math.min(this.rangeSplits.size() - 1, nextDivisionIndex));

        // Get the division:
        return this.rangeSplits.get(nextDivisionIndex);
    }

    /**
     * Adds the given item to the index.
     *
     * @param item The item to add to the index.
     */
    public void add(TItem item)
    {
        // Find the index of the division in the range:
        int index = findIndexInRange(this.minRange, this.maxRange, this.divisions, item);

        // Get the list at the index:
        addItemToIndex(item, index);
    }


    /**
     * This finds the division index for an item in a range that is defined by two other items.
     *
     * @param minRange          The item at the minimum range to measure the distance from.
     * @param maxRange          The item at the maximum range to measure the distance to.
     * @param divisions         The number of divisions to split the range into.
     * @param itemToFindInRange The item to find the index of for the given range.
     * @return The index of the division of the given item in the range specified.
     */
    protected int findIndexInRange(TItem minRange, TItem maxRange, int divisions, TItem itemToFindInRange)
    {
        return this.rangeFinder.findIndexInRange(minRange, maxRange, divisions, itemToFindInRange);
    }

    /**
     * Adds the given item to the specific division index in the local index.
     *
     * @param item          The item to add.
     * @param divisionIndex The specific division index to add the item to.
     */
    protected void addItemToIndexLocal(TItem item, int divisionIndex)
    {
        // Get or create the list at the given division in the range:
        List<TItem> itemsAtDivision = getOrCreateItemsAtDivision(divisionIndex);

        // Add the item:
        itemsAtDivision.add(item);
    }

    /**
     * Adds the given item to the specific division index.
     *
     * @param item          The item to add.
     * @param divisionIndex The specific division index to add the item to.
     */
    protected void addItemToIndex(TItem item, int divisionIndex)
    {
        /// Check whether we have a sub-grid at this index:
        TSubGrid existingSubGrid = this.getSubGridAtDivision(divisionIndex);
        if (existingSubGrid != null)
        {
            // We already have an existing sub-grid.

            // Delegate the call to the sub-grid:
            existingSubGrid.add(item);
        }
        else
        {
            // We don't have an existing sub-grid yet.

            // Get the list at the given division in the range:
            List<TItem> itemsAtDivision = getItemsAtDivision(divisionIndex);

            // Check whether we have too many items:
            if (itemsAtDivision == null || itemsAtDivision.size() < this.getMaxItemThreshold())
            {
                // We are still below the item threshold.

                // Allow the base class to handle this:
                addItemToIndexLocal(item, divisionIndex);
            }
            else
            {
                // We are above the threshold of items.

                // Get or create the sub-grid for this division:
                TSubGrid subGrid = this.getOrCreateSubGridAtDivision(divisionIndex);

                // Check whether we got a sub-grid (NOTE: if we can't make the sub-grid smaller, we get null):
                if (subGrid != null)
                {
                    // We got a sub-grid.

                    // Add the item to the sub-grid:
                    subGrid.add(item);
                }
                else
                {
                    // We couldn't get a sub-grid, probably because we are at the smallest size allowable.

                    // Just add the item:
                    addItemToIndexLocal(item, divisionIndex);
                }
            }
        }
    }

    /**
     * This gets the sub-grid that is currently at the division index.
     * It does not create a sub-grid if one doesn't exist yet. Instead it returns null.
     *
     * @param divisionIndex The specific division index to get the sub-grid for.
     * @return The sub-grid at the give division index, or null if one doesn't exist there yet.
     */
    protected TSubGrid getSubGridAtDivision(int divisionIndex)
    {
        return this.subGrids.get(divisionIndex);
    }

    /**
     * This gets the sub-grid that is currently at the division index.
     * It does not create a sub-grid if one doesn't exist yet. Instead, it returns null.
     * It will re-index all the existing items into the created sub-grid if it creates one.
     * It then clears the items in the local collection if it creates a sub-grid.
     *
     * @param divisionIndex The specific division index to get the sub-grid for.
     * @return The sub-grid at the give division index, or null if one we cannot split the sub-grid up any further.
     */
    protected TSubGrid getOrCreateSubGridAtDivision(int divisionIndex)
    {
        // Check whether we already have a sub-grid:
        TSubGrid subGrid = this.getSubGridAtDivision(divisionIndex);

        // Make sure one exists:
        if (subGrid == null)
        {
            // Work out the range for the sub-grid:
            TItem divisionMinRange = this.getDivisionMinRange(divisionIndex);
            TItem divisionMaxRange = this.getDivisionMaxRange(divisionIndex);

            // Make sure that we are allowed to split the sub-grid up further:
            TDistance distance = this.getMeasurer().measureDistanceBetween(divisionMinRange, divisionMaxRange);

            // Check whether the size of the range is still bigger than our minimum:
            if (this.getDistanceComparator().compare(distance, this.getSmallestSplittingDistance()) <= 0)
            {
                // The size of the sub-grid is smaller than the smallest allowable size.

                // Flag that we can't create a sub-grid:
                return null;
            }

            // Create a new sub-grid:
            subGrid = createSubGrid(
                divisionMinRange, divisionMaxRange,
                this.getDivisions(),
                this.getMeasurer(), this.getDistanceComparator(), getRangeSplitter(), getRangeFinder(),
                this.getMaxItemThreshold(),
                this.getSmallestSplittingDistance(),
                this.getRepoHandler(), this.getRootRepoPath()
            );

            // Add the sub-grid:
            this.subGrids.put(divisionIndex, subGrid);
        }

        return subGrid;
    }

    /**
     * Gets the current list of items at the given division index.
     * It doesn't create the list if it doesn't exist. Instead, if just returns null.
     *
     * @param divisionIndex The index of the division that we want to interrogate.
     * @return The current list of items at the given division index.
     */
    protected List<TItem> getItemsAtDivision(int divisionIndex)
    {
        // Get the list at the given division in the range:
        //noinspection UnnecessaryLocalVariable
        List<TItem> itemsAtDivision = this.items.get(divisionIndex);
        return itemsAtDivision;
    }

    /**
     * Gets or creates the current list of items at the given division index.
     * It creates the list if it doesn't exist yet.
     *
     * @param divisionIndex The index of the division that we want to interrogate.
     * @return The current list of items at the given division index. This will be a newly created list if this is the first time it is being queried.
     */
    protected List<TItem> getOrCreateItemsAtDivision(int divisionIndex)
    {
        // Get the list at the given division in the range:
        List<TItem> itemsAtDivision = getItemsAtDivision(divisionIndex);

        // Make sure we have a list:
        if (itemsAtDivision == null)
        {
            // This is the first time we are adding and item.
            // Create the list of items for this division:
            itemsAtDivision = new ArrayList<>();

            // Set this list at the division index:
            this.items.put(divisionIndex, itemsAtDivision);
        }
        // Now we know we have a list at the given division index.

        return itemsAtDivision;
    }

    /**
     * Clears and releases the item collection at the given division index.
     *
     * @param divisionIndex The index of the division that we want to clear.
     */
    protected void clearItemsAtDivision(int divisionIndex)
    {
        this.items.put(divisionIndex, null);
    }

    /**
     * This finds the nearest item in the index to the given item.
     *
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    public TItem searchNearest(TItem item)
    {
        // Find the index of the item that we are interested in:
        int index = this.findIndexInRange(this.minRange, this.maxRange, this.divisions, item);
        int previousIndex = index - 1;
        int nextIndex = index + 1;

        // Get the list of items at that division:
        MeasuredItem<TItem, TDistance> nearestItemAtIndex = searchNearestAtIndex(item, index);

        // Check whether we had an exact match:
        if (nearestItemAtIndex != null && nearestItemAtIndex.distance == null) return nearestItemAtIndex.item;
        // Now we know that we didn't have an exact match at the index.

        // Expand out the previous index until we find items (keep searching left until we find a list of adjacent items):
        while (previousIndex > 0)
        {
            // Check whether we have a sub-grid at that index:
            TSubGrid previousSubGrid = this.subGrids.get(previousIndex);
            if (previousSubGrid != null)
            {
                // We found a sub-grid at this index.
                // Stop searching:
                break;
            }
            else
            {
                // There is no sub-grid at this index.
                // Search for items at this index:
                // Check whether we have items at that index:
                List<TItem> itemsAtIndex = this.items.get(previousIndex);
                if (itemsAtIndex != null)
                {
                    // We found items at this index.
                    // Stop searching:
                    break;
                }
                else
                {
                    // The items at this index were empty.
                    // Move to the previous index:
                    previousIndex--;
                }
            }
        }
        // Now we have the positions of the previous index that contain items (or we are outside the range).

        // Check the previous index (there might be closer items near the edges of the division):
        MeasuredItem<TItem, TDistance> nearestItemAtPreviousIndex;
        if (previousIndex >= 0)
        {
            // We have a previous index.

            // Find the nearest item in the previous index:
            nearestItemAtPreviousIndex = searchNearestAtIndex(item, previousIndex);

            // Check whether we found an exact match:
            if (nearestItemAtPreviousIndex != null && nearestItemAtPreviousIndex.distance == null)
                return nearestItemAtPreviousIndex.item;
        }
        else
        {
            // We are at the left edge, so we don't have a previous index that is in range.
            nearestItemAtPreviousIndex = null;
        }

        // Expand out the next index until we find items (keep searching right until we find a list of adjacent items):
        while (nextIndex < this.divisions)
        {
            // Check whether we have a sub-grid at that index:
            TSubGrid nextSubGrid = this.subGrids.get(nextIndex);
            if (nextSubGrid != null)
            {
                // We found a sub-grid at this index.
                // Stop searching:
                break;
            }
            else
            {
                // There is no sub-grid at this index.

                // Check whether we have items at that index:
                List<TItem> itemsAtIndex = this.items.get(nextIndex);
                if (itemsAtIndex != null)
                {
                    // We found items at this index.
                    // Stop searching:
                    break;
                }
                else
                {
                    // The items at this index were empty.
                    // Move to the next index:
                    nextIndex++;
                }
            }
        }
        // Now we have the positions of the next index that contain items (or we are outside the range).

        // Check the next index (there might be closer items near the edges of the division):
        MeasuredItem<TItem, TDistance> nearestItemAtNextIndex;
        if (nextIndex < this.divisions)
        {
            // We have a next index.

            // Find the nearest item in the next index:
            nearestItemAtNextIndex = searchNearestAtIndex(item, nextIndex);

            // Check whether we found an exact match:
            if (nearestItemAtNextIndex != null && nearestItemAtNextIndex.distance == null)
                return nearestItemAtNextIndex.item;
        }
        else
        {
            // We are at the right edge, so we don't have a next index that is in range.
            nearestItemAtNextIndex = null;
        }

        // If we get here then we know that neither match was an exact match.

        // Determine which item is closest:
        if (nearestItemAtIndex == null)
        {
            // We didn't find an item at the index.

            if (nearestItemAtPreviousIndex == null)
            {
                // We didn't find an item at the previous index, nor at the index.

                if (nearestItemAtNextIndex == null)
                {
                    // We didn't find an item at any index.
                    // There is no item.
                    return null;
                }
                else
                {
                    // We found an item in the next index, but not in the previous index, nor at the index.
                    // Therefore, this is the closest match:
                    return nearestItemAtNextIndex.item;
                }
            }
            else
            {
                // We found an item at the previous index, but not at the index.

                if (nearestItemAtNextIndex == null)
                {
                    // We found an item at the previous index, but not at the index, nor at the next index.
                    // Therefore, this is the closest match:
                    return nearestItemAtPreviousIndex.item;
                }
                else
                {
                    // We found an item in the next index, and we found an item at the previous index, but not at the index.

                    // Determine whether the previous or next item is closest:
                    if (distanceComparator.compare(nearestItemAtPreviousIndex.distance, nearestItemAtNextIndex.distance) < 0) // NOTE: If they are equal, choose the next index answer
                    {
                        // The item at the previous index is closest.
                        return nearestItemAtPreviousIndex.item;
                    }
                    else
                    {
                        // The item at the next index is closest.
                        return nearestItemAtNextIndex.item;
                    }
                }
            }
        }
        else
        {
            // We did find an item at the index.

            if (nearestItemAtPreviousIndex == null)
            {
                // We didn't find an item at the previous index, but we did find one at the index.

                if (nearestItemAtNextIndex == null)
                {
                    // We didn't find an item at the next index, nor at the previous index, but we did find one at the index.

                    // This means that the item at the index is the nearest one:
                    return nearestItemAtIndex.item;
                }
                else
                {
                    // We found an item in the next index and at the index, but not at the previous index.

                    // Determine whether the item at the index or next item is closest:
                    if (distanceComparator.compare(nearestItemAtIndex.distance, nearestItemAtNextIndex.distance) <= 0)
                    {
                        // The item at the current index is closest.
                        return nearestItemAtIndex.item;
                    }
                    else
                    {
                        // The item at the next index is closest.
                        return nearestItemAtNextIndex.item;
                    }
                }
            }
            else
            {
                // We found an item at the previous index and at the index.

                if (nearestItemAtNextIndex == null)
                {
                    // We didn't find an item at the next index, but we did find an item at the previous index and at the index.

                    // Determine whether the item at the index or next item is closest:
                    if (distanceComparator.compare(nearestItemAtIndex.distance, nearestItemAtPreviousIndex.distance) <= 0)
                    {
                        // The item at the current index is closest.
                        return nearestItemAtIndex.item;
                    }
                    else
                    {
                        // The item at the previous index is closest.
                        return nearestItemAtPreviousIndex.item;
                    }
                }
                else
                {
                    // We found items in the previous, next and at the index (all three).

                    // Determine whether the item at the index or next item is closest:
                    if (distanceComparator.compare(nearestItemAtIndex.distance, nearestItemAtPreviousIndex.distance) <= 0)
                    {
                        // The item at the current index is closest.

                        // Determine whether the item at the index or next item is closest:
                        if (distanceComparator.compare(nearestItemAtIndex.distance, nearestItemAtNextIndex.distance) <= 0)
                        {
                            // The item at the current index is closest.
                            return nearestItemAtIndex.item;
                        }
                        else
                        {
                            // The item at the next index is closest.
                            return nearestItemAtNextIndex.item;
                        }

                    }
                    else
                    {
                        // The item at the previous index is closest.

                        // Determine whether the previous item or next item is closest:
                        if (distanceComparator.compare(nearestItemAtPreviousIndex.distance, nearestItemAtNextIndex.distance) <= 0)
                        {
                            // The item at the previous index is closest.
                            return nearestItemAtPreviousIndex.item;
                        }
                        else
                        {
                            // The item at the next index is closest.
                            return nearestItemAtNextIndex.item;
                        }

                    }
                }

            }
        }
    }

    /**
     * This searches for the nearest item in the given division index.
     *
     * @param item          The item to search for.
     * @param divisionIndex The division index to search in.
     * @return The nearest item that was found at the given division index.
     */
    protected MeasuredItem<TItem, TDistance> searchNearestAtIndexLocal(TItem item, int divisionIndex)
    {
        // Get the items at the division index:
        List<TItem> items = this.items.get(divisionIndex);

        // Check whether we have items:
        if (items == null)
        {
            // We don't have any items at the given index yet.
            return null;
        }
        else
        {
            // We have items at this division index.

            // Keep track of the best result so far:
            TItem bestItemSoFar = null;
            TDistance bestDistanceSoFar = null;

            // Search for the nearest item:
            for (TItem indexedItem : items)
            {
                // Check whether the item is equal to the item:
                if (item.equals(indexedItem))
                {
                    // This item is equal.

                    // Create the measured result:
                    MeasuredItem<TItem, TDistance> result = new MeasuredItem<>();
                    result.item = indexedItem;
                    return result;
                }
                // Now we know that the items are not equal.

                // Get the distance to the item:
                TDistance distance = measureDistanceBetween(item, indexedItem);

                // Check whether this distance is the best so far:
                if (bestDistanceSoFar == null || this.distanceComparator.compare(distance, bestDistanceSoFar) < 0)
                {
                    // This item is closer.

                    // Flag this as the best item so far:
                    bestItemSoFar = indexedItem;
                    bestDistanceSoFar = distance;
                }
            }
            // Now we have found the best item.

            // Create the result:
            MeasuredItem<TItem, TDistance> result = new MeasuredItem<>();
            result.item = bestItemSoFar;
            result.distance = bestDistanceSoFar;
            return result;
        }
    }

    /**
     * This searches for the nearest item in the given division index.
     *
     * @param item          The item to search for.
     * @param divisionIndex The division index to search in.
     * @return The nearest item that was found at the given division index.
     */
    protected MeasuredItem<TItem, TDistance> searchNearestAtIndex(TItem item, int divisionIndex)
    {
        // Check whether we have a sub-grid at this division index:
        TSubGrid existingSubGrid = this.getSubGridAtDivision(divisionIndex);
        if (existingSubGrid != null)
        {
            // We have an existing sub-grid at this division index.

            // Search for the item in the sub-grid:
            TItem foundItem = existingSubGrid.searchNearest(item);

            // Check whether an item was found:
            if (foundItem != null)
            {
                // We found an item.

                // Create the measured item:
                MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();

                // Save the item:
                measuredItem.item = foundItem;

                // Check whether we have an exact match so that we don't have to measure the distance:
                if (!foundItem.equals(item))
                {
                    // This is not the same item.

                    // Measure the distance from the requested item to the found item:
                    measuredItem.distance = this.measureDistanceBetween(item, measuredItem.item);
                }

                // Return the result:
                return measuredItem;
            }
            else
            {
                // The sub-grid didn't find the nearest item.
                return null;
            }
        }
        else
        {
            // We don't have an existing sub-grid at this division index.
            // Use the base implementation:
            return searchNearestAtIndexLocal(item, divisionIndex);
        }
    }

    /**
     * Measures the distance between the two items.
     * @param item1 The first item to measure the distance between.
     * @param item2 The second item to measure the distance between.
     * @return The distance between the two items.
     */
    protected TDistance measureDistanceBetween(TItem item1, TItem item2)
    {
        return this.measurer.measureDistanceBetween(item1, item2);
    }

    /**
     * A factory method to create a new sub-grid for the given range.
     *
     * @return A new sub-grid for the given range.
     */
    protected TSubGrid createSubGrid(TItem minRange, TItem maxRange, int divisions, TMeasurer measurer, TDistanceComparator distanceComparator, TRangeSplitter rangeSplitter, TRangeFinder rangeFinder, int maxItemThreshold, TDistance smallestSplittingDistance, TRepoHandler repoHandler, RepoPath rootRepoPath)
    {
        return this.subGridSupplier.createSubGrid(minRange, maxRange, divisions, measurer, distanceComparator, rangeSplitter, rangeFinder, maxItemThreshold, smallestSplittingDistance, repoHandler, rootRepoPath);
    }

    /**
     * Gets the minimum range of this index.
     *
     * @return The minimum range of this index.
     */
    @Override
    public TItem getMinRange()
    {
        return this.minRange;
    }

    /**
     * Gets the maximum range of this index.
     *
     * @return The maximum range of this index.
     */
    @Override
    public TItem getMaxRange()
    {
        return this.maxRange;
    }

    /**
     * Gets the number of divisions to use for this grid index.
     *
     * @return The number of divisions to use for this grid index.
     */
    @Override
    public int getDivisions()
    {
        return this.divisions;
    }

    /**
     * Gets the measurer that measures distances between items.
     *
     * @return The measurer that measures distances between items.
     */
    @Override
    public TMeasurer getMeasurer()
    {
        return this.measurer;
    }

    /**
     * Gets the comparator to use for comparing distances of items.
     *
     * @return The comparator to use for comparing distances of items.
     */
    @Override
    public TDistanceComparator getDistanceComparator()
    {
        return this.distanceComparator;
    }

    /**
     * Gets the range splitter that divides the range into a set of divisions.
     *
     * @return The range splitter that divides the range into a set of divisions.
     */
    @Override
    public TRangeSplitter getRangeSplitter()
    {
        return this.rangeSplitter;
    }

    /**
     * Gets the range finder that gets the index of an item in the divisions of a range.
     *
     * @return The range finder that gets the index of an item in the divisions of a range.
     */
    @Override
    public TRangeFinder getRangeFinder()
    {
        return this.rangeFinder;
    }

    /**
     * Gets the maximum number of items to keep in the grid before it splits the cell into a subgrid.
     *
     * @return The maximum number of items to keep in the grid before it splits the cell into a subgrid.
     */
    @Override public int getMaxItemThreshold()
    {
        return this.maxItemThreshold;
    }

    /**
     * Gets the smallest distance that we do not split beyond.
     *
     * @return The smallest distance that we do not split beyond.
     */
    @Override public TDistance getSmallestSplittingDistance()
    {
        return this.smallestSplittingDistance;
    }

    /**
     * Gets the repo handler to use for this repo index.
     *
     * @return The repo handler to use for this repo index.
     */
    @Override public TRepoHandler getRepoHandler()
    {
        return this.repoHandler;
    }

    /**
     * Gets the repo path to the root of this repo index where we update the index information.
     *
     * @return The repo path to the root of this repo index where we update the index information.
     */
    @Override public RepoPath getRootRepoPath()
    {
        return this.rootRepoPath;
    }
}
