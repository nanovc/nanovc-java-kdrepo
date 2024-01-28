package io.nanovc.indexing.repo;

import io.nanovc.indexing.examples.x.X;
import io.nanovc.indexing.examples.x.XLinearIndex1D;
import io.nanovc.indexing.examples.x.XRepoIndexKD;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks correctness of the {@link XRepoIndexKD} implementation against a linear index.
 */
public class XRepoIndexKDCorrectnessTests
{
    /**
     * A factory to generate the parameters for the correctness test against the linear implementation in {@link #compareCorrectnessAgainstLinearIndex(int, int, int, int, int, int, int, int, int, String, String)}}.
     * @return The stream of parameters for the correctness test.
     */
    public static Stream<Object[]> compareCorrectnessAgainstLinearIndex_Factory()
    {
        return Stream.<Object[]>builder()
            //                  divisions  , addCount , addMin, addMax , addSeed , searchCount , searchMin , searchMax , searchSeed , scenario                , comment
            .add(new Object[] { 10         , 1        , 0     , 10     , 1       , 1           , 0         , 10        , 1          , "Single In Range"       , "Should query the same as what was added." })
            .add(new Object[] { 10         , 1        , 0     , 10     , 1       , 1_000       , -10       , 20        , 10         , "Single Out of Range"   , "Checks both sides of the added range." })
            .add(new Object[] { 10         , 2        , 0     , 10     , 1       , 2           , 0         , 10        , 1          , "Two In Range"          , "Should query the same as what was added." })
            .add(new Object[] { 10         , 2        , 0     , 10     , 1       , 1_000       , -10       , 20        , 10         , "Two Out of Range"      , "Checks both sides of the added range." })
            .add(new Object[] { 10         , 100      , 0     , 10     , 1       , 100         , 0         , 10        , 1          , "100 In Range"          , "Should query the same as what was added." })
            .add(new Object[] { 10         , 100      , 0     , 10     , 1       , 1_000       , -10       , 20        , 10         , "100 Out of Range"      , "Checks both sides of the added range." })
            .add(new Object[] { 10         , 100      , 0     , 10_000 , 1       , 100         , 0         , 10_000    , 1          , "Sparse In Range"       , "Should query the same as what was added." })
            .add(new Object[] { 10         , 100      , 0     , 10_000 , 1       , 1_000       , -10_000   , 20_000    , 10         , "Sparse Out of Range"   , "Checks both sides of the added range." })
            .add(new Object[] { 10         , 100      , -10   , 10     , 1       , 100         , 0         , 10        , 1          , "Negative In Range"     , "Should query the same as what was added." })
            .add(new Object[] { 10         , 100      , -10   , 10     , 1       , 1_000       , -30       , 30        , 10         , "Negative Out of Range" , "Checks both sides of the added range." })
            .add(new Object[] { 100        , 100_000  , -1_000, 1_000  , 1       , 1_000       , -3_000    , 3_000     , 10         , "Large Set"             , "Checks both sides of the added range." })
            .build();
    }

    @ParameterizedTest(name = "[{index}] {9} - Divisions: {0} Added: {1}:[{2},{3}), seed: {4} Searched: {5}:[{6},{7}), seed: {8}")
    @MethodSource("compareCorrectnessAgainstLinearIndex_Factory")
    public void compareCorrectnessAgainstLinearIndex(int divisions, int addCount , int addMin, int addMax , int addSeed , int searchCount , int searchMin , int searchMax , int searchSeed , String scenario , String comment)
    {
        // Create the indexes:
        XLinearIndex1D linearIndex = new XLinearIndex1D();
        XRepoIndexKD repoIndex = new XRepoIndexKD(new X(addMin), new X(addMax), divisions, 10);

        // Create the random number generators:
        Random addRandom = new Random(addSeed);
        Random searchRandom = new Random(searchSeed);

        // Add the items to the index:
        for (int i = 0; i < addCount; i++)
        {
            // Generate the next random item to add:
            X item = new X(addRandom.nextInt(addMin, addMax));

            // Add the item to the indexes:
            linearIndex.add(item);
            repoIndex.add(item);
        }

        // Allow the indexes to index themselves:
        linearIndex.index();
        repoIndex.index();

        // Query the items:
        X nearest;
        for (int i = 0; i < searchCount; i++)
        {
            // Generate the next random item to search for:
            X item = new X(searchRandom.nextInt(searchMin, searchMax));

            // Query the indexes:
            X nearestLinear = linearIndex.searchNearest(item);
            X nearestRepo = repoIndex.searchNearest(item);

            // Check whether the results were the same:
            if (!nearestLinear.equals(nearestRepo))
            {
                System.out.println("The index implementations had different results when searching for " + item);
                System.out.println("The linear index returned " + nearestLinear);
                System.out.println("The repo index returned " + nearestRepo);

                // Measure the actual distance between the items:
                var linearDistance = repoIndex.measureDistanceBetween(item, nearestLinear);
                var repoDistance = repoIndex.measureDistanceBetween(item, nearestRepo);

                // Check whether they match:
                if (repoIndex.getDistanceComparator().compare(linearDistance, repoDistance) != 0)
                {
                    // The distances were not the same.

                    // For debugging when the values are different, put a breakpoint in the next line:
                    repoIndex.searchNearest(item);

                    // Make sure that the results are the same:
                    assertEquals(
                        linearDistance, repoDistance,
                        () ->
                            "The nearest item from the repo index was not the same as the linear index, and the distance was different" + "\n" +
                            "Scenario: " + scenario + (comment.isEmpty() ? "" : " [" + comment + "]") + "\n"+
                            "Input was: " + item + "\n" +
                            "Repo Index was:\n" + repoIndex
                    );
                }
                else
                {
                    // Their distances were the same.
                    System.out.println("But their distances were the same, so it doesn't matter: " + repoDistance);
                }
            }
        }
    }

}
