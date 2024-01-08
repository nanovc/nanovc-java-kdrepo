package io.nanovc.indexing.repo;

import io.nanovc.AreaAPI;
import io.nanovc.ContentAPI;

import java.util.Map;
import java.util.NavigableMap;

/**
 * A division of the {@link RepoIndex1D} range.
 * This is used to keep all context for the division together.
 *
 * @param <TItem>    The specific type of data that the index is for.
 * @param <TContent> The specific type of content that the repo commits.
 * @param <TArea>    The specific type of content area that the repo commits.
 */
public class Division<
    TItem,
    TContent extends ContentAPI,
    TArea extends AreaAPI<TContent>
    >
{
    /**
     * The index of this division in the range.
     */
    public int divisionIndex;

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
     * The minimum range of this division.
     */
    public TItem minRange;

    /**
     * The maximum range of this division.
     */
    public TItem maxRange;

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
        stringBuilder.append("Division: ");
        stringBuilder.append(this.divisionIndex);
        stringBuilder.append(" from ");
        stringBuilder.append(this.minRange);
        stringBuilder.append(" to ");
        stringBuilder.append(this.maxRange);
        stringBuilder.append(":\n");

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
