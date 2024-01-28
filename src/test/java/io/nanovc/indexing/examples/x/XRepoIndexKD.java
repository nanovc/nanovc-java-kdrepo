package io.nanovc.indexing.examples.x;

import io.nanovc.RepoPath;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import io.nanovc.indexing.Index1D;
import io.nanovc.indexing.repo.ContentCreator;
import io.nanovc.indexing.repo.ContentReader;
import io.nanovc.indexing.repo.RepoIndexKDImplementation;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.strings.StringMemoryRepoHandler;

/**
 * A {@link RepoIndexKDImplementation} for single dimensional values of type {@link X}.
 */
public class XRepoIndexKD extends RepoIndexKDImplementation<
    X,
    Integer,
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    StringMemoryRepoHandler
    >
    implements Index1D<X>
{

    public XRepoIndexKD(X minRange, X maxRange, int divisions, StringMemoryRepoHandler repoHandler, RepoPath rootRepoPath, ContentCreator<X, StringContent> contentCreator, ContentReader<X, StringContent> contentReader)
    {
        super(
            X.defineHyperCube(minRange, maxRange),
            minRange, maxRange, divisions, 10,
            X::extractCoordinate, X::measureDistance, Integer::compare,
            Integer::sum, (left, right) -> left - right, Integer.MAX_VALUE,
            X::splitRange, X::findIndexInRange,
            repoHandler, rootRepoPath,
            contentCreator, contentReader
            );
    }

    public XRepoIndexKD(X minRange, X maxRange, int divisions)
    {
        this(
            minRange, maxRange, divisions,
            new StringMemoryRepoHandler(), RepoPath.atRoot(),
            XRepoIndexKD::createXContent, XRepoIndexKD::readXFromContent
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
