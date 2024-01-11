package io.nanovc.indexing.kdtree.bentley1990;

import io.nanovc.indexing.examples.xy.XY;
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
        new KDTree<XY>(XY::extractCoordinate, XY::measureDistanceL2NormEuclidean);
    }

    @Test
    public void test_X1Y1_X2Y2()
    {
        KDTree<XY> kdTree = new KDTree<>(XY::extractCoordinate, XY::measureDistanceL2NormEuclidean);

        var items = new ArrayList<XY>();
        items.add(new XY(1.0, 1.0));
        items.add(new XY(2.0, 2.0));

        // Build the index:
        kdTree.index(items);

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
        KDTree<XY> kdTree = new KDTree<>(XY::extractCoordinate, XY::measureDistanceL2NormEuclidean);

        var items = new ArrayList<XY>();
        items.add(new XY(1.0, 1.0));
        items.add(new XY(1.0, -1.0));
        items.add(new XY(-1.0, -1.0));
        items.add(new XY(-1.0, 1.0));

        // Build the index:
        kdTree.index(items);

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
}
