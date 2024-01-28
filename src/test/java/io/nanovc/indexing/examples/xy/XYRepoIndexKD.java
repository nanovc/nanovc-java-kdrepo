package io.nanovc.indexing.examples.xy;

import io.nanovc.RepoPath;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import io.nanovc.indexing.repo.ContentCreator;
import io.nanovc.indexing.repo.ContentReader;
import io.nanovc.indexing.repo.RepoIndexKDImplementation;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.strings.StringMemoryRepoHandler;

/**
 * A {@link RepoIndexKDImplementation} for single dimensional values of type {@link XY}.
 */
public class XYRepoIndexKD extends RepoIndexKDImplementation<
    XY,
    Double,
    StringContent,
    StringHashMapArea,
    MemoryCommit,
    StringMemoryRepoHandler
    >
{

    public XYRepoIndexKD(
        XY minRange, XY maxRange, int divisions, int bucketThreshold,
        StringMemoryRepoHandler repoHandler, RepoPath rootRepoPath,
        ContentCreator<XY, StringContent> contentCreator, ContentReader<XY, StringContent> contentReader
    )
    {
        super(
            XY.defineHyperCube(minRange, maxRange),
            divisions, bucketThreshold,
            XY::extractCoordinate, XY::measureDistanceL2NormEuclidean, Double::compare,
            repoHandler, rootRepoPath,
            contentCreator, contentReader
            );
    }

    public XYRepoIndexKD(XY minRange, XY maxRange, int divisions, int bucketThreshold)
    {
        this(
            minRange, maxRange, divisions, bucketThreshold,
            new StringMemoryRepoHandler(), RepoPath.atRoot(),
            XYRepoIndexKD::createXYContent, XYRepoIndexKD::readXYFromContent
        );
    }

    /**
     * Gets the content for the given item.
     * @param item The item to create the content for.
     * @return The content for the given item.
     */
    public static StringContent createXYContent(XY item)
    {
        return new StringContent(item.x() + "|" + item.y());
    }

    /**
     * Gets the item from the given content.
     * @param content The content to read the item from.
     * @return The item for the given content.
     */
    public static XY readXYFromContent(StringContent content)
    {
        String[] split = content.value.split("\\|");
        return new XY(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }
}
