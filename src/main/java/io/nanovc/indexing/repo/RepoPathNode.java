package io.nanovc.indexing.repo;

import io.nanovc.RepoPath;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * A node of a {@link RepoPathTree repo path tree} which represents a single {@link io.nanovc.RepoPath repo path}.
 * This is used to efficiently traverse a hierarchy of repo paths.
 */
public class RepoPathNode
{
    /**
     * The parent node.
     * Null if this is the root node.
     */
    private final RepoPathNode parent;

    /**
     * The name of this node.
     */
    private final String name;

    /**
     * The cached repo path.
     */
    private RepoPath repoPath;

    /**
     * A map of all the child nodes, indexed by their name.
     */
    private NavigableMap<String, RepoPathNode> childrenByName;

    public RepoPathNode(RepoPathNode parent, String name)
    {
        this.parent = parent;
        this.name = name;
    }

    /**
     * Checks whether this node has children.
     * @return True if this node has children. False it it doesn't.
     */
    public boolean hasChildren()
    {
        return this.childrenByName != null && this.childrenByName.size() > 0;
    }

    /**
     * Gets whether this is a root node or not.
     * @return True if this is the root node. False if it is a child node.
     */
    public boolean isRootNode()
    {
        return this.parent == null;
    }

    /**
     * Gets the name of this node.
     * @return The name of this node.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets the parent node.
     * Null if this is the root node.
     * @return The parent node. Null if this is the root node.
     */
    public RepoPathNode getParent()
    {
        return parent;
    }

    /**
     * Gets the repo path of this node.
     * @return The repo path of this node.
     */
    public RepoPath getRepoPath()
    {
        // Check if we already have our repo path:
        if (this.repoPath == null)
        {
            // We have not cached our repo path yet.

            // Check whether we have a parent node:
            if (this.parent == null)
            {
                // We are the root node.
                this.repoPath = RepoPath.atRoot();
            }
            else
            {
                // We have a parent node.

                // Get the repo path of the parent:
                RepoPath parentRepoPath = this.parent.getRepoPath();

                // Resolve our repo path relative to the parent:
                this.repoPath = parentRepoPath.resolve(this.getName());
            }
        }
        // Now we have our repo path.

        return repoPath;
    }

    /**
     * Gets a map of all the child nodes, indexed by their name.
     * @return A map of all the child nodes, indexed by their name.
     */
    public NavigableMap<String, RepoPathNode> getChildrenByName()
    {
        // Check whether we have the children indexed already:
        if (this.childrenByName != null) return this.childrenByName;
        else
        {
            // We do not have the children yet.
            // Create the map of children:
            this.childrenByName = new TreeMap<>(String::compareTo);
        }
        return childrenByName;
    }

    @Override
    public String toString()
    {
        return this.getName();
    }
}
