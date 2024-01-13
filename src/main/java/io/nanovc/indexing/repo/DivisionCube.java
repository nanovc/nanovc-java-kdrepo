package io.nanovc.indexing.repo;

/**
 * A cube of divisions.
 * This is used to divide a {@link HyperCube} into {@link Division divisions}.
 */
public class DivisionCube
{
    /**
     * The {@link HyperCube} of the kd-space that we are dividing.
     */
    public HyperCube hyperCube;

    /**
     * This is the root of the division tree.
     * Walk this to find the specific division of data that we have.
     */
    public DivisionDimension rootDimension;
}
