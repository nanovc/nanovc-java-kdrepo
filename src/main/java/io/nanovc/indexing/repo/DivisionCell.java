package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;

/**
 * A cell of a {@link DivisionCube}.
 * This gives us a specific volume of the kd-space that we are looking at.
 * It also contains the {@link io.nanovc.AreaAPI content area} where the content is indexed.
 * @param <TArea> The specific type of content area that this {@link DivisionCell} holds.
 */
public class DivisionCell<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
{
    /**
     * The parent dimension in the chain that we walked to get to this {@link DivisionCell}.
     */
    public DivisionDimension parentDimension;

    /**
     * The hyper cube of the kd-space that this {@link DivisionCell cell} represents.
     * This gives us the range of values in each dimension.
     */
    public HyperCube hyperCube;

    /**
     * The root of the kd-tree.
     */
    public KDNode kdTreeRoot;

    /**
     * The content area for this division.
     */
    public TArea contentArea;

    /**
     * The repo path tree for this division.
     * It matches up with the paths in the {@link #contentArea}
     */
    public RepoPathTree repoPathTree;

    @Override public String toString()
    {
        return "DivisionCell{" + "\n" +
               "hyperCube=\n" + hyperCube + ",\n" +
               '}';
    }
}
