package io.nanovc.indexing.repo;

import io.nanovc.RepoPath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link RepoPathTree}.
 */
class RepoPathTreeTests
{
    @Test
    public void creationTest()
    {
        new RepoPathTree();
    }

    @Test
    public void path_root()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode rootNode = tree.addPath(RepoPath.atRoot());
        assertEquals("", rootNode.getName());
    }

    @Test
    public void path_a()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode rootNode = tree.addPath(RepoPath.atRoot().resolve("a"));
        assertEquals("a", rootNode.getName());
        assertEquals("/a", rootNode.getRepoPath().toString());
    }

    @Test
    public void path_a1_b1()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        assertEquals("a1", node.getName());
        assertEquals("/a1", node.getRepoPath().toString());

        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b1"));
        assertEquals("b1", node.getName());
        assertEquals("/a1/b1", node.getRepoPath().toString());
    }

    @Test
    public void path_a1_b1_b2()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        assertEquals("a1", node.getName());
        assertEquals("/a1", node.getRepoPath().toString());

        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b1"));
        assertEquals("b1", node.getName());
        assertEquals("/a1/b1", node.getRepoPath().toString());

        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b2"));
        assertEquals("b2", node.getName());
        assertEquals("/a1/b2", node.getRepoPath().toString());
    }

    @Test
    public void path_a1_b1_straight()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b1"));
        assertEquals("b1", node.getName());
        assertEquals("/a1/b1", node.getRepoPath().toString());
    }
}
