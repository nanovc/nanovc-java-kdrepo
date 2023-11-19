package io.nanovc.indexing.examples.x;

/**
 * A record for an X coordinate.
 * @param x The x coordinate.
 */
public record X(int x)
{
    /**
     * Compares the one item to the other.
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
     * @param item1 The first item.
     * @param item2 The second item.
     * @return The distance between the two items.
     */
    public static int measureDistance(X item1, X item2)
    {
        return Math.abs(item1.x() - item2.x());
    }
}
