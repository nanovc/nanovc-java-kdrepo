package io.nanovc.indexing.examples.xy;

import io.nanovc.indexing.Measurer;
import io.nanovc.indexing.linear.LinearIndex2DImplementation;

import java.util.Comparator;

/**
 * A {@link LinearIndex2DImplementation} for two-dimensional values of type {@link XY}.
 */
public class XYLinearIndex2D extends LinearIndex2DImplementation<
    XY,
    Double,
    Measurer<XY,Double>,
    Comparator<Double>
    >
{

    public XYLinearIndex2D()
    {
        super(XY::measureDistanceL2NormEuclidean,  Double::compare);
    }

}
