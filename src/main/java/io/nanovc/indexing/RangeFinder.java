package io.nanovc.indexing;

/**
 * This finds the division index for an item in a range that is defined by two other items.
 *
 * @param <T> The type of item that we want to place in a range.
 */
@FunctionalInterface
public interface RangeFinder<T>
{
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
    int findIndexInRange(T o1, T o2, int divisions, T itemToFindInRange, int dimension);

    /**
     * This finds the division index for an item in a range that is defined by two other items.
     * This finds it in the first dimension (index zero).
     *
     * @param o1                The first item to measure the distance from.
     * @param o2                The second item to measure the distance to.
     * @param divisions         The number of divisions to split the range into.
     * @param itemToFindInRange The item to find the index of for the given range.
     * @return The index of the division of the given item in the range specified.
     */
    default int findIndexInRange(T o1, T o2, int divisions, T itemToFindInRange)
    {
        return findIndexInRange(o1, o2, divisions, itemToFindInRange, 0);
    }
}
