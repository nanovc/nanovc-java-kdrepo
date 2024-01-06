package io.nanovc.indexing.repo;

import io.nanovc.RepoPath;

import java.util.*;

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

}
