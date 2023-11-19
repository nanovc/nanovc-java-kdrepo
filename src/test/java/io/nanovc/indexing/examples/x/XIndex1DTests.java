package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.Index1D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the various index implementations for a 1D data structure.
 */
public abstract class XIndex1DTests
    <TIndex extends Index1D<X>>
{


    @Test
    public void creationTest()
    {
        createIndex(1);
    }

    @Test
    public void index_Zero()
    {
        // Create the index:
        TIndex index = createIndex(1);

        // Create the items we want to index:
        X itemZero = new X(0);

        // Index the items:
        index.add(itemZero);

        // Make sure the index returns the expected items:
        assertNeg2Neg1ZeroPos1Pos2(
            index,

            // Closest to -2:
            itemZero,

            // Closest to -1:
            itemZero,

            // Closest to 0:
            itemZero,

            // Closest to +1:
            itemZero,

            // Closest to +2:
            itemZero
        );
    }

    @Test
    public void index_Pos1()
    {
        // Create the index:
        TIndex index = createIndex(1);

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
        TIndex index = createIndex(1);

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
    public void index_Neg1_Pos1()
    {
        // Create the index:
        TIndex index = createIndex(1);

        // Create the items we want to index:
        X itemNeg1 = new X(-1);
        X itemPos1 = new X(1);

        // Index the items:
        index.add(itemNeg1);
        index.add(itemPos1);

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
            itemPos1,

            // Closest to +2:
            itemPos1
        );
    }

    @Test
    public void index_Pos1_Neg1()
    {
        // Create the index:
        TIndex index = createIndex(1);

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

    @Test
    public void index_Neg1_Zero_Pos1()
    {
        // Create the index:
        TIndex index = createIndex(1);

        // Create the items we want to index:
        X itemNeg1 = new X(-1);
        X itemZero = new X(0);
        X itemPos1 = new X(1);

        // Index the items:
        index.add(itemNeg1);
        index.add(itemZero);
        index.add(itemPos1);

        // Make sure the index returns the expected items:
        assertNeg2Neg1ZeroPos1Pos2(
            index,

            // Closest to -2:
            itemNeg1,

            // Closest to -1:
            itemNeg1,

            // Closest to 0:
            itemZero,

            // Closest to +1:
            itemPos1,

            // Closest to +2:
            itemPos1
        );
    }

    @Test
    public void index_Zero_Pos1_Neg1()
    {
        // Create the index:
        TIndex index = createIndex(1);

        // Create the items we want to index:
        X itemNeg1 = new X(-1);
        X itemZero = new X(0);
        X itemPos1 = new X(1);

        // Index the items:
        index.add(itemZero);
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
            itemZero,

            // Closest to +1:
            itemPos1,

            // Closest to +2:
            itemPos1
        );
    }

    @Test
    public void index_Pos1_Neg1_Zero()
    {
        // Create the index:
        TIndex index = createIndex(1);

        // Create the items we want to index:
        X itemNeg1 = new X(-1);
        X itemZero = new X(0);
        X itemPos1 = new X(1);

        // Index the items:
        index.add(itemPos1);
        index.add(itemNeg1);
        index.add(itemZero);

        // Make sure the index returns the expected items:
        assertNeg2Neg1ZeroPos1Pos2(
            index,

            // Closest to -2:
            itemNeg1,

            // Closest to -1:
            itemNeg1,

            // Closest to 0:
            itemZero,

            // Closest to +1:
            itemPos1,

            // Closest to +2:
            itemPos1
        );
    }

    @Test
    public void index_Pos1_Zero_Neg1()
    {
        // Create the index:
        TIndex index = createIndex(1);

        // Create the items we want to index:
        X itemNeg1 = new X(-1);
        X itemZero = new X(0);
        X itemPos1 = new X(1);

        // Index the items:
        index.add(itemPos1);
        index.add(itemZero);
        index.add(itemNeg1);

        // Make sure the index returns the expected items:
        assertNeg2Neg1ZeroPos1Pos2(
            index,

            // Closest to -2:
            itemNeg1,

            // Closest to -1:
            itemNeg1,

            // Closest to 0:
            itemZero,

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
            assertEquals(assertionEntry.expectedNearest, nearest, "We didn't get the expected nearest answer when querying " + assertionEntry.samplePoint);
            assertSame(assertionEntry.expectedNearest, nearest, "We didn't get the expected nearest instance when querying " + assertionEntry.samplePoint);
        }
    }


    /**
     * This generates uniformly distributed random values from -RANGE to +RANGE.
     * It then queries it a defined number of times.
     * <p>
     * Parameterised tests are described here:
     * <a href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources-CsvSource">CSV Sources</a>
     */
    @ParameterizedTest(name = "[{index}] Random Linear Sampling {arguments}")
    @CsvSource(
        delimiter = '|', quoteCharacter = '"',
        useHeadersInDisplayName = true,
        textBlock = """
            Scenario     |  ITEM_COUNT  | RANGE       | QUERIES
            Warmup       |  20_000      | 1_000_000.0 | 20_000
            #
            Tiny Few     |  1_000       | 1_000_000.0 | 1_000
            Small Few    |  10_000      | 1_000_000.0 | 1_000
            Medium Few   |  100_000     | 1_000_000.0 | 1_000
            Large Few    |  1_000_000   | 1_000_000.0 | 1_000
            #
            Tiny Some    |  1_000       | 1_000_000.0 | 10_000
            Small Some   |  10_000      | 1_000_000.0 | 10_000
            Medium Some  |  100_000     | 1_000_000.0 | 10_000
            # Large Some   |  1_000_000   | 1_000_000.0 | 10_000
            #
            Tiny Many    |  1_000       | 1_000_000.0 | 100_000
            Small Many   |  10_000      | 1_000_000.0 | 100_000
            # Medium Many  |  100_000     | 1_000_000.0 | 100_000
            # Large Many   |  1_000_000   | 1_000_000.0 | 100_000
            #
            Tiny Many    |  1_000       | 1_000_000.0 | 1_000_000
            # Small Lots   |  10_000      | 1_000_000.0 | 1_000_000
            # Medium Lots  |  100_000     | 1_000_000.0 | 1_000_000
            # Large Lots   |  1_000_000   | 1_000_000.0 | 1_000_000
            """
    )
    public void index_Random_Linear(String scenario, long itemCount, double range, long queries)
    {
        assertRandom(
            itemCount, range, queries,
            randomGenerator -> randomGenerator.nextDouble(0d, range * 2) - range
        );
    }

    /**
     * This generates normally  distributed random values (gaussian) with a mean of 0 and a standard deviation of RANGE.
     * It then queries it a defined number of times.
     * <p>
     * Parameterised tests are described here:
     * <a href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources-CsvSource">CSV Sources</a>
     */
    @ParameterizedTest(name = "[{index}] Random Gaussian Sampling {arguments}")
    @CsvSource(
        delimiter = '|', quoteCharacter = '"',
        useHeadersInDisplayName = true,
        textBlock = """
            Scenario     |  ITEM_COUNT  | RANGE       | QUERIES
            Warmup       |  20_000      | 1_000_000.0 | 20_000
            #
            Tiny Few     |  1_000       | 1_000_000.0 | 1_000
            Small Few    |  10_000      | 1_000_000.0 | 1_000
            Medium Few   |  100_000     | 1_000_000.0 | 1_000
            Large Few    |  1_000_000   | 1_000_000.0 | 1_000
            #
            Tiny Some    |  1_000       | 1_000_000.0 | 10_000
            Small Some   |  10_000      | 1_000_000.0 | 10_000
            Medium Some  |  100_000     | 1_000_000.0 | 10_000
            # Large Some   |  1_000_000   | 1_000_000.0 | 10_000
            #
            Tiny Many    |  1_000       | 1_000_000.0 | 100_000
            Small Many   |  10_000      | 1_000_000.0 | 100_000
            # Medium Many  |  100_000     | 1_000_000.0 | 100_000
            # Large Many   |  1_000_000   | 1_000_000.0 | 100_000
            #
            Tiny Many    |  1_000       | 1_000_000.0 | 1_000_000
            # Small Lots   |  10_000      | 1_000_000.0 | 1_000_000
            # Medium Lots  |  100_000     | 1_000_000.0 | 1_000_000
            # Large Lots   |  1_000_000   | 1_000_000.0 | 1_000_000
            """
    )
    public void index_Random_Gaussian(String scenario, long itemCount, double range, long queries)
    {
        assertRandom(
            itemCount, range, queries,
            randomGenerator -> randomGenerator.nextGaussian(0d, range)
        );
    }

    /**
     * Asserts the random implementation.
     *
     * @param itemCount      The number of items to generate.
     * @param range          The range of the numbers to generate.
     * @param queries        The queries to perform.
     * @param randomProvider The implementation that gets the next random number for the item.
     */
    private void assertRandom(long itemCount, double range, long queries, Function<RandomGenerator, Double> randomProvider)
    {
        // Create the index:
        TIndex index = createIndex(range);

        // Create the random number generator:
        RandomGeneratorFactory<RandomGenerator> randomGeneratorFactory = RandomGeneratorFactory.getDefault();
        RandomGenerator randomGenerator = randomGeneratorFactory.create(1234L);

        // Generate random values to index:
        for (long i = 0; i < itemCount; i++)
        {
            // Get the next randomValue
            double nextValue = randomProvider.apply(randomGenerator);

            // Create the item:
            X item = new X((int) nextValue);

            // Index the item:
            index.add(item);
        }

        // Create a histogram of the distances:
        Map<Integer, Integer> histogram = new TreeMap<>();

        // Query the index by sweeping through it:
        for (int step = (int) (range * 2 / queries), i = (int) -range; i < range; i += step)
        {
            // Create the item that we want to query:
            X item = new X(i);

            // Query the index:
            X nearest = index.searchNearest(item);

            // Work out the distance to the item:
            int distance = X.measureDistance(item, nearest);

            // Update the histogram:
            histogram.compute(distance, (k, v) -> v == null ? 1 : v + 1);
        }

        // Print out the histogram:
        // for (Map.Entry<Integer, Integer> entry : histogram.entrySet())
        // {
        //     System.out.println("Distance: " + entry.getKey() + " Count: " + entry.getValue());
        // }
    }


    //#region Implementation Specific Methods

    /**
     * A factory method to create an index of the specific type.
     *
     * @param range The range of the data set.
     * @return A new index of the specific type.
     */
    protected abstract TIndex createIndex(double range);

    //#endregion Implementation Specific Methods


    //#region Index Implementations to Test

    public static class LinearTests extends XIndex1DTests<XLinearIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XLinearIndex1D createIndex(double range)
        {
            return new XLinearIndex1D();
        }
    }

    public static class BinaryTreeTests extends XIndex1DTests<XBinaryTreeIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XBinaryTreeIndex1D createIndex(double range)
        {
            return new XBinaryTreeIndex1D();
        }
    }

    public static class GridTests extends XIndex1DTests<XGridIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XGridIndex1D createIndex(double range)
        {
            return new XGridIndex1D(new X((int) -range), new X((int) range), 100);
        }
    }

    public static class RepoTests extends XIndex1DTests<XRepoIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XRepoIndex1D createIndex(double range)
        {
            return new XRepoIndex1D();
        }
    }

    //#endregion


}
