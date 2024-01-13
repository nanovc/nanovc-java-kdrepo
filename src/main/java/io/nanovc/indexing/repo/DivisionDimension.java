package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.Range;

import java.util.List;
import java.util.TreeMap;

/**
 * A dimension in a {@link DivisionCube}.
 * This is used to divide a {@link HyperCube} into {@link Division divisions}.
 * Each {@link DivisionDimension} makes up a link in the chain for each dimension, ultimately making a tree.
 * When there are 3 or more {@link Dimension dimensions} in a {@link HyperCubeDefinition hyper cube definition}
 * then the chain will look like this:
 * {@link First} -> {@link Intermediate} -> ... -> {@link Last}
 *
 * If there are only 2 dimensions then it looks like this:
 * {@link First} -> {@link Last}
 *
 * If there is only 1 dimension then it looks like this:
 * {@link Last}
 *
 */
public abstract sealed class DivisionDimension
    permits
    DivisionDimension.First,
    DivisionDimension.Intermediate,
    DivisionDimension.Last
{
    /**
     * The {@link HyperCube} of kd-space that this node of the chain (tree) represents.
     */
    public HyperCube hyperCube;

    /**
     * The {@link Dimension dimension} in this {@link HyperCubeDefinition hyper cube definition} that this corresponds to.
     */
    public Dimension<Object> dimension;

    /**
     * The ranges for each split of this dimension.
     * The index in this list also corresponds to the divisionIndex.
     */
    public List<Range<Object>> splits;

    /**
     * This is the first {@link DivisionDimension} in the chain.
     * This corresponds to the first {@link Dimension} in the {@link HyperCubeDefinition}.
     */
    public static final class First extends DivisionDimension
    {
        /**
         * A pointer to the next dimension that we point to.
         */
        public DivisionDimension nextDivisionDimension;
    }

    /**
     * This is an intermediate (middle) {@link DivisionDimension} in the chain.
     */
    public static final class Intermediate extends DivisionDimension
    {
        /**
         * A pointer to the previous dimension that we point to.
         */
        public DivisionDimension previousDivisionDimension;

        /**
         * A pointer to the next dimension that we point to.
         */
        public DivisionDimension nextDivisionDimension;
    }

    /**
     * This is the last {@link DivisionDimension} in the chain.
     * This corresponds to the last {@link Dimension} in the {@link HyperCubeDefinition}.
     * If a {@link HyperCubeDefinition} only has one {@link Dimension},\
     * then we go straight to using the {@link Last} division dimension.
     */
    public static final class Last extends DivisionDimension
    {
        /**
         * A pointer to the previous dimension that we point to.
         * If this is null then there is only one dimension in this chain.
         */
        public DivisionDimension previousDivisionDimension;


        /**
         * The cells where we have indexed data.
         * We only have cells when we are in the last dimension.
         */
        public TreeMap<Integer, DivisionCell<?,?>> cellsByIndex = new TreeMap<>();
    }
}
