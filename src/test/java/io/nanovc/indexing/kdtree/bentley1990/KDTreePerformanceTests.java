package io.nanovc.indexing.kdtree.bentley1990;

import io.nanovc.indexing.examples.xy.XY;
import io.nanovc.indexing.examples.xy.XYKDTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

/**
 * Checks performance of the {@link KDTree} implementation.
 */
public class KDTreePerformanceTests
{
    /**
     * A factory to generate the parameters for the correctness test against the linear implementation in {@link #compareCorrectnessAgainstLinearIndex(int, int, int, int, int, int, int, int, int, String, String)}}.
     * @return The stream of parameters for the correctness test.
     */
    public static Stream<Object[]> compareCorrectnessAgainstLinearIndex_Factory()
    {
        return Stream.<Object[]>builder()
            //                  addCount   , addMin     , addMax    , addSeed , searchCount , searchMin , searchMax  , searchSeed , scenario    , comment
            // Warmup:        {            ,            ,           ,         ,             ,           ,            ,            ,             ,
            .add(new Object[] { 100_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 10         , "Warmup"    , "Allows Just In Time compilation" })
            // Exact Matches: {            ,            ,           ,         ,             ,            ,           ,            ,             ,
            .add(new Object[] { 1_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 1_250      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 1_500      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 1_750      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 2_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 2_500      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 3_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 4_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 5_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 6_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 7_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 8_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 9_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Tiny"      , "Exact Match" })
            .add(new Object[] { 10_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 12_500     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 15_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 17_500     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 20_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 25_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 30_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 40_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 50_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 60_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 70_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 80_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 90_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Small"     , "Exact Match" })
            .add(new Object[] { 100_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 125_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 150_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 175_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 200_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 250_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 300_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 400_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 500_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 600_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 700_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 800_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 900_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Medium"    , "Exact Match" })
            .add(new Object[] { 1_000_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Large"     , "Exact Match" })
            .add(new Object[] { 1_250_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Large"     , "Exact Match" })
            .add(new Object[] { 1_500_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Large"     , "Exact Match" })
            .add(new Object[] { 1_750_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Large"     , "Exact Match" })
            .add(new Object[] { 2_000_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Large"     , "Exact Match" })
            .add(new Object[] { 2_500_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Large"     , "Exact Match" })
            .add(new Object[] { 3_000_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -1_000_000 , 1_000_000 , 1          , "Large"     , "Exact Match" })
            // Near Misses: {              ,            ,           ,         ,             ,           ,            ,            ,             ,
            .add(new Object[] { 1_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 1_250      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 1_500      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 1_750      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 2_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 2_500      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 3_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 4_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 5_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 6_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 7_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 8_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 9_000      , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Tiny"      , "Checks both sides of the added range." })
            .add(new Object[] { 10_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 12_500     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 15_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 17_500     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 20_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 25_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 30_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 40_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 50_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 60_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 70_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 80_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 90_000     , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Small"     , "Checks both sides of the added range." })
            .add(new Object[] { 100_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 125_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 150_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 175_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 200_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 250_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 300_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 400_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 500_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 600_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 700_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 800_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 900_000    , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Medium"    , "Checks both sides of the added range." })
            .add(new Object[] { 1_000_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Large"     , "Checks both sides of the added range." })
            .add(new Object[] { 1_250_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Large"     , "Checks both sides of the added range." })
            .add(new Object[] { 1_500_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Large"     , "Checks both sides of the added range." })
            .add(new Object[] { 1_750_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Large"     , "Checks both sides of the added range." })
            .add(new Object[] { 2_000_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Large"     , "Checks both sides of the added range." })
            .add(new Object[] { 2_500_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Large"     , "Checks both sides of the added range." })
            .add(new Object[] { 3_000_000  , -1_000_000 , 1_000_000 , 1       , 10_000      , -3_000_000 , 3_000_000 , 10         , "Large"     , "Checks both sides of the added range." })
            .build();
    }

    @ParameterizedTest(name = "[{index}] {8} - Added: {0}:[{1},{2}), seed: {3} Searched: {4}:[{5},{6}), seed: {7}")
    @MethodSource("compareCorrectnessAgainstLinearIndex_Factory")
    public void compareCorrectnessAgainstLinearIndex(int addCount , int addMin, int addMax , int addSeed , int searchCount , int searchMin , int searchMax , int searchSeed , String scenario , String comment)
    {
        // Create the index:
        var testedIndex = new XYKDTree();

        // Create the random number generators:
        Random addRandom = new Random(addSeed);
        Random searchRandom = new Random(searchSeed);

        // Add the items to the index:
        for (int i = 0; i < addCount; i++)
        {
            // Generate the next random item to add:
            XY item = new XY(addRandom.nextDouble(addMin, addMax), addRandom.nextDouble(addMin, addMax));

            // Add the item to the indexes:
            testedIndex.add(item);
        }

        // Index the items:
        testedIndex.index();

        // Query the items:
        for (int i = 0; i < searchCount; i++)
        {
            // Generate the next random item to search for:
            XY item = new XY(searchRandom.nextDouble(searchMin, searchMax), searchRandom.nextDouble(searchMin, searchMax));

            // Query the index:
            XY nearest = testedIndex.searchNearest(item);

            if (nearest.x() == 0.0) System.out.println("nearest = " + nearest);
        }
    }

}
