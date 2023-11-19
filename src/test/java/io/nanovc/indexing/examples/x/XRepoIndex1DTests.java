package io.nanovc.indexing.examples.x;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class XRepoIndex1DTests
{
    @Test
    public void creationTest()
    {
        new XRepoIndex1D();
    }

    @Test
    public void index_Pos1()
    {
        // Create the index:
        XRepoIndex1D index = new XRepoIndex1D();

        // Create the items we want to index:
        X itemPos1 = new X(1);

        // Index the items:
        index.add(itemPos1);

        // Make sure the index returns the expected items:
        assertNeg2Neg1ZeroPos1Pos2(
            index,

            // Closest to -2:
            itemPos1,

            // Closest to -1:
            itemPos1,

            // Closest to 0:
            itemPos1,

            // Closest to +1:
            itemPos1,

            // Closest to +2:
            itemPos1
        );
    }

    @Test
    public void index_Neg1()
    {
        // Create the index:
        XRepoIndex1D index = new XRepoIndex1D();

        // Create the items we want to index:
        X itemNeg1 = new X(-1);

        // Index the items:
        index.add(itemNeg1);

        // Make sure the index returns the expected items:
        assertNeg2Neg1ZeroPos1Pos2(
            index,

            // Closest to -2:
            itemNeg1,

            // Closest to -1:
            itemNeg1,

            // Closest to 0:
            itemNeg1,

            // Closest to +1:
            itemNeg1,

            // Closest to +2:
            itemNeg1
        );
    }

    @Test
    public void index_Pos1_Neg1()
    {
        // Create the index:
        XRepoIndex1D index = new XRepoIndex1D();

        // Create the items we want to index:
        X itemPos1 = new X(1);
        X itemNeg1 = new X(-1);

        // Index the items:
        index.add(itemPos1);
        index.add(itemNeg1);

        // Make sure the index returns the expected items:
        assertNeg2Neg1ZeroPos1Pos2(
            index,

            // Closest to -2:
            itemNeg1,

            // Closest to -1:
            itemNeg1,

            // Closest to 0:
            itemPos1,

            // Closest to +1:
            itemPos1,

            // Closest to +2:
            itemPos1
        );
    }

    /**
     * This performs the assertion of the index at specific intervals.
     *
     * @param index                 The index to test.
     * @param expectedClosestToNeg2 The item that is expected to be closest to -2.
     * @param expectedClosestToNeg1 The item that is expected to be closest to -1.
     * @param expectedClosestToZero The item that is expected to be closest to 0.
     * @param expectedClosestToPos1 The item that is expected to be closest to +1.
     * @param expectedClosestToPos2 The item that is expected to be closest to +2.
     */
    void assertNeg2Neg1ZeroPos1Pos2(
        XRepoIndex1D index,
        X expectedClosestToNeg2,
        X expectedClosestToNeg1,
        X expectedClosestToZero,
        X expectedClosestToPos1,
        X expectedClosestToPos2
    )
    {
        // Create the assertion entries that we need:
        record IndexAssertionEntry(X samplePoint, X expectedNearest) {}
        List<IndexAssertionEntry> assertionEntries = new ArrayList<>();
        assertionEntries.add(new IndexAssertionEntry(new X(-2), expectedClosestToNeg2));
        assertionEntries.add(new IndexAssertionEntry(new X(-1), expectedClosestToNeg1));
        assertionEntries.add(new IndexAssertionEntry(new X(0), expectedClosestToZero));
        assertionEntries.add(new IndexAssertionEntry(new X(1), expectedClosestToPos1));
        assertionEntries.add(new IndexAssertionEntry(new X(2), expectedClosestToPos2));

        // Go through each assertion:
        for (IndexAssertionEntry assertionEntry : assertionEntries)
        {
            // Search for the nearest item:
            X nearest = index.searchNearest(assertionEntry.samplePoint());

            // Make sure the item is the exact same instance as the original one:
            assertEquals(assertionEntry.expectedNearest, nearest);
            assertSame(assertionEntry.expectedNearest, nearest);
        }
    }


}
