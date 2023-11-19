package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.RepoIndexKD;
import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RepoIndex1D;

import java.util.Comparator;

/**
 * A {@link RepoIndexKD} for single dimensional values of type {@link X}.
 */
public class XRepoIndex1D extends RepoIndex1D<
    X,
    Integer,
    Measurer<X,Integer>,
    Comparator<Integer>
    >
{

    public XRepoIndex1D()
    {
        super(XRepoIndex1D::measureDistance, Integer::compare);
    }

    public static int measureDistance(X o1, X o2)
    {
        return Math.abs(o1.x() - o2.x());
    }
}
