package io.nanovc.indexing.repo;

/**
 * This is a measured result for an item.
 */
public class MeasuredItem<TItem, TDistance>
{
    /**
     * The item that was measured.
     */
    public TItem item;


    /**
     * The distance to the target that was measured.
     * If this is null then the item is an exact match so the distance was not measured explicitly.
     */
    public TDistance distance;
}
