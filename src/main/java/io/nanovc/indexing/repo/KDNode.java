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

    /**
     * The parent of this node.
     * Null if this is a root node.
     */
    public KDNode parent;
}
