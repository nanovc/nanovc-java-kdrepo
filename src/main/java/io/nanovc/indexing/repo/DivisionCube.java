package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;
import io.nanovc.indexing.repo.ranges.Range;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * A cube of divisions.
 * This is used to divide a {@link HyperCube} into {@link Division divisions}.
 * @param <TItem>    The specific type of item that this division cube holds.
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public class DivisionCube<
    TItem,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
{
    /**
     * The {@link HyperCube} of the kd-space that we are dividing.
     */
    public HyperCube hyperCube;

    /**
     * This is the root of the division tree.
     * Walk this to find the specific division of data that we have.
     */
    public DivisionDimension<TItem, TContent, TArea> rootDimension;

    /**
     * This is an index of all the {@link DivisionCell division cells} indexed by their {@link DivisionCell#branchName branch name.}
     * This makes it easy to go through each cell.
     * This is especially useful when we have a sparse amount of data.
     */
    public final TreeMap<String, DivisionCell<TItem, TContent, TArea>> cellsByBranchName = new TreeMap<>();

    /**
     * This contains the range splits that exist for each dimension of this cube.
     * This is useful so that we can understand the size of this division cube.
     */
    public final LinkedHashMap<Integer, List<Range<Object>>> rangeSplitsByDimensionIndex = new LinkedHashMap<>();

    @Override public String toString()
    {
        return "DivisionCube{" + "\n" +
               "hyperCube=\n" + hyperCube + "\n" +
               '}';
    }
}
