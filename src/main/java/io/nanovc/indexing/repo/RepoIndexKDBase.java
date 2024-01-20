package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.*;
import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;
import io.nanovc.indexing.repo.ranges.RangeSplit;
import io.nanovc.indexing.repo.ranges.RangeSplitInclusion;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * A base class for a k-dimensional {@link RepoIndexKD}.
 *
 * @param <TItem>        The specific type of data that the index is for.
 * @param <TDistance>    The type for the distance between the items.
 * @param <TContent>     The specific type of content that the repo commits.
 * @param <TArea>        The specific type of content area that the repo commits.
 * @param <TCommit>      The specific type of commit that the repo creates.
 * @param <TRepoHandler> The specific type of repo handler to use for this index.
 */
public abstract class RepoIndexKDBase<
    TItem,
    TDistance,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>,
    TCommit extends CommitAPI,
    TRepoHandler extends RepoHandlerAPI<TContent, TArea, TCommit, ? extends SearchQueryAPI<TCommit>, ? extends SearchResultsAPI<?, ?>, ? extends RepoAPI<TContent, TArea, TCommit>, ? extends RepoEngineAPI<TContent, TArea, TCommit, ?, ?, ?>>
    >
    extends IndexKDBase<TItem>
    implements RepoIndexKD<TItem, TDistance, TContent, TArea, TCommit, TRepoHandler>
{
    /**
     * The path name for content.
     * Yes, it's an emoji. This ensures that we don't get clashes with the trie-byte splitting for the keys. And it looks cool.
     */
    public final static String CONTENT_PATH_NAME = "📄";

    /**
     * The path name for bucket items.
     * Yes, it's an emoji. It looks cool and we can.
     */
    public final static String BUCKET_ITEMS_PATH_NAME = "📁";

    /**
     * The number of dimensions that this index supports.
     * This is defined by the type of content of TItem that is being indexed.
     */
    private final int numberOfDimensions;

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
     * This is the maximum number of items that we want to keep in a bucket before we split the range further.
     */
    private final int bucketThreshold = 1;

    /**
     * This contains information for each division of the range for the repo index.
     * Each division corresponds to the grid based split of the range for this index {@link #getMinRange()} {@link #getMaxRange()}.
     */
    private final NavigableMap<Integer, Division<TItem, TContent, TArea>> divisionsByIndex = new TreeMap<>();

    /**
     * This is used for extracting specific dimensional values from an item.
     */
    private final Extractor<TItem> extractor;

    /**
     * The measurer that measures distances between items.
     */
    private final Measurer<TItem, TDistance> measurer;

    /**
     * The comparator to use for comparing distances of items.
     */
    private final Comparator<TDistance> distanceComparator;

    /**
     * The range splitter that divides the range into a set of divisions.
     */
    private final RangeSplitter<TItem> rangeSplitter;

    /**
     * The range finder that gets the index of an item in the divisions of a range.
     */
    private final RangeFinder<TItem> rangeFinder;

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

    /**
     * The operator for adding distances together.
     */
    private final Operator<TDistance> distanceAdder;

    /**
     * The operator for subtracting distances.
     */
    private final Operator<TDistance> distanceSubtractor;

    /**
     * The maximum value for the distance to start out with.
     * This is used to initialise the best distance so far when looking for nearest neighbours.
     */
    private final TDistance maxDistance;

    /**
     * The definition of the hyper cube that defines the dimensions for this index.
     */
    protected final HyperCubeDefinition hyperCubeDefinition;

    /**
     * This is the cube of divisions that slices up the {@link HyperCubeDefinition} into smaller divisions.
     * This is also what defines the branches that we have because it's one branch for each {@link DivisionCell} in this {@link DivisionCube}.
     */
    protected DivisionCube<TContent, TArea> divisionCube;

    public RepoIndexKDBase(
        HyperCubeDefinition hyperCubeDefinition,
        int numberOfDimensions,
        TItem minRange, TItem maxRange, int divisions,
        Extractor<TItem> extractor, Measurer<TItem, TDistance> measurer, Comparator<TDistance> distanceComparator,
        Operator<TDistance> distanceAdder, Operator<TDistance> distanceSubtractor, TDistance maxDistance,
        RangeSplitter<TItem> rangeSplitter, RangeFinder<TItem> rangeFinder,
        TRepoHandler repoHandler, RepoPath rootRepoPath,
        ContentCreator<TItem, TContent> contentCreator, ContentReader<TItem, TContent> contentReader
    )
    {
        this.hyperCubeDefinition = hyperCubeDefinition;
        this.numberOfDimensions = numberOfDimensions;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.divisions = divisions;
        this.extractor = extractor;
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
        this.distanceAdder = distanceAdder;
        this.distanceSubtractor = distanceSubtractor;
        this.maxDistance = maxDistance;
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

        // Inside each division of the search space (branch),
        // we use a simple list in each bucket if the number of items is small.

        // TODO:
        // As the number of items in each bucket grows beyond a certain point,
        // we use a trie based approach where the content byte representation is a path to the content
        // similar to how the git hash of the content gives us the address of the content,
        // thus making a content-addressable-file-system.

        // Get the coordinate of the given item:
        HyperCoord itemCoord = extractItemCoordinate(item, hyperCubeDefinition);

        // Find the right division for this item at the given coordinate:
        DivisionCell<TContent, TArea> divisionCell = getOrCreateDivisionCell(itemCoord);

        // Make sure that we have a root node for this division cell:
        if (divisionCell.kdTreeRoot == null)
        {
            // We don't have a root yet.

            // Create the root node:
            KDBucketNode<TContent, TArea> root = new KDBucketNode<>();

            // Save the reference to the division cell for this node:
            root.divisionCell = divisionCell;

            // Create the hyper cube for this bucket:
            root.hyperCube = divisionCell.hyperCube;

            // Set the content path root:
            root.repoPathNode = divisionCell.repoPathTree.getRootNode();

            // Define the path where bucket items go:
            root.bucketItemsRepoPathNode = divisionCell.repoPathTree.getOrCreateChildNode(root.repoPathNode, BUCKET_ITEMS_PATH_NAME);

            // Save the root node:
            divisionCell.kdTreeRoot = root;
        }

        // Index the item recursively:
        divisionCell.kdTreeRoot = addItemToKDNode(item, itemCoord, divisionCell.kdTreeRoot);


        // // Find the index of the division in the range:
        // int divisionIndex = findIndexInRange(this.minRange, this.maxRange, this.divisions, item);
        //
        // // Get the division to add to:
        // var division = getOrCreateDivision(divisionIndex);
        //
        // // Add the item in this division:
        // addItemToDivisionTrieApproach(item, division);

    }

    /**
     * This walks the {@link #divisionCube} and gets or creates the {@link DivisionCell} that we need for the given coordinate.
     *
     * @param itemCoord The coordinate of the item that we want to find the cell of.
     * @return The {@link DivisionCell} that corresponds to the given {@link HyperCoord hyper coordinate}.
     */
    protected DivisionCell<TContent, TArea> getOrCreateDivisionCell(HyperCoord itemCoord)
    {
        // Check whether we need to create the division cube:
        if (this.divisionCube == null)
        {
            // This is the first time we are adding.
            // We need to create the division cube.

            // Create the division cube:
            this.divisionCube = new DivisionCube<>();

            // Set the extents of the division cube:
            this.divisionCube.hyperCube = this.hyperCubeDefinition.createHyperCube();

            // Create the root dimension:
            //noinspection UnnecessaryLocalVariable
            DivisionDimension<TContent, TArea> rootDimension = createDivisionDimension(0, null);

            // Save this as the root dimension for our division cube:
            this.divisionCube.rootDimension = rootDimension;
        }

        // Walk the tree recursively until we find the division cell:
        return getOrCreateDivisionCellRecursively(itemCoord, this.divisionCube.rootDimension);
    }

    /**
     * Creates the {@link DivisionDimension} with the given parameters.
     *
     * @param dimensionIndexToCreate    The dimensionIndex that we are creating.
     * @param previousDivisionDimension The previous division dimension that we are coming from. Null if this is the first dimension.
     * @return The {@link DivisionDimension} that was created.
     */
    private DivisionDimension<TContent, TArea> createDivisionDimension(int dimensionIndexToCreate, DivisionDimension<TContent, TArea> previousDivisionDimension)
    {
        // Check whether we have more than one division dimension so that we know what type of node to create:
        int dimensionCount = this.hyperCubeDefinition.getDimensionCount();
        int lastDimensionIndex = dimensionCount - 1;

        // Create the root dimension:
        DivisionDimension<TContent, TArea> divisionDimension;
        if (dimensionIndexToCreate == lastDimensionIndex)
        {
            // This is the last dimension index:
            DivisionDimension.Last<TContent, TArea> last = new DivisionDimension.Last<>();
            divisionDimension = last;

            // Save the previous division dimension that we came from:
            last.previousDivisionDimension = previousDivisionDimension;

            // Check if this is just one dimension:
            if (dimensionIndexToCreate == 0)
            {
                // This is the first and last dimension.
                // Set the division cube that we came from:
                last.divisionCube = this.divisionCube;
            }
            else
            {
                // We have more than one dimension.
                // Set the division cube that we came from:
                last.divisionCube = previousDivisionDimension.divisionCube;
            }

            // Set the hyper cube:
            last.hyperCube = last.divisionCube.hyperCube;
        }
        else if (dimensionIndexToCreate == 0)
        {
            // This is the first dimension.
            DivisionDimension.First<TContent, TArea> first = new DivisionDimension.First<>();
            divisionDimension = first;

            // Set the division cube that we came from:
            first.divisionCube = this.divisionCube;

            // Use the same hyper cube as the division cube:
            first.hyperCube = first.divisionCube.hyperCube;
        }
        else if (dimensionIndexToCreate < lastDimensionIndex)
        {
            // This is an intermediate dimension.
            DivisionDimension.Intermediate<TContent, TArea> intermediate = new DivisionDimension.Intermediate<>();
            divisionDimension = intermediate;

            // Save the previous division dimension that we came from:
            intermediate.previousDivisionDimension = previousDivisionDimension;

            // Set the division cube that we came from:
            intermediate.divisionCube = previousDivisionDimension.divisionCube;
        }
        else throw new IllegalArgumentException("Dimension Index is out of bounds");

        // Set the dimension that this is for:
        divisionDimension.dimension = this.hyperCubeDefinition.getDimension(dimensionIndexToCreate);

        // Create the list for range splits to be the same size as the number of divisions that we want:
        // NOTE: Depending on the smallest steps size for the dimension, it might be more or less than that.
        divisionDimension.splits = new ArrayList<>(this.divisions);

        // Work out the range splits for this dimension:
        divisionDimension.dimension.calculateRangeSplitsForDimension(this.divisions, divisionDimension.splits);
        // NOTE: The number of splits MIGHT be different to what was requested because of the smallest step size for the dimension.
        //       Therefore, it's important to use the size of the splits array to know what index is appropriate.

        return divisionDimension;
    }

    /**
     * This walks the {@link #divisionCube} and gets or creates the {@link DivisionCell} that we need for the given coordinate.
     *
     * @param itemCoord The coordinate of the item that we want to find the cell of.
     * @return The {@link DivisionCell} that corresponds to the given {@link HyperCoord hyper coordinate}.
     */
    private DivisionCell<TContent, TArea> getOrCreateDivisionCellRecursively(HyperCoord itemCoord, DivisionDimension<TContent, TArea> currentDimensionNode)
    {
        // Get the dimensionIndex that we are processing:
        int dimensionIndex = currentDimensionNode.dimension.getDimensionIndex();

        // Get the value of the coordinate for this dimension:
        Object value = itemCoord.getValue(dimensionIndex);

        // Find the range split that this coordinate falls into:
        for (int divisionIndex = 0; divisionIndex < currentDimensionNode.splits.size(); divisionIndex++)
        {
            // Get the range for this split:
            Range<Object> splitRange = currentDimensionNode.splits.get(divisionIndex);

            // Check whether the coordinate is in this range:
            if (currentDimensionNode.dimension.getRangeCalculator().isInRange(value, splitRange))
            {
                // This item is in this split range.

                // Check where we are in the dimension chain to decide how to walk next:
                switch (currentDimensionNode)
                {
                    case DivisionDimension.First<TContent, TArea> firstDivisionDimension ->
                    {
                        // Make sure we have the next dimension to walk down:
                        DivisionDimension<TContent, TArea> nextDivisionDimension = firstDivisionDimension.nextDivisionDimensionByIndex.get(divisionIndex);
                        if (nextDivisionDimension == null)
                        {
                            // Create the next dimension:
                            nextDivisionDimension = createDivisionDimension(dimensionIndex + 1, firstDivisionDimension);

                            // Set the hyper cube for this split range:
                            nextDivisionDimension.hyperCube = firstDivisionDimension.hyperCube.createHyperCubeWithChangedRange(dimensionIndex, splitRange);

                            // Index the next dimension:
                            firstDivisionDimension.nextDivisionDimensionByIndex.put(divisionIndex, nextDivisionDimension);
                        }

                        // Walk to the next dimension until we find the division cell:
                        return getOrCreateDivisionCellRecursively(itemCoord, nextDivisionDimension);
                    }
                    case DivisionDimension.Intermediate<TContent, TArea> intermediateDivisionDimension ->
                    {
                        // Make sure we have the next dimension to walk down:
                        DivisionDimension<TContent, TArea> nextDivisionDimension = intermediateDivisionDimension.nextDivisionDimensionByIndex.get(divisionIndex);
                        if (nextDivisionDimension == null)
                        {
                            // Create the next dimension:
                            nextDivisionDimension = createDivisionDimension(dimensionIndex + 1, intermediateDivisionDimension);

                            // Set the hyper cube for this split range:
                            nextDivisionDimension.hyperCube = intermediateDivisionDimension.hyperCube.createHyperCubeWithChangedRange(dimensionIndex, splitRange);

                            // Index the next dimension:
                            intermediateDivisionDimension.nextDivisionDimensionByIndex.put(divisionIndex, nextDivisionDimension);
                        }

                        // Walk to the next dimension until we find the division cell:
                        return getOrCreateDivisionCellRecursively(itemCoord, nextDivisionDimension);
                    }
                    case DivisionDimension.Last<TContent, TArea> lastDivisionDimension ->
                    {
                        // We are at the last dimension.

                        // Search for the division cell:
                        //noinspection unchecked
                        DivisionCell<TContent, TArea> divisionCell = (DivisionCell<TContent, TArea>) lastDivisionDimension.cellsByIndex.get(divisionIndex);
                        if (divisionCell == null)
                        {
                            // This is the first time we are accessing this division cell.

                            // Create the division cell:
                            divisionCell = new DivisionCell<>();
                            lastDivisionDimension.cellsByIndex.put(divisionIndex, divisionCell);
                            divisionCell.parentDimension = lastDivisionDimension;
                            divisionCell.hyperCube = lastDivisionDimension.hyperCube.createHyperCubeWithChangedRange(
                                lastDivisionDimension.dimension.getDimensionIndex(),
                                splitRange
                            );

                            // Create the content area:
                            //noinspection UnnecessaryLocalVariable
                            TArea contentArea = getRepoHandler().createArea();
                            divisionCell.contentArea = contentArea;

                            // Initialise the repo path tree:
                            //noinspection UnnecessaryLocalVariable
                            RepoPathTree repoPathTree = new RepoPathTree();
                            divisionCell.repoPathTree = repoPathTree;

                            // Define the branch name for this cell:
                            divisionCell.branchName = divisionCell.hyperCube.toString();

                            // Register this division cell with the division cube:
                            divisionCell.parentDimension.divisionCube.cellsByBranchName.put(divisionCell.branchName, divisionCell);

                        }
                        // Now we have the division cell.
                        return divisionCell;
                    }
                }
            }
        }
        // If we get here then the coordinate was not in any of the ranges.
        return null;
    }

    /**
     * Extracts the coordinate for this item.
     *
     * @param item                The item to get the coordinate of.
     * @param hyperCubeDefinition The definition of the hyper cube that we need a coordinate for.
     * @return The coordinate of the given item in the hyper cube.
     */
    protected HyperCoord extractItemCoordinate(TItem item, HyperCubeDefinition hyperCubeDefinition)
    {
        // Get the number of dimensions:
        int dimensionCount = hyperCubeDefinition.getDimensionCount();

        // Create the coordinate:
        Object[] coords = new Object[dimensionCount];
        for (int dimIndex = 0; dimIndex < dimensionCount; dimIndex++)
        {
            // Get the value at this coordinate:
            Object value = this.extractor.extractDimensionalValue(item, dimIndex);

            // Add this to the coordinate:
            coords[dimIndex] = value;
        }
        return new HyperCoord(coords);
    }

    /**
     * Recursively adds the given item at the coordinate by adding to or building the KD-Tree.
     *
     * @param item      The item to add.
     * @param itemCoord The coordinate of the item.
     * @param node      The node that we are walking.
     * @return The replacement node to use in place of the inputted one. This is in case the node is replaced with another one.
     */
    protected KDNode<TContent, TArea> addItemToKDNode(TItem item, HyperCoord itemCoord, KDNode<TContent, TArea> node)
    {
        // Check what type of node this is:
        switch (node)
        {
            case KDBucketNode<TContent, TArea> bucketNode ->
            {
                // Get the dimension index that we want to index by:
                int dimensionIndex = bucketNode.level % this.hyperCubeDefinition.getDimensionCount();
                Dimension<Object> dimension = this.hyperCubeDefinition.getDimension(dimensionIndex);

                // Get the range calculator so that we can work out new ranges:
                RangeCalculator<Object> rangeCalculator = dimension.getRangeCalculator();

                // Get the range of the bucket that we came from:
                Range<Object> bucketRange = bucketNode.hyperCube.getRangeForDimension(dimensionIndex);

                // Work out the value to split the dimension range in:
                Object midPoint = rangeCalculator.midPoint(bucketRange);

                // Check whether we have exceeded our bucket threshold:
                if (bucketNode.contentMap.size() == this.bucketThreshold && rangeCalculator.canSplitRange(bucketRange, midPoint))
                {
                    // We have exceeded the bucket size for this node.

                    // Create a new intermediate node:
                    KDIntermediateNode<Object, TContent, TArea> newNode = new KDIntermediateNode<>();
                    newNode.parent = bucketNode.parent;
                    newNode.divisionCell = bucketNode.divisionCell;
                    newNode.level = bucketNode.level;
                    newNode.hyperCube = bucketNode.hyperCube;

                    // Set the dimension index that we want to index by:
                    newNode.dimension = dimension;

                    // Work out the value to split the dimension range in:
                    newNode.cutValue = midPoint;

                    // Work out the range split:
                    newNode.rangeSplit = rangeCalculator.splitRange(bucketRange, newNode.cutValue, RangeSplitInclusion.Lower);

                    // Work out the new path for this node:
                    String newNodeName = newNode.dimension.getName() + ":" + newNode.cutValue.toString();
                    newNode.repoPathNode = newNode.divisionCell.repoPathTree.getOrCreateChildNode(bucketNode.repoPathNode, newNodeName);

                    // Keep track of the node to return as we iterate recursively:
                    KDNode<TContent, TArea> nodeToReturn = newNode;

                    // Recursively update the new node with the items in the bucket:
                    for (Map.Entry<RepoPathNode, TContent> entry : bucketNode.contentMap.entrySet())
                    {
                        // Get the content:
                        TContent bucketItemContent = entry.getValue();

                        // Get the item from the content:
                        TItem bucketItem = readItemFromContent(bucketItemContent);

                        // Get the repo path for the item:
                        RepoPathNode bucketItemRepoPathNode = entry.getKey();
                        RepoPath bucketItemRepoPath = bucketItemRepoPathNode.getRepoPath();

                        // Remove the item from the content area:
                        bucketNode.divisionCell.contentArea.removeContent(bucketItemRepoPath);

                        // Get the repo path tree that we can use for navigating:
                        RepoPathTree repoPathTree = bucketNode.divisionCell.repoPathTree;

                        // Remove the item from the repo path tree:
                        repoPathTree.removeFromParent(bucketItemRepoPathNode);

                        // Remove the bucket path if it is empty:
                        repoPathTree.removeIfHasNoChildren(bucketItemRepoPathNode.getParent());

                        // Get the coordinate for this item:
                        HyperCoord bucketItemCoord = extractItemCoordinate(bucketItem, this.hyperCubeDefinition);

                        // Recursively add the bucket item:
                        nodeToReturn = addItemToKDNode(bucketItem, bucketItemCoord, nodeToReturn);
                    }

                    // Recursively update the new node with the items that was added (which triggered this re-indexing):
                    nodeToReturn = addItemToKDNode(item, itemCoord, nodeToReturn);

                    // Replace the previous node with the new node:
                    return nodeToReturn;
                }
                else
                {
                    // We are still within the allowed bucket threshold.

                    // Get the content for the item:
                    TContent itemContent = createContentForItem(item);

                    // Get the index of the item in this bucket:
                    int itemIndex = bucketNode.contentMap.size();

                    // Get the repo path for the content:
                    RepoPathNode itemRepoPathNode = bucketNode.divisionCell.repoPathTree.getOrCreateChildNode(bucketNode.bucketItemsRepoPathNode, Integer.toString(itemIndex));
                    RepoPath itemRepoPath = itemRepoPathNode.getRepoPath();

                    // Add the content to our content map:
                    bucketNode.contentMap.put(itemRepoPathNode, itemContent);

                    // Add the content to the content area:
                    bucketNode.divisionCell.contentArea.putContent(itemRepoPath, itemContent);

                    // Leave the node as it was:
                    return bucketNode;
                }
            }
            case KDIntermediateNode<?, TContent, TArea> intermediateNodeUntyped ->
            {
                //noinspection unchecked
                KDIntermediateNode<Object, TContent, TArea> intermediateNode = (KDIntermediateNode<Object, TContent, TArea>) intermediateNodeUntyped;

                // Get the dimension that we are splitting by:
                Dimension<Object> dimension = intermediateNode.dimension;

                // Get the split for this node:
                RangeSplit<Object> rangeSplit = intermediateNode.rangeSplit;

                // Get the range calculator:
                RangeCalculator<Object> rangeCalculator = dimension.getRangeCalculator();

                // Get the coordinate for the item:
                Object value = itemCoord.getValue(intermediateNode.dimension);

                // Check whether to index the item in the lower branch:
                if (rangeCalculator.isInRange(value, rangeSplit.lower()))
                {
                    // This item belongs in the lower range.

                    // Check whether we need to create the node for the lower range:
                    if (intermediateNode.lowerNode == null)
                    {
                        // This is the first time we are walking down the lower node.

                        // Create the child node for the lower range:
                        KDBucketNode<TContent, TArea> lowerNode = new KDBucketNode<>();
                        intermediateNode.lowerNode = lowerNode;
                        lowerNode.level = intermediateNode.level + 1;
                        lowerNode.parent = intermediateNode;
                        lowerNode.divisionCell = intermediateNode.divisionCell;
                        lowerNode.hyperCube = intermediateNode.hyperCube.createHyperCubeWithChangedRange(dimension.getDimensionIndex(), intermediateNode.rangeSplit.lower());
                        lowerNode.repoPathNode = lowerNode.divisionCell.repoPathTree.getOrCreateChildNode(intermediateNode.repoPathNode, "<");
                        lowerNode.bucketItemsRepoPathNode = lowerNode.divisionCell.repoPathTree.getOrCreateChildNode(lowerNode.repoPathNode, BUCKET_ITEMS_PATH_NAME);
                    }

                    // Recursively add the item to that branch:
                    intermediateNode.lowerNode = addItemToKDNode(item, itemCoord, intermediateNode.lowerNode);
                }

                // Check whether to index the item in the higher branch:
                if (rangeCalculator.isInRange(value, rangeSplit.higher()))
                {
                    // This item belongs in the higher range.

                    // Check whether we need to create the node for the higher range:
                    if (intermediateNode.higherNode == null)
                    {
                        // This is the first time we are walking down the higher node.

                        // Create the child node for the higher range:
                        KDBucketNode<TContent, TArea> higherNode = new KDBucketNode<>();
                        intermediateNode.higherNode = higherNode;
                        higherNode.level = intermediateNode.level + 1;
                        higherNode.parent = intermediateNode;
                        higherNode.divisionCell = intermediateNode.divisionCell;
                        higherNode.hyperCube = intermediateNode.hyperCube.createHyperCubeWithChangedRange(dimension.getDimensionIndex(), intermediateNode.rangeSplit.higher());
                        higherNode.repoPathNode = higherNode.divisionCell.repoPathTree.getOrCreateChildNode(intermediateNode.repoPathNode, ">");
                        higherNode.bucketItemsRepoPathNode = higherNode.divisionCell.repoPathTree.getOrCreateChildNode(higherNode.repoPathNode, BUCKET_ITEMS_PATH_NAME);
                    }

                    // Recursively add the item to that branch:
                    intermediateNode.higherNode = addItemToKDNode(item, itemCoord, intermediateNode.higherNode);
                }

                // Return the intermediate node unchanged:
                return intermediateNode;
            }
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
    }

    /**
     * Adds the given item to the specific division.
     * It uses a trie to encode the content.
     *
     * @param item     The item to add.
     * @param division The specific division to add the item to.
     */
    protected void addItemToDivisionTrieApproach(TItem item, Division<TItem, TContent, TArea> division)
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
                (node) -> {
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
                addItemToDivisionTrieApproach(itemToReIndex, division);
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
        // Get the coordinate of the given item:
        HyperCoord itemCoordinate = extractItemCoordinate(item, hyperCubeDefinition);

        // Create the list of division cells to search:
        List<DivisionCell<TContent, TArea>> divisionCellsToSearch = new ArrayList<>();
        {
            // Find the right division for this item at the given coordinate:
            DivisionCell<TContent, TArea> divisionCell = getOrCreateDivisionCell(itemCoordinate);

            // Check whether we found the division cell:
            if (divisionCell == null)
            {
                // We are not within the extents that we have indexed.

                // Search ALL the division cells that we have:
                divisionCellsToSearch.addAll(this.divisionCube.cellsByBranchName.values());
            }
            else
            {
                // We know that the item is in one of the divisions for the cube.

                // Add the specific division cell that we are in:
                divisionCellsToSearch.add(divisionCell);

                // Get all the neighbours of the specific division cell so that we can search those:
                // TODO
            }
        }
        // Now we know all the division cells that we want to search.

        // Search each division cell and find the nearest item:
        MeasuredItem<TItem, TDistance> bestResult = null;
        for (DivisionCell<TContent, TArea> divisionCellToSearch : divisionCellsToSearch)
        {
            // Search for the item in this division cell:
            MeasuredItem<TItem, TDistance> measuredItem = null;

            // Check whether we even want to search this division cell or if it's too far away:
            if (bestResult == null)
            {
                // We don't have a best result so far, so we can just perform the search.

                // Search within the division cell:
                measuredItem = searchNearestInDivisionCell(item, itemCoordinate, divisionCellToSearch);
            }
            else
            {
                // We do have a best result so far.

                // Check whether the extents of the hyper-cube for the division cell are within our best measurement so far:
                {
                    // Search each of the dimensions:
                    boolean isWithinRange = true;
                    for (int dimensionIndex = 0; dimensionIndex < this.hyperCubeDefinition.getDimensionCount(); dimensionIndex++)
                    {
                        // Get the dimension:
                        Dimension<Object> dimension = this.hyperCubeDefinition.getDimension(dimensionIndex);

                        // Get the coordinate in this dimension:
                        Object coordOfItemInDimension = itemCoordinate.getValue(dimensionIndex);
                        Object coordOfNearestInDimension = bestResult.coordinate.getValue(dimensionIndex);

                        // Get the range of this divisions cell for this dimension:
                        Range<Object> dimensionRange = divisionCellToSearch.hyperCube.getRangeForDimension(dimensionIndex);

                        // Check whether the point we are searching for is within the best distance of this range:
                        if (!dimension.getRangeCalculator().isWithinDistanceOfRange(coordOfItemInDimension, coordOfNearestInDimension, true, dimensionRange))
                        {
                            // This division cell is not within range of the nearest distance to the item for this dimension.

                            // Flag that we are not within range and break out early:
                            isWithinRange = false;
                            break;
                        }
                    }
                    // Now we know whether the division cell is within the nearest range to the item.

                    // Check whether the division cell is within range across all dimensions:
                    if (isWithinRange)
                    {
                        // This division cell is within range of the nearest distance to the item.
                        // Search within the division cell:
                        measuredItem = searchNearestInDivisionCell(item, itemCoordinate, divisionCellToSearch);
                    }
                }
            }

            // Check whether we have a measured item:
            if (measuredItem != null)
            {
                // We measured an item for this division cell.

                // Check whether we have an exact match:
                if (measuredItem.hasExactMatch())
                {
                    // We got an exact match.

                    // Break out early:
                    return measuredItem.item;
                }
                // Now we know that it wasn't an exact match.

                // Check if it is better than the best result so far:
                if (bestResult == null)
                {
                    // We don't have a best yet.

                    // This is our new best:
                    bestResult = measuredItem;
                }
                else
                {
                    // We have a best result so far.

                    // Check whether this distance is the best so far:
                    if (this.distanceComparator.compare(measuredItem.distance, bestResult.distance) < 0)
                    {
                        // This item is closer.

                        // Flag this as the best item so far:
                        bestResult = measuredItem;
                    }
                }
            }
        }
        // Return the result:
        return bestResult == null ? null : bestResult.item;
    }


    /**
     * Searches for the nearest item for this kd-node.
     *
     * @param itemToSearchFor The item to search for.
     * @param itemCoord       The coordinate of the item in the hyper cube.
     * @param currentNode     The current node that we are searching.
     * @return The nearest item. Null if there is no item that can be found.
     */
    protected MeasuredItem<TItem, TDistance> searchNearestInKDNode(TItem itemToSearchFor, HyperCoord itemCoord, KDNode<TContent, TArea> currentNode)
    {
        // Keep track of the best result so far:
        TItem bestItemSoFar = null;
        TDistance bestDistanceSoFar = null;

        // Perform the search based on what type of node it is:
        switch (currentNode)
        {
            case KDBucketNode<TContent, TArea> bucketNode ->
            {
                // Search through the bucket:
                for (TContent itemContent : bucketNode.contentMap.values())
                {
                    // Get the item from the content:
                    TItem item = readItemFromContent(itemContent);

                    // Check whether the existing item is equal to the item:
                    if (item.equals(itemToSearchFor))
                    {
                        // This item is equal.

                        // Create the measured item:
                        MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();
                        measuredItem.item = item;
                        measuredItem.coordinate = itemCoord;

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
            case KDIntermediateNode<?, TContent, TArea> intermediateNodeUntyped ->
            {
                //noinspection unchecked
                KDIntermediateNode<Object, TContent, TArea> intermediateNode = (KDIntermediateNode<Object, TContent, TArea>) intermediateNodeUntyped;

                // Get the value for the dimension of this node:
                Object value = itemCoord.getValue(intermediateNode.dimension);

                // Get the range calculator so that we can inspect the ranges for this value:
                RangeCalculator<Object> rangeCalculator = intermediateNode.dimension.getRangeCalculator();

                // Check whether to go down the lower node:
                if (intermediateNode.lowerNode != null)
                {
                    // We have a lower node.

                    // Check whether the item is within the current best distance from the lower range:
                    if (rangeCalculator.isWithinDistanceOfRange(value, bestDistanceSoFar, true, intermediateNode.rangeSplit.lower()))
                    {
                        // This item is either in the range or within the distance of the range that we must check.

                        // Walk down the lower node recursively:
                        MeasuredItem<TItem, TDistance> measuredItem = searchNearestInKDNode(itemToSearchFor, itemCoord, intermediateNode.lowerNode);

                        // Check whether we found an item in this range:
                        if (measuredItem != null)
                        {
                            // We found an item in this range.

                            // Check for an exact match (no distance):
                            if (measuredItem.hasExactMatch())
                            {
                                // We got an exact match.
                                return measuredItem;
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
                }

                // Check whether to go down the higher node:
                if (intermediateNode.higherNode != null)
                {
                    // We have a higher node.

                    // Check whether the item is within the current best distance from the higher range:
                    if (rangeCalculator.isWithinDistanceOfRange(value, bestDistanceSoFar, true, intermediateNode.rangeSplit.higher()))
                    {
                        // This item is either in the range or within the distance of the range that we must check.

                        // Walk down the higher node recursively:
                        MeasuredItem<TItem, TDistance> measuredItem = searchNearestInKDNode(itemToSearchFor, itemCoord, intermediateNode.higherNode);

                        // Check whether we found an item in this range:
                        if (measuredItem != null)
                        {
                            // We found an item in this range.

                            // Check for an exact match (no distance):
                            if (measuredItem.hasExactMatch())
                            {
                                // We got an exact match.
                                return measuredItem;
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
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentNode);
        }

        // Check whether we found an item:
        if (bestItemSoFar != null)
        {
            // Create the measured item:
            MeasuredItem<TItem, TDistance> measuredItem = new MeasuredItem<>();
            measuredItem.item = bestItemSoFar;
            measuredItem.coordinate = extractItemCoordinate(bestItemSoFar, this.hyperCubeDefinition);
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
     * Searches for the nearest item in all the division cells.
     * CAUTION: Slow Performance
     *
     * @param item           The item to search for.
     * @param itemCoordinate The coordinate of the item in the hyper cube.
     * @param divisionCell   The division cell to search in.
     * @return The nearest item in that division cell. Null if there is no item in this division.
     */
    protected MeasuredItem<TItem, TDistance> searchNearestInDivisionCell(TItem item, HyperCoord itemCoordinate, DivisionCell<TContent, TArea> divisionCell)
    {
        // Search for the nearest item in this division recursively:
        return searchNearestInKDNode(item, itemCoordinate, divisionCell.kdTreeRoot);
    }

    /**
     * Searches for the nearest item in the given division.
     *
     * @param item     The item to search for.
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
     *
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
     *
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

        // Go through each division cell:
        for (DivisionCell<TContent, TArea> divisionCell : this.divisionCube.cellsByBranchName.values())
        {
            // Write the division cell details:
            divisionCell.toString(stringBuilder);
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
    public Measurer<TItem, TDistance> getMeasurer()
    {
        return this.measurer;
    }

    /**
     * Gets the comparator to use for comparing distances of items.
     *
     * @return The comparator to use for comparing distances of items.
     */
    @Override
    public Comparator<TDistance> getDistanceComparator()
    {
        return this.distanceComparator;
    }

    /**
     * Gets the range splitter that divides the range into a set of divisions.
     *
     * @return The range splitter that divides the range into a set of divisions.
     */
    @Override
    public RangeSplitter<TItem> getRangeSplitter()
    {
        return this.rangeSplitter;
    }

    /**
     * Gets the range finder that gets the index of an item in the divisions of a range.
     *
     * @return The range finder that gets the index of an item in the divisions of a range.
     */
    @Override
    public RangeFinder<TItem> getRangeFinder()
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
