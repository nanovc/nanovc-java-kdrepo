package io.nanovc.indexing.repo;

/**
 * A coordinate in a multidimensional {@link HyperCube hyper cube}.
 * @param values The values in this {@link HyperCoord hyper coordinate}.
 */
public record HyperCoord(Object... values)
{
}
