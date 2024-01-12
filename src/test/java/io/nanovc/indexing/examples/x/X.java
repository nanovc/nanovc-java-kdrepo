package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.repo.HyperCube;
import io.nanovc.indexing.repo.ranges.MinInclusiveMaxInclusiveRange;
import io.nanovc.indexing.repo.ranges.Range;

import java.util.List;

/**
 * A record for an X coordinate.
 *
 * @param x The x coordinate.
 */
public record X(int x)
{
    /**
     * Compares the one item to the other.
     *
     * @param item1 The first item to compare.
     * @param item2 The second item to compare.
     * @return The comparison between the two items.
     */
    public static int compareTo(X item1, X item2)
    {
        return Integer.compare(item1.x(), item2.x());
    }

    /**
     * Extracts the coordinate for the given dimension.
     *
     * @param dimension The dimension to extract.
     * @return The coordinate for the given dimension.
     */
    public static int extractCoordinate(X item, Integer dimension)
    {
        return switch (dimension)
        {
            case 0 -> item.x();
            default -> throw new IllegalStateException("Unexpected value: " + dimension);
        };
    }

    /**
     * Measures the distance between two items.
     *
     * @param item1 The first item.
     * @param item2 The second item.
     * @return The distance between the two items.
     */
    public static int measureDistance(X item1, X item2)
    {
        return Math.abs(item1.x() - item2.x());
    }

    /**
     * This splits the range given by the two items into the given number of divisions.
     *
     * @param o1                         The first item to measure the distance from.
     * @param o2                         The second item to measure the distance to.
     * @param divisions                  The number of divisions to split the range into.
     * @param includeExtraRightMostSplit True to include an extra item in the list (1 more than the requested number of divisions) to represent the right most edge of the range. If this is false then we only have the left edges, leading up to but not including the right part of the range.
     * @param splitsToAddTo              The collection of splits to add to while we split the range.
     */
    public static void splitRange(X o1, X o2, int divisions, boolean includeExtraRightMostSplit, List<X> splitsToAddTo)
    {
        // Get the range:
        int startingValue = o1.x();
        int endingValue = o2.x();
        int range = endingValue - startingValue;

        // Get the step:
        int step = range / divisions;

        // Make sure the step is at least one unit:
        if (step == 0) step = 1;

        // Create the splits:
        for (
            int i = 0, currentValue = startingValue;
            (i < divisions) && (currentValue < endingValue);
            i++, currentValue += step
        )
        {
            // Create the new value:
            X item = new X(currentValue);

            // Add the item to the splits:
            splitsToAddTo.add(item);
        }

        // Add the ending item if desired (NOTE: it means we might have more than the requested (divisions) items in the result, but this is useful to have an entry as the right most edge:
        if (includeExtraRightMostSplit) splitsToAddTo.add(o2);
    }

    /**
     * This finds the division index for an item in a range that is defined by two other items.
     *
     * @param o1                The first item to measure the distance from.
     * @param o2                The second item to measure the distance to.
     * @param divisions         The number of divisions to split the range into.
     * @param itemToFindInRange The item to find the index of for the given range.
     * @param dimension         The index of the dimension to perform the range split in. Zero based.
     * @return The index of the division of the given item in the range specified.
     */
    public static int findIndexInRange(X o1, X o2, int divisions, X itemToFindInRange, int dimension)
    {
        // Get the range:
        int startingValue = extractCoordinate(o1, dimension);
        int endingValue = extractCoordinate(o2, dimension);
        int range = endingValue - startingValue;

        // Get the step:
        int step = range / divisions;

        // Make sure the step is at least one unit:
        if (step == 0) step = 1;

        // Get the value for the item to find:
        int itemToFindValueInDimension = extractCoordinate(itemToFindInRange, dimension);

        // Get the index of the item:
        int index = (itemToFindValueInDimension - startingValue) / step;

        // Clamp the value in range:
        index = Math.max(0, Math.min(index, divisions - 1));

        return index;
    }

    /**
     * Defines the hyper cube for this data structure.
     * @param min The minimum value. Inclusive.
     * @param max The maximum value. Inclusive.
     * @return The hyper cube for this data structure.
     */
    public static HyperCube defineHyperCube(X min, X max)
    {
        return defineHyperCube(new MinInclusiveMaxInclusiveRange<>(min.x(), max.x()));
    }

    /**
     * Defines the hyper cube for this data structure.
     * @param xRange The range of X values for this cube.
     * @param yRange The range of Y values for this cube.
     * @return The hyper cube for this data structure.
     */
    public static HyperCube defineHyperCube(Range<Integer> xRange)
    {
        HyperCube cube = new HyperCube();
        cube.addDimension(Integer::compare, Integer::sum, (l,r) -> l - r, "X", xRange);
        return cube;
    }
}
