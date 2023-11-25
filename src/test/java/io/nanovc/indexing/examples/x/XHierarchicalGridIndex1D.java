package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.RangeFinder;
import io.nanovc.indexing.RangeSplitter;
import io.nanovc.indexing.grid.GridIndex1DImplementation;
import io.nanovc.indexing.hierarchicalgrid.HierarchicalGridIndex1DImplementation;

import java.util.Comparator;

/**
 * A {@link GridIndex1DImplementation} for single dimensional values of type {@link X}.
 */
public class XHierarchicalGridIndex1D extends HierarchicalGridIndex1DImplementation<
    X,
    Integer,
    Measurer<X,Integer>,
    Comparator<Integer>,
    RangeSplitter<X>,
    RangeFinder<X>,
    XHierarchicalGridIndex1D
    >
{
    public XHierarchicalGridIndex1D(X minRange, X maxRange, int divisions, int maxItemThreshold, int smallestSplittingDistance)
    {
        super(
            minRange, maxRange,
            divisions,
            X::measureDistance, Integer::compare, X::splitRange, X::findIndexInRange,
            XHierarchicalGridIndex1D::createXHierarchicalGridIndex1DSubGrid,
            maxItemThreshold,
            smallestSplittingDistance
        );
    }

    /**
     * A factory method to create a new sub-grid for the given range.
     *
     * @return A new sub-grid for the given range.
     */
    public static XHierarchicalGridIndex1D createXHierarchicalGridIndex1DSubGrid(X minRange, X maxRange, int divisions, Measurer<X, Integer> measurer, Comparator<Integer> comparator, RangeSplitter<X> rangeSplitter, RangeFinder<X> rangeFinder, int maxItemThreshold, int smallestSplittingDistance)
    {
        return new XHierarchicalGridIndex1D(minRange, maxRange, divisions, maxItemThreshold, smallestSplittingDistance);
    }
}
