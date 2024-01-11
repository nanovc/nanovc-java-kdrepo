package io.nanovc.indexing.examples.xy;

import io.nanovc.indexing.kdtree.bentley1990.KDTree;

/**
 * A {@link io.nanovc.indexing.kdtree.bentley1990.KDTree} for two-dimensional values of type {@link XY}.
 */
public class XYKDTree extends KDTree<
    XY,
    Double
    >
{

    public XYKDTree()
    {
        super(
            XY::extractCoordinate,
            XY::measureDistanceL2NormEuclidean,
            Double::compare,
            Double::sum,
            (left, right) -> left - right,
            Double.MAX_VALUE,
            2
        );
    }

}
