package io.nanovc.indexing;

/**
 * An index of items.
 * This is used for searching for items efficiently.
 *
 * @param <TItem> The specific type of data that the index is for.
 */
public interface Index<
    TItem
    >
{
    /**
     * Adds the given item to the index.
     *
     * @param item The item to add to the index.
     */
    void add(TItem item);

    /**
     * Indexes the items that have been added.
     * This is a pre-computation step that needs to be called before we search for nearest neighbours.
     */
    void index();

    /**
     * This finds the nearest item in the index to the given item.
     *
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    TItem searchNearest(TItem item);
}
