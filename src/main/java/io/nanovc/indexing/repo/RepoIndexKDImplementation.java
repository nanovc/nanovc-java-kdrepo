package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.*;

import java.util.Comparator;

/**
 * A k-dimensional {@link RepoIndexKD}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TContent>            The specific type of content that the repo commits.
 * @param <TArea>               The specific type of content area that the repo commits.
 * @param <TCommit>             The specific type of commit that the repo creates.
 * @param <TRepoHandler>        The specific type of repo handler to use for this index.
 */
public class RepoIndexKDImplementation<
    TItem,
    TDistance,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?, ?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>
    > extends RepoIndexKDBase<TItem, TDistance, TContent, TArea, TCommit, TRepoHandler>
{
    public RepoIndexKDImplementation(
        int numberOfDimensions,
        TItem minRange, TItem maxRange, int divisions,
        Extractor<TItem, TDistance> extractor, Measurer<TItem, TDistance> measurer, Comparator<TDistance> distanceComparator,
        Operator<TDistance> distanceAdder, Operator<TDistance> distanceSubtractor, TDistance maxDistance,
        RangeSplitter<TItem> rangeSplitter, RangeFinder<TItem> rangeFinder,
        TRepoHandler repoHandler, RepoPath rootRepoPath,
        ContentCreator<TItem, TContent> contentCreator, ContentReader<TItem, TContent> contentReader
    )
    {
        super(
            numberOfDimensions,
            minRange, maxRange, divisions,
            extractor, measurer, distanceComparator,
            distanceAdder, distanceSubtractor, maxDistance,
            rangeSplitter, rangeFinder,
            repoHandler, rootRepoPath,
            contentCreator, contentReader
        );
    }
}