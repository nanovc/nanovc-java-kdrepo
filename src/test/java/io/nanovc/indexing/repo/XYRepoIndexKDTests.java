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
        String expectedIndex = """
Index:  from XY[x=-1.0, y=-1.0] to XY[x=1.0, y=1.0] with 10 divisions:
Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.0,0.2)
.
└───📁
    └───0'0.0|0.0'
""";
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
        String expectedIndex = """
Index:  from XY[x=-1.0, y=-1.0] to XY[x=1.0, y=1.0] with 10 divisions:
Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.0,0.2)
.
└───📁
    └───0'0.0|0.0'

Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.8,1.0]
.
└───📁
    └───0'1.0|1.0'
""";
        assertIndex(expectedIndex, index);

        // Query the item:
        XY nearest = index.searchNearest(itemZero);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);
    }

    @Test
    public void test_Index_0_0p3_1_Query_0p2()
    {
        // Create the index:
        XYRepoIndexKD index = new XYRepoIndexKD(new XY(-1, -1), new XY(1, 1), 10);

        // Add an item to the index:
        XY itemZero = new XY(0.0, 0.0);
        XY itemZeroPointThree = new XY(0.3, 0.3);
        XY itemOne = new XY(1.0, 1.0);
        index.add(itemZero);
        index.add(itemZeroPointThree);
        index.add(itemOne);

        // Make sure the index is as expected:
        String expectedIndex = """
Index:  from XY[x=-1.0, y=-1.0] to XY[x=1.0, y=1.0] with 10 divisions:
Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.0,0.2)
.
└───📁
    └───0'0.0|0.0'

Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.2,0.4)
.
└───📁
    └───0'0.3|0.3'

Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.8,1.0]
.
└───📁
    └───0'1.0|1.0'
""";
        assertIndex(expectedIndex, index);

        // Query the item:
        XY itemZeroPointTwo = new XY(0.2, 0.2);
        XY nearest = index.searchNearest(itemZeroPointTwo);

        // Make sure the value matches:
        assertEquals(itemZeroPointThree, nearest);
    }

    @Test
    public void test_Index_0_0p03_1_Query_0p02()
    {
        // Create the index:
        XYRepoIndexKD index = new XYRepoIndexKD(new XY(-1, -1), new XY(1, 1), 10);

        // Add an item to the index:
        XY itemZero = new XY(0.0, 0.0);
        XY itemZeroPointZeroThree = new XY(0.03, 0.03);
        XY itemOne = new XY(1.0, 1.0);
        index.add(itemZero);
        index.add(itemZeroPointZeroThree);
        index.add(itemOne);

        // Make sure the index is as expected:
        String expectedIndex =
            """
Index:  from XY[x=-1.0, y=-1.0] to XY[x=1.0, y=1.0] with 10 divisions:
Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.0,0.2)
.
└───X:0.1
    └───<
        └───Y:0.1
            └───<
                └───X:0.05
                    └───<
                        └───Y:0.05
                            └───<
                                └───X:0.025
                                    ├───<
                                    │   └───📁
                                    │       └───0'0.0|0.0'
                                    └───>
                                        └───📁
                                            └───0'0.03|0.03'

Division Cell Branch Name:
X:[0.0,0.2)
Y:[0.8,1.0]
.
└───📁
    └───0'1.0|1.0'
""";
        assertIndex(expectedIndex, index);

        // Query the item:
        XY itemZeroPointZeroTwo = new XY(0.02, 0.02);
        XY nearest = index.searchNearest(itemZeroPointZeroTwo);

        // Make sure the value matches:
        assertEquals(itemZeroPointZeroThree, nearest);
    }

    public void assertIndex(String expectedIndex, XYRepoIndexKD index)
    {
        // Get the representation of the index:
        var actualIndex = index.toString();

        // Make sure it is as expected:
        assertEquals(expectedIndex, actualIndex);
    }
}
