package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;
import io.nanovc.indexing.repo.ranges.Range;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A dimension in a {@link DivisionCube}.
 * This is used to divide a {@link HyperCube} into {@link Division divisions}.
 * Each {@link DivisionDimension} makes up a link in the chain for each dimension, ultimately making a tree.
 * When there are 3 or more {@link Dimension dimensions} in a {@link HyperCubeDefinition hyper cube definition}
 * then the chain will look like this:
 * {@link First} -> {@link Intermediate} -> ... -> {@link Last}
 * <p>
 * If there are only 2 dimensions then it looks like this:
 * {@link First} -> {@link Last}
 * <p>
 * If there is only 1 dimension then it looks like this:
 * {@link Last}
 *
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public abstract sealed class DivisionDimension<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
    permits
    DivisionDimension.First,
    DivisionDimension.Intermediate,
    DivisionDimension.Last
{
    /**
     * The {@link DivisionCube division cube } that this {@link DivisionDimension division dimension} belongs to.
     */
    public DivisionCube<TContent, TArea> divisionCube;

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
     *
     * @param <TContent> The specific type of content that the repo commits.
     * @param <TArea>    The specific type of content area that the repo commits.
     */
    public static final class First<
        TContent extends ContentAPI,
        TArea extends AreaAPI<TContent>
        >
        extends DivisionDimension<TContent, TArea>
    {
        /**
         * The next dimension node, indexed by the division index.
         * This makes up the tree structure for the KD-Repo.
         */
        public final Map<Integer, DivisionDimension<TContent, TArea>> nextDivisionDimensionByIndex = new HashMap<>();
    }

    /**
     * This is an intermediate (middle) {@link DivisionDimension} in the chain.
     *
     * @param <TContent> The specific type of content that the repo commits.
     * @param <TArea>    The specific type of content area that the repo commits.
     */
    public static final class Intermediate<
        TContent extends ContentAPI,
        TArea extends AreaAPI<TContent>
        >
        extends DivisionDimension<TContent, TArea>
    {
        /**
         * A pointer to the previous dimension that we point to.
         */
        public DivisionDimension<TContent, TArea> previousDivisionDimension;

        /**
         * The next dimension node, indexed by the division index.
         * This makes up the tree structure for the KD-Repo.
         */
        public final Map<Integer, DivisionDimension<TContent, TArea>> nextDivisionDimensionByIndex = new HashMap<>();
    }

    /**
     * This is the last {@link DivisionDimension} in the chain.
     * This corresponds to the last {@link Dimension} in the {@link HyperCubeDefinition}.
     * If a {@link HyperCubeDefinition} only has one {@link Dimension},\
     * then we go straight to using the {@link Last} division dimension.
     *
     * @param <TContent> The specific type of content that the repo commits.
     * @param <TArea>    The specific type of content area that the repo commits.
     */
    public static final class Last<
        TContent extends ContentAPI,
        TArea extends AreaAPI<TContent>
        >
        extends DivisionDimension<TContent, TArea>
    {
        /**
         * A pointer to the previous dimension that we point to.
         * If this is null then there is only one dimension in this chain.
         */
        public DivisionDimension<TContent, TArea> previousDivisionDimension;

        /**
         * The cells where we have indexed data.
         * We only have cells when we are in the last dimension.
         */
        public TreeMap<Integer, DivisionCell<?, ?>> cellsByIndex = new TreeMap<>();
    }

    @Override public String toString()
    {
        return "DivisionDimension{" + "\n" +
               "hyperCube=\n" + hyperCube + ",\n" +
               "dimension=\n" + dimension + ",\n" +
               "splits=" + (splits == null ? "0" : splits.size()) + "\n" +
               '}';
    }
}
