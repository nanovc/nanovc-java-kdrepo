package io.nanovc.indexing.repo;

import io.nanovc.ContentAPI;

/**
 * An interface for an implementation that reads an item from the given content.
 * @param <TItem>    The specific type of item that it reads.
 * @param <TContent> The specific type of content that it reads from.
 */
@FunctionalInterface
public interface ContentReader<TItem, TContent extends ContentAPI>
{
    /**
     * Reads the item from the given content.
     * @param content The content to read the item from.
     * @return The item for the given content.
     */
    TItem readItemFromContent(TContent content);
}
