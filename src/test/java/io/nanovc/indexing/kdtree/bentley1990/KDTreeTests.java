package io.nanovc.indexing.kdtree.bentley1990;

import io.nanovc.indexing.Index;
import io.nanovc.indexing.examples.x.X;
import io.nanovc.indexing.examples.x.XKDTreeIndex1D;
import io.nanovc.indexing.examples.xy.XY;
import io.nanovc.indexing.examples.xy.XYKDTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link KDTree}.
 */
class KDTreeTests
{
    @Test
    public void creationTest()
    {
        new XYKDTree();
    }

    @Test
    public void test_X1Y1_X2Y2()
    {
        XYKDTree kdTree = new XYKDTree();

        var items = new ArrayList<XY>();
        items.add(new XY(1.0, 1.0));
        items.add(new XY(2.0, 2.0));

        // Build the index:
        kdTree.index(items);

        String expectedIndex =
            ">───1D:1.0\n" +
            "    ├─L─XY[x=1.0, y=1.0]\n" +
            "    └─H─XY[x=2.0, y=2.0]";
        assertIndex(expectedIndex, kdTree);

        // Search for the nearest neighbour:
        XY nearest;

        nearest = kdTree.searchNearest(0);
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(1);
        assertEquals(new XY(2.0, 2.0), nearest);

        nearest = kdTree.searchNearest(new XY(0.9, 0.9));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(1.1, 1.1));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(1.9, 1.9));
        assertEquals(new XY(2.0, 2.0), nearest);

        nearest = kdTree.searchNearest(new XY(2.1, 2.1));
        assertEquals(new XY(2.0, 2.0), nearest);
    }

    @Test
    public void test_X1Y1_X1YNeg1_XNeg1YNeg1_XNeg1Y1()
    {
        XYKDTree kdTree = new XYKDTree();

        var items = new ArrayList<XY>();
        items.add(new XY(1.0, 1.0));
        items.add(new XY(1.0, -1.0));
        items.add(new XY(-1.0, -1.0));
        items.add(new XY(-1.0, 1.0));

        // Build the index:
        kdTree.index(items);

        String expectedIndex =
            ">───1D:-1.0\n" +
            "    ├─L─2D:-1.0\n" +
            "    │   ├─L─XY[x=-1.0, y=-1.0]\n" +
            "    │   └─H─XY[x=-1.0, y=1.0]\n" +
            "    └─H─2D:-1.0\n" +
            "        ├─L─XY[x=1.0, y=-1.0]\n" +
            "        └─H─XY[x=1.0, y=1.0]";
        assertIndex(expectedIndex, kdTree);

        // Search for the nearest neighbour:
        XY nearest;

        nearest = kdTree.searchNearest(new XY(0.0, 0.0));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(0.5, 0.5));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(-0.5, -0.5));
        assertEquals(new XY(-1.0, -1.0), nearest);

        nearest = kdTree.searchNearest(new XY(-0.5, 0.5));
        assertEquals(new XY(-1.0, 1.0), nearest);
    }

    @Test
    public void test_X1Y1_X2Y2_X3Y3_X4Y4_X3Y3_X2Y2_X1Y1_Bucket1()
    {
        XYKDTree kdTree = new XYKDTree();
        kdTree.cutoff = 1;

        var items = new ArrayList<XY>();
        items.add(new XY(1.0, 1.0));
        items.add(new XY(2.0, 2.0));
        items.add(new XY(3.0, 3.0));
        items.add(new XY(4.0, 4.0));
        items.add(new XY(3.0, 3.0));
        items.add(new XY(2.0, 2.0));
        items.add(new XY(1.0, 1.0));

        // Build the index:
        kdTree.index(items);

        String expectedIndex =
            ">───1D:2.0\n" +
            "    ├─L─2D:1.0\n" +
            "    │   ├─L─1D:1.0\n" +
            "    │   │   ├─L─XY[x=1.0, y=1.0]\n" +
            "    │   │   └─H─XY[x=1.0, y=1.0]\n" +
            "    │   └─H─1D:2.0\n" +
            "    │       ├─L─XY[x=2.0, y=2.0]\n" +
            "    │       └─H─XY[x=2.0, y=2.0]\n" +
            "    └─H─2D:3.0\n" +
            "        ├─L─1D:3.0\n" +
            "        │   ├─L─XY[x=3.0, y=3.0]\n" +
            "        │   └─H─XY[x=3.0, y=3.0]\n" +
            "        └─H─XY[x=4.0, y=4.0]";
        assertIndex(expectedIndex, kdTree);

        // Search for the nearest neighbour:
        XY nearest;

        nearest = kdTree.searchNearest(new XY(0.0, 0.0));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(0.5, 0.5));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(-0.5, -0.5));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(-0.5, 0.5));
        assertEquals(new XY(1.0, 1.0), nearest);
    }

    @Test
    public void test_X1Y1_X2Y2_X3Y3_X4Y4_X3Y3_X2Y2_X1Y1_Bucket2()
    {
        XYKDTree kdTree = new XYKDTree();
        kdTree.cutoff = 2;

        var items = new ArrayList<XY>();
        items.add(new XY(1.0, 1.0));
        items.add(new XY(2.0, 2.0));
        items.add(new XY(3.0, 3.0));
        items.add(new XY(4.0, 4.0));
        items.add(new XY(3.0, 3.0));
        items.add(new XY(2.0, 2.0));
        items.add(new XY(1.0, 1.0));

        // Build the index:
        kdTree.index(items);

        String expectedIndex =
            ">───1D:2.0\n" +
            "    ├─L─2D:1.0\n" +
            "    │   ├─L─XY[x=1.0, y=1.0], XY[x=1.0, y=1.0]\n" +
            "    │   └─H─XY[x=2.0, y=2.0], XY[x=2.0, y=2.0]\n" +
            "    └─H─2D:3.0\n" +
            "        ├─L─XY[x=3.0, y=3.0], XY[x=3.0, y=3.0]\n" +
            "        └─H─XY[x=4.0, y=4.0]";
        assertIndex(expectedIndex, kdTree);

        // Search for the nearest neighbour:
        XY nearest;

        nearest = kdTree.searchNearest(new XY(0.0, 0.0));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(0.5, 0.5));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(-0.5, -0.5));
        assertEquals(new XY(1.0, 1.0), nearest);

        nearest = kdTree.searchNearest(new XY(-0.5, 0.5));
        assertEquals(new XY(1.0, 1.0), nearest);
    }

    @Test
    public void test_X1_X2_X3_X4()
    {
        XKDTreeIndex1D kdTree = new XKDTreeIndex1D();

        var items = new ArrayList<X>();
        items.add(new X(1));
        items.add(new X(2));
        items.add(new X(3));
        items.add(new X(4));

        // Build the index:
        kdTree.index(items);

        String expectedIndex =
            ">───1D:2\n" +
            "    ├─L─1D:1\n" +
            "    │   ├─L─X[x=1]\n" +
            "    │   └─H─X[x=2]\n" +
            "    └─H─1D:3\n" +
            "        ├─L─X[x=3]\n" +
            "        └─H─X[x=4]";
        assertIndex(expectedIndex, kdTree);

        // Search for the nearest neighbour:
        X nearest;

        nearest = kdTree.searchNearest(new X(0));
        assertEquals(new X(1), nearest);

        nearest = kdTree.searchNearest(new X(5));
        assertEquals(new X(4), nearest);
    }

    @Test
    public void test_X4_X3_X2_X1()
    {
        XKDTreeIndex1D kdTree = new XKDTreeIndex1D();

        var items = new ArrayList<X>();
        items.add(new X(4));
        items.add(new X(3));
        items.add(new X(2));
        items.add(new X(1));

        // Build the index:
        kdTree.index(items);

        String expectedIndex =
            ">───1D:2\n" +
            "    ├─L─1D:1\n" +
            "    │   ├─L─X[x=1]\n" +
            "    │   └─H─X[x=2]\n" +
            "    └─H─1D:3\n" +
            "        ├─L─X[x=3]\n" +
            "        └─H─X[x=4]";
        assertIndex(expectedIndex, kdTree);

        // Search for the nearest neighbour:
        X nearest;

        nearest = kdTree.searchNearest(new X(0));
        assertEquals(new X(1), nearest);

        nearest = kdTree.searchNearest(new X(5));
        assertEquals(new X(4), nearest);
    }

    @Test
    public void test_X1_X2_X3_X4_X3_X2_X1()
    {
        XKDTreeIndex1D kdTree = new XKDTreeIndex1D();

        var items = new ArrayList<X>();
        items.add(new X(1));
        items.add(new X(2));
        items.add(new X(3));
        items.add(new X(4));
        items.add(new X(3));
        items.add(new X(2));
        items.add(new X(1));

        // Build the index:
        kdTree.index(items);

        String expectedIndex =
            ">───1D:2\n" +
            "    ├─L─1D:1\n" +
            "    │   ├─L─1D:1\n" +
            "    │   │   ├─L─X[x=1]\n" +
            "    │   │   └─H─X[x=1]\n" +
            "    │   └─H─1D:2\n" +
            "    │       ├─L─X[x=2]\n" +
            "    │       └─H─X[x=2]\n" +
            "    └─H─1D:3\n" +
            "        ├─L─1D:3\n" +
            "        │   ├─L─X[x=3]\n" +
            "        │   └─H─X[x=3]\n" +
            "        └─H─X[x=4]";
        assertIndex(expectedIndex, kdTree);

        // Search for the nearest neighbour:
        X nearest;

        nearest = kdTree.searchNearest(new X(0));
        assertEquals(new X(1), nearest);

        nearest = kdTree.searchNearest(new X(5));
        assertEquals(new X(4), nearest);
    }

    public void assertIndex(String expectedIndex, Index<?> index)
    {
        // Get the index value:
        String actualIndex = index.toString();

        // Make sure it is as expected:
        assertEquals(expectedIndex, actualIndex);
    }
}
