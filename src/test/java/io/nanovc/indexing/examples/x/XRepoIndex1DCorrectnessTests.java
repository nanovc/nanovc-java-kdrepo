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
     * A factory to generate the parameters for the correctness test against the linear implementation in {@link #compareCorrectnessAgainstLinearIndex(int, int, int, int, int, int, int, int, String, String)}}.
     * @return The stream of parameters for the correctness test.
     */
    public static Stream<Object[]> compareCorrectnessAgainstLinearIndexFactory()
    {
        return Stream.<Object[]>builder()
            //                  addCount , addMin, addMax , addSeed , searchCount , searchMin , searchMax , searchSeed , scenario              , comment
            .add(new Object[] { 1        , 0     , 10     , 1       , 1           , 0         , 10        , 1          , "Single In Range"     , "Should query the same as what was added." })
            .add(new Object[] { 1        , 0     , 10     , 1       , 1_000       , -10       , 20        , 1          , "Single Out of Range" , "Checks both sides of the added range." })
            .add(new Object[] { 2        , 0     , 10     , 1       , 2           , 0         , 10        , 1          , "Two In Range"        , "Should query the same as what was added." })
            .add(new Object[] { 2        , 0     , 10     , 1       , 1_000       , -10       , 20        , 1          , "Two Out of Range"    , "Checks both sides of the added range." })
            .add(new Object[] { 100      , 0     , 10     , 1       , 100         , 0         , 10        , 1          , "100 In Range"        , "Should query the same as what was added." })
            .add(new Object[] { 100      , 0     , 10     , 1       , 1_000       , -10       , 20        , 1          , "100 Out of Range"    , "Checks both sides of the added range." })
            .add(new Object[] { 100      , 0     , 10_000 , 1       , 100         , 0         , 10_000    , 1          , "Sparse In Range"     , "Should query the same as what was added." })
            .add(new Object[] { 100      , 0     , 10_000 , 1       , 1_000       , -10       , 20_000    , 1          , "Sparse Out of Range" , "Checks both sides of the added range." })
            .build();
    }

    @ParameterizedTest(name = "[{index}] {8} - Added: {0}:[{1},{2}), seed: {3} Searched: {4}:[{5},{6}), seed: {7}")
    @MethodSource("compareCorrectnessAgainstLinearIndexFactory")
    public void compareCorrectnessAgainstLinearIndex(int addCount , int addMin, int addMax , int addSeed , int searchCount , int searchMin , int searchMax , int searchSeed , String scenario , String comment)
    {
        // Create the indexes:
        XLinearIndex1D linearIndex = new XLinearIndex1D();
        XRepoIndex1D repoIndex = new XRepoIndex1D(new X(-1), new X(1), 10, 10, 1);

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
