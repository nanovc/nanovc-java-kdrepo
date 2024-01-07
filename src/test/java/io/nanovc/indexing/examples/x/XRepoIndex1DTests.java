package io.nanovc.indexing.examples.x;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link XRepoIndex1D} implementation.
 */
public class XRepoIndex1DTests
{
    @Test
    public void creationTest()
    {
        new XRepoIndex1D(new X(-1), new X(1), 10, 10, 1);
    }

    @Test
    public void test_Index_0_Query_0()
    {
        // Create the index:
        XRepoIndex1D index = new XRepoIndex1D(new X(-1), new X(1), 10, 10, 1);

        // Add an item to the index:
        X itemZero = new X(0);
        index.add(itemZero);

        // Query the item:
        X nearest = index.searchNearest(itemZero);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);
    }

    @Test
    public void test_Index_0_Query_0_1()
    {
        // Create the index:
        XRepoIndex1D index = new XRepoIndex1D(new X(-1), new X(1), 10, 10, 1);

        // Add an item to the index:
        X itemZero = new X(0);
        index.add(itemZero);

        // Query the item:
        X nearest = index.searchNearest(itemZero);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);

        // Query the item:
        X itemOne = new X(1);
        nearest = index.searchNearest(itemOne);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);
    }
}
