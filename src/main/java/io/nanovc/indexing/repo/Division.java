package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;

/**
 * A division of the {@link RepoIndex1D} range.
 * This is used to keep all context for the division together.
 *
 * @param <TItem>    The specific type of data that the index is for.
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public class Division<
    TItem,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
{
    /**
     * The index of this division in the range.
     */
    public int divisionIndex;

    /**
     * The content area for this division.
     */
    public TArea contentArea;

    /**
     * The repo path tree for this division.
     * It matches up with the paths in the {@link #contentArea}
     */
    public RepoPathTree repoPathTree;

    /**
     * The minimum range of this division.
     */
    public TItem minRange;

    /**
     * The maximum range of this division.
     */
    public TItem maxRange;

}
