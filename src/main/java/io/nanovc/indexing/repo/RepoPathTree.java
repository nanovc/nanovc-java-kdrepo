package io.nanovc.indexing.repo;

import io.nanovc.RepoPath;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Scanner;

/**
 * A tree of {@link io.nanovc.RepoPath repo paths}.
 * This is used to efficiently traverse a hierarchy of repo paths.
 */
public class RepoPathTree
{
    /**
     * The root of the repo path tree.
     */
    private final RepoPathNode rootNode = new RepoPathNode(null, "");

    /**
     * Adds the given repo path to the repo tree.
     *
     * @param path The repo path to add to the tree.
     * @return The node at that path in the tree.
     */
    public RepoPathNode addPath(RepoPath path)
    {
        // Make sure we have a path:
        if (path == null || path.path == null) return null;

        // Make sure the path is an absolute path:
        path = path.toAbsolutePath();

        // Create a scanner to walk the path:
        Scanner scanner = new Scanner(path.path);
        scanner.useDelimiter("/");

        // Walk the tree while we scan the path:
        RepoPathNode currenNode = this.rootNode;

        // Scan the path for the root delimiter:
        while (scanner.hasNext())
        {
            // Get the next path:
            String pathName = scanner.next();

            // Check whether we have a child with this name:
            NavigableMap<String, RepoPathNode> childrenByName = currenNode.getChildrenByName();
            RepoPathNode child = childrenByName.get(pathName);
            if (child == null)
            {
                // We do not have a child with this name yet.

                // Create the child node:
                child = new RepoPathNode(currenNode, pathName);

                // Index the child:
                childrenByName.put(pathName, child);
            }

            // Move to the child node:
            currenNode = child;
        }
        // Now we have processed the path.

        // Return the current node that we are on:
        return currenNode;
    }

    /**
     * This either gets the existing child node or creates a new one if necessary.
     * @param nodeToAddTo The node of the tree to add a child to.
     * @param childName The name of the child to add to the node.
     * @return The child node with the given name.
     */
    public RepoPathNode getOrCreateChildNode(RepoPathNode nodeToAddTo, String childName)
    {
        // Check whether the node already has a child with this name:
        NavigableMap<String, RepoPathNode> childrenByName = nodeToAddTo.getChildrenByName();
        RepoPathNode childNode = childrenByName.get(childName);
        if (childNode == null)
        {
            // We do not have this child node yet.

            // Create the child node:
            childNode = new RepoPathNode(nodeToAddTo, childName);

            // Index the child node:
            childrenByName.put(childName, childNode);
        }

        // Return the child node:
        return childNode;
    }

    /**
     * Gets the root of the repo path tree.
     *
     * @return The root of the repo path tree.
     */
    public RepoPathNode getRootNode()
    {
        return rootNode;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        printNodeToStringRecursively(this.getRootNode(), false, sb, "");
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
}
