package io.nanovc.indexing;

/**
 * A multidimensional (k-dimensional) index.
 * This is used for searching for items efficiently.
 *
 * @param <TItem> The specific type of data that the index is for.
 */
public interface IndexKD<
    TItem
    >
    extends Index<TItem>
{
}
