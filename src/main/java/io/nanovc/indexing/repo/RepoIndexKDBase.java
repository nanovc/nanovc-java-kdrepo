package io.nanovc.indexing.repo;

import io.nanovc.*;
import io.nanovc.indexing.Extractor;
import io.nanovc.indexing.IndexKDBase;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.repo.ranges.Range;
import io.nanovc.indexing.repo.ranges.RangeCalculator;
import io.nanovc.indexing.repo.ranges.RangeSplit;
import io.nanovc.indexing.repo.ranges.RangeSplitInclusion;

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
     * The number of divisions to use for this grid index.
     */
    private final int divisions;

    /**
     * This is the maximum number of items that we want to keep in a bucket before we split the range further.
     */
    private final int bucketThreshold;

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
     * The definition of the hyper cube that defines the dimensions for this index.
     */
    protected final HyperCubeDefinition hyperCubeDefinition;

    /**
     * This is the cube of divisions that slices up the {@link HyperCubeDefinition} into smaller divisions.
     * This is also what defines the branches that we have because it's one branch for each {@link DivisionCell} in this {@link DivisionCube}.
     */
    protected DivisionCube<TItem, TContent, TArea> divisionCube;

    public RepoIndexKDBase(
        HyperCubeDefinition hyperCubeDefinition,
        int divisions, int bucketThreshold,
        Extractor<TItem> extractor, Measurer<TItem, TDistance> measurer, Comparator<TDistance> distanceComparator,
        TRepoHandler repoHandler, RepoPath rootRepoPath,
        ContentCreator<TItem, TContent> contentCreator, ContentReader<TItem, TContent> contentReader
    )
    {
        this.hyperCubeDefinition = hyperCubeDefinition;
        this.divisions = divisions;
        this.bucketThreshold = bucketThreshold;
        this.extractor = extractor;
        this.measurer = measurer;
        this.distanceComparator = distanceComparator;
        this.repoHandler = repoHandler;
        this.rootRepoPath = rootRepoPath;
        this.contentCreator = contentCreator;
        this.contentReader = contentReader;
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
        DivisionCell<TItem, TContent, TArea> divisionCell = getOrCreateDivisionCell(itemCoord);

        // Make sure that we have a root node for this division cell:
        if (divisionCell.kdTreeRoot == null)
        {
            // We don't have a root yet.

            // Create the root node:
            KDBucketNode<TItem, TContent, TArea> root = new KDBucketNode<>();

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
    protected DivisionCell<TItem, TContent, TArea> getOrCreateDivisionCell(HyperCoord itemCoord)
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
            DivisionDimension<TItem, TContent, TArea> rootDimension = createDivisionDimension(0, null);

            // Save this as the root dimension for our division cube:
            this.divisionCube.rootDimension = rootDimension;
        }

        // Walk the tree recursively until we find the division cell:
        return getOrCreateDivisionCellRecursively(itemCoord, this.divisionCube.rootDimension, null);
    }

    /**
     * This walks the {@link #divisionCube} and gets the {@link DivisionCell} that we need for the given coordinate.
     *
     * @param itemCoord The coordinate of the item that we want to find the cell of.
     * @return The {@link DivisionCell} that corresponds to the given {@link HyperCoord hyper coordinate}. Null if there is no division cell for that coordinate.
     */
    protected DivisionCell<TItem, TContent, TArea> getDivisionCell(HyperCoord itemCoord)
    {
        // Make sure we have a division cube:
        if (this.divisionCube == null) return null;

        // Walk the tree recursively until we find the division cell:
        return getDivisionCellRecursively(itemCoord, this.divisionCube.rootDimension);
    }

    /**
     * Creates the {@link DivisionDimension} with the given parameters.
     *
     * @param dimensionIndexToCreate    The dimensionIndex that we are creating.
     * @param previousDivisionDimension The previous division dimension that we are coming from. Null if this is the first dimension.
     * @return The {@link DivisionDimension} that was created.
     */
    private DivisionDimension<TItem, TContent, TArea> createDivisionDimension(int dimensionIndexToCreate, DivisionDimension<TItem, TContent, TArea> previousDivisionDimension)
    {
        // Check whether we have more than one division dimension so that we know what type of node to create:
        int dimensionCount = this.hyperCubeDefinition.getDimensionCount();
        int lastDimensionIndex = dimensionCount - 1;

        // Create the root dimension:
        DivisionDimension<TItem, TContent, TArea> divisionDimension;
        if (dimensionIndexToCreate == lastDimensionIndex)
        {
            // This is the last dimension index:
            DivisionDimension.Last<TItem, TContent, TArea> last = new DivisionDimension.Last<>();
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
            DivisionDimension.First<TItem, TContent, TArea> first = new DivisionDimension.First<>();
            divisionDimension = first;

            // Set the division cube that we came from:
            first.divisionCube = this.divisionCube;

            // Use the same hyper cube as the division cube:
            first.hyperCube = first.divisionCube.hyperCube;
        }
        else if (dimensionIndexToCreate < lastDimensionIndex)
        {
            // This is an intermediate dimension.
            DivisionDimension.Intermediate<TItem, TContent, TArea> intermediate = new DivisionDimension.Intermediate<>();
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
        //       We also create additional ranges for the values that are out of bounds.

        // Check whether this is our first set of splits that we are calculating for this dimension:
        if (!divisionDimension.divisionCube.rangeSplitsByDimensionIndex.containsKey(dimensionIndexToCreate))
        {
            // We have not worked out these splits before for the division cube.

            // Add these splits to the division cube:
            divisionDimension.divisionCube.rangeSplitsByDimensionIndex.put(dimensionIndexToCreate, divisionDimension.splits);
        }

        return divisionDimension;
    }

    /**
     * This walks the {@link #divisionCube} and gets or creates the {@link DivisionCell} that we need for the given coordinate.
     *
     * @param itemCoord            The coordinate of the item that we want to find the cell of.
     * @param currentDimensionNode The dimension node that we are currently traversing.
     * @param parentDivisionCoord  The (partial) division coordinate of the parent. This used to create the division coordinate once we create the division cell. If this is null then this is the first dimension we are processing.
     * @return The {@link DivisionCell} that corresponds to the given {@link HyperCoord hyper coordinate}.
     */
    private DivisionCell<TItem, TContent, TArea> getOrCreateDivisionCellRecursively(HyperCoord itemCoord, DivisionDimension<TItem, TContent, TArea> currentDimensionNode, DivisionCoord parentDivisionCoord)
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

                // Create the division coordinate for this step:
                DivisionCoord currentDivisionCoordinate = parentDivisionCoord == null ? new DivisionCoord(divisionIndex) : parentDivisionCoord.withNextValue(divisionIndex);

                // Check where we are in the dimension chain to decide how to walk next:
                switch (currentDimensionNode)
                {
                    case DivisionDimension.First<TItem, TContent, TArea> firstDivisionDimension ->
                    {
                        // Make sure we have the next dimension to walk down:
                        DivisionDimension<TItem, TContent, TArea> nextDivisionDimension = firstDivisionDimension.nextDivisionDimensionByIndex.get(divisionIndex);
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
                        return getOrCreateDivisionCellRecursively(itemCoord, nextDivisionDimension, currentDivisionCoordinate);
                    }
                    case DivisionDimension.Intermediate<TItem, TContent, TArea> intermediateDivisionDimension ->
                    {
                        // Make sure we have the next dimension to walk down:
                        DivisionDimension<TItem, TContent, TArea> nextDivisionDimension = intermediateDivisionDimension.nextDivisionDimensionByIndex.get(divisionIndex);
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
                        return getOrCreateDivisionCellRecursively(itemCoord, nextDivisionDimension, currentDivisionCoordinate);
                    }
                    case DivisionDimension.Last<TItem, TContent, TArea> lastDivisionDimension ->
                    {
                        // We are at the last dimension.

                        // Search for the division cell:
                        DivisionCell<TItem, TContent, TArea> divisionCell = lastDivisionDimension.cellsByIndex.get(divisionIndex);
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
                            divisionCell.divisionCellCoordinate = currentDivisionCoordinate;

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
     * This walks the {@link #divisionCube} and gets the {@link DivisionCell} that we need for the given coordinate.
     *
     * @param itemCoord The coordinate of the item that we want to find the cell of.
     * @return The {@link DivisionCell} that corresponds to the given {@link HyperCoord hyper coordinate}. Null if there is not division cell for this coordinate.
     */
    private DivisionCell<TItem, TContent, TArea> getDivisionCellRecursively(HyperCoord itemCoord, DivisionDimension<TItem, TContent, TArea> currentDimensionNode)
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
                    case DivisionDimension.First<TItem, TContent, TArea> firstDivisionDimension ->
                    {
                        // Make sure we have the next dimension to walk down:
                        DivisionDimension<TItem, TContent, TArea> nextDivisionDimension = firstDivisionDimension.nextDivisionDimensionByIndex.get(divisionIndex);
                        if (nextDivisionDimension == null)
                        {
                            // Flag that we don't have a division cell for this coordinate by breaking out early:
                            return null;
                        }

                        // Walk to the next dimension until we find the division cell:
                        return getDivisionCellRecursively(itemCoord, nextDivisionDimension);
                    }
                    case DivisionDimension.Intermediate<TItem, TContent, TArea> intermediateDivisionDimension ->
                    {
                        // Make sure we have the next dimension to walk down:
                        DivisionDimension<TItem, TContent, TArea> nextDivisionDimension = intermediateDivisionDimension.nextDivisionDimensionByIndex.get(divisionIndex);
                        if (nextDivisionDimension == null)
                        {
                            // Flag that we don't have a division cell for this coordinate by breaking out early:
                            return null;
                        }

                        // Walk to the next dimension until we find the division cell:
                        return getDivisionCellRecursively(itemCoord, nextDivisionDimension);
                    }
                    case DivisionDimension.Last<TItem, TContent, TArea> lastDivisionDimension ->
                    {
                        // We are at the last dimension.

                        // Search for the division cell:
                        DivisionCell<TItem, TContent, TArea> divisionCell = lastDivisionDimension.cellsByIndex.get(divisionIndex);
                        if (divisionCell == null)
                        {
                            // Flag that we don't have a division cell for this coordinate by breaking out early:
                            return null;
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
    protected KDNode<TItem, TContent, TArea> addItemToKDNode(TItem item, HyperCoord itemCoord, KDNode<TItem, TContent, TArea> node)
    {
        // Check what type of node this is:
        switch (node)
        {
            case KDBucketNode<TItem, TContent, TArea> bucketNode ->
            {
                // Get the dimension index that we want to index by:
                int dimensionIndex = bucketNode.level % this.hyperCubeDefinition.getDimensionCount();
                Dimension<Object> dimension = this.hyperCubeDefinition.getDimension(dimensionIndex);

                // Get the range calculator so that we can work out new ranges:
                RangeCalculator<Object> rangeCalculator = dimension.getRangeCalculator();

                // Get the range of the bucket that we came from:
                Range<Object> bucketRange = bucketNode.hyperCube.getRangeForDimension(dimensionIndex);

                // Work out whether we need to split our range:
                boolean shouldSplit = false;
                Object midPoint = null;

                // Check whether we have exceeded our bucket threshold:
                if (bucketNode.contentMap.size() == this.bucketThreshold)
                {
                    // We have exceeded the bucket size for this node.

                    // Work out the value to split the dimension range in:
                    midPoint = rangeCalculator.midPoint(bucketRange);

                    // Check whether this range can be split at this midpoint:
                    shouldSplit = rangeCalculator.canSplitRange(bucketRange, midPoint);
                }
                // Now we know whether to split the node.

                // Split the node if we need to:
                if (shouldSplit)
                {
                    // Create a new intermediate node:
                    KDIntermediateNode<TItem, Object, TContent, TArea> newNode = new KDIntermediateNode<>();
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
                    KDNode<TItem, TContent, TArea> nodeToReturn = newNode;

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

                    // Add the item to our item map:
                    bucketNode.itemMap.put(itemRepoPathNode, item);

                    // Add the content to our content map:
                    bucketNode.contentMap.put(itemRepoPathNode, itemContent);

                    // Add the content to the content area:
                    bucketNode.divisionCell.contentArea.putContent(itemRepoPath, itemContent);

                    // Leave the node as it was:
                    return bucketNode;
                }
            }
            case KDIntermediateNode<TItem, ?, TContent, TArea> intermediateNodeUntyped ->
            {
                //noinspection unchecked
                KDIntermediateNode<TItem, Object, TContent, TArea> intermediateNode = (KDIntermediateNode<TItem, Object, TContent, TArea>) intermediateNodeUntyped;

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
                        KDBucketNode<TItem, TContent, TArea> lowerNode = new KDBucketNode<>();
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
                        KDBucketNode<TItem, TContent, TArea> higherNode = new KDBucketNode<>();
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
     * Indexes the items that have been added.
     * This is a pre-computation step that needs to be called before we search for nearest neighbours.
     */
    @Override public void index()
    {
        // We need to work out nearest neighbors for divisions in k-dimensions:
        workOutNearestNeighborsForDivisionCells(this.divisionCube);
    }

    /**
     * The division cube to update with links to nearest neighbour division cells for each cell that actually exists.
     *
     * @param divisionCube The division cube to update with nearest neighbour information.
     */
    protected void workOutNearestNeighborsForDivisionCells(DivisionCube<TItem, TContent, TArea> divisionCube)
    {
        // NOTE: The algorithm is to ripple out for each cell where we have a division cell in concentric multidimensional spheres.
        //       If we come across any existing division cells within that sphere radius, we need to search all items in that cell.

        // Get the number of dimensions that we have:
        int dimensionCount = divisionCube.hyperCube.getDefinition().getDimensionCount();

        // Index each of the cells by their coordinates so that we can look them up easily:
        HashMap<DivisionCoord, DivisionCell<TItem, TContent, TArea>> cellsByCoord = new HashMap<>();
        for (DivisionCell<TItem, TContent, TArea> existingDivisionCell : divisionCube.cellsByBranchName.values())
        {
            cellsByCoord.put(existingDivisionCell.divisionCellCoordinate, existingDivisionCell);
        }
        // Now we have each of the cells indexed by their coordinates.

        // Define the coordinate that we are going to be navigating:
        int[] currentCoord = new int[dimensionCount];

        // Go through each division cell that we actually have:
        for (DivisionCell<TItem, TContent, TArea> existingDivisionCell : divisionCube.cellsByBranchName.values())
        {
            // Create the list of cells that are the nearest for this existing cell:
            Set<DivisionCell<TItem, TContent, TArea>> nearestCells = new LinkedHashSet<>();

            // Start off searching for other cells.
            // NOTE: We have a special case where there is only one cell, so we don't have to search:
            boolean hasFoundAnyCells = cellsByCoord.size() == 1;

            // Keep increasing the sphere radius until we find something:
            int sphereRadius = 0;
            search:
            while (!hasFoundAnyCells)
            {
                // Increase the radius of the sphere for this iteration:
                sphereRadius++;

                // Go through each dimension as the anchor dimension:
                for (int anchorDimensionIndex = 0; anchorDimensionIndex < dimensionCount; anchorDimensionIndex++)
                {
                    // Get the dimension range information:
                    var anchorDimensionRangeSplits = divisionCube.rangeSplitsByDimensionIndex.get(anchorDimensionIndex);

                    // Check whether the sphere radius is larger than all the splits in the dimension (meaning that we are too far):
                    if (sphereRadius > anchorDimensionRangeSplits.size()) break search;

                    // Get the coordinate for this dimension as the anchor value:
                    int anchorCoordinateValue = existingDivisionCell.divisionCellCoordinate.getValue(anchorDimensionIndex);

                    // Work out the lowest index at the sphere distance for this dimension:
                    int anchorLowestValue = Math.max(anchorCoordinateValue - sphereRadius, 0);

                    // Work out the highest index at the sphere distance for this dimension:
                    int anchorHighestValue = Math.min(anchorCoordinateValue + sphereRadius, anchorDimensionRangeSplits.size() - 1);

                    // Work out the ranges to iterate for each of the dimensions:
                    int[] lowerIndexRangePerDimension = new int[dimensionCount];
                    int[] upperIndexRangePerDimension = new int[dimensionCount];

                    // Loop through every dimension to work out the index ranges that we want to iterate:
                    for (int dimensionIndex = 0; dimensionIndex < dimensionCount; dimensionIndex++)
                    {
                        // Check whether this is the anchor dimension:
                        if (dimensionIndex == anchorDimensionIndex)
                        {
                            // This is the anchor dimension.

                            // This will be set later on when we are doing the lower and upper planes.
                            lowerIndexRangePerDimension[dimensionIndex] = -1;
                            upperIndexRangePerDimension[dimensionIndex] = -1;
                        }
                        else
                        {
                            // This is not the anchor dimension.

                            // Get the dimension range information:
                            var otherDimensionRangeSplits = divisionCube.rangeSplitsByDimensionIndex.get(dimensionIndex);

                            // Get the coordinate for this dimension:
                            int otherCoordinateValue = existingDivisionCell.divisionCellCoordinate.getValue(dimensionIndex);

                            // Work out the lowest index at the sphere distance for this dimension:
                            int otherLowestValue = Math.max(otherCoordinateValue - sphereRadius, 0);

                            // Work out the highest index at the sphere distance for this dimension:
                            int otherHighestValue = Math.min(otherCoordinateValue + sphereRadius, otherDimensionRangeSplits.size() - 1);

                            // Save the ranges:
                            lowerIndexRangePerDimension[dimensionIndex] = otherLowestValue;
                            upperIndexRangePerDimension[dimensionIndex] = otherHighestValue;
                        }
                    }
                    // Now we have the ranges that we want to iterate.

                    // Check if we must search the lowest position:
                    if (anchorLowestValue != anchorCoordinateValue)
                    {
                        // Walk the entire lowest plane:
                        walkDivisionPlaneToFindCells(
                            anchorLowestValue,
                            anchorDimensionIndex, lowerIndexRangePerDimension, upperIndexRangePerDimension, currentCoord, dimensionCount, cellsByCoord, nearestCells
                        );
                    }
                    // Now we have walked the entire lowest plane.

                    // Check if we must search the highest position:
                    if (anchorHighestValue != anchorCoordinateValue)
                    {
                        // Walk the entire highest plane:
                        walkDivisionPlaneToFindCells(
                            anchorHighestValue,
                            anchorDimensionIndex, lowerIndexRangePerDimension, upperIndexRangePerDimension, currentCoord, dimensionCount, cellsByCoord, nearestCells
                        );
                    }
                    // Now we have walked the entire highest plane.
                }

                // Flag whether we have found any cells:
                hasFoundAnyCells = !nearestCells.isEmpty();

            }
            // Now we know we have found some cells.

            // Save the nearest cells for this existing cells:
            existingDivisionCell.nearestCells = new ArrayList<>(nearestCells);
        }

    }

    /**
     * This walks the set of coordinates and looks for division cells.
     */
    private void walkDivisionPlaneToFindCells(
        int anchorValue,
        int anchorDimensionIndex,
        int[] lowerIndexRangePerDimension,
        int[] upperIndexRangePerDimension,
        int[] currentCoord,
        int dimensionCount,
        HashMap<DivisionCoord, DivisionCell<TItem, TContent, TArea>> cellsByCoord,
        Set<DivisionCell<TItem, TContent, TArea>> nearestCells
    )
    {
        // Update the anchor position to the given value:
        lowerIndexRangePerDimension[anchorDimensionIndex] = anchorValue;
        upperIndexRangePerDimension[anchorDimensionIndex] = anchorValue;

        // Initialise the coordinate to the starting locations:
        System.arraycopy(lowerIndexRangePerDimension, 0, currentCoord, 0, dimensionCount);
        // Now we are at the starting locations for each dimension.

        // Start working through each cell:
        boolean stillGoing = true;
        do
        {
            // Check whether we have a cell at the current value:
            var cell = cellsByCoord.get(new DivisionCoord(currentCoord));
            if (cell != null)
            {
                // We found a cell.

                // Add it as one of our nearest cells:
                nearestCells.add(cell);
            }

            // Increment the coordinate:
            for (int dimIndex = 0; dimIndex < dimensionCount; dimIndex++)
            {
                // Increment the coordinate:
                currentCoord[dimIndex] = currentCoord[dimIndex] + 1;

                // Check if we have exceeded the upper bound:
                if (currentCoord[dimIndex] > upperIndexRangePerDimension[dimIndex])
                {
                    // Check whether we are done iterating:
                    if (dimIndex == dimensionCount - 1)
                    {
                        // We are done iterating.
                        stillGoing = false;
                        break;
                    }
                    else
                    {
                        // We must keep iterating.
                        // We need to reset this dimension to the lower bound and increment the next dimension by continuing the loop:
                        currentCoord[dimIndex] = lowerIndexRangePerDimension[dimIndex];
                    }
                }
                else
                {
                    // We haven't exceeded any upper bound yet, so don't move to the next dimension:
                    break;
                }
            }
        }
        while (stillGoing);
        // Now we have iterated the entire plane.
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
        List<DivisionCell<TItem, TContent, TArea>> divisionCellsToSearch = new ArrayList<>();
        {
            // Find the right division for this item at the given coordinate:
            DivisionCell<TItem, TContent, TArea> divisionCell = getDivisionCell(itemCoordinate);

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
                if (divisionCell.nearestCells != null && !divisionCell.nearestCells.isEmpty())
                {
                    // We have nearest cells to search too:
                    divisionCellsToSearch.addAll(divisionCell.nearestCells);
                }
            }
        }
        // Now we know all the division cells that we want to search.

        // Search each division cell and find the nearest item:
        MeasuredItem<TItem, TDistance> bestResult = null;
        for (DivisionCell<TItem, TContent, TArea> divisionCellToSearch : divisionCellsToSearch)
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
                        Object coordOfNearestInDimension = extractor.extractDimensionalValue(bestResult.item, dimensionIndex);

                        // Get the distance between the two coordinates in this dimension:
                        Object distanceBetween = dimension.getArithmetic().distanceBetween(coordOfItemInDimension, coordOfNearestInDimension);

                        // Get the range of this divisions cell for this dimension:
                        Range<Object> dimensionRange = divisionCellToSearch.hyperCube.getRangeForDimension(dimensionIndex);

                        // Check whether the point we are searching for is within the best distance of this range:
                        if (!dimension.getRangeCalculator().isWithinDistanceOfRange(coordOfItemInDimension, distanceBetween, true, dimensionRange))
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
     * @param itemToSearchFor    The item to search for.
     * @param itemCoord          The coordinate of the item in the hyper cube.
     * @param currentNode        The current node that we are searching.
     * @param bestResultToUpdate This is a measured item instance to update while searching with the best result so far. This is used so that we can avoid memory allocations.
     */
    protected void searchNearestInKDNode(TItem itemToSearchFor, HyperCoord itemCoord, KDNode<TItem, TContent, TArea> currentNode, MeasuredItem<TItem, TDistance> bestResultToUpdate)
    {
        // Perform the search based on what type of node it is:
        switch (currentNode)
        {
            case KDBucketNode<TItem, TContent, TArea> bucketNode ->
            {
                // NOTE: We currently cache the items in the bucket,
                //       but we could easily re-hydrate them from the content if we needed to
                //       (for example if we were offloading the division cell to offline storage)
                // Search through the bucket:
                // for (TContent itemContent : bucketNode.contentMap.values())
                // {
                //     // Get the item from the content:
                //     TItem item = readItemFromContent(itemContent);

                // Search through the bucket:
                for (TItem item : bucketNode.itemMap.values())
                {
                    // Check whether the existing item is equal to the item:
                    if (item.equals(itemToSearchFor))
                    {
                        // This item is equal.

                        // Flag this as an exact result:
                        bestResultToUpdate.item = item;
                        bestResultToUpdate.distance = null;

                        // Break out early:
                        return;
                    }
                    // Now we know that the items are not equal.

                    // Get the distance to the item:
                    TDistance distance = measureDistanceBetween(item, itemToSearchFor);

                    // Check whether this distance is the best so far:
                    if (bestResultToUpdate.distance == null || this.distanceComparator.compare(distance, bestResultToUpdate.distance) < 0)
                    {
                        // This item is closer.

                        // Flag this as the best item so far:
                        bestResultToUpdate.item = item;
                        bestResultToUpdate.distance = distance;
                    }
                }
            }
            case KDIntermediateNode<TItem, ?, TContent, TArea> intermediateNodeUntyped ->
            {
                //noinspection unchecked
                KDIntermediateNode<TItem, Object, TContent, TArea> intermediateNode = (KDIntermediateNode<TItem, Object, TContent, TArea>) intermediateNodeUntyped;

                // Get the value for the dimension of this node:
                Object value = itemCoord.getValue(intermediateNode.dimension);

                // Get the range calculator so that we can inspect the ranges for this value:
                RangeCalculator<Object> rangeCalculator = intermediateNode.dimension.getRangeCalculator();

                // Check whether to go down the lower node:
                if (intermediateNode.lowerNode != null)
                {
                    // We have a lower node.

                    // Check whether the item is within the current best distance from the lower range:
                    if (rangeCalculator.isWithinDistanceOfRange(value, bestResultToUpdate.distance, true, intermediateNode.rangeSplit.lower()))
                    {
                        // This item is either in the range or within the distance of the range that we must check.

                        // Walk down the lower node recursively:
                        searchNearestInKDNode(itemToSearchFor, itemCoord, intermediateNode.lowerNode, bestResultToUpdate);

                        // Check for an exact match (no distance):
                        if (bestResultToUpdate.hasExactMatch())
                        {
                            // We got an exact match.
                            // Break out early:
                            return;
                        }
                    }
                }

                // Check whether to go down the higher node:
                if (intermediateNode.higherNode != null)
                {
                    // We have a higher node.

                    // Check whether the item is within the current best distance from the higher range:
                    if (rangeCalculator.isWithinDistanceOfRange(value, bestResultToUpdate.distance, true, intermediateNode.rangeSplit.higher()))
                    {
                        // This item is either in the range or within the distance of the range that we must check.

                        // Walk down the higher node recursively:
                        searchNearestInKDNode(itemToSearchFor, itemCoord, intermediateNode.higherNode, bestResultToUpdate);

                        // Check for an exact match (no distance):
                        if (bestResultToUpdate.hasExactMatch())
                        {
                            // We got an exact match.
                            // Break out early:
                            return;
                        }
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentNode);
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
    protected MeasuredItem<TItem, TDistance> searchNearestInDivisionCell(TItem item, HyperCoord itemCoordinate, DivisionCell<TItem, TContent, TArea> divisionCell)
    {
        // Search for the nearest item in this division recursively:
        MeasuredItem<TItem, TDistance> bestResultToUpdate = new MeasuredItem<>();
        searchNearestInKDNode(item, itemCoordinate, divisionCell.kdTreeRoot, bestResultToUpdate);
        return bestResultToUpdate;
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

    @Override public String toString()
    {
        // Print out the content tree:
        StringBuilder stringBuilder = new StringBuilder();

        // Get parameters:
        HyperCubeDefinition hyperCubeDefinition = this.getHyperCubeDefinition();

        // Print out details of the index:
        stringBuilder.append("KDRepo Index for: ");
        stringBuilder.append(hyperCubeDefinition == null ? "" : hyperCubeDefinition.toString());
        stringBuilder.append(" with ");
        stringBuilder.append(this.getDivisions());
        stringBuilder.append(" division");
        if (this.getDivisions() != 1) stringBuilder.append("s");
        stringBuilder.append(":");

        // Go through each division cell:
        for (DivisionCell<TItem, TContent, TArea> divisionCell : this.divisionCube.cellsByBranchName.values())
        {
            // Write the division cell details:
            divisionCell.toString(stringBuilder);
        }

        return stringBuilder.toString();
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

    /**
     * Gets the definition of the hyper cube that defines the dimensions for this index.
     *
     * @return The definition of the hyper cube that defines the dimensions for this index.
     */
    public HyperCubeDefinition getHyperCubeDefinition()
    {
        return hyperCubeDefinition;
    }
}
