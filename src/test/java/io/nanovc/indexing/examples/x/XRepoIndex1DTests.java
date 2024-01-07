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

    @Test
    public void test_Index_1_10_11_Query_1_10_11()
    {
        // Create the index:
        XRepoIndex1D index = new XRepoIndex1D(new X(-1), new X(1), 10, 10, 1);

        // Define the items of interest:
        X item1 = new X(1);
        X item10 = new X(10);
        X item11 = new X(11);

        // Add the items to the index:
        index.add(item1);
        index.add(item10);
        index.add(item11);

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item1);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item10);
        assertEquals(item10, nearest);

        nearest = index.searchNearest(item11);
        assertEquals(item11, nearest);
    }

    @Test
    public void test_Index_1_10_11_Query_0_9_12()
    {
        // Create the index:
        XRepoIndex1D index = new XRepoIndex1D(new X(-1), new X(1), 10, 10, 1);

        // Define the items of interest:
        X item0 = new X(0);
        X item1 = new X(1);
        X item9 = new X(9);
        X item10 = new X(10);
        X item11 = new X(11);
        X item12 = new X(12);

        // Add the items to the index:
        index.add(item1);
        index.add(item10);
        index.add(item11);

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item0);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item9);
        assertEquals(item10, nearest);

        nearest = index.searchNearest(item12);
        assertEquals(item11, nearest);
    }
}
