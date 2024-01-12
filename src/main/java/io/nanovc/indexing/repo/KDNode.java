package io.nanovc.indexing.repo;

/**
 * A node in the KD-Tree
 */
public class KDNode
{
    /**
     * The index that this node is at.
     * Zero based.
     */
    public int level;

    /**
     * The dimension that this node slices the search space by.
     */
    public Dimension dimension;
}
