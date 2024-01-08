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

        String expectedTree =
            ".";
        assertTree(expectedTree, tree);
    }

    @Test
    public void path_a()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode rootNode = tree.addPath(RepoPath.atRoot().resolve("a"));
        assertEquals("a", rootNode.getName());
        assertEquals("/a", rootNode.getRepoPath().toString());

        String expectedTree =
            ".\n" +
            "└───a";
        assertTree(expectedTree, tree);
    }

    @Test
    public void path_a1_b11()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        assertEquals("a1", node.getName());
        assertEquals("/a1", node.getRepoPath().toString());

        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b11"));
        assertEquals("b11", node.getName());
        assertEquals("/a1/b11", node.getRepoPath().toString());

        String expectedTree =
            ".\n" +
            "└───a1\n" +
            "    └───b11";
        assertTree(expectedTree, tree);
    }


    @Test
    public void path_a1_b1_straight()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b11"));
        assertEquals("b11", node.getName());
        assertEquals("/a1/b11", node.getRepoPath().toString());

        String expectedTree =
            ".\n" +
            "└───a1\n" +
            "    └───b11";
        assertTree(expectedTree, tree);
    }

    @Test
    public void path_a1_b11_b12()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b11"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b12"));

        String expectedTree =
            ".\n" +
            "└───a1\n" +
            "    ├───b11\n" +
            "    └───b12";
        assertTree(expectedTree, tree);
    }

    @Test
    public void path_a1_b11_b12_a2_b21()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b11"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b12"));
        node = tree.addPath(RepoPath.atRoot().resolve("a2").resolve("b21"));

        String expectedTree =
            ".\n" +
            "├───a1\n" +
            "│   ├───b11\n" +
            "│   └───b12\n" +
            "└───a2\n" +
            "    └───b21";
        assertTree(expectedTree, tree);
    }

    @Test
    public void path_a1_b11_b12_c121_a2_b21()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b11"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b12"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b12").resolve("c121"));
        node = tree.addPath(RepoPath.atRoot().resolve("a2").resolve("b21"));

        String expectedTree =
            ".\n" +
            "├───a1\n" +
            "│   ├───b11\n" +
            "│   └───b12\n" +
            "│       └───c121\n" +
            "└───a2\n" +
            "    └───b21";
        assertTree(expectedTree, tree);
    }

    @Test
    public void path_a1_b11_b12_c121_a2_b21_c211_c212_b22_a3()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b11"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b12"));
        node = tree.addPath(RepoPath.atRoot().resolve("a1").resolve("b12").resolve("c121"));
        node = tree.addPath(RepoPath.atRoot().resolve("a2").resolve("b21"));
        node = tree.addPath(RepoPath.atRoot().resolve("a2").resolve("b21").resolve("c211"));
        node = tree.addPath(RepoPath.atRoot().resolve("a2").resolve("b21").resolve("c212"));
        node = tree.addPath(RepoPath.atRoot().resolve("a2").resolve("b22"));
        node = tree.addPath(RepoPath.atRoot().resolve("a3"));

        String expectedTree =
            ".\n" +
            "├───a1\n" +
            "│   ├───b11\n" +
            "│   └───b12\n" +
            "│       └───c121\n" +
            "├───a2\n" +
            "│   ├───b21\n" +
            "│   │   ├───c211\n" +
            "│   │   └───c212\n" +
            "│   └───b22\n" +
            "└───a3";
        assertTree(expectedTree, tree);
    }

    @Test
    public void path_emojis_first()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a"));
        node = tree.addPath(RepoPath.atRoot().resolve("a").resolve("b"));
        node = tree.addPath(RepoPath.atRoot().resolve("a").resolve("🔑"));

        String expectedTree =
            ".\n" +
            "└───a\n" +
            "    ├───🔑\n" +
            "    └───b";
        assertTree(expectedTree, tree);
    }

    @Test
    public void getOrCreateChildNode_a1_b11_c111()
    {
        RepoPathTree tree = new RepoPathTree();
        RepoPathNode node;

        node = tree.addPath(RepoPath.atRoot().resolve("a1"));
        node = tree.getOrCreateChildNode(node, "b11");
        node = tree.getOrCreateChildNode(node, "c11");

        String expectedTree =
            ".\n" +
            "└───a1\n" +
            "    └───b11\n" +
            "        └───c11";
        assertTree(expectedTree, tree);
    }

    /**
     * This makes sure that the given tree is as expected.
     * @param expectedTree The expected structure of the tree.
     * @param tree The tree that we want to assert.
     */
    public void assertTree(String expectedTree, RepoPathTree tree)
    {
        assertEquals(expectedTree, tree.toString());
    }
}
