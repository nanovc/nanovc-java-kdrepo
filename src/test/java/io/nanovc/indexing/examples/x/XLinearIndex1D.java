package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.linear.LinearIndex1DImplementation;

import java.util.Comparator;

/**
 * A {@link LinearIndex1DImplementation} for single dimensional values of type {@link X}.
 */
public class XLinearIndex1D extends LinearIndex1DImplementation<
    X,
    Integer,
    Measurer<X,Integer>,
    Comparator<Integer>
    >
{

    public XLinearIndex1D()
    {
        super(X::measureDistance, Integer::compare);
    }

}
