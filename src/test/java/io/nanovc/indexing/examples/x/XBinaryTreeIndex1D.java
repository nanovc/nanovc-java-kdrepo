package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.binarytree.BinaryTreeIndex1DImplementation;

import java.util.Comparator;

/**
 * A {@link BinaryTreeIndex1DImplementation} for single dimensional values of type {@link X}.
 */
public class XBinaryTreeIndex1D extends BinaryTreeIndex1DImplementation<
    X,
    Integer,
    Measurer<X,Integer>,
    Comparator<Integer>
    >
{

    public XBinaryTreeIndex1D()
    {
        super(X::measureDistance, Integer::compare);
    }

}
