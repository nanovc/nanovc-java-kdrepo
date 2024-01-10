package io.nanovc.indexing.kdtree.bentley1990;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Tests the {@link KDTree}.
 */
class KDTreeTests
{
    @Test
    public void creationTest()
    {
        new KDTree<XY>(XY::extractCoordinate);
    }

    @Test
    public void test_X1Y1_X2Y2()
    {
        KDTree<XY> kdTree = new KDTree<>(XY::extractCoordinate);

        var items = new ArrayList<XY>();
        items.add(new XY(1.0, 1.0));
        items.add(new XY(2.0, 2.0));

        kdTree.index(items);

    }
}
