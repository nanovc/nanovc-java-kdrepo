package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.Index1D;
import io.nanovc.indexing.Measurer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the various index implementations for a 1D data structure.
 */
public abstract class XIndex1DTests
    <TIndex extends Index1D<X, Integer, Measurer<X, Integer>, Comparator<Integer>>>
{


    @Test
    public void creationTest()
    {
        createIndex();
    }

    @Test
    public void index_Pos1()
    {
        // Create the index:
        TIndex index = createIndex();

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
        TIndex index = createIndex();

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
        TIndex index = createIndex();

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
        TIndex index,
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


    //#region Implementation Specific Methods

    /**
     * A factory method to create an index of the specific type.
     * @return A new index of the specific type.
     */
    protected abstract TIndex createIndex();

    //#endregion Implementation Specific Methods


    //#region Index Implementations to Test

    public static class LinearTests extends XIndex1DTests<XLinearIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XLinearIndex1D createIndex()
        {
            return new XLinearIndex1D();
        }
    }

    public static class BinaryTests extends XIndex1DTests<XBinaryTreeIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XBinaryTreeIndex1D createIndex()
        {
            return new XBinaryTreeIndex1D();
        }
    }

    //#endregion


}
