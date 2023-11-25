package io.nanovc.indexing.grid;

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
     */
    public TDistance distance;
}
