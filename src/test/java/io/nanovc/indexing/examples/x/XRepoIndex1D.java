package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;
import io.nanovc.indexing.repo.RepoIndex1DImplementation;

import java.util.Comparator;

/**
 * A {@link RepoIndex1DImplementation} for single dimensional values of type {@link X}.
 */
public class XRepoIndex1D extends RepoIndex1DImplementation<
    X,
    Integer,
    Measurer<X,Integer>,
    Comparator<Integer>,
    RangeSplitter<X>,
    RangeFinder<X>
    >
{

    public XRepoIndex1D(X minRange, X maxRange, int divisions)
    {
        super(minRange, maxRange, divisions, X::measureDistance, Integer::compare, X::splitRange, X::findIndexInRange);
    }

}
