package io.nanovc.indexing.repo;

/**
 * A node in the KD-Tree
 * @param <TUnit> The unit for the values in this node.
 */
public final class KDIntermediateNode<TUnit> extends KDNode
{

    /**
     * The dimension that this node slices the search space by.
     */
    public Dimension<TUnit> dimension;

    /**
     * The value that we slice the dimension into for this node.
     */
    public TUnit cutValue;
}
