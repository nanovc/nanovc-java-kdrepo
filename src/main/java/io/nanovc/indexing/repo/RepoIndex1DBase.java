package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.Index1DBase;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;

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
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?, ?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>
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
     * This contains information for each division of the range for the repo index.
     * Each division corresponds to the grid based split of the range for this index {@link #getMinRange()} {@link #getMaxRange()}.
     */
    private final NavigableMap<Integer, Division<TItem, TContent, TArea>> divisionsByIndex = new TreeMap<>();

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
     * The content creator to use for getting content from the given item.
     */
    private final ContentCreator<TItem, TContent> contentCreator;

    /**
     * The content reader to use for getting an item from the given content.
     */
    private final ContentReader<TItem, TContent> contentReader;

    public RepoIndex1DBase(
        TItem minRange, TItem maxRange, int divisions,
        TMeasurer measurer, TDistanceComparator distanceComparator,
        TRangeSplitter rangeSplitter, TRangeFinder rangeFinder,
        TRepoHandler repoHandler, RepoPath rootRepoPath,
        ContentCreator<TItem, TContent> contentCreator, ContentReader<TItem, TContent> contentReader
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
        this.contentCreator = contentCreator;
        this.contentReader = contentReader;

        // Split the range:
        this.rangeSplits = new ArrayList<>(divisions);
        this.splitRange(minRange, maxRange, divisions, true, this.rangeSplits);
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
     *
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
     *
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

        // Get the division to add to:
        var division = getOrCreateDivision(divisionIndex);

        // Add the item in this division:
        addItemToDivision(item, division);
    }

    /**
     * Adds the given item to the specific division.
     *
     * @param item     The item to add.
     * @param division The specific division to add the item to.
     */
    protected void addItemToDivision(TItem item, Division<TItem, TContent, TArea> division)
    {
        // We use a trie based approach where the content byte representation is a path to the content
        // similar to how the git hash of the content gives us the address of the content,
        // thus making a content-addressable-file-system.

        // Get the content for the item:
        TContent itemContent = createContentForItem(item);

        // Get the byte representation of the content so that we can index it:
        ByteBuffer itemContentByteBuffer = itemContent.asByteBuffer();

        // Start walking the repo path until there are no entries:
        RepoPathNode currentNode = division.repoPathTree.getRootNode();
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
            RepoPathNode childNode = division.repoPathTree.getOrCreateChildNode(currentNode, nextPathName);

            // Get the repo path to the child node:
            RepoPath childRepoPath = childNode.getRepoPath();

            // Get the node to the child content:
            RepoPathNode childContentNode = division.repoPathTree.getOrCreateChildNode(childNode, CONTENT_PATH_NAME);

            // Get the repo path to the content for this child node:
            RepoPath childContentRepoPath = childContentNode.getRepoPath();

            // Check whether we have any content at this child node:
            TContent childContent = division.contentArea.getContent(childContentRepoPath);

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

            // Get the bytes for the current content:
            ByteBuffer currentContentByteBuffer = currentContent.asByteBuffer();

            // Check whether the item is the same as the current content:
            if (itemContentByteBuffer.limit() == currentContentByteBuffer.limit())
            {
                // The content length is the same.

                // There is a chance that the content is the same.

                // Save the current buffer offset, so we can set it back later:
                int previousPosition = itemContentByteBuffer.position();

                // Reset the position for us to check for equality:
                itemContentByteBuffer.position(0);

                // Check if the buffers are the same:
                if (itemContentByteBuffer.equals(currentContentByteBuffer))
                {
                    // The content is the same.
                    // Don't do anything further because this item is already indexed.
                    return;
                }

                // Set the current buffer position back:
                itemContentByteBuffer.position(previousPosition);
            }
            // Now we know that the item content is different to the current content.

            // Check whether the items content is shorter than the currently indexed content:
            if (itemContentByteBuffer.limit() < currentContentByteBuffer.limit())
            {
                // This item has smaller content than the current content.
                // This is a sign that we need to re-index.

                // Break out so that we can re-index.
                break;
            }
        }
        // Now we have found a place where we can add the content.

        // Flag whether the entire value for the item has been scanned:
        boolean hasEntireValueBeenScannedForItem = !itemContentByteBuffer.hasRemaining();

        // Check whether we can add new content or whether we are about to replace content:
        if (currentContent == null)
        {
            // We are adding new content.

            // Add the content to the current location:
            division.contentArea.putContent(currentContentRepoPath, itemContent);
        }
        else
        {
            // We are about to replace content.

            // Check whether we need to re-index sub content (if this item is shorter than the existing content):

            // We have content in the current location, and we have searched through the whole value for the index.
            // This means that we need to re-index this part of the tree because we want the shorter sub-sequence first.
            // We also know that all sub-content will strictly be longer or the same as the current content (because of our re-indexing criteria).

            // Create a list of all the nested child items that we need to re-index:
            List<TItem> itemsToReIndex = new ArrayList<>();

            // Start walking from the current node all the way down through the children so that we can re-index the descendants:
            division.repoPathTree.iterateAndRemoveEachDescendant(
                currentNode,
                (node)-> {
                    // Check whether this is a content node:
                    if (node.getName().equals(CONTENT_PATH_NAME))
                    {
                        // This is a content node.

                        // Get the path of the item to re-index:
                        RepoPath reIndexRepoPath = node.getRepoPath();

                        // Get the content for this node:
                        TContent reIndexItemContent = division.contentArea.getContent(reIndexRepoPath);

                        // Get the item that we need to re-index:
                        TItem itemToReIndex = readItemFromContent(reIndexItemContent);

                        // Add this to the list of items that need re-indexing:
                        itemsToReIndex.add(itemToReIndex);

                        // Remove the content for this item to make space for re-indexing:
                        division.contentArea.removeContent(reIndexRepoPath);
                    }
                }
            );
            // Now we have all the items that we need to re-index.

            // Replace the content for this path with the shorter item:
            currentContentNode = division.repoPathTree.addPath(currentContentRepoPath);
            currentNode = currentContentNode.getParent();
            division.contentArea.putContent(currentContentRepoPath, itemContent);

            // Recursively call this method with the item that needs re-indexing:
            for (TItem itemToReIndex : itemsToReIndex)
            {
                // Re-Index the item:
                addItemToDivision(itemToReIndex, division);
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
        // We search through the division where the queried item is found,
        // along with the neighbouring divisions around it.

        // Keep track of the best result so far:
        TItem bestItemSoFar = null;
        TDistance bestDistanceSoFar = null;

        // Find the index of the division in the range:
        int divisionIndex = findIndexInRange(this.minRange, this.maxRange, this.divisions, item);

        // Get the divisions to search (including neighbours):
        List<Division<TItem, TContent, TArea>> divisionsToSearch = new ArrayList<>(3);
        {
            // Search for the specific division:
            var division = getDivision(divisionIndex);
            if (division != null) divisionsToSearch.add(division);

            // Search for the lower neighbour:
            division = getNearestLowerDivision(divisionIndex);
            if (division != null) divisionsToSearch.add(division);

            // Search for the higher neighbour:
            division = getNearestHigherDivision(divisionIndex);
            if (division != null) divisionsToSearch.add(division);
        }
        // Now we have identified all the divisions we want to search through.

        // Check if we have any items:
        if (divisionsToSearch.size() == 0)
        {
            // There is nothing to search through.
            return null;
        }
        else
        {
            // We have divisions to search through.

            // Search through the division:
            for (Division<TItem, TContent, TArea> division : divisionsToSearch)
            {
                // Search for the item in this division:
                MeasuredItem<TItem, TDistance> measuredItem = searchNearestInDivision(item, division);

                // Check whether we found an item in this division:
                if (measuredItem != null)
                {
                    // We found an item in this division.

                    // Check for an exact match (no distance):
                    if (measuredItem.hasExactMatch())
                    {
                        // We got an exact match.
                        return measuredItem.item;
                    }

                    // Check whether this distance is the best so far:
                    if (bestDistanceSoFar == null || this.distanceComparator.compare(measuredItem.distance, bestDistanceSoFar) < 0)
                    {
                        // This item is closer.

                        // Flag this as the best item so far:
                        bestItemSoFar = measuredItem.item;
                        bestDistanceSoFar = measuredItem.distance;
                    }
                }
            }
            // Now we have searched through each division that we need to inspect.
        }

        return bestItemSoFar;
    }

    /**
     * Searches for the nearest item in the given division.
     * @param item The item to search for.
     * @param division The division to search in.
     * @return The nearest item in that division. Null if there is no item in this division.
     */
    protected MeasuredItem<TItem, TDistance> searchNearestInDivision(TItem item, Division<TItem, TContent, TArea> division)
    {
        // We use a trie based approach where the content byte representation is a path to the content
        // similar to how the git hash of the content gives us the address of the content,
        // thus making a content-addressable-file-system.

        // Get the content for the item:
        TContent itemContent = createContentForItem(item);

        // Get the byte representation of the content so that we can index it:
        ByteBuffer itemContentByteBuffer = itemContent.asByteBuffer();

        // Start walking the repo path until there are no entries:
        RepoPathNode currentNode = division.repoPathTree.getRootNode();
        while (itemContentByteBuffer.hasRemaining())
        {
            // Get the next byte from the item content:
            byte b = itemContentByteBuffer.get();

            // Get the next path name for the byte:
            String nextPathName = Character.toString(b);

            // Get the child nodes so we can explore them:
            NavigableMap<String, RepoPathNode> childrenByName = currentNode.getChildrenByName();

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
            RepoPathNode childContentNode = division.repoPathTree.getChildNode(childNode, CONTENT_PATH_NAME);

            // Check whether this child node has content:
            if (childContentNode != null)
            {
                // We have content at this node.

                // Get the path to the child content:
                RepoPath childContentRepoPath = childContentNode.getRepoPath();

                // Get the content for the child item:
                TContent childContent = division.contentArea.getContent(childContentRepoPath);

                // Get the item from the content:
                TItem indexedItem = readItemFromContent(childContent);

                // Check whether the existing item is equal to the item:
                if (item.equals(indexedItem))
                {
                    // This item is equal.

                    // Create the measured item:
                    MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();
                    measuredItem.item = indexedItem;

                    return measuredItem;
                }
                // Now we know that the items are not equal.
            }

            // Move to the child node:
            currentNode = childNode;
        }
        // Now we have searched the whole space and not found an exact match.

        // We only did a partial search of the item, and we didn't find a match.
        // This means that we will need to broaden our search to all items in this division.

        // Scan all the items:
        return searchDivisionWithFullScan(item, division);
    }

    /**
     * Searches for the nearest item by doing a full scan of the global item map.
     * WARNING: Slow performance.
     * @param itemToSearchFor The item to search for.
     * @param division        The division to search through.
     * @return The nearest item. Null if there are no items in this division.
     */
    protected MeasuredItem<TItem, TDistance> searchDivisionWithFullScan(TItem itemToSearchFor, Division<TItem, TContent, TArea> division)
    {
        // Keep track of the best result so far:
        TItem bestItemSoFar = null;
        TDistance bestDistanceSoFar = null;

        // Go through all content in this division:
        for (AreaEntry<TContent> entry : division.contentArea)
        {
            // Get the path for the entry:
            RepoPath repoPath = entry.getPath();

            // Check whether this is content for items:
            if (repoPath.path.endsWith(CONTENT_PATH_NAME))
            {
                // This is content for an item.

                // Get the content for this item:
                TContent itemContent = entry.getContent();

                // Get the item from this content:
                TItem item = readItemFromContent(itemContent);

                // Check whether the existing item is equal to the item:
                if (item.equals(itemToSearchFor))
                {
                    // This item is equal.

                    // Create the measured item:
                    MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();
                    measuredItem.item = item;

                    return measuredItem;
                }
                // Now we know that the items are not equal.

                // Get the distance to the item:
                TDistance distance = measureDistanceBetween(item, itemToSearchFor);

                // Check whether this distance is the best so far:
                if (bestDistanceSoFar == null || this.distanceComparator.compare(distance, bestDistanceSoFar) < 0)
                {
                    // This item is closer.

                    // Flag this as the best item so far:
                    bestItemSoFar = item;
                    bestDistanceSoFar = distance;
                }
            }
        }

        // Check whether we found an item:
        if (bestItemSoFar != null)
        {
            // Create the measured item:
            MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();
            measuredItem.item = bestItemSoFar;
            measuredItem.distance = bestDistanceSoFar;

            return measuredItem;
        }
        else
        {
            // We didn't find an item.
            return null;
        }
    }

    /**
     * Searches for the nearest item by doing a full scan of the global item map.
     * WARNING: Slow performance.
     * @param itemToSearchFor The item to search for.
     * @return The nearest item. Null if there are no items in this division.
     */
    protected MeasuredItem<TItem, TDistance> searchIndexWithFullScan(TItem itemToSearchFor)
    {
        // Keep track of the best result so far:
        TItem bestItemSoFar = null;
        TDistance bestDistanceSoFar = null;

        // Search through each division:
        for (Division<TItem, TContent, TArea> division : this.divisionsByIndex.values())
        {
            // Go through all content in this division:
            for (AreaEntry<TContent> entry : division.contentArea)
            {
                // Get the path for the entry:
                RepoPath repoPath = entry.getPath();

                // Check whether this is content for items:
                if (repoPath.path.endsWith(CONTENT_PATH_NAME))
                {
                    // This is content for an item.

                    // Get the content for this item:
                    TContent itemContent = entry.getContent();

                    // Get the item from this content:
                    TItem item = readItemFromContent(itemContent);

                    // Check whether the existing item is equal to the item:
                    if (item.equals(itemToSearchFor))
                    {
                        // This item is equal.

                        // Create the measured item:
                        MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();
                        measuredItem.item = item;

                        return measuredItem;
                    }
                    // Now we know that the items are not equal.

                    // Get the distance to the item:
                    TDistance distance = measureDistanceBetween(item, itemToSearchFor);

                    // Check whether this distance is the best so far:
                    if (bestDistanceSoFar == null || this.distanceComparator.compare(distance, bestDistanceSoFar) < 0)
                    {
                        // This item is closer.

                        // Flag this as the best item so far:
                        bestItemSoFar = item;
                        bestDistanceSoFar = distance;
                    }
                }
            }
        }

        // Check whether we found an item:
        if (bestItemSoFar != null)
        {
            // Create the measured item:
            MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();
            measuredItem.item = bestItemSoFar;
            measuredItem.distance = bestDistanceSoFar;

            return measuredItem;
        }
        else
        {
            // We didn't find an item.
            return null;
        }
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
     *
     * @param divisionIndex The index of the division that the item is in.
     * @param itemKey       The global item key for the item.
     * @return The repo path for the given item.
     */
    protected RepoPath getRepoPathForItem(int divisionIndex, int itemKey)
    {
        return this.getRootRepoPath().resolve(Integer.toString(divisionIndex)).resolve("items").resolve(Integer.toString(itemKey));
    }

    /**
     * Creates the content for the given item.
     *
     * @param item The item that we need to create the content for.
     * @return The content for the given item.
     */
    protected TContent createContentForItem(TItem item)
    {
        return this.getContentCreator().createContentForItem(item);
    }

    /**
     * Reads the item key from the given content.
     *
     * @param content The content to read the item key out of.
     * @return The item key for the given content.
     */
    protected TItem readItemFromContent(TContent content)
    {
        return this.getContentReader().readItemFromContent(content);
    }

    /**
     * Measures the distance between the two items.
     *
     * @param item1 The first item to measure the distance between.
     * @param item2 The second item to measure the distance between.
     * @return The distance between the two items.
     */
    protected TDistance measureDistanceBetween(TItem item1, TItem item2)
    {
        return this.measurer.measureDistanceBetween(item1, item2);
    }

    /**
     * Gets the division for the given index.
     *
     * @param index The index of the division that we want to get the division for.
     * @return The division for the specific index. Null if there is no division at that index yet.
     */
    protected Division<TItem, TContent, TArea> getDivision(int index)
    {
        return this.divisionsByIndex.get(index);
    }

    /**
     * Gets or creates the division for the given index.
     *
     * @param index The index of the division that we want to get the division for.
     * @return The division for the specific index.
     */
    protected Division<TItem, TContent, TArea> getOrCreateDivision(int index)
    {
        return this.divisionsByIndex.computeIfAbsent(
            index, _unused ->
            {
                var division = new Division<TItem, TContent, TArea>();
                division.divisionIndex = index;
                division.minRange = this.getDivisionMinRange(index);
                division.maxRange = this.getDivisionMaxRange(index);
                division.repoPathTree = new RepoPathTree();
                division.contentArea = this.repoHandler.createArea();
                return division;
            }
        );
    }


    /**
     * Gets the next nearest division that is lower than the given index.
     *
     * @param index The index of the division that we want to get the division for.
     * @return The nearest division that is lower than the specific index. Null if there is no division lower than that index.
     */
    protected Division<TItem, TContent, TArea> getNearestLowerDivision(int index)
    {
        // Find the next lower entry:
        Map.Entry<Integer, Division<TItem, TContent, TArea>> entry = this.divisionsByIndex.lowerEntry(index);
        return entry == null ? null : entry.getValue();
    }

    /**
     * Gets the next nearest division that is higher than the given index.
     *
     * @param index The index of the division that we want to get the division for.
     * @return The nearest division that is higher than the specific index. Null if there is no division higher than that index.
     */
    protected Division<TItem, TContent, TArea> getNearestHigherDivision(int index)
    {
        // Find the next higher entry:
        Map.Entry<Integer, Division<TItem, TContent, TArea>> entry = this.divisionsByIndex.higherEntry(index);
        return entry == null ? null : entry.getValue();
    }

    @Override public String toString()
    {
        // Print out the content tree:
        StringBuilder stringBuilder = new StringBuilder();

        // Print out details of the index:
        stringBuilder.append("Index: ");
        stringBuilder.append(" from ");
        stringBuilder.append(this.getMinRange());
        stringBuilder.append(" to ");
        stringBuilder.append(this.getMaxRange());
        stringBuilder.append(" with ");
        stringBuilder.append(this.getDivisions());
        stringBuilder.append(" division");
        if (this.getDivisions() != 1) stringBuilder.append("s");
        stringBuilder.append(":");

        // Go through each division:
        for (Division<TItem, TContent, TArea> division : this.divisionsByIndex.values())
        {
            // Write the division details:
            division.toString(stringBuilder);
        }

        return stringBuilder.toString();
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
     * Gets the content creator to use.
     *
     * @return The content creator to use.
     */
    @Override
    public ContentCreator<TItem, TContent> getContentCreator()
    {
        return this.contentCreator;
    }

    /**
     * Gets the content reader to use for getting an item from the given content.
     *
     * @return The content reader to use for getting an item from the given content.
     */
    @Override
    public ContentReader<TItem, TContent> getContentReader()
    {
        return this.contentReader;
    }
}
