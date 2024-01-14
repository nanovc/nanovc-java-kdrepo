package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;
import io.nanovc.indexing.repo.ranges.RangeSplit;

/**
 * A node in the KD-Tree
 * @param <TUnit> The unit for the values in this node.
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public final class KDIntermediateNode<
    TUnit,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
    extends KDNode<TContent, TArea>
{

    /**
     * The dimension that this node slices the search space by.
     */
    public Dimension<TUnit> dimension;

    /**
     * The value that we slice the dimension into for this node.
     */
    public TUnit cutValue;

    /**
     * This is the split for the range at this node.
     * {@link RangeSplit#lower()} corresponds to {@link #lowerNode}.
     * {@link RangeSplit#higher()} corresponds to {@link #higherNode}.
     */
    public RangeSplit<TUnit> rangeSplit;

    /**
     * The node for the lower part of this range split.
     */
    public KDNode<TContent, TArea> lowerNode;

    /**
     * The node for the higher part of this range split.
     */
    public KDNode<TContent, TArea> higherNode;

    @Override public String toString()
    {
        return "Intermediate Node at level " + level +
               " with range split for dimension " + dimension.getName() +
               " at value " + cutValue +
               "\n" +
               "Low Range: " + rangeSplit.lower() +
               "\n" +
               "High Range: " + rangeSplit.higher()
               ;
    }
}
