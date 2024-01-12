package io.nanovc.indexing.repo.ranges;

/**
 * This determines in which split (lower, higher or both)  to include the splitValue in.
 */
public enum RangeSplitInclusion
{
    /**
     * Include the splitValue in the lower split.
     */
    Lower,

    /**
     * Include the splitValue in the higher split.
     */
    Higher,

    /**
     * Include the splitValue in both the lower split and higher split.
     */
    Both
}
