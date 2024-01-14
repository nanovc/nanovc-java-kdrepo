package io.nanovc.indexing.repo;

import io.nanovc.indexing.examples.xy.XY;
import io.nanovc.indexing.examples.xy.XYRepoIndexKD;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link XYRepoIndexKD} implementation.
 */
public class XYRepoIndexKDTests
{
    @Test
    public void creationTest()
    {
        new XYRepoIndexKD(new XY(-1, -1), new XY(1, 1), 10);
    }

    @Test
    public void test_Index_0_Query_0()
    {
        // Create the index:
        XYRepoIndexKD index = new XYRepoIndexKD(new XY(-1, -1), new XY(1, 1), 10);

        // Add an item to the index:
        XY itemZero = new XY(0, 0);
        index.add(itemZero);

        // Make sure the index is as expected:
        String expectedIndex =
            "Index:  from XY[x=-1.0, y=-1.0] to XY[x=1.0, y=1.0] with 10 divisions:\n" +
            "Division Cell Branch Name: \n" +
            "X:[0.0,0.2)\n" +
            "Y:[0.0,0.2)\n" +
            "Hyper Cube:\n" +
            "X:[0.0,0.2)\n" +
            "Y:[0.0,0.2)\n" +
            ".\n" +
            "└───📁\n" +
            "    └───0'0.0|0.0'\n";
        assertIndex(expectedIndex, index);

        // Query the item:
        XY nearest = index.searchNearest(itemZero);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);
    }

    @Test
    public void test_Index_0_1_Query_0()
    {
        // Create the index:
        XYRepoIndexKD index = new XYRepoIndexKD(new XY(-1, -1), new XY(1, 1), 10);

        // Add an item to the index:
        XY itemZero = new XY(0.0, 0.0);
        XY itemOne = new XY(1.0, 1.0);
        index.add(itemZero);
        index.add(itemOne);

        // Make sure the index is as expected:
        String expectedIndex =
            "Index:  from XY[x=-1.0, y=-1.0] to XY[x=1.0, y=1.0] with 10 divisions:\n" +
            "Division Cell Branch Name: \n" +
            "X:[0.0,0.2)\n" +
            "Y:[0.0,0.2)\n" +
            "Hyper Cube:\n" +
            "X:[0.0,0.2)\n" +
            "Y:[0.0,0.2)\n" +
            ".\n" +
            "└───\uD83D\uDCC1\n" +
            "    └───0'0.0|0.0'\n" +
            "\n" +
            "Division Cell Branch Name: \n" +
            "X:[0.0,0.2)\n" +
            "Y:[0.8,1.0]\n" +
            "Hyper Cube:\n" +
            "X:[0.0,0.2)\n" +
            "Y:[0.8,1.0]\n" +
            ".\n" +
            "└───\uD83D\uDCC1\n" +
            "    └───0'1.0|1.0'\n";
        assertIndex(expectedIndex, index);

        // Query the item:
        XY nearest = index.searchNearest(itemZero);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);
    }

    public void assertIndex(String expectedIndex, XYRepoIndexKD index)
    {
        // Get the representation of the index:
        var actualIndex = index.toString();

        // Make sure it is as expected:
        assertEquals(expectedIndex, actualIndex);
    }
}
