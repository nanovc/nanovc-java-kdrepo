package io.nanovc.indexing.kdtree.bentley1990;

/**
 * A node in the {@link KDTree}.
 */
public class KDNode
{
    /**
     * The depth of this node in the kd-tree.
     * 0 is the root node.
     */
    public int level;

    /**
     * True to flag that this is a bucket.
     * False to flag that this is an internal node.
     */
    public boolean bucket;

    /**
     * The dimension being partitioned.
     */
    public int cutdim;

    /**
     * A value in the {@link #cutdim} dimension.
     */
    public double cutval;

    /**
     * The pointer to the low son subtree.
     * It contains points that are not greater than {@link #cutval} in that dimension.
     */
    public KDNode loson;

    /**
     * The pointer to the high son subtree.
     * It contains points that are not less than {@link #cutval} in that dimension.
     */
    public KDNode hison;

    /**
     * In a {@link #bucket} node, {@link #lopt} is the index into the global permutation vector perm[n].
     * The {@link #hipt} - {@link #lopt} + 1 integers in perm[lopt..hipt] give the indices of the points
     * in the bucket (thus perm is just a convenient set representation).
     */
    public int lopt;

    /**
     * In a {@link #bucket} node, {@link #hipt} is the index into the global permutation vector perm[n].
     * The {@link #hipt} - {@link #lopt} + 1 integers in perm[lopt..hipt] give the indices of the points
     * in the bucket (thus perm is just a convenient set representation).
     */
    public int hipt;
}
