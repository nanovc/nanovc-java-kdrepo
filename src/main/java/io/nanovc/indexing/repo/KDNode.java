package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;

/**
 * A node in the KD-Tree
 *
 * @param <TItem>    The specific type of item that this node holds.
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public sealed class KDNode<
    TItem,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
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
    public KDNode<TItem, TContent, TArea> parent;

    /**
     * The {@link DivisionCell division cell} that we are in.
     * This allows us to get broader context for this node.
     */
    public DivisionCell<TItem, TContent, TArea> divisionCell;

    /**
     * The {@link HyperCube} that defines the ranges for each dimension of the volume that this node encloses.
     */
    public HyperCube hyperCube;

    /**
     * The repo path node that keeps track of where in the content area this node belongs.
     */
    public RepoPathNode repoPathNode;
}
