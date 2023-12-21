package io.nanovc.indexing.examples.x;

import io.nanovc.RepoPath;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;
import io.nanovc.indexing.repo.ContentCreator;
import io.nanovc.indexing.repo.ItemGlobalMap;
import io.nanovc.indexing.repo.RepoIndex1DImplementation;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.strings.StringMemoryRepoHandler;

import java.util.Comparator;

/**
 * A {@link RepoIndex1DImplementation} for single dimensional values of type {@link X}.
 */
public class XRepoIndex1D extends RepoIndex1DImplementation<
    X,
    Integer,
    Measurer<X, Integer>,
    Comparator<Integer>,
    RangeSplitter<X>,
    RangeFinder<X>,
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    StringMemoryRepoHandler,
    XRepoIndex1D
    >
{

    public XRepoIndex1D(X minRange, X maxRange, int divisions, int maxItemThreshold, int smallestSplittingDistance, StringMemoryRepoHandler repoHandler, RepoPath rootRepoPath, ItemGlobalMap<X> itemGlobalMap, ContentCreator<X, StringContent> contentCreator)
    {
        super(
            minRange, maxRange, divisions, X::measureDistance, Integer::compare, X::splitRange, X::findIndexInRange,
            maxItemThreshold, smallestSplittingDistance,
            XRepoIndex1D::createXRepoIndex1DSubGrid,
            repoHandler, rootRepoPath,
            itemGlobalMap,
            contentCreator
            );
    }

    public XRepoIndex1D(X minRange, X maxRange, int divisions, int maxItemThreshold, int smallestSplittingDistance)
    {
        this(
            minRange, maxRange, divisions,
            maxItemThreshold, smallestSplittingDistance,
            new StringMemoryRepoHandler(), RepoPath.atRoot(),
            new ItemGlobalMap<>(),
            XRepoIndex1D::createXContent
        );
    }

    /**
     * A factory method to create a new sub-grid for the given range.
     *
     * @return A new sub-grid for the given range.
     */
    public static XRepoIndex1D createXRepoIndex1DSubGrid(
        X minRange, X maxRange, int divisions,
        Measurer<X, Integer> measurer, Comparator<Integer> comparator,
        RangeSplitter<X> rangeSplitter, RangeFinder<X> rangeFinder,
        int maxItemThreshold, int smallestSplittingDistance,
        StringMemoryRepoHandler repoHandler, RepoPath rootRepoPath,
        ItemGlobalMap<X> itemGlobalMap,
        ContentCreator<X, StringContent> contentCreator
        )
    {
        return new XRepoIndex1D(minRange, maxRange, divisions, maxItemThreshold, smallestSplittingDistance, repoHandler, rootRepoPath, itemGlobalMap, contentCreator);
    }

    /**
     * Gets the content for the given item.
     * @param item The item to create the content for.
     * @return The content for the given item.
     */
    public static StringContent createXContent(X item)
    {
        return new StringContent(Integer.toString(item.x()));
    }
}
