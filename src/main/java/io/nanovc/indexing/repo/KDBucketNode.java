package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;

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
     * The ranges for each dimension that define the volume that this bucket encloses.
     */
    public List<Range<?>> ranges = new ArrayList<>();
}
