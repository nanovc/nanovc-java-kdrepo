package io.nanovc.indexing.kdtree.bentley1990;

import io.nanovc.indexing.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;


/**
 * See the papers folder for the reference: 98524.98564 - K-d Trees for Semidynamic Point Sets.pdf
 * Bentley, Jon Louis. "K-d trees for semidynamic point sets." Proceedings of the sixth annual symposium on Computational geometry. 1990.
 * <p>
 * The reference to an optimized kd-tree is where we check which dimension has the biggest spread at each step.
 * See the paper: 355744.355745 - An Algorithm for Finding Best Matches in Logarithmic Expected Time.pdf
 * Friedman, Jerome H., Jon Louis Bentley, and Raphael Ari Finkel. "An algorithm for finding best matches in logarithmic expected time." ACM Transactions on Mathematical Software (TOMS) 3.3 (1977): 209-226.
 * <p>
 * The SELECT algorithm is informed by:
 * 360680.360691 - Expected Time Bounds for Selection.pdf
 * 360680.360694 - Algorithm 489 - The Algorithm SELECT - for finding the ith smallest of n elements.pdf
 * Floyd, Robert W., and Ronald L. Rivest. "Expected time bounds for selection." Communications of the ACM 18.3 (1975): 165-172.
 * Floyd, Robert W., and Ronald L. Rivest. "Algorithm 489: The algorithm SELECT—For finding the i th smallest of n elements [m1]." Communications of the ACM 18.3 (1975): 173.
 * <p>
 * Additional info from here:
 * https://stackoverflow.com/questions/253767/kdtree-implementation-in-java
 * https://simondlevy.academic.wlu.edu/software/kd/
 * https://robowiki.net/wiki/Kd-tree
 * https://github.com/AReallyGoodName/OfflineReverseGeocode/blob/master/src/main/java/geocode/kdtree/KDTree.java
 * https://algorist.com/problems/Kd-Trees.html
 * https://github.com/phishman3579/java-algorithms-implementation/blob/master/src/com/jwetherell/algorithms/data_structures/KdTree.java
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 */
public class KDTree<
    TItem,
    TDistance extends Number
    >
    implements Index1D<TItem>, Index2D<TItem>, IndexKD<TItem>
{
    /**
     * Two pointers into {@link #perm} represent a subset of the points.
     * This is used for permuting the order of the points as the algorithm builds the kd-tree.
     */
    public int[] perm;

    /**
     * The root of the {@link KDTree}.
     */
    public KDNode root;

    /**
     * The points that are being indexed.
     */
    public List<TItem> points = new ArrayList<>();

    /**
     * The cut-off number for the buckets.
     * This is how many points to store in a bucket.
     */
    public int cutoff = 1;

    /**
     * This is the logic to extract the d-th dimensional coordinate for the given item.
     * The first argument is the item.
     * The second argument is the dimension to extract. Zero based.
     */
    public final Extractor<TItem> coordinateExtractor;

    /**
     * This is the specific distance measurement that we want to use.
     * It measures the distance between the two items.
     */
    public final Measurer<TItem, TDistance> distanceMeasurer;

    /**
     * This is the specific distance comparator that we want to us.
     * It compares the distances between items.
     */
    public final Comparator<TDistance> distanceComparator;

    /**
     * The logic to add distances.
     */
    public final Operator<TDistance> distanceAdder;

    /**
     * The logic to subtract distances.
     */
    public final Operator<TDistance> distanceSubtractor;

    /**
     * This is the maximum distance amount.
     */
    public final TDistance maxDistance;

    /**
     * The number of dimensions for this kd-Tree.
     */
    public final int numberOfDimensions;

    public KDTree(
        Extractor<TItem> extractor,
        Measurer<TItem, TDistance> measurer,
        Comparator<TDistance> distanceComparator,
        Operator<TDistance> distanceAdder,
        Operator<TDistance> distanceSubtractor,
        TDistance maxDistance,
        int numberOfDimensions
    )
    {
        this.coordinateExtractor = extractor;
        this.distanceMeasurer = measurer;
        this.distanceComparator = distanceComparator;
        this.distanceAdder = distanceAdder;
        this.distanceSubtractor = distanceSubtractor;
        this.maxDistance = maxDistance;
        this.numberOfDimensions = numberOfDimensions;
    }

    /**
     * Indexes the points that have been added.
     */
    public void index()
    {
        this.index(this.points);
    }

    /**
     * Indexes the given points.
     *
     * @param points The points to index.
     */
    public void index(List<TItem> points)
    {
        // Save the points:
        this.points = points;

        // Define constants:
        int n = points.size();

        // Initialise the arrays:
        this.perm = new int[n];

        // The tree is built by this code:
        for (int i = 0; i < n; i++)
        {
            perm[i] = i;
        }

        // Build the kd-tree:
        root = build(0, 0, n - 1);
    }

    /**
     * Builds the node for the given bounds recursively.
     *
     * @param level The level that we are building. 0 is the root level.
     * @param l     The lower bound to build.
     * @param u     The upper bound to build.
     * @return The node that was built.
     */
    public KDNode build(int level, int l, int u)
    {
        // Create the node:
        KDNode p = new KDNode();

        // Save the level of this node:
        p.level = level;

        // Check whether we are below the cutoff to make a bucket:
        if (u - l + 1 <= cutoff)
        {
            // We are at our cutoff limit to create a bucket.
            p.bucket = true; // 1
            p.lopt = l;
            p.hipt = u;
        }
        else
        {
            // We are not small enough to make a bucket, we are still an intermediate node.
            p.bucket = false; // 0
            p.cutdim = findmaxspread(l, u, p);
            int m = (l + u) / 2;
            select(l, u, m, p.cutdim);
            p.cutval = px(m, p.cutdim);
            p.loson = build(level + 1, l, m);
            p.hison = build(level + 1, m + 1, u);
        }
        return p;
    }

    /**
     * Returns the dimension with largest difference between minimum and maximum among the points in perm[l..u].
     * This step makes this an optimized kd-tree.
     * We will reduce the time of build by finding the dimension of maximum spread in a sample of
     * the point set of size sqrt(N); the cost of partitioning after the sample is O(N).
     *
     * @param l    The lower bound to search.
     * @param u    The upper bound to search.
     * @param node The node that we are processing. This is useful for context.
     * @return The dimension with the largest difference between minimum and maximum among the points in perm[l..u]
     */
    public int findmaxspread(int l, int u, KDNode node)
    {
        // The original kd-tree alternates between dimensions at each level of the tree.
        return node.level % numberOfDimensions;
    }

    /**
     * The function px(i,j) accesses the j-th coordinate of point perm[i].
     *
     * @param i The i-th point from perm[i] to get the j-th coordinate for.
     * @param j The j-th coordinate of point perm[i].
     * @return The j-th coordinate of point perm[i].
     */
    public TDistance px(int i, int j)
    {
        return x(this.perm[i], j);
    }

    /**
     * The function x ( i , j ) accesses the j-th dimension of point i.
     *
     * @param i The i-th point to get the j-th coordinate for.
     * @param j The j-th coordinate of point i.
     * @return The j-th coordinate of point i.
     */
    public TDistance x(int i, int j)
    {
        return this.coordinateExtractor.extractDimensionalValue(this.points.get(i), j);
    }

    /**
     * This finds the nearest item in the index to the given item.
     *
     * @param pointIndex The index of the point to search for.
     * @return The nearest item to the given item.
     */
    public TItem searchNearest(int pointIndex)
    {
        int nearestIndex = nn(pointIndex);
        return this.points.get(this.perm[nearestIndex]);
    }


    /**
     * The target for the nearest neighbour search.
     */
    private int nntarget;

    /**
     * This is the nearest neighbour target item to search for.
     * This is assumed to usually be outside the indexed set of points.
     * This is our modification of the original algorithm to support this type of query.
     */
    private TItem nnTargetItem;

    /**
     * The nearest neighbour point number that has been found.
     */
    private int nnptnum;

    /**
     * The distance to the nearest neighbour.
     */
    private TDistance nndist;

    /**
     * The new global variable nndist2 represents the square of nndist.
     * For the common case of the Euclidean metric,
     * Sproull also observes that one need not compute the (expensive)
     * true distance to the nearest neighbor. Computing only
     * the square of the distance is clearly sufficient within a
     * bucket.
     */
    private TDistance nndist2;

    /**
     * The nn function in Program 2.2 computes the nearest
     * neighbor to a point. The external (global) variables are used
     * by the recursive procedure rnn, which does the work. At a
     * bucket node, rnn performs a sequential nearest neighbor
     * search. At an internal node, the search first proceeds down
     * the closer son, and then searches the farther son only if
     * necessary. (The function x ( i , j ) accesses the j-th dimension
     * of point i.) These functions are correct for any metric
     * in which the difference between point coordinates in any single
     * dimension does not exceed the metric distance; the Minkowski
     * L 1, L 2 and L inf. metrics all display this property.
     * @param j The index of the point to search for.
     * @return The index in perm of the nearest neighbour to the given point index.
     */
    public int nn(int j)
    {
        nntarget = j;
        nndist = maxDistance;
        rnn(root);
        return nnptnum;
    }

    private void rnn(KDNode<TDistance> p)
    {
        if (p.bucket)
        {
            for (int i = p.lopt; i <= p.hipt; i++)
            {
                TDistance thisdist = dist(perm[i], nntarget);
                //if (thisdist < nndist)
                if (distanceComparator.compare(thisdist,nndist) < 0)
                {
                    nndist = thisdist;
                    nnptnum = perm[i];
                }
            }
        }
        else
        {
            TDistance val = p.cutval;
            TDistance thisx = x(nntarget, p.cutdim);
            //if (thisx < val)
            if (distanceComparator.compare(thisx, val) < 0)
            {
                rnn(p.loson);
                //if (thisx + nndist > val)
                if (distanceComparator.compare(distanceAdder.performOperation(thisx,nndist), val) > 0)
                    rnn(p.hison);
            }
            else
            {
                rnn(p.hison);
                //if (thisx - nndist < val)
                if (distanceComparator.compare(distanceSubtractor.performOperation(thisx,nndist), val) < 0)
                    rnn(p.loson);
            }
        }
    }

    /**
     * This finds the nearest item in the index to the given item.
     * This is assumed that the given point is not necessarily one of the indexed points.
     * This is our modification of the original algorithm to support this type of query.
     * We try to keep the algorithm as close to the original as possible.
     *
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    public TItem searchNearest(TItem item)
    {
        int nearestIndex = nn_WithExternalTarget(item);
        return this.points.get(nearestIndex);
    }

    /**
     * The nn function in Program 2.2 computes the nearest
     * neighbor to a point. The external (global) variables are used
     * by the recursive procedure rnn, which does the work. At a
     * bucket node, rnn performs a sequential nearest neighbor
     * search. At an internal node, the search first proceeds down
     * the closer son, and then searches the farther son only if
     * necessary. (The function x ( i , j ) accesses the j-th dimension
     * of point i.) These functions are correct for any metric
     * in which the difference between point coordinates in any single
     * dimension does not exceed the metric distance; the Minkowski
     * L 1, L 2 and L inf. metrics all display this property.
     * @param nnTargetItem The target item to search for nearest neighbours to. This is assumed to be outside the indexed set.
     * @return The index in perm of the nearest neighbour to the given item.
     */
    public int nn_WithExternalTarget(TItem nnTargetItem)
    {
        this.nnTargetItem = nnTargetItem;
        nndist = maxDistance;
        rnn_WithExternalTarget(root);
        return nnptnum;
    }

    private void rnn_WithExternalTarget(KDNode<TDistance> p)
    {
        if (p.bucket)
        {
            for (int i = p.lopt; i <= p.hipt; i++)
            {
                //double thisdist = dist(perm[i], nntarget);
                TDistance thisdist = this.distanceMeasurer.measureDistanceBetween(this.points.get(this.perm[i]), this.nnTargetItem);
                //if (thisdist < nndist)
                if (distanceComparator.compare(thisdist, nndist) < 0)
                {
                    nndist = thisdist;
                    nnptnum = perm[i];
                }
            }
        }
        else
        {
            TDistance val = p.cutval;
            //double thisx = x(nntarget, p.cutdim);
            TDistance thisx = this.coordinateExtractor.extractDimensionalValue(nnTargetItem, p.cutdim);
            //if (thisx < val)
            if (distanceComparator.compare(thisx, val) < 0)
            {
                rnn_WithExternalTarget(p.loson);
                //if (thisx + nndist > val)
                if (distanceComparator.compare(distanceAdder.performOperation(thisx, nndist), val) > 0)
                    rnn_WithExternalTarget(p.hison);
            }
            else
            {
                rnn_WithExternalTarget(p.hison);
                // if (thisx - nndist < val)
                if (distanceComparator.compare(distanceSubtractor.performOperation(thisx, nndist), val) < 0)
                    rnn_WithExternalTarget(p.loson);
            }
        }
    }

    /**
     * The function dist( i , j) returns the distance from point i to point j.
     */
    private TDistance dist(int i, int j)
    {
        return this.distanceMeasurer.measureDistanceBetween(this.points.get(i), this.points.get(j));
    }

    /**
     * The function select permutes perm[l..u] such that perm[m] contains a point
     * that is not greater in the p->cutdim-th dimension than any point to its left,
     * and is similarly not less than the points to its right.
     * <p>
     * The bulk of the time of building a tree is now spent in the select function
     * (which uses a median-of-three partition, which is a sample of size 3).
     * We could reduce that time by using the selection algorithm of Floyd and Rivest [1975],
     * which uses a sample of size (roughly) sqrt(N) to find the median in (roughly) 3N/2 comparisons.
     * Instead, we will compute the true median of a sample of size sqrt(N) elements,
     * and partition around that value (which approximates the median of the set) in just N comparisons
     * <p>
     *
     * @param l The lower bound to select in.
     * @param u The upper bound to select in.
     * @param m The middle of the bound.
     * @param d The dimension to select.
     */
    public void select(int l, int u, int m, int d)
    {
        SELECT(perm, l, u, m, d, this::x);
    }

    /**
     * SELECT will rearrange the values of array segment X[L:R]
     * so that X[K] (for some given K; L <= K <= R) will contain the (K-L+1)-th smallest value,
     * L <= I <= K will imply X[I] <= X[K],
     * and K <= I <= R will imply X[I] >= X[K].
     * <p>
     * While SELECT is thus functionally equivalent to Hoare's algorothm FIND,
     * it is significantly faster on the average due to the effective use of sampling
     * to determine the element T about which to partition X.
     * <p>
     * The arbitrary constants 600, .5, and .5 appearing in the algorithm minimize execution time on the particular machine used.
     * <p>
     * SELECT has been shown to run in time asymptotically proportional to N + min(I,N-1),
     * where N = L - R + 1 and I = K - L + 1.
     * <p>
     * A lower bound on the running time within 9 percent of this value has also been proved.
     * <p>
     * This is taken from:
     * Algorithm 489: The algorithm SELECT—For finding the i th smallest of n elements [m1]
     * <p>
     * Floyd, Robert W., and Ronald L. Rivest. "Algorithm 489: The algorithm SELECT—For finding the i th smallest of n elements [m1]." Communications of the ACM 18.3 (1975): 173.
     *
     * @param X        The array of index pointers (to points) to rearrange the values in.
     * @param L        The lower bound to select in.
     * @param R        The upper bound to select in.
     * @param K        The kth item that we want to select.
     * @param d        The dimension to select.
     * @param XSampler This samples the point for the given index. The first argument is the index of the point to sample. The second argument is the dimension to sample.
     */
    public void SELECT(int[] X, int L, int R, int K, int d, BiFunction<Integer, Integer, TDistance> XSampler)
    {
        // Algorithm 489: The algorithm SELECT—For finding the i th smallest of n elements [m1]

        // Constants:
        final int THRESHOLD = 600;
        final double S_FACTOR = 0.5;
        final double SD_FACTOR = 0.5;

        // Variables:
        int N, I, J, S, SD, LL, RR;
        Number Z;
        TDistance T;

        // The algorithm:
        while (R > L)
        {
            if (R - L > THRESHOLD)
            {
                // Use SELECT recursively on a sample of size S
                // to get an estimate for the (K-L+1)-th smallest element into X[K],
                // biased slightly so that the (K-L+1)-th
                // element is expected to lie in the smaller set after partitioning.
                N = R - L + 1;
                I = K - L + 1;
                Z = Math.log(N);
                S = (int) (S_FACTOR * Math.exp(2 * Z.doubleValue() / 3));
                SD = (int) (SD_FACTOR * Math.sqrt(Z.doubleValue() * S * (N - S) / N) * Math.signum(I - N / 2));
                LL = Math.max(L, K - I * S / N + SD);
                RR = Math.min(R, K + (N - I) * S / N + SD);
                SELECT(X, LL, RR, K, d, XSampler);
            }
            T = XSampler.apply(X[K], d);
            // The following code partitions X[L : R] about T.
            // It is similar to PARTITION but will run faster on most
            // machines since subscript range checking on I and J has been eliminated.
            I = L;
            J = R;

            // exchange(X[L],X[K]);
            {
                int exchangeTemp = X[L];
                X[L] = X[K];
                X[K] = exchangeTemp;
            }

            //if (XSampler.apply(X[R], d) > T)
            if (distanceComparator.compare(XSampler.apply(X[R], d), T) > 0)
            {
                // exchange(X[R],X[L]);
                {
                    int exchangeTemp = X[R];
                    X[R] = X[L];
                    X[L] = exchangeTemp;
                }
            }

            while (I < J)
            {
                // exchange(X[I],X[J]);
                {
                    int exchangeTemp = X[I];
                    X[I] = X[J];
                    X[J] = exchangeTemp;
                }
                // I = I + 1; J = J - 1;
                I++;
                J--;
                //while (XSampler.apply(X[I], d) < T) I++;
                while (distanceComparator.compare(XSampler.apply(X[I], d), T) < 0) I++;
                //while (XSampler.apply(X[J], d) > T) J--;
                while (distanceComparator.compare(XSampler.apply(X[J], d), T) > 0) J--;
            }

            //if (XSampler.apply(X[L], d) == T)
            if (distanceComparator.compare(XSampler.apply(X[L], d), T) == 0)
            {
                // exchange(X[L],X[J]);
                {
                    int exchangeTemp = X[L];
                    X[L] = X[J];
                    X[J] = exchangeTemp;
                }
            }
            else
            {
                // J = J + 1;
                J++;
                // exchange(X[J],X[R]);
                {
                    int exchangeTemp = X[J];
                    X[J] = X[R];
                    X[R] = exchangeTemp;
                }
            }
            // Now adjust L, R so that they surround the subset containing the
            // (K-L+1)-th smallest element.
            if (J <= K) L = J + 1;
            if (K <= J) R = J - 1;
        }
    }

    /**
     * Adds the item to the index.
     * @param item The item to add.
     */
    public void add(TItem item)
    {
        this.points.add(item);
    }


    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        toString(stringBuilder);
        return stringBuilder.toString();
    }

    public void toString(StringBuilder stringBuilder)
    {
        // // Write the details:
        // stringBuilder.append("Nearest Distance: ");
        // stringBuilder.append(this.nndist);
        // stringBuilder.append(":\n");

        // Write out the content for this division:
        printNodeToStringRecursively(this.root, false, stringBuilder, "", "─");
    }

    /**
     * Prints out the current node recursively.
     *
     * @param node          The node to process.
     * @param hasSibling    True to flag that this node has a sibling that comes next. False to say that this node doesn't have a sibling that comes next.
     * @param stringBuilder The string builder to add to.
     * @param indent        The indent to prefix with.
     * @param branchName    L for Low and H for High.
     */
    private void printNodeToStringRecursively(KDNode<TDistance> node, boolean hasSibling, StringBuilder stringBuilder, String indent, String branchName)
    {
        // Check whether this is the root node:
        boolean isRootNode = node.level == 0;
        if (isRootNode)
        {
            // This is the root node.
            stringBuilder.append(">─");
        }
        else
        {
            // This is not a root node.

            // Add the indent:
            stringBuilder.append(indent);

            // Check whether this node has a sibling so that we can render the lines correctly:
            if (hasSibling)
            {
                // This node has another sibling that follows.

                // Flag that there are more siblings to come:
                stringBuilder.append("├─");
            }
            else
            {
                // This node does not have any more siblings to come.

                // Flag that this is the last node:
                stringBuilder.append("└─");
            }

        }

        // Render the children correctly:
        if (!node.bucket)
        {
            // The node has children.

            // Write the node name:
            stringBuilder.append(branchName);
            stringBuilder.append("─");
            stringBuilder.append(node.cutdim + 1);
            stringBuilder.append("D:");
            stringBuilder.append(node.cutval);

            // Create the new indent for this child:
            String nextIndent = indent + (hasSibling ? "│   " : "    ");

            // Create a new line:
            stringBuilder.append("\n");

            // Print out the left child recursively:
            printNodeToStringRecursively(node.loson, true, stringBuilder, nextIndent, "L");

            // Create a new line:
            stringBuilder.append("\n");

            // Print out the left child recursively:
            printNodeToStringRecursively(node.hison, false, stringBuilder, nextIndent, "H");
        }
        else
        {
            // This node is a leaf node.

            // Write the branch name:
            stringBuilder.append(branchName);
            stringBuilder.append("─");

            // Write all the points in this bucket:
            for (int i = node.lopt, j = 0; i <= node.hipt ; i++, j++)
            {
                if (j > 0) stringBuilder.append(", ");
                stringBuilder.append(points.get(perm[i]));
            }
        }
    }
}
