package io.nanovc.indexing.repo;

import io.nanovc.ContentAPI;

/**
 * An interface for an implementation that creates content from the given item.
 * @param <TItem>    The specific type of item that it creates.
 * @param <TContent> The specific type of content that it creates.
 */
@FunctionalInterface
public interface ContentCreator<TItem, TContent extends ContentAPI>
{
    /**
     * Creates content for the given item.
     * @param item The item to create the content for.
     * @return The content for the given item.
     */
    TContent createContentForItem(TItem item);
}
