package io.nanovc.kdrepos.examples.x;

import io.nanovc.kdrepos.KDRepoIndex;
import io.nanovc.kdrepos.Measurer;
import io.nanovc.kdrepos.OneDRepoIndex;

import java.util.Comparator;

/**
 * A {@link KDRepoIndex} for single dimensional values of type {@link X}.
 */
public class XKDRepoIndex extends OneDRepoIndex<
    X,
    Integer,
    Measurer<X,Integer>,
    Comparator<Integer>
    >
{

    public XKDRepoIndex()
    {
        super(XKDRepoIndex::measureDistance, Integer::compare);
    }

    public static int measureDistance(X o1, X o2)
    {
        return Math.abs(o1.x() - o2.x());
    }
}
