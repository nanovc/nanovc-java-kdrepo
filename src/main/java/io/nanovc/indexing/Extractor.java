package io.nanovc.indexing;

/**
 * This extracts the specific dimensional value from an item.
 * @param <T> The type of item that we want to extract dimensional information from.
 * @param <R> The type for the dimension (must be the same as what is used for distances).
 */
@FunctionalInterface
public interface Extractor<T,R>
{
    /**
     * Extracts the specific dimensional value from an item.
     * @param item      The item to extract the dimensional value from.
     * @param dimension The index of the dimension that we want to extract. Zero based.
     * @return The value of the given dimension for the item.
     */
    public R extractDimensionalValue(T item, int dimension);
}