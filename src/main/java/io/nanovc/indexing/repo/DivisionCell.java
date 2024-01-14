package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;

import java.util.Map;
import java.util.NavigableMap;

/**
 * A cell of a {@link DivisionCube}.
 * This gives us a specific volume of the kd-space that we are looking at.
 * It also contains the {@link io.nanovc.AreaAPI content area} where the content is indexed.
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public class DivisionCell<
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
{
    /**
     * The parent dimension in the chain that we walked to get to this {@link DivisionCell}.
     */
    public DivisionDimension<TContent, TArea> parentDimension;

    /**
     * The hyper cube of the kd-space that this {@link DivisionCell cell} represents.
     * This gives us the range of values in each dimension.
     */
    public HyperCube hyperCube;

    /**
     * The root of the kd-tree.
     */
    public KDNode<TContent, TArea> kdTreeRoot;

    /**
     * The content area for this division.
     */
    public TArea contentArea;

    /**
     * The repo path tree for this division.
     * It matches up with the paths in the {@link #contentArea}
     */
    public RepoPathTree repoPathTree;

    /**
     * The name of the branch for this {@link DivisionCell division cell}.
     */
    public String branchName;

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        toString(stringBuilder);
        return stringBuilder.toString();
    }

    public void toString(StringBuilder stringBuilder)
    {
        // Write the division details:
        stringBuilder.append("\n");
        stringBuilder.append("Division Cell Branch Name:");
        stringBuilder.append("\n");
        stringBuilder.append(this.branchName);
        stringBuilder.append("\n");
        // stringBuilder.append("Hyper Cube:");
        // stringBuilder.append("\n");
        // stringBuilder.append(this.hyperCube);
        // stringBuilder.append("\n");

        // Check whether there is too much content to render:
        if (this.contentArea.getContentStream().count() > 1_000)
        {
            // There is too much content.
            stringBuilder.append("Too Large to Render");
        }
        else
        {
            // Now we know that we have small enough content to render.

            // Write out the content for this division:
            printNodeToStringRecursively(this.repoPathTree.getRootNode(), false, stringBuilder, "", this.contentArea);
        }
        stringBuilder.append("\n");
    }

    /**
     * Prints out the current node recursively.
     *
     * @param node          The node to process.
     * @param hasSibling    True to flag that this node has a sibling that comes next. False to say that this node doesn't have a sibling that comes next.
     * @param stringBuilder The string builder to add to.
     * @param indent        The indent to prefix with.
     * @param contentArea   The content area to query.
     */
    private void printNodeToStringRecursively(RepoPathNode node, boolean hasSibling, StringBuilder stringBuilder, String indent, TArea contentArea)
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
            TContent nodeContent = contentArea.getContent(node.getRepoPath());

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
                printNodeToStringRecursively(childNode, hasChildSibling, stringBuilder, nextIndent, contentArea);
            }
        }
        else
        {
            // This node is a leaf node.
        }
    }
}
