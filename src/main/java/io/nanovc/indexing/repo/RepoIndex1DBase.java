package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.Index1DBase;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;
import io.nanovc.indexing.hierarchicalgrid.SubGridSupplier;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * A base class for a one dimensional {@link RepoIndex1D}.
 *
 * @param <TItem>               The specific type of data that the index is for.
 * @param <TDistance>           The type for the distance between the items.
 * @param <TMeasurer>           The type for the measurer that can measure the distance between items.
 * @param <TDistanceComparator> The comparator that compares distances between items.
 * @param <TRangeSplitter>      The type for the range splitter that we need to use.
 * @param <TRangeFinder>        The type for finding the index of an item in the divisions of a range.
 * @param <TContent>            The specific type of content that the repo commits.
 * @param <TArea>               The specific type of content area that the repo commits.
 * @param <TCommit>             The specific type of commit that the repo creates.
 * @param <TRepoHandler>        The specific type of repo handler to use for this index.
 */
public abstract class RepoIndex1DBase<
    TItem,
    TDistance,
    TMeasurer extends Measurer<TItem, TDistance>,
    TDistanceComparator extends Comparator<TDistance>,
    TRangeSplitter extends RangeSplitter<TItem>,
    TRangeFinder extends RangeFinder<TItem>,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?,?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>
    >
    extends Index1DBase<TItem>
    implements RepoIndex1D<TItem, TDistance, TMeasurer, TDistanceComparator, TRangeSplitter, TRangeFinder, TContent, TArea, TCommit, TRepoHandler>
{
    /**
     * The path name for content.
     * Yes, it's an emoji. This ensures that we don't get clashes with the trie-byte splitting for the keys. And it looks cool.
     */
    public final static String CONTENT_PATH_NAME = "📄";

    /**
     * The path name for content ID.
     * Yes, it's an emoji. This ensures that we don't get clashes with the trie-byte splitting for the keys. And it looks cool.
     */
    public final static String ID_PATH_NAME = "🔑";

    /**
     * The minimum range of this index.
     */
    private final TItem minRange;

    /**
     * The maximum range of this index.
     */
    private final TItem maxRange;

    /**
     * The number of divisions to use for this grid index.
     */
    private final int divisions;

    /**
     * The measurer that measures distances between items.
     */
    private final TMeasurer measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final TDistanceComparator distanceComparator;

    /**
     * The range splitter that divides the range into a set of divisions.
     */
    private final TRangeSplitter rangeSplitter;

    /**
     * The range finder that gets the index of an item in the divisions of a range.
     */
    private final TRangeFinder rangeFinder;

    /**
     * The splits for the range.
     */
    private final List<TItem> rangeSplits;

    /**
     * The repo handler to use for this repo index.
     */
    private final TRepoHandler repoHandler;

    /**
     * The repo path to the root of this repo index where we update the index information.
     */
    private final RepoPath rootRepoPath;

    /**
     * The global map of items that we reference.
     * This is the common map that gives us the index of the item, no matter what level of the grid we are in.
     */
    private final ItemGlobalMap<TItem> itemGlobalMap;

    /**
     * The content creator to use for getting content from the given item.
     */
    private final ContentCreator<TItem, TContent> contentCreator;

    /**
     * The content creator to use for getting content from the given item key.
     */
    private final ContentCreator<Integer, TContent> itemKeyContentCreator;

    /**
     * The content reader to use for getting an item key from the given content.
     */
    private final ContentReader<Integer, TContent> itemKeyContentReader;

    /**
     * The current content area that we are adding content to.
     */
    private TArea currentContentArea;

    /**
     * The tree of repo paths that we have indexed.
     */
    private final RepoPathTree repoPathTree = new RepoPathTree();

    public RepoIndex1DBase(
        TItem minRange, TItem maxRange, int divisions,
        TMeasurer measurer, TDistanceComparator distanceComparator,
        TRangeSplitter rangeSplitter, TRangeFinder rangeFinder,
        TRepoHandler repoHandler, RepoPath rootRepoPath,
        ItemGlobalMap<TItem> itemGlobalMap,
        ContentCreator<TItem, TContent> contentCreator,
        ContentCreator<Integer, TContent> itemKeyContentCreator, ContentReader<Integer, TContent> itemKeyContentReader
        )
    {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.divisions = divisions;
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
        this.rangeSplitter = rangeSplitter;
        this.rangeFinder = rangeFinder;
        this.repoHandler = repoHandler;
        this.rootRepoPath = rootRepoPath;
        this.itemGlobalMap = itemGlobalMap;
        this.contentCreator = contentCreator;
        this.itemKeyContentCreator = itemKeyContentCreator;
        this.itemKeyContentReader = itemKeyContentReader;

        // Split the range:
        this.rangeSplits = new ArrayList<>(divisions);
        this.splitRange(minRange, maxRange, divisions, true, this.rangeSplits);

        // Create the content area that we are adding content to:
        this.currentContentArea = this.repoHandler.createArea();
    }

    /**
     * This splits the range given by the two items into the given number of divisions.
     *
     * @param minRange                   The item at the minimum range to measure the distance from.
     * @param maxRange                   The item at the maximum range to measure the distance to.
     * @param divisions                  The number of divisions to split the range into.
     * @param includeExtraRightMostSplit True to include an extra item in the list (1 more than the requested number of divisions) to represent the right most edge of the range. If this is false then we only have the left edges, leading up to but not including the right part of the range.
     * @param splitsToAddTo              The collection of splits to add to while we split the range.
     */
    protected void splitRange(TItem minRange, TItem maxRange, int divisions, boolean includeExtraRightMostSplit, List<TItem> splitsToAddTo)
    {
        this.rangeSplitter.splitRange(minRange, maxRange, divisions, includeExtraRightMostSplit, splitsToAddTo);
    }

    /**
     * Gets the lower range of the given division.
     * @param divisionIndex The division that we want to get the lower range value of.
     * @return The lower range of the given division.
     */
    protected TItem getDivisionMinRange(int divisionIndex)
    {
        // Make sure we are within our bounds:
        divisionIndex = Math.max(0, Math.min(this.rangeSplits.size() - 1, divisionIndex));

        // Get the division:
        return this.rangeSplits.get(divisionIndex);
    }

    /**
     * Gets the upper range of the given division.
     * @param divisionIndex The division that we want to get the lower range value of.
     * @return The lower range of the given division.
     */
    protected TItem getDivisionMaxRange(int divisionIndex)
    {
        // Get the next division:
        int nextDivisionIndex = divisionIndex + 1;

        // Make sure we are within our bounds:
        nextDivisionIndex = Math.max(0, Math.min(this.rangeSplits.size() - 1, nextDivisionIndex));

        // Get the division:
        return this.rangeSplits.get(nextDivisionIndex);
    }

    /**
     * Adds the given item to the index.
     *
     * @param item The item to add to the index.
     */
    public void add(TItem item)
    {
        // We divide the search space into divisions so that we can perform range searches in them.
        // We use branches in the repo for each division.
        // This allows us to get grid-like decomposition of the search space when we don't find an exact match.

        // Inside each branch region of the search space (branch),
        // we use a trie based approach where the content byte representation is a path to the content
        // similar to how the git hash of the content gives us the address of the content,
        // thus making a content-addressable-file-system.

        // Find the index of the division in the range:
        int divisionIndex = findIndexInRange(this.minRange, this.maxRange, this.divisions, item);

        // Add the item in this division:
        addItemToIndex(item, divisionIndex);
    }

    /**
     * Adds the given item to the specific division index.
     *
     * @param item          The item to add.
     * @param divisionIndex The specific division index to add the item to.
     */
    protected void addItemToIndex(TItem item, int divisionIndex)
    {
        // We use a trie based approach where the content byte representation is a path to the content
        // similar to how the git hash of the content gives us the address of the content,
        // thus making a content-addressable-file-system.

        // Get the content for the item:
        TContent itemContent = createContentForItem(item);

        // Get the byte representation of the content so that we can index it:
        ByteBuffer itemContentByteBuffer = itemContent.asByteBuffer();

        // Start walking the repo path until there are no entries:
        RepoPathNode currentNode = this.repoPathTree.getRootNode();
        RepoPath currentRepoPath = null;
        RepoPathNode currentContentNode = null;
        RepoPath currentContentRepoPath = null;
        TContent currentContent = null;
        while (itemContentByteBuffer.hasRemaining())
        {
            // Get the next byte from the item content:
            byte b = itemContentByteBuffer.get();

            // Get the next path name for the byte:
            String nextPathName = Character.toString(b);

            // Get the next path node for this value:
            RepoPathNode childNode = this.repoPathTree.getOrCreateChildNode(currentNode, nextPathName);

            // Get the repo path to the child node:
            RepoPath childRepoPath = childNode.getRepoPath();

            // Get the node to the child content:
            RepoPathNode childContentNode = this.repoPathTree.getOrCreateChildNode(childNode, CONTENT_PATH_NAME);

            // Get the repo path to the content for this child node:
            RepoPath childContentRepoPath = childContentNode.getRepoPath();

            // Check whether we have any content at this child node:
            TContent childContent = this.currentContentArea.getContent(childContentRepoPath);

            // Move to the child node:
            currentNode = childNode;
            currentRepoPath = childRepoPath;
            currentContentNode = childContentNode;
            currentContentRepoPath = childContentRepoPath;
            currentContent = childContent;

            // Check whether we can break out and index the content here:
            if (currentContent == null)
            {
                // We don't have any child content at this path yet.

                // Break out early because we can add the content here.
                break;
            }
        }
        // Now we have found a place where we can add the content.

        // Add the item to the global map:
        int itemKey = this.getItemGlobalMap().add(item);

        // Create the content for the item key:
        TContent itemKeyContent = createContentForItemKey(itemKey);

        // Get the path for the item key:
        RepoPathNode itemKeyNode = this.repoPathTree.getOrCreateChildNode(currentContentNode, ID_PATH_NAME);
        RepoPath itemKeyRepoPath = itemKeyNode.getRepoPath();

        // Add the ID for the current item:
        this.currentContentArea.putContent(itemKeyRepoPath, itemKeyContent);

        // Add the content to the current location:
        this.currentContentArea.putContent(currentContentRepoPath, itemContent);
    }

    /**
     * This finds the nearest item in the index to the given item.
     *
     * @param item The item to search for.
     * @return The nearest item to the given item.
     */
    public TItem searchNearest(TItem item)
    {
        // We use a trie based approach where the content byte representation is a path to the content
        // similar to how the git hash of the content gives us the address of the content,
        // thus making a content-addressable-file-system.

        // Get the content for the item:
        TContent itemContent = createContentForItem(item);

        // Get the byte representation of the content so that we can index it:
        ByteBuffer itemContentByteBuffer = itemContent.asByteBuffer();

        // Create a list of additional nodes to search if we don't get an exact match:
        Deque<RepoPathNode> additionalNodesToSearch = new LinkedList<>();

        // Keep track of the best result so far:
        TItem bestItemSoFar = null;
        TDistance bestDistanceSoFar = null;

        // Start walking the repo path until there are no entries:
        RepoPathNode currentNode = this.repoPathTree.getRootNode();
        while (itemContentByteBuffer.hasRemaining())
        {
            // Get the next byte from the item content:
            byte b = itemContentByteBuffer.get();

            // Get the next path name for the byte:
            String nextPathName = Character.toString(b);

            // Get the child nodes so we can explore them:
            NavigableMap<String, RepoPathNode> childrenByName = currentNode.getChildrenByName();

            // Try to find the entry smaller than the given one so that we can save additional nodes to explore later if we don't find an exact match:
            Map.Entry<String, RepoPathNode> lowerEntry = childrenByName.lowerEntry(nextPathName);
            if (lowerEntry != null)
            {
                // We found the lower entry.
                // Add this as a node to search:
                additionalNodesToSearch.addFirst(lowerEntry.getValue());
            }

            // Try to find the entry larger than the given one:
            Map.Entry<String, RepoPathNode> higherEntry = childrenByName.higherEntry(nextPathName);
            if (higherEntry != null)
            {
                // We found the higher entry.
                // Add this as a node to search:
                additionalNodesToSearch.addFirst(higherEntry.getValue());
            }

            // Get the next path node for this value:
            RepoPathNode childNode = childrenByName.get(nextPathName);

            // Check whether the child node exists:
            if (childNode == null)
            {
                // There is no child node. This means that we have exhausted the indexed search space for this specific item.

                // Break out because there is nothing more to search down this path.
                break;
            }
            // Now we know that we have the child node.

            // Get the node to the child content:
            RepoPathNode childContentNode = this.repoPathTree.getChildNode(childNode, CONTENT_PATH_NAME);

            // Check whether this child node has content:
            if (childContentNode != null)
            {
                // We have content at this node.

                // Get the path for the item key:
                RepoPathNode itemKeyNode = this.repoPathTree.getChildNode(childContentNode, ID_PATH_NAME);
                RepoPath itemKeyRepoPath = itemKeyNode.getRepoPath();

                // Get the content for the item key:
                TContent itemKeyContent = this.currentContentArea.getContent(itemKeyRepoPath);

                // Get the item key for this content:
                int itemKey = readItemKeyFromContent(itemKeyContent);

                // Get the item from the global map:
                TItem indexedItem = this.getItemGlobalMap().getItem(itemKey);

                // Check whether the existing item is equal to the item:
                if (item.equals(indexedItem))
                {
                    // This item is equal.
                    return indexedItem;
                }
                // Now we know that the items are not equal.

                // Get the distance to the item:
                TDistance distance = measureDistanceBetween(item, indexedItem);

                // Check whether this distance is the best so far:
                if (bestDistanceSoFar == null || this.distanceComparator.compare(distance, bestDistanceSoFar) < 0)
                {
                    // This item is closer.

                    // Flag this as the best item so far:
                    bestItemSoFar = indexedItem;
                    bestDistanceSoFar = distance;
                }
            }

            // Move to the child node:
            currentNode = childNode;
        }
        // Now we have searched the whole space and not found an exact match.

        // Check if we have additional nodes to search:
        if (additionalNodesToSearch != null)
        {
            // We have additional nodes to search.

            // Get the next node to search:
            RepoPathNode nodeToSearch = additionalNodesToSearch.pollFirst();

            // Traverse the additional nodes to search:
            while (nodeToSearch != null)
            {
                // Get the node to the content:
                RepoPathNode contentNode = this.repoPathTree.getChildNode(nodeToSearch, CONTENT_PATH_NAME);

                // Check whether this node has content:
                if (contentNode != null)
                {
                    // We have content at this node.

                    // Get the path for the item key:
                    RepoPathNode itemKeyNode = this.repoPathTree.getChildNode(contentNode, ID_PATH_NAME);
                    RepoPath itemKeyRepoPath = itemKeyNode.getRepoPath();

                    // Get the content for the item key:
                    TContent itemKeyContent = this.currentContentArea.getContent(itemKeyRepoPath);

                    // Get the item key for this content:
                    int itemKey = readItemKeyFromContent(itemKeyContent);

                    // Get the item from the global map:
                    TItem indexedItem = this.getItemGlobalMap().getItem(itemKey);

                    // Check whether the existing item is equal to the item:
                    if (item.equals(indexedItem))
                    {
                        // This item is equal.
                        return indexedItem;
                    }
                    // Now we know that the items are not equal.

                    // Get the distance to the item:
                    TDistance distance = measureDistanceBetween(item, indexedItem);

                    // Check whether this distance is the best so far:
                    if (bestDistanceSoFar == null || this.distanceComparator.compare(distance, bestDistanceSoFar) < 0)
                    {
                        // This item is closer.

                        // Flag this as the best item so far:
                        bestItemSoFar = indexedItem;
                        bestDistanceSoFar = distance;
                    }

                    // Add all the children to be searched:
                    for (RepoPathNode childToSearch : nodeToSearch.getChildrenByName().values())
                    {
                        // Check whether this is the content node:
                        if (childToSearch.getName().equals(CONTENT_PATH_NAME)) continue;
                        // Now we know that this is not the content path.

                        // Add the child to search:
                        additionalNodesToSearch.addLast(childToSearch);
                    }
                }

                // Get the next node to search:
                nodeToSearch = additionalNodesToSearch.pollFirst();
            }
        }
        // Now we have the best item that is closest to the given item.
        return bestItemSoFar;
    }


    /**
     * This finds the division index for an item in a range that is defined by two other items.
     *
     * @param minRange          The item at the minimum range to measure the distance from.
     * @param maxRange          The item at the maximum range to measure the distance to.
     * @param divisions         The number of divisions to split the range into.
     * @param itemToFindInRange The item to find the index of for the given range.
     * @return The index of the division of the given item in the range specified.
     */
    protected int findIndexInRange(TItem minRange, TItem maxRange, int divisions, TItem itemToFindInRange)
    {
        return this.rangeFinder.findIndexInRange(minRange, maxRange, divisions, itemToFindInRange);
    }

    /**
     * Gets the repo path for the given item details.
     * @param divisionIndex The index of the division that the item is in.
     * @param itemKey The global item key for the item.
     * @return The repo path for the given item.
     */
    protected RepoPath getRepoPathForItem(int divisionIndex, int itemKey)
    {
        return this.getRootRepoPath().resolve(Integer.toString(divisionIndex)).resolve("items").resolve(Integer.toString(itemKey));
    }

    /**
     * Creates the content for the given item.
     * @param item The item that we need to create the content for.
     * @return The content for the given item.
     */
    protected TContent createContentForItem(TItem item)
    {
        return this.getContentCreator().createContentForItem(item);
    }

    /**
     * Creates the content for the given item key.
     * @param itemKey The key of the item that we want to create the content for.
     * @return The content for the given item key.
     */
    protected TContent createContentForItemKey(int itemKey)
    {
        return this.getItemKeyContentCreator().createContentForItem(itemKey);
    }

    /**
     * Reads the item key from the given content.
     * @param content The content to read the item key out of.
     * @return The item key for the given content.
     */
    protected int readItemKeyFromContent(TContent content)
    {
        return this.getItemKeyContentReader().readItemFromContent(content);
    }

    /**
     * Measures the distance between the two items.
     * @param item1 The first item to measure the distance between.
     * @param item2 The second item to measure the distance between.
     * @return The distance between the two items.
     */
    protected TDistance measureDistanceBetween(TItem item1, TItem item2)
    {
        return this.measurer.measureDistanceBetween(item1, item2);
    }

    @Override public String toString()
    {
        // Check whether there is too much content to render:
        if (this.currentContentArea.getContentStream().count() > 1_000) return "Too Large to Render";
        // Now we know that we have small enough content to render.

        // Print out the content tree:
        StringBuilder sb = new StringBuilder();
        printNodeToStringRecursively(this.repoPathTree.getRootNode(), false, sb, "");
        return sb.toString();
    }

    /**
     * Prints out the current node recursively.
     *
     * @param node          The node to process.
     * @param hasSibling    True to flag that this node has a sibling that comes next. False to say that this node doesn't have a sibling that comes next.
     * @param stringBuilder The string builder to add to.
     * @param indent        The indent to prefix with.
     */
    private void printNodeToStringRecursively(RepoPathNode node, boolean hasSibling, StringBuilder stringBuilder, String indent)
    {
        // Check whether this is the root node:
        boolean isRootNode = node.isRootNode();
        if (isRootNode)
        {
            // This is the root node.
            stringBuilder.append(".");
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

            // Write the node name:
            stringBuilder.append("──");
            stringBuilder.append(node.getName());

            // Get the content for the node:
            TContent nodeContent = this.currentContentArea.getContent(node.getRepoPath());

            // Check whether we actually have content at this path:
            if (nodeContent != null)
            {
                // Write the node content:
                stringBuilder.append(nodeContent.toString());
            }
        }

        // Render the children correctly:
        if (node.hasChildren())
        {
            // The node has children.

            // Write each child:
            NavigableMap<String, RepoPathNode> childrenByName = node.getChildrenByName();
            for (Map.Entry<String, RepoPathNode> entry : childrenByName.entrySet())
            {
                // Get the values:
                String childName = entry.getKey();
                RepoPathNode childNode = entry.getValue();

                // Create a new line:
                stringBuilder.append("\n");

                // Check whether the child has another sibling:
                String childSiblingName = childrenByName.higherKey(childName);
                boolean hasChildSibling = childSiblingName != null;

                // Create the new indent for this child:
                String nextIndent = indent + (hasSibling ? "│   " : (isRootNode ? "" : "    "));

                // Print out the child recursively:
                printNodeToStringRecursively(childNode, hasChildSibling, stringBuilder, nextIndent);
            }
        }
        else
        {
            // This node is a leaf node.
        }
    }

    /**
     * Gets the minimum range of this index.
     *
     * @return The minimum range of this index.
     */
    @Override
    public TItem getMinRange()
    {
        return this.minRange;
    }

    /**
     * Gets the maximum range of this index.
     *
     * @return The maximum range of this index.
     */
    @Override
    public TItem getMaxRange()
    {
        return this.maxRange;
    }

    /**
     * Gets the number of divisions to use for this grid index.
     *
     * @return The number of divisions to use for this grid index.
     */
    @Override
    public int getDivisions()
    {
        return this.divisions;
    }

    /**
     * Gets the measurer that measures distances between items.
     *
     * @return The measurer that measures distances between items.
     */
    @Override
    public TMeasurer getMeasurer()
    {
        return this.measurer;
    }

    /**
     * Gets the comparator to use for comparing distances of items.
     *
     * @return The comparator to use for comparing distances of items.
     */
    @Override
    public TDistanceComparator getDistanceComparator()
    {
        return this.distanceComparator;
    }

    /**
     * Gets the range splitter that divides the range into a set of divisions.
     *
     * @return The range splitter that divides the range into a set of divisions.
     */
    @Override
    public TRangeSplitter getRangeSplitter()
    {
        return this.rangeSplitter;
    }

    /**
     * Gets the range finder that gets the index of an item in the divisions of a range.
     *
     * @return The range finder that gets the index of an item in the divisions of a range.
     */
    @Override
    public TRangeFinder getRangeFinder()
    {
        return this.rangeFinder;
    }

    /**
     * Gets the repo handler to use for this repo index.
     *
     * @return The repo handler to use for this repo index.
     */
    @Override public TRepoHandler getRepoHandler()
    {
        return this.repoHandler;
    }

    /**
     * Gets the repo path to the root of this repo index where we update the index information.
     *
     * @return The repo path to the root of this repo index where we update the index information.
     */
    @Override public RepoPath getRootRepoPath()
    {
        return this.rootRepoPath;
    }

    /**
     * The global map of items that we reference.
     * This is the common map that gives us the index of the item, no matter what level of the grid we are in.
     *
     * @return The global map of items that we reference.
     */
    @Override public ItemGlobalMap<TItem> getItemGlobalMap()
    {
        return this.itemGlobalMap;
    }

    /**
     * Gets the content creator to use.
     *
     * @return The content creator to use.
     */
    @Override public ContentCreator<TItem, TContent> getContentCreator()
    {
        return this.contentCreator;
    }

    /**
     * Gets the content creator to use for getting content from the given item key.
     *
     * @return The content creator to use for getting content from the given item key.
     */
    @Override public ContentCreator<Integer, TContent> getItemKeyContentCreator()
    {
        return this.itemKeyContentCreator;
    }

    /**
     * Gets the content reader to use for getting an item key from the given content.
     *
     * @return The content reader to use for getting an item key from the given content.
     */
    @Override public ContentReader<Integer, TContent> getItemKeyContentReader()
    {
        return this.itemKeyContentReader;
    }
}
