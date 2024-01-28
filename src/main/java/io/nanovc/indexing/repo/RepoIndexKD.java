package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.*;

import java.util.Comparator;

/**
 * A k-dimensional {@link IndexKD} that uses a repo for its implementation.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TContent>            The specific type of content that the repo commits.
 * @param <TArea>               The specific type of content area that the repo commits.
 * @param <TCommit>             The specific type of commit that the repo creates.
 * @param <TRepoHandler>        The specific type of repo handler to use for this index.
 */
public interface RepoIndexKD<
    TItem,
    TDistance,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?, ?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>
    >
    extends IndexKD<TItem>
{

    /**
     * Gets the minimum range of this index.
     *
     * @return The minimum range of this index.
     */
    TItem getMinRange();

    /**
     * Gets the maximum range of this index.
     *
     * @return The maximum range of this index.
     */
    TItem getMaxRange();

    /**
     * Gets the number of divisions to use for this grid index.
     *
     * @return The number of divisions to use for this grid index.
     */
    int getDivisions();

    /**
     * Gets the measurer that measures distances between items.
     *
     * @return The measurer that measures distances between items.
     */
    Measurer<TItem, TDistance> getMeasurer();

    /**
     * Gets the comparator to use for comparing distances of items.
     *
     * @return The comparator to use for comparing distances of items.
     */
    Comparator<TDistance> getDistanceComparator();

    /**
     * Gets the range splitter that divides the range into a set of divisions.
     *
     * @return The range splitter that divides the range into a set of divisions.
     */
    RangeSplitter<TItem> getRangeSplitter();

    /**
     * Gets the repo handler to use for this repo index.
     *
     * @return The repo handler to use for this repo index.
     */
    TRepoHandler getRepoHandler();

    /**
     * Gets the repo path to the root of this repo index where we update the index information.
     *
     * @return The repo path to the root of this repo index where we update the index information.
     */
    RepoPath getRootRepoPath();

    /**
     * Gets the content creator to use.
     *
     * @return The content creator to use.
     */
    ContentCreator<TItem, TContent> getContentCreator();

    /**
     * Gets the content reader to use for getting an item from the given content.
     *
     * @return The content reader to use for getting an item from the given content.
     */
    ContentReader<TItem, TContent> getContentReader();
}
