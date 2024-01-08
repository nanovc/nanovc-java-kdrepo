package io.nanovc.indexing.examples.x;

import io.nanovc.indexing.Index1D;
import io.nanovc.indexing.TimeHistogram;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests the various index implementations for a 1D data structure.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class XIndex1DTests
    <TIndex extends Index1D<X>>
{

    /**
     * The root path for performance data.
     */
    public static final Path PERFORMANCE_DATA_ROOT_PATH = Path.of("analysis", "performance", "data");

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
            new Single<>(itemZero),

            // Closest to -1:
            new Single<>(itemZero),

            // Closest to 0:
            new Single<>(itemZero),

            // Closest to +1:
            new Single<>(itemZero),

            // Closest to +2:
            new Single<>(itemZero)
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
            new Single<>(itemPos1),

            // Closest to -1:
            new Single<>(itemPos1),

            // Closest to 0:
            new Single<>(itemPos1),

            // Closest to +1:
            new Single<>(itemPos1),

            // Closest to +2:
            new Single<>(itemPos1)
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
            new Single<>(itemNeg1),

            // Closest to -1:
            new Single<>(itemNeg1),

            // Closest to 0:
            new Single<>(itemNeg1),

            // Closest to +1:
            new Single<>(itemNeg1),

            // Closest to +2:
            new Single<>(itemNeg1)
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
            new Single<>(itemNeg1),

            // Closest to -1:
            new Single<>(itemNeg1),

            // Closest to 0:
            new Either<>(itemNeg1, itemPos1),

            // Closest to +1:
            new Single<>(itemPos1),

            // Closest to +2:
            new Single<>(itemPos1)
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
            new Single<>(itemNeg1),

            // Closest to -1:
            new Single<>(itemNeg1),

            // Closest to 0:
            new Either<>(itemNeg1, itemPos1),

            // Closest to +1:
            new Single<>(itemPos1),

            // Closest to +2:
            new Single<>(itemPos1)
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
            new Single<>(itemNeg1),

            // Closest to -1:
            new Single<>(itemNeg1),

            // Closest to 0:
            new Single<>(itemZero),

            // Closest to +1:
            new Single<>(itemPos1),

            // Closest to +2:
            new Single<>(itemPos1)
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
            new Single<>(itemNeg1),

            // Closest to -1:
            new Single<>(itemNeg1),

            // Closest to 0:
            new Single<>(itemZero),

            // Closest to +1:
            new Single<>(itemPos1),

            // Closest to +2:
            new Single<>(itemPos1)
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
            new Single<>(itemNeg1),

            // Closest to -1:
            new Single<>(itemNeg1),

            // Closest to 0:
            new Single<>(itemZero),

            // Closest to +1:
            new Single<>(itemPos1),

            // Closest to +2:
            new Single<>(itemPos1)
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
            new Single<>(itemNeg1),

            // Closest to -1:
            new Single<>(itemNeg1),

            // Closest to 0:
            new Single<>(itemZero),

            // Closest to +1:
            new Single<>(itemPos1),

            // Closest to +2:
            new Single<>(itemPos1)
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
        SingleOrEither<X> expectedClosestToNeg2,
        SingleOrEither<X> expectedClosestToNeg1,
        SingleOrEither<X> expectedClosestToZero,
        SingleOrEither<X> expectedClosestToPos1,
        SingleOrEither<X> expectedClosestToPos2
    )
    {
        // Create the assertion entries that we need:
        record IndexAssertionEntry(X samplePoint, SingleOrEither<X> expectedNearest) {}
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

            // Get the options that the item could be:
            SingleOrEither<X> expectedNearest = assertionEntry.expectedNearest;
            List<X> possibleItems = expectedNearest.get();

            // Make sure that the item matches any of the possible options:
            boolean matchesAny = false;
            for (X possibleItem : possibleItems)
            {
                // Check if the value is the same:
                if (nearest.equals(possibleItem))
                {
                    // This is a match.
                    matchesAny = true;
                }
            }

            // Make sure we matched any option:
            if (!matchesAny)
                fail("We didn't get the expected nearest answer when querying " + assertionEntry.samplePoint + "... Expected: " + assertionEntry.expectedNearest + " Got: " + nearest);
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
            Scenario |  ITEM_COUNT  | RANGE       | QUERIES
            Warmup   |  20_000      | 1_000_000.0 | 20_000
            #
            Tiny     |  1_000       | 1_000_000.0 | 10_000
            Tiny     |  1_250       | 1_000_000.0 | 10_000
            Tiny     |  1_500       | 1_000_000.0 | 10_000
            Tiny     |  1_750       | 1_000_000.0 | 10_000
            Tiny     |  2_000       | 1_000_000.0 | 10_000
            Tiny     |  2_500       | 1_000_000.0 | 10_000
            Tiny     |  3_000       | 1_000_000.0 | 10_000
            Tiny     |  4_000       | 1_000_000.0 | 10_000
            Tiny     |  5_000       | 1_000_000.0 | 10_000
            Tiny     |  6_000       | 1_000_000.0 | 10_000
            Tiny     |  7_000       | 1_000_000.0 | 10_000
            Tiny     |  8_000       | 1_000_000.0 | 10_000
            Tiny     |  9_000       | 1_000_000.0 | 10_000
            Small    |  10_000      | 1_000_000.0 | 10_000
            Small    |  12_500      | 1_000_000.0 | 10_000
            Small    |  15_000      | 1_000_000.0 | 10_000
            Small    |  17_500      | 1_000_000.0 | 10_000
            Small    |  20_000      | 1_000_000.0 | 10_000
            Small    |  25_000      | 1_000_000.0 | 10_000
            Small    |  30_000      | 1_000_000.0 | 10_000
            Small    |  40_000      | 1_000_000.0 | 10_000
            Small    |  50_000      | 1_000_000.0 | 10_000
            Small    |  60_000      | 1_000_000.0 | 10_000
            Small    |  70_000      | 1_000_000.0 | 10_000
            Small    |  80_000      | 1_000_000.0 | 10_000
            Small    |  90_000      | 1_000_000.0 | 10_000
            Medium   |  100_000     | 1_000_000.0 | 10_000
            Medium   |  125_000     | 1_000_000.0 | 10_000
            Medium   |  150_000     | 1_000_000.0 | 10_000
            Medium   |  175_000     | 1_000_000.0 | 10_000
            Medium   |  200_000     | 1_000_000.0 | 10_000
            Medium   |  250_000     | 1_000_000.0 | 10_000
            Medium   |  300_000     | 1_000_000.0 | 10_000
            Medium   |  400_000     | 1_000_000.0 | 10_000
            Medium   |  500_000     | 1_000_000.0 | 10_000
            Medium   |  600_000     | 1_000_000.0 | 10_000
            Medium   |  700_000     | 1_000_000.0 | 10_000
            Medium   |  800_000     | 1_000_000.0 | 10_000
            Medium   |  900_000     | 1_000_000.0 | 10_000
            Large    |  1_000_000   | 1_000_000.0 | 10_000
            Large    |  1_250_000   | 1_000_000.0 | 10_000
            Large    |  1_500_000   | 1_000_000.0 | 10_000
            Large    |  1_750_000   | 1_000_000.0 | 10_000
            Large    |  2_000_000   | 1_000_000.0 | 10_000
            Large    |  2_500_000   | 1_000_000.0 | 10_000
            Large    |  3_000_000   | 1_000_000.0 | 10_000
            Large    |  4_000_000   | 1_000_000.0 | 10_000
            Large    |  5_000_000   | 1_000_000.0 | 10_000
            Large    |  6_000_000   | 1_000_000.0 | 10_000
            Large    |  7_000_000   | 1_000_000.0 | 10_000
            Large    |  8_000_000   | 1_000_000.0 | 10_000
            Large    |  9_000_000   | 1_000_000.0 | 10_000
            Huge     |  10_000_000  | 1_000_000.0 | 10_000
            """
    )
    public void index_Random_Linear(String scenario, long itemCount, double range, long queries, TestInfo testInfo)
    {
        PerformanceStats performanceStats = assertRandom(
            itemCount, range, queries,
            randomGenerator -> randomGenerator.nextDouble(0d, range * 2) - range
        );
        System.out.println(performanceStats.getPerformanceStatsAsString());
        savePerformanceData(new PerformanceData(testInfo, performanceStats, Map.of("Scenario", scenario, "Item Count", itemCount, "Range", range, "Queries", queries)));
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
            Scenario |  ITEM_COUNT  | RANGE       | QUERIES
            Warmup   |  20_000      | 1_000_000.0 | 20_000
            #
            Tiny     |  1_000       | 1_000_000.0 | 10_000
            Tiny     |  1_250       | 1_000_000.0 | 10_000
            Tiny     |  1_500       | 1_000_000.0 | 10_000
            Tiny     |  1_750       | 1_000_000.0 | 10_000
            Tiny     |  2_000       | 1_000_000.0 | 10_000
            Tiny     |  2_500       | 1_000_000.0 | 10_000
            Tiny     |  3_000       | 1_000_000.0 | 10_000
            Tiny     |  4_000       | 1_000_000.0 | 10_000
            Tiny     |  5_000       | 1_000_000.0 | 10_000
            Tiny     |  6_000       | 1_000_000.0 | 10_000
            Tiny     |  7_000       | 1_000_000.0 | 10_000
            Tiny     |  8_000       | 1_000_000.0 | 10_000
            Tiny     |  9_000       | 1_000_000.0 | 10_000
            Small    |  10_000      | 1_000_000.0 | 10_000
            Small    |  12_500      | 1_000_000.0 | 10_000
            Small    |  15_000      | 1_000_000.0 | 10_000
            Small    |  17_500      | 1_000_000.0 | 10_000
            Small    |  20_000      | 1_000_000.0 | 10_000
            Small    |  25_000      | 1_000_000.0 | 10_000
            Small    |  30_000      | 1_000_000.0 | 10_000
            Small    |  40_000      | 1_000_000.0 | 10_000
            Small    |  50_000      | 1_000_000.0 | 10_000
            Small    |  60_000      | 1_000_000.0 | 10_000
            Small    |  70_000      | 1_000_000.0 | 10_000
            Small    |  80_000      | 1_000_000.0 | 10_000
            Small    |  90_000      | 1_000_000.0 | 10_000
            Medium   |  100_000     | 1_000_000.0 | 10_000
            Medium   |  125_000     | 1_000_000.0 | 10_000
            Medium   |  150_000     | 1_000_000.0 | 10_000
            Medium   |  175_000     | 1_000_000.0 | 10_000
            Medium   |  200_000     | 1_000_000.0 | 10_000
            Medium   |  250_000     | 1_000_000.0 | 10_000
            Medium   |  300_000     | 1_000_000.0 | 10_000
            Medium   |  400_000     | 1_000_000.0 | 10_000
            Medium   |  500_000     | 1_000_000.0 | 10_000
            Medium   |  600_000     | 1_000_000.0 | 10_000
            Medium   |  700_000     | 1_000_000.0 | 10_000
            Medium   |  800_000     | 1_000_000.0 | 10_000
            Medium   |  900_000     | 1_000_000.0 | 10_000
            Large    |  1_000_000   | 1_000_000.0 | 10_000
            Large    |  1_250_000   | 1_000_000.0 | 10_000
            Large    |  1_500_000   | 1_000_000.0 | 10_000
            Large    |  1_750_000   | 1_000_000.0 | 10_000
            Large    |  2_000_000   | 1_000_000.0 | 10_000
            Large    |  2_500_000   | 1_000_000.0 | 10_000
            Large    |  3_000_000   | 1_000_000.0 | 10_000
            Large    |  4_000_000   | 1_000_000.0 | 10_000
            Large    |  5_000_000   | 1_000_000.0 | 10_000
            Large    |  6_000_000   | 1_000_000.0 | 10_000
            Large    |  7_000_000   | 1_000_000.0 | 10_000
            Large    |  8_000_000   | 1_000_000.0 | 10_000
            Large    |  9_000_000   | 1_000_000.0 | 10_000
            Huge     |  10_000_000  | 1_000_000.0 | 10_000
            """
    )
    public void index_Random_Gaussian(String scenario, long itemCount, double range, long queries, TestInfo testInfo)
    {
        PerformanceStats performanceStats = assertRandom(
            itemCount, range, queries,
            randomGenerator -> randomGenerator.nextGaussian(0d, range)
        );
        System.out.println(performanceStats.getPerformanceStatsAsString());
        savePerformanceData(new PerformanceData(testInfo, performanceStats, Map.of("Scenario", scenario, "Item Count", itemCount, "Range", range, "Queries", queries)));
    }

    /**
     * This holds all the performance data that is to be written to the analysis data folder.
     */
    public List<PerformanceData> allPerformanceData = new ArrayList<>();

    /**
     * Saves the performance data to save so that we can write it to CSV at the end of the tests.
     *
     * @param performanceData The performance data to save so that we can write it to CSV at the end of the tests.
     */
    public void savePerformanceData(PerformanceData performanceData)
    {
        allPerformanceData.add(performanceData);
    }

    /**
     * This writes all the performance data that was gathered for this run.
     */
    @AfterAll
    public void writePerformanceCSVData()
    {
        try
        {
            // Group all the performance tests by the file path that we need to write to:
            Map<Path, List<PerformanceData>> groupedPerformanceData = new LinkedHashMap<>();

            // Get the current time stamp for this run:
            LocalDateTime now = LocalDateTime.now();

            // Format the time stamp:
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

            // Go through all the performance data and write it out:
            for (PerformanceData performanceData : allPerformanceData)
            {
                // Get path to write the performance data to:
                Path filePath = getPerformanceStatsCSVFilePath(PERFORMANCE_DATA_ROOT_PATH, timestamp, performanceData);

                // Make sure the path exists:
                Files.createDirectories(filePath.getParent());

                // Get the grouping of performance data for this file path:
                List<PerformanceData> group = groupedPerformanceData.computeIfAbsent(filePath, path -> new ArrayList<>());

                // Add the performance data to the group:
                group.add(performanceData);
            }
            // Now we have grouped all the performance data by the file path that we want to save it to.

            // Go through each group and write it to CSV file:
            for (Map.Entry<Path, List<PerformanceData>> entry : groupedPerformanceData.entrySet())
            {
                // Get the file path:
                Path filePath = entry.getKey();

                // Get the group of performance data to write:
                List<PerformanceData> group = entry.getValue();

                // Write the performance data:
                writePerformanceStatsToCSV(filePath, now, group);

                // Write the histogram data:
                writePerformanceHistogramsToCSV(filePath, now, group);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is used for tracking performance stats.
     *
     * @param itemCount           The number of items that were processed.
     * @param addDurationNanos    The total number of nanoseconds it took to add all the items.
     * @param queryCount          The total number of items that were searched for.
     * @param searchDurationNanos The total number of nanoseconds it took to add all the items.
     * @param addHistogram        The histogram of the time taken to add entries.
     * @param searchHistogram     The histogram of the time taken to search.
     */
    public record PerformanceStats(long itemCount, long addDurationNanos, long queryCount, long searchDurationNanos, TimeHistogram addHistogram, TimeHistogram searchHistogram)
    {
        /**
         * Gets the performance stats as a string.
         *
         * @return The performance stats as a string.
         */
        public String getPerformanceStatsAsString()
        {
            return String.format(
                "Items: %,-15d    Adding: %,20dns %,20d/s    Queries: %,-15d   Searching %,20dns %,20d/s",
                itemCount(),
                addDurationNanos(),
                itemCount() * 1_000_000_000L / addDurationNanos(),
                queryCount(),
                searchDurationNanos(),
                queryCount() * 1_000_000_000L / searchDurationNanos()
            );
        }
    }

    /**
     * This captures the performance data for a test that is to be written later.
     *
     * @param testInfo         The information about the test that has been run.
     * @param performanceStats The performance stats for the actual run.
     * @param context          Additional context for the test that is running.
     */
    public record PerformanceData(TestInfo testInfo, PerformanceStats performanceStats, Map<String, Object> context) {}

    /**
     * Writes the given performance stats to a CSV file.
     *
     * @param filePath               The path to write the performance data to.
     * @param timestamp              The timestamp when the data was written.
     * @param groupedPerformanceData The group of performance data to write to the CSV file.
     */
    public static void writePerformanceStatsToCSV(Path filePath, LocalDateTime timestamp, List<PerformanceData> groupedPerformanceData)
    {
        // Get the unique column names based on the context:
        LinkedHashSet<String> contextColumns = new LinkedHashSet<>();
        for (PerformanceData performanceDatum : groupedPerformanceData)
        {
            // Add all the context columns:
            contextColumns.addAll(performanceDatum.context().keySet());
        }
        // Now we have all the context column names.

        // Open the file:
        try (
            var fileWriter = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
            var writer = new PrintWriter(fileWriter);
        )
        {
            // Write the header:
            writer.print("Class");
            writer.print(",");
            writer.print("Test");
            writer.print(",");
            writer.print("Display");
            writer.print(",");
            writer.print("Timestamp");
            contextColumns.forEach(
                column ->
                {
                    // Write the context header:
                    writer.print(",");
                    writer.print(column);
                });
            writer.print(",");
            writer.print("Item Count");
            writer.print(",");
            writer.print("Add Duration Nanos");
            writer.print(",");
            writer.print("Query Count");
            writer.print(",");
            writer.print("Search Duration Nanos");
            writer.println();

            // Get the formatted timestamp:
            String timestampString = timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Write each line of performance data:
            for (PerformanceData performanceDatum : groupedPerformanceData)
            {
                writer.print(performanceDatum.testInfo().getTestClass().orElseThrow().getName());
                writer.print(",");
                writer.print(performanceDatum.testInfo().getTestMethod().orElseThrow().getName());
                writer.print(",");
                writer.print("\"");
                writer.print(performanceDatum.testInfo().getDisplayName());
                writer.print("\"");
                writer.print(",");
                writer.print(timestampString);
                contextColumns.forEach(
                    column ->
                    {
                        // Get the value for this column:
                        Object value = performanceDatum.context().getOrDefault(column, "");

                        // Write the context:
                        writer.print(",");
                        writer.print(value == null ? "" : value);
                    });
                writer.print(",");
                writer.print(performanceDatum.performanceStats.itemCount());
                writer.print(",");
                writer.print(performanceDatum.performanceStats.addDurationNanos());
                writer.print(",");
                writer.print(performanceDatum.performanceStats.queryCount());
                writer.print(",");
                writer.print(performanceDatum.performanceStats.searchDurationNanos());
                writer.println();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the given performance histograms to a CSV file.
     *
     * @param filePath               The path to write the performance data to.
     * @param timestamp              The timestamp when the data was written.
     * @param groupedPerformanceData The group of performance data to write to the CSV file.
     */
    public static void writePerformanceHistogramsToCSV(Path filePath, LocalDateTime timestamp, List<PerformanceData> groupedPerformanceData)
    {
        // Derive the file names for the add and search histograms:
        var addHistogramPath = filePath.getParent().resolve(filePath.getFileName().toString().replace(".stats.csv", ".add.histogram.csv"));
        var searchHistogramPath = filePath.getParent().resolve(filePath.getFileName().toString().replace(".stats.csv", ".search.histogram.csv"));

        // Write out the histograms:
        writePerformanceHistogramToCSV(addHistogramPath, timestamp, groupedPerformanceData, pd -> pd.performanceStats().addHistogram() );
        writePerformanceHistogramToCSV(searchHistogramPath, timestamp, groupedPerformanceData, pd -> pd.performanceStats().searchHistogram() );
    }

    /**
     * Writes the given performance histograms to a CSV file.
     *
     * @param filePath               The path to write the performance data to.
     * @param timestamp              The timestamp when the data was written.
     * @param groupedPerformanceData The group of performance data to write to the CSV file.
     * @param histogramExtractor     The logic to extract the specific histogram to extract.
     */
    public static void writePerformanceHistogramToCSV(Path filePath, LocalDateTime timestamp, List<PerformanceData> groupedPerformanceData, Function<PerformanceData, TimeHistogram> histogramExtractor)
    {
        // Get the unique column names based on the context:
        LinkedHashSet<String> contextColumns = new LinkedHashSet<>();
        for (PerformanceData performanceDatum : groupedPerformanceData)
        {
            // Add all the context columns:
            contextColumns.addAll(performanceDatum.context().keySet());
        }
        // Now we have all the context column names.

        // Derive

        // Open the file:
        try (
            var fileWriter = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
            var writer = new PrintWriter(fileWriter);
        )
        {
            // Write the header:
            writer.print("Class");
            writer.print(",");
            writer.print("Test");
            writer.print(",");
            writer.print("Display");
            writer.print(",");
            writer.print("Timestamp");
            contextColumns.forEach(
                column ->
                {
                    // Write the context header:
                    writer.print(",");
                    writer.print(column);
                });
            writer.print(",");
            writer.print("Start Duration Inclusive");
            writer.print(",");
            writer.print("End Duration Exclusive");
            writer.print(",");
            writer.print("Count");
            writer.println();

            // Get the formatted timestamp:
            String timestampString = timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Write each line of performance data:
            for (PerformanceData performanceDatum : groupedPerformanceData)
            {
                // Get the histogram that we are interested in:
                TimeHistogram histogram = histogramExtractor.apply(performanceDatum);

                // Write out each bin of the histogram:
                histogram.forEach(
                    (binRange, count) ->
                    {
                        writer.print(performanceDatum.testInfo().getTestClass().orElseThrow().getName());
                        writer.print(",");
                        writer.print(performanceDatum.testInfo().getTestMethod().orElseThrow().getName());
                        writer.print(",");
                        writer.print("\"");
                        writer.print(performanceDatum.testInfo().getDisplayName());
                        writer.print("\"");
                        writer.print(",");
                        writer.print(timestampString);
                        contextColumns.forEach(
                            column ->
                            {
                                // Get the value for this column:
                                Object value = performanceDatum.context().getOrDefault(column, "");

                                // Write the context:
                                writer.print(",");
                                writer.print(value == null ? "" : value);
                            });
                        writer.print(",");
                        writer.print(binRange.startInclusive());
                        writer.print(",");
                        writer.print(binRange.endExclusive());
                        writer.print(",");
                        writer.print(count);
                        writer.println();

                    }
                );

            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the path to write performance stats to.
     *
     * @param rootPath        The root path where to write results to.
     * @param timestamp       The timestamp for this entry.
     * @param performanceData The performance data to get the path for.
     * @return The path to write to.
     */
    private static Path getPerformanceStatsCSVFilePath(Path rootPath, String timestamp, PerformanceData performanceData)
    {
        String className = performanceData.testInfo().getTestClass().orElseThrow().getSimpleName();
        String methodName = performanceData.testInfo().getTestMethod().orElseThrow().getName();

        // Get the name of the file that we want to write:
        String fileName = String.format(
            "%s_%s.stats.csv",
            timestamp,
            methodName
        );

        // Get the path to the file:
        //noinspection UnnecessaryLocalVariable
        Path filePath = rootPath
            .resolve(className)
            .resolve(fileName);
        return filePath;
    }

    /**
     * Asserts the random implementation.
     *
     * @param itemCount      The number of items to generate.
     * @param range          The range of the numbers to generate.
     * @param queries        The queries to perform.
     * @param randomProvider The implementation that gets the next random number for the item.
     */
    private PerformanceStats assertRandom(long itemCount, double range, long queries, Function<RandomGenerator, Double> randomProvider)
    {
        // Create the index:
        TIndex index = createIndex(range);

        // Create the random number generator:
        RandomGeneratorFactory<RandomGenerator> randomGeneratorFactory = RandomGeneratorFactory.getDefault();
        RandomGenerator randomGenerator = randomGeneratorFactory.create(1234L);

        // Track the duration and rate of operations:
        long startNanos = System.nanoTime();
        long addDurationNanos = 0L;
        long searchDurationNanos = 0L;
        TimeHistogram addHistogram = new TimeHistogram(10);
        TimeHistogram searchHistogram = new TimeHistogram(10);

        // Generate random values to index:
        for (long i = 0; i < itemCount; i++)
        {
            // Get the next randomValue
            double nextValue = randomProvider.apply(randomGenerator);

            // Create the item:
            X item = new X((int) nextValue);

            // Track the time before adding:
            long addStartNanos = System.nanoTime();

            // Index the item:
            index.add(item);

            // Track the time after adding:
            long addEndNanos = System.nanoTime();

            // Get the duration for this addition:
            long addDelta = addEndNanos - addStartNanos;

            // Histogram the time:
            addHistogram.add(addDelta);

            // Accumulate the time:
            addDurationNanos += addDelta;
        }

        // Create a histogram of the distances:
        Map<Integer, Integer> histogram = new TreeMap<>();

        // Query the index by sweeping through it:
        for (int step = (int) (range * 2 / queries), i = (int) -range; i < range; i += step)
        {
            // Create the item that we want to query:
            X item = new X(i);

            // Track the time it takes to search:
            long searchStartNanos = System.nanoTime();

            // Query the index:
            X nearest = index.searchNearest(item);

            // Track the time after searching:
            long searchEndNanos = System.nanoTime();

            // Get the duration for this search:
            long searchDelta = searchEndNanos - searchStartNanos;

            // Histogram the time:
            searchHistogram.add(searchDelta);

            // Accumulate the search time:
            searchDurationNanos += searchDelta;

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

        // Return the performance stats:
        return new PerformanceStats(itemCount, addDurationNanos, queries, searchDurationNanos, addHistogram, searchHistogram);
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

    public static class Grid2Tests extends XIndex1DTests<XGridIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XGridIndex1D createIndex(double range)
        {
            return new XGridIndex1D(new X((int) -range), new X((int) range), 2);
        }
    }

    public static class Grid10Tests extends XIndex1DTests<XGridIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XGridIndex1D createIndex(double range)
        {
            return new XGridIndex1D(new X((int) -range), new X((int) range), 10);
        }
    }

    public static class Grid100Tests extends XIndex1DTests<XGridIndex1D>
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

    public static class HierarchicalGridDiv10Max1Tests extends XIndex1DTests<XHierarchicalGridIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XHierarchicalGridIndex1D createIndex(double range)
        {
            return new XHierarchicalGridIndex1D(new X((int) -range), new X((int) range), 10, 1, 1);
        }
    }

    public static class HierarchicalGridDiv100Max1Tests extends XIndex1DTests<XHierarchicalGridIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XHierarchicalGridIndex1D createIndex(double range)
        {
            return new XHierarchicalGridIndex1D(new X((int) -range), new X((int) range), 100, 1, 1);
        }
    }

    public static class HierarchicalGridDiv10Max10Tests extends XIndex1DTests<XHierarchicalGridIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XHierarchicalGridIndex1D createIndex(double range)
        {
            return new XHierarchicalGridIndex1D(new X((int) -range), new X((int) range), 10, 10, 10);
        }
    }

    public static class HierarchicalGridDiv100Max10Tests extends XIndex1DTests<XHierarchicalGridIndex1D>
    {

        /**
         * A factory method to create an index of the specific type.
         *
         * @return A new index of the specific type.
         */
        @Override protected XHierarchicalGridIndex1D createIndex(double range)
        {
            return new XHierarchicalGridIndex1D(new X((int) -range), new X((int) range), 100, 10, 10);
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
            return new XRepoIndex1D(new X((int) -range), new X((int) range), 100, 10, 10);
        }
    }

    //#endregion

    //#region Helper Classes

    /**
     * This represents either a single or either results.
     */
    abstract static class SingleOrEither<T>
    {
        /**
         * Gets the options that the result might be.
         *
         * @return The options that the result might be.
         */
        abstract List<T> get();
    }

    /**
     * A single item.
     *
     * @param <T> The type of item.
     */
    static class Single<T> extends SingleOrEither<T>
    {
        public final T item;

        Single(T item) {this.item = item;}

        List<T> get()
        {
            return Collections.singletonList(item);
        }

        @Override public String toString()
        {
            return item.toString();
        }
    }

    /**
     * Either of two items.
     *
     * @param <T> The type of item.
     */
    static class Either<T> extends SingleOrEither<T>
    {
        public final T item1;

        public final T item2;

        Either(T item1, T item2)
        {
            this.item1 = item1;
            this.item2 = item2;
        }

        List<T> get()
        {
            return Arrays.asList(this.item1, this.item2);
        }

        @Override public String toString()
        {
            return "Either " +
                   item1
                   + " OR " +
                   item2;
        }
    }

    //#endregion

}
