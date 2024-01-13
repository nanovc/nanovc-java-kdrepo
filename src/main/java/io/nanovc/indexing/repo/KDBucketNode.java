package io.nanovc.indexing.repo;

import io.nanovc.RepoPath;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A node in the KD-Tree
 * @param <TContent> The type of content that this bucket holds.
 */
public final class KDBucketNode<TContent> extends KDNode
{
    /**
     * The list of content stored at this bucket.
     */
    public List<TContent> contentList = new ArrayList<>();

    /**
     * The map of content stored in this bucket.
     */
    public Map<RepoPath, TContent> contentMap = new LinkedHashMap<>();

    /**
     * The {@link HyperCube} that defines the ranges for each dimension of the volume that this bucket encloses.
     */
    public HyperCube hyperCube;

    @Override public String toString()
    {
        return "Bucket Node at level " + level +
               " with " +
               contentList.size() + " item" + (contentList.size() == 1 ? "" : "s") + "\n" +
               "Bucket range:" + "\n" +
               hyperCube + "\n" +
               "Items:" + "\n" +
               contentList.stream().map(Object::toString).collect(Collectors.joining("\n"))
               ;
    }
}
