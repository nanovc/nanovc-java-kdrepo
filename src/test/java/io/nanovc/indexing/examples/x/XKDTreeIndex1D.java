package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.kdtree.bentley1990.KDTree;

/**
 * A {@link io.nanovc.indexing.kdtree.bentley1990.KDTree} for single dimensional values of type {@link X}.
 */
public class XKDTreeIndex1D extends KDTree<
    X,
    Integer
    >
{

    public XKDTreeIndex1D()
    {
        super(
            X::extractCoordinate,
            X::measureDistance,
            Integer::compare,
            Integer::sum,
            (left, right) -> left - right ,
            Integer.MAX_VALUE,
            1
        );
    }

}
