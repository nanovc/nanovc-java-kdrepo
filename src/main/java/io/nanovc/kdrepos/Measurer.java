package io.nanovc.kdrepos;

/**
 * This measures the distance between two items.
 * @param <T> The type of item that we want to measure the distance between.
 * @param <R> The type for the distance.
 */
@FunctionalInterface
public interface Measurer<T,R>
{
    /**
     * @param o1 The first item to measure the distance from.
     * @param o2 The second item to measure the distance to.
     * @return The distance between the two items.
     */
    public R measureDistanceBetween(T o1, T o2);
}
