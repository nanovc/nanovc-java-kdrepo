package io.nanovc.indexing.hierarchicalgrid;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;
import io.nanovc.indexing.grid.GridIndex1DBase;
import io.nanovc.indexing.grid.MeasuredItem;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base class for a one dimensional {@link HierarchicalGridIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 * @param <TSubGrid>            The type of sub-grid item. Normally it refers back to itself.
 */
public abstract class HierarchicalGridIndex1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>,
    TSubGrid extends HierarchicalGridIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid>
    >
    extends GridIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder>
    implements HierarchicalGridIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid>
{
    /**
     * This is the maximum number of items to keep in the grid before it splits the cell into a subgrid.
     */
    private final int maxItemThreshold;

    /**
     * The smallest distance that we do not split beyond.
     */
    private final TDistance smallestSplittingDistance;

    /**
     * These are the sub-grids that exist.
     */
    private final Map<Integer, TSubGrid> subGrids = new HashMap<>();

    /**
     * A factory method to create a new sub-grid for the given range.
     */
    private final SubGridSupplier<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid> subGridSupplier;

    public HierarchicalGridIndex1DBase(
        TItem minRange, TItem maxRange,
        int divisions,
        TMeasurer measurer, TDistanceComparator distanceComparator, TRangeSplitter rangeSplitter, TRangeFinder rangeFinder,
        SubGridSupplier<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TSubGrid> subGridSupplier,
        int maxItemThreshold,
        TDistance smallestSplittingDistance
    )
    {
        super(minRange, maxRange, divisions, measurer, distanceComparator, rangeSplitter, rangeFinder);

        this.maxItemThreshold = maxItemThreshold;
        this.subGridSupplier = subGridSupplier;
        this.smallestSplittingDistance = smallestSplittingDistance;
    }

    /**
     * Gets the maximum number of items to keep in the grid before it splits the cell into a subgrid.
     *
     * @return The maximum number of items to keep in the grid before it splits the cell into a subgrid.
     */
    @Override public int getMaxItemThreshold()
    {
        return maxItemThreshold;
    }

    /**
     * Adds the given item to the specific division index.
     *
     * @param item          The item to add.
     * @param divisionIndex The specific division index to add the item to.
     */
    @Override protected void addItemToIndex(TItem item, int divisionIndex)
    {
        // Check whether we have a sub-grid at this index:
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
                super.addItemToIndex(item, divisionIndex);
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
                    super.addItemToIndex(item, divisionIndex);
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
                getMaxItemThreshold(),
                getSmallestSplittingDistance()
            );

            // Add the sub-grid:
            this.subGrids.put(divisionIndex, subGrid);

            // Get all the items that are currently at the sub-division:
            List<TItem> items = getItemsAtDivision(divisionIndex);

            // Index all the items in the sub-grid:
            for (TItem item : items)
            {
                // Index the item in the sub-grid:
                subGrid.add(item);
            }

            // Clear the items from the current grid, because we have them indexed in the sub-grid:
            this.clearItemsAtDivision(divisionIndex);
        }

        return subGrid;
    }

    /**
     * This searches for the nearest item in the given division index.
     *
     * @param item          The item to search for.
     * @param divisionIndex The division index to search in.
     * @return The nearest item that was found at the given division index.
     */
    @Override protected MeasuredItem<TItem, TDistance> searchNearestAtIndex(TItem item, int divisionIndex)
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
            return super.searchNearestAtIndex(item, divisionIndex);
        }
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
     * A factory method to create a new sub-grid for the given range.
     *
     * @return A new sub-grid for the given range.
     */
    protected TSubGrid createSubGrid(TItem minRange, TItem maxRange, int divisions, TMeasurer measurer, TDistanceComparator distanceComparator, TRangeSplitter rangeSplitter, TRangeFinder rangeFinder, int maxItemThreshold, TDistance smallestSplittingDistance)
    {
        return this.subGridSupplier.createSubGrid(minRange, maxRange, divisions, measurer, distanceComparator, rangeSplitter, rangeFinder, maxItemThreshold, smallestSplittingDistance);
    }
}
