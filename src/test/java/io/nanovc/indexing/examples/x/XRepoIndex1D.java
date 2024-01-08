package io.nanovc.indexing.examples.x;

import io.nanovc.RepoPath;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;
import io.nanovc.indexing.repo.ContentCreator;
import io.nanovc.indexing.repo.ContentReader;
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
    StringMemoryRepoHandler
    >
{

    public XRepoIndex1D(X minRange, X maxRange, int divisions, StringMemoryRepoHandler repoHandler, RepoPath rootRepoPath, ContentCreator<X, StringContent> contentCreator, ContentReader<X, StringContent> contentReader)
    {
        super(
            minRange, maxRange, divisions, X::measureDistance, Integer::compare, X::splitRange, X::findIndexInRange,
            repoHandler, rootRepoPath,
            contentCreator, contentReader
            );
    }

    public XRepoIndex1D(X minRange, X maxRange, int divisions)
    {
        this(
            minRange, maxRange, divisions,
            new StringMemoryRepoHandler(), RepoPath.atRoot(),
            XRepoIndex1D::createXContent, XRepoIndex1D::readXFromContent
        );
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

    /**
     * Gets the item from the given content.
     * @param content The content to read the item from.
     * @return The item for the given content.
     */
    public static X readXFromContent(StringContent content)
    {
        return new X(Integer.parseInt(content.value));
    }
}
