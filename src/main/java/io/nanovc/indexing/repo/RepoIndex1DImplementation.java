package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;

import java.util.Comparator;

/**
 * A one dimensional {@link RepoIndex1D}.
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
 */
public class RepoIndex1DImplementation<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?, ?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>
    > extends RepoIndex1DBase<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TContent, TArea, TCommit, TRepoHandler>
{
    public RepoIndex1DImplementation(
        TItem minRange, TItem maxRange, int divisions,
        TMeasurer measurer, TDistanceComparator comparator,
        TRangeSplitter rangeSplitter, TRangeFinder rangeFinder,
        TRepoHandler repoHandler, RepoPath rootRepoPath,
        ContentCreator<TItem, TContent> contentCreator, ContentReader<TItem, TContent> contentReader
    )
    {
        super(minRange, maxRange, divisions, measurer, comparator, rangeSplitter, rangeFinder, repoHandler, rootRepoPath, contentCreator, contentReader);
    }
}
