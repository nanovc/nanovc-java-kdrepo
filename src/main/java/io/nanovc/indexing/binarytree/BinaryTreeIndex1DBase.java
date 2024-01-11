package io.nanovc.indexing.binarytree;

import io.nanovc.indexing.Index1DBase;
import io.nanovc.indexing.Measurer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A base class for a one dimensional {@link BinaryTreeIndex1D}.
 * <p>
 * <a href="https://en.wikipedia.org/wiki/K-d_tree">K-D Tree Wiki</a>
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TItemComparator>     The comparator that compares items.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 */
public abstract class BinaryTreeIndex1DBase<
    TItem,
    TItemComparator extends Comparator<TItem>,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>
    >
    extends Index1DBase<TItem>
    implements BinaryTreeIndex1D<TItem, TItemComparator, TDistance, TMeasurer, TDistanceComparator>
{
    /**
     * The comparator that compares items.
     * This is needed, so we know whether to go left or right down the binary tree.
     */
    private final TItemComparator itemComparator;

    /**
     * The measurer that measures distances between items.
     */
    private final TMeasurer measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final TDistanceComparator distanceComparator;

    /**
     * The root node of this binary tree.
     */
    private final Node root = new Node();

    public BinaryTreeIndex1DBase(TItemComparator itemComparator, TMeasurer measurer, TDistanceComparator distanceComparator)
    {
        this.itemComparator = itemComparator;
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
    }

    /**
     * Adds the given item to the index.
     *
     * @param item The item to add to the index.
     */
    public void add(TItem item)
    {
        addRecursively(item, this.root);
    }

    /**
     * Adds the given item recursively to the tree.
     *
     * @param item        The item to add.
     * @param currentNode The current node that we are on.
     */
    private void addRecursively(TItem item, Node currentNode)
    {
        // Check whether we have an item at this node:
        if (currentNode.item == null)
        {
            // We have not added an item to this node yet.

            // Add this item:
            currentNode.item = item;
        }
        else
        {
            // We have an item at this node.

            // Do the comparison of the item to the current node:
            int itemComparison = this.itemComparator.compare(item, currentNode.item);

            // Check whether the item is to the left or right of this node:
            if (itemComparison == 0)
            {
                // The item is the same as the current item.

                // Check whether we need to make space to store multiple items at this node:
                if (currentNode.moreItems == null)
                {
                    // We need to make space to store more items:
                    currentNode.moreItems = new ArrayList<>();
                }

                // Add the additional item at this node:
                currentNode.moreItems.add(item);
            }
            else if (itemComparison < 0)
            {
                // The item is less than or equal to the current node.

                // Check whether we need to make a node to the left:
                if (currentNode.left == null)
                {
                    // Create a new node:
                    currentNode.left = new Node();
                    currentNode.left.depth = currentNode.depth + 1;
                }

                // Add the item recursively:
                addRecursively(item, currentNode.left);
            }
            else
            {
                // The item is greater than the current node.

                // Check whether we need to make a node to the right:
                if (currentNode.right == null)
                {
                    // Create a new node:
                    currentNode.right = new Node();
                    currentNode.right.depth = currentNode.depth + 1;
                }

                // Add the item recursively:
                addRecursively(item, currentNode.right);
            }
        }
    }

    /**
     * Indexes the items that have been added.
     * This is a pre-computation step that needs to be called before we search for nearest neighbours.
     */
    @Override public void index()
    {
        // Do nothing for this index.
    }

    /**
     * This finds the nearest item in the index to the given item.
     *
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    public TItem searchNearest(TItem item)
    {
        // Create a new search context:
        SearchContext searchContext = new SearchContext();

        // Keep track of the closest item:
        searchNearestRecursively(item, this.root, searchContext);

        return searchContext.nearestNode.item;
    }

    /**
     * This finds the nearest item in the index to the given item recursively.
     *
     * @param item The item to search for.
     * @param currentNode The current node that we are on.
     * @param searchContext The search context that we update as we search.
     */
    private void searchNearestRecursively(TItem item, Node currentNode, SearchContext searchContext)
    {
        // Make sure we have an item for the current node:
        if (currentNode == null || currentNode.item == null) return;
        // Now we know we have an item for the current node.

        // Compare the item to the current node:
        int itemComparison = Integer.signum(this.itemComparator.compare(item, currentNode.item));

        // Act depending on what result we get for the comparison:
        switch (itemComparison)
        {
            case 0 ->
            {
                // Flag this as an exact match:
                searchContext.isExactMatch = true;

                // Flag this as the nearest item:
                searchContext.nearestNode = currentNode;

                // Stop searching further:
                return;
            }
            case -1 ->
            {
                // The item is to the left of the current node.
                searchNearestRecursively(item, currentNode.left, searchContext);
            }
            case 1 ->
            {
                // The item is to the right of the current node.
                searchNearestRecursively(item, currentNode.right, searchContext);
            }
        }
        // If we get here then we have walked left or right in the binary tree recursively.

        // Check whether we got an exact match, so that we can short circuit:
        if (searchContext.isExactMatch)
        {
            // We have an exact match in the recursive call.
            // So we can short circuit:
            return;
        }
        // Now we know we don't have an exact match if we get here.

        // Get the distance of the item to the current node:
        TDistance distance = this.measurer.measureDistanceBetween(item, currentNode.item);

        // Check whether this is the first distance we are measuring:
        if ( (searchContext.shortestDistance == null) || (distanceComparator.compare(distance, searchContext.shortestDistance) <= 0) )
        {
            // Flag this as the best distance so far:
            searchContext.shortestDistance = distance;
            searchContext.nearestNode = currentNode;
        }
    }

    /**
     * A node in the binary tree index.
     */
    class Node
    {
        /**
         * The depth of the tree, where the root is at depth 0.
         */
        int depth = 0;

        /**
         * The item at this node.
         */
        TItem item;

        /**
         * This is a list of more items if there is more than one item with the same value at this node.
         * If this is null then there is only one {@link #item}.
         * If this is populated, then the total number of items is moreItems plus {@link #item}.
         * We do not instantiate this list unless we actually have a need for multiple items with the same value.
         */
        List<TItem> moreItems;

        /**
         * The next node to the left of the tree.
         */
        Node left;

        /**
         * The next node to the right of the tree.
         */
        Node right;
    }

    /**
     * The context that contains information during a search.
     */
    class SearchContext
    {
        /**
         * Flags that we have an exact match, so we don't need to search more.
         */
        public boolean isExactMatch;

        /**
         * The shortest distance so far.
         */
        TDistance shortestDistance;

        /**
         * The nearest node so far.
         */
        Node nearestNode;

    }
}
