package io.nanovc.indexing.repo;

/**
 * A node in the KD-Tree
 */
public sealed class KDNode
    permits KDIntermediateNode, KDBucketNode
{
    /**
     * The index that this node is at.
     * Zero based.
     */
    public int level;
}
