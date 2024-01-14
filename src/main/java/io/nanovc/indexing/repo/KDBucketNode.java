package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A node in the KD-Tree
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public final class KDBucketNode<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
    extends KDNode<TContent, TArea>
{
    /**
     * The map of content stored in this bucket.
     */
    public Map<RepoPathNode, TContent> contentMap = new LinkedHashMap<>();

    /**
     * The path where bucket items are added.
     * This gives us a sub-path where the bucket content is kept.
     */
    public RepoPathNode bucketItemsRepoPathNode;

    /**
     * The {@link HyperCube} that defines the ranges for each dimension of the volume that this bucket encloses.
     */
    public HyperCube hyperCube;

    @Override public String toString()
    {
        return "Bucket Node at level " + level +
               " with " +
               contentMap.size() + " item" + (contentMap.size() == 1 ? "" : "s") + "\n" +
               "Bucket range:" + "\n" +
               hyperCube + "\n" +
               "Items:" + "\n" +
               contentMap.values().stream().map(Object::toString).collect(Collectors.joining("\n"))
               ;
    }
}
