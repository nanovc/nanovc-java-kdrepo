package io.nanovc.indexing.examples.x;

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
     * @param o1            The first item to measure the distance from.
     * @param o2            The second item to measure the distance to.
     * @param divisions     The number of divisions to split the range into.
     * @param splitsToAddTo The collection of splits to add to while we split the range.
     */
    public static void splitRange(X o1, X o2, int divisions, List<X> splitsToAddTo)
    {
        // Get the range:
        int startingValue = o1.x();
        int endingValue = o2.x();
        int range = endingValue - startingValue;

        // Get the step:
        int step = range / (divisions - 1);

        // Make sure the step is at least one unit:
        if (step == 0) step = 1;

        // Add the starting item:
        splitsToAddTo.add(o1);

        // Create the splits:
        for (
            int innerDivisions = divisions - 2, i = 0, currentValue = startingValue + step;
            (i < innerDivisions) && (currentValue < endingValue);
            i++, currentValue += step
        )
        {
            // Create the new value:
            X item = new X(currentValue);

            // Add the item to the splits:
            splitsToAddTo.add(item);
        }

        // Add the ending item:
        splitsToAddTo.add(o2);
    }

    /**
     * This finds the division index for an item in a range that is defined by two other items.
     *
     * @param o1                The first item to measure the distance from.
     * @param o2                The second item to measure the distance to.
     * @param divisions         The number of divisions to split the range into.
     * @param itemToFindInRange The item to find the index of for the given range.
     * @return The index of the division of the given item in the range specified.
     */
    public static int findIndexInRange(X o1, X o2, int divisions, X itemToFindInRange)
    {
        // Get the range:
        int startingValue = o1.x();
        int endingValue = o2.x();
        int range = endingValue - startingValue;

        // Get the step:
        int step = range / (divisions - 1);

        // Make sure the step is at least one unit:
        if (step == 0) step = 1;

        // Get the index of the item:
        int index = (itemToFindInRange.x() - startingValue) / step;

        // Clamp the value in range:
        index = Math.max(0, Math.min(index, divisions - 1));

        return index;
    }
}
