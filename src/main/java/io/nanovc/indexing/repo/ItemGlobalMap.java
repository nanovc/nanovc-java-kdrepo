package io.nanovc.indexing.repo;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a global map of items.
 * It associates an integer ID with each item that is added.
 * @param <TItem> The specific type of item that we store in this map.
 */
public class ItemGlobalMap<TItem>
{
    /**
     * The map that associates the item key to the item.
     */
    private final HashMap<Integer, TItem> keyToItemMap = new HashMap<>();

    /**
     * The map that associates the item to the key.
     */
    private final HashMap<TItem, Integer> itemToKeyMap = new HashMap<>();

    /**
     * This keeps track of the next item key to use when an item is added.
     */
    private final AtomicInteger nextItemKey = new AtomicInteger();

    /**
     * Adds the given item to the map.
     * If it already exists, it does nothing.
     * @param item The item to add.
     * @return The key that the item was added with.
     */
    public int add(TItem item)
    {
        // Check whether we already have this item in the map:
        Integer existingItemKey = getKeyForItem(item);

        // Check whether we already have this item:
        if (existingItemKey != null)
        {
            // We already have this item.

            return existingItemKey;
        }
        else
        {
            // We have not seen this item yet.
            // Get the next key:
            Integer nextKey = this.nextItemKey.getAndIncrement();

            // Index the item:
            this.keyToItemMap.put(nextKey, item);
            this.itemToKeyMap.put(item, nextKey);

            return nextKey;
        }
    }

    /**
     * This gets the key for the given item.
     * @param item The item to search for.
     * @return The key for the given item. Null if the item doesn't exist in the map.
     */
    public Integer getKeyForItem(TItem item)
    {
        return this.itemToKeyMap.get(item);
    }

    /**
     * Gets the item with the given item key.
     * @param itemKey The item key to get.
     * @return The item with the given key.
     */
    public TItem getItem(int itemKey)
    {
        return this.keyToItemMap.get(itemKey);
    }
}
