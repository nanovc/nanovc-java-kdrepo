package io.nanovc.indexing.repo;

import java.util.ArrayList;
import java.util.List;

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
     * The {@link HyperCube} that defines the ranges for each dimension of the volume that this bucket encloses.
     */
    public HyperCube hyperCube;
}
