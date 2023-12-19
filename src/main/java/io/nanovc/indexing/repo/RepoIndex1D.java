package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.Index1D;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;

import java.util.Comparator;

/**
 * A one dimensional {@link Index1D} that uses a repo for its implementation.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 * @param <TContent>            The specific type of content that the repo commits.
 * @param <TArea>               The specific type of content area that the repo commits.
 * @param <TCommit>             The specific type of commit that the repo creates.
 * @param <TRepoHandler>        The specific type of repo handler to use for this index.
 * @param <TSubGrid>            The type of sub-grid item. Normally it refers back to itself.
 */
public interface RepoIndex1D<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?,?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>,
    TSubGrid extends RepoIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TContent, TArea, TCommit, TRepoHandler, TSubGrid>
    >
    extends Index1D<TItem>
{

    /**
     * Gets the minimum range of this index.
     * @return The minimum range of this index.
     */
    TItem getMinRange();

    /**
     * Gets the maximum range of this index.
     * @return The maximum range of this index.
     */
    TItem getMaxRange();

    /**
     * Gets the number of divisions to use for this grid index.
     * @return The number of divisions to use for this grid index.
     */
    int getDivisions();

    /**
     * Gets the measurer that measures distances between items.
     * @return The measurer that measures distances between items.
     */
    TMeasurer getMeasurer();

    /**
     * Gets the comparator to use for comparing distances of items.
     * @return The comparator to use for comparing distances of items.
     */
    TDistanceComparator getDistanceComparator();

    /**
     * Gets the range splitter that divides the range into a set of divisions.
     * @return The range splitter that divides the range into a set of divisions.
     */
    TRangeSplitter getRangeSplitter();

    /**
     * Gets the range finder that gets the index of an item in the divisions of a range.
     * @return The range finder that gets the index of an item in the divisions of a range.
     */
    TRangeFinder getRangeFinder();

    /**
     * Gets the maximum number of items to keep in the grid before it splits the cell into a subgrid.
     * @return The maximum number of items to keep in the grid before it splits the cell into a subgrid.
     */
    int getMaxItemThreshold();

    /**
     * Gets the smallest distance that we do not split beyond.
     * @return The smallest distance that we do not split beyond.
     */
    TDistance getSmallestSplittingDistance();

    /**
     * Gets the repo handler to use for this repo index.
     * @return The repo handler to use for this repo index.
     */
    TRepoHandler getRepoHandler();

    /**
     * Gets the repo path to the root of this repo index where we update the index information.
     *
     * @return The repo path to the root of this repo index where we update the index information.
     */
    RepoPath getRootRepoPath();
}
