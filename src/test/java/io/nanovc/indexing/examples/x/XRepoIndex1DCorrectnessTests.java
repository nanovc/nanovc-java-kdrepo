package io.nanovc.indexing.examples.x;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks correctness of the {@link XRepoIndex1D} implementation against a linear index.
 */
public class XRepoIndex1DCorrectnessTests
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
        XRepoIndex1D repoIndex = new XRepoIndex1D(new X(addMin), new X(addMax), divisions);

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

        // Query the items:
        X nearest;
        for (int i = 0; i < searchCount; i++)
        {
            // Generate the next random item to search for:
            X item = new X(searchRandom.nextInt(searchMin, searchMax));

            // Query the indexes:
            X nearestLinear = linearIndex.searchNearest(item);
            X nearestRepo = repoIndex.searchNearest(item);

            // For debugging when the values are different, put a breakpoint in the next line:
            if (!nearestLinear.equals(nearestRepo))
            {
                repoIndex.searchNearest(item);
            }

            // Make sure that the results are the same:
            assertEquals(
                nearestLinear, nearestRepo,
                () ->
                    "Scenario: " + scenario + (comment.isEmpty() ? "" : " [" + comment + "]") + "\n"+
                    "Input was: " + item + "\n" +
                    "Repo Index was:\n" + repoIndex
            );
        }
    }

}
