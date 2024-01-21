package io.nanovc.indexing.repo;

import io.nanovc.indexing.examples.x.X;
import io.nanovc.indexing.examples.x.XRepoIndexKD;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link XRepoIndexKD} implementation.
 */
public class XRepoIndexKDTests
{
    @Test
    public void creationTest()
    {
        new XRepoIndexKD(new X(-1), new X(1), 10);
    }

    @Test
    public void test_Index_0_Query_0()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Add an item to the index:
        X itemZero = new X(0);
        index.add(itemZero);

        // Query the item:
        X nearest = index.searchNearest(itemZero);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);
    }

    @Test
    public void test_Index_0_Query_0_1()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Add an item to the index:
        X itemZero = new X(0);
        index.add(itemZero);

        // Query the item:
        X nearest = index.searchNearest(itemZero);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);

        // Query the item:
        X itemOne = new X(1);
        nearest = index.searchNearest(itemOne);

        // Make sure the value matches:
        assertEquals(itemZero, nearest);
    }

    @Test
    public void test_Index_1_10_11_Query_1_10_11()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Define the items of interest:
        X item1 = new X(1);
        X item10 = new X(10);
        X item11 = new X(11);

        // Add the items to the index:
        index.add(item1);
        index.add(item10);
        index.add(item11);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item1);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item10);
        assertEquals(item10, nearest);

        nearest = index.searchNearest(item11);
        assertEquals(item11, nearest);
    }

    @Test
    public void test_Index_1_10_11_Query_0_9_12()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Define the items of interest:
        X item0 = new X(0);
        X item1 = new X(1);
        X item9 = new X(9);
        X item10 = new X(10);
        X item11 = new X(11);
        X item12 = new X(12);

        // Add the items to the index:
        index.add(item1);
        index.add(item10);
        index.add(item11);

        var expectedIndex = """
Index:  from X[x=-1] to X[x=1] with 10 divisions:
Division Cell Branch Name:
X:>1
.
└───📁
    ├───0'10'
    └───1'11'

Division Cell Branch Name:
X:[0,1]
.
└───📁
    └───0'1'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item0);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item9);
        assertEquals(item10, nearest);

        nearest = index.searchNearest(item12);
        assertEquals(item11, nearest);
    }

    @Test
    public void test_NearMisses_0()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Define the items of interest:
        int[] itemsOfInterest = {
            0
        };

        // Add the items to the index:
        for (int itemOfInterest : itemsOfInterest)
        {
            index.add(new X(itemOfInterest));
        }

        // Make sure the index is as expected:
        var expectedIndex = """
Index:  from X[x=-1] to X[x=1] with 10 divisions:
Division Cell Branch Name:
X:[0,1]
.
└───📁
    └───0'0'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(new X(-1));
        assertEquals(new X(0), nearest);

        nearest = index.searchNearest(new X(0));
        assertEquals(new X(0), nearest);

        nearest = index.searchNearest(new X(1));
        assertEquals(new X(0), nearest);
    }

    @Test
    public void test_NearMisses_1()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Define the items of interest:
        int[] itemsOfInterest = {
            1
        };

        // Add the items to the index:
        for (int itemOfInterest : itemsOfInterest)
        {
            index.add(new X(itemOfInterest));
        }

        // Make sure the index is as expected:
        var expectedIndex = """
Index:  from X[x=-1] to X[x=1] with 10 divisions:
Division Cell Branch Name:
X:[0,1]
.
└───📁
    └───0'1'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(new X(-10));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(0));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(2));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(10));
        assertEquals(new X(1), nearest);
    }

    @Test
    public void test_NearMisses_1_10()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Define the items of interest:
        int[] itemsOfInterest = {
            1, 10
        };

        // Add the items to the index:
        for (int itemOfInterest : itemsOfInterest)
        {
            index.add(new X(itemOfInterest));
        }

        // Make sure the index is as expected:
        var expectedIndex = """
Index:  from X[x=-1] to X[x=1] with 10 divisions:
Division Cell Branch Name:
X:>1
.
└───📁
    └───0'10'

Division Cell Branch Name:
X:[0,1]
.
└───📁
    └───0'1'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(new X(-111));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-110));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-11));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-10));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(0));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(2));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(3));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(4));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(5));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(6));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(7));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(8));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(9));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(10));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(11));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(100));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(110));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(111));
        assertEquals(new X(10), nearest);
    }

    @Test
    public void test_NearMisses_1_9_10()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(-1), new X(1), 10);

        // Define the items of interest:
        int[] itemsOfInterest = {
            1, 9, 10
        };

        // Add the items to the index:
        for (int itemOfInterest : itemsOfInterest)
        {
            index.add(new X(itemOfInterest));
        }

        // Make sure the index is as expected:
        var expectedIndex = """
Index:  from X[x=-1] to X[x=1] with 10 divisions:
Division Cell Branch Name:
X:>1
.
└───📁
    ├───0'9'
    └───1'10'

Division Cell Branch Name:
X:[0,1]
.
└───📁
    └───0'1'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(new X(-111));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-110));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-11));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-10));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(0));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(2));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(3));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(4));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(5));
        assertEquals(new X(9), nearest); // 1 and 9 are the same distance away from 5, but 5 falls into the same bucket at 9, therefore it wins.

        nearest = index.searchNearest(new X(6));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(7));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(8));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(9));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(10));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(11));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(100));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(110));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(111));
        assertEquals(new X(10), nearest);
    }

    @Test
    public void test_NearMisses_1_2_9_10()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(10), 10);

        // Define the items of interest:
        int[] itemsOfInterest = {
            1, 2, 9, 10
        };

        // Add the items to the index:
        for (int itemOfInterest : itemsOfInterest)
        {
            index.add(new X(itemOfInterest));
        }

        // Make sure the index is as expected:
        var expectedIndex = """
Index:  from X[x=0] to X[x=10] with 10 divisions:
Division Cell Branch Name:
X:[1,2)
.
└───📁
    └───0'1'

Division Cell Branch Name:
X:[2,3)
.
└───📁
    └───0'2'

Division Cell Branch Name:
X:[9,10]
.
└───📁
    ├───0'9'
    └───1'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(new X(-111));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-110));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-11));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-10));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(-1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(0));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(1));
        assertEquals(new X(1), nearest);

        nearest = index.searchNearest(new X(2));
        assertEquals(new X(2), nearest);

        nearest = index.searchNearest(new X(3));
        assertEquals(new X(2), nearest);

        nearest = index.searchNearest(new X(4));
        assertEquals(new X(2), nearest);

        nearest = index.searchNearest(new X(5));
        assertEquals(new X(2), nearest);

        nearest = index.searchNearest(new X(6));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(7));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(8));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(9));
        assertEquals(new X(9), nearest);

        nearest = index.searchNearest(new X(10));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(11));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(100));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(110));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(111));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(90));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(91));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(92));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(93));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(94));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(95));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(96));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(97));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(98));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(99));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(100));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(101));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(102));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(109));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(110));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(111));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(112));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(119));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(120));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(121));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(122));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(129));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(190));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(191));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(192));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(199));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(200));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(201));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(202));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(209));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(210));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(211));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(212));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(219));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(220));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(221));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(222));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(229));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(290));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(291));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(292));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(299));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(900));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(901));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(902));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(909));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(910));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(911));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(912));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(919));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(920));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(921));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(922));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(929));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(990));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(991));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(992));
        assertEquals(new X(10), nearest);

        nearest = index.searchNearest(new X(999));
        assertEquals(new X(10), nearest);
    }

    @Test
    public void test_Index_Random_1234()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(1000), 10);

        // Add the items to the index:
        Random random = new Random(1234);
        final int COUNT = 10;
        final int RANGE = 1000;
        for (int i = 0; i < COUNT; i++)
        {
            index.add(new X(random.nextInt(RANGE)));
        }

        var expectedIndex = """
Index:  from X[x=0] to X[x=1000] with 10 divisions:
Division Cell Branch Name:
X:[0,100)
.
└───📁
    └───0'37'

Division Cell Branch Name:
X:[100,200)
.
└───📁
    └───0'133'

Division Cell Branch Name:
X:[200,300)
.
└───X:250
    ├───<
    │   └───X:225
    │       └───<
    │           └───X:212
    │               ├───<
    │               │   └───📁
    │               │       └───0'210'
    │               └───>
    │                   └───📁
    │                       └───0'220'
    └───>
        └───📁
            └───0'297'

Division Cell Branch Name:
X:[300,400)
.
└───📁
    └───0'393'

Division Cell Branch Name:
X:[400,500)
.
└───📁
    └───0'449'

Division Cell Branch Name:
X:[500,600)
.
└───📁
    └───0'529'

Division Cell Branch Name:
X:[600,700)
.
└───X:650
    └───<
        └───X:625
            └───>
                └───X:637
                    └───<
                        └───X:631
                            ├───<
                            │   └───📁
                            │       └───0'628'
                            └───>
                                └───📁
                                    └───0'633'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(new X(0));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(1));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(2));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(36));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(37));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(38));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(84));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(85));
        assertEquals(new X(37), nearest);

        nearest = index.searchNearest(new X(86));
        assertEquals(new X(133), nearest);

        nearest = index.searchNearest(new X(132));
        assertEquals(new X(133), nearest);

        nearest = index.searchNearest(new X(133));
        assertEquals(new X(133), nearest);

        nearest = index.searchNearest(new X(134));
        assertEquals(new X(133), nearest);

        nearest = index.searchNearest(new X(200));
        assertEquals(new X(210), nearest);

        nearest = index.searchNearest(new X(211));
        assertEquals(new X(210), nearest);

        nearest = index.searchNearest(new X(214));
        assertEquals(new X(210), nearest);

        nearest = index.searchNearest(new X(215));
        assertEquals(new X(210), nearest); // 210 is the same distance as 220 but 210 is in the same bucket as 215 so it wins.

        nearest = index.searchNearest(new X(216));
        assertEquals(new X(220), nearest);

        nearest = index.searchNearest(new X(220));
        assertEquals(new X(220), nearest);

        nearest = index.searchNearest(new X(221));
        assertEquals(new X(220), nearest);
    }

    @Test
    public void Reindex_10_1_Query_1()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(10), 1);

        // Define the items of interest:
        X item10 = new X(10);
        X item1 = new X(1);

        // Add the first item:
        index.add(item10);

        // Make sure the index is as expected:
        var expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:[0,10]
.
└───📁
    └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the second item:
        index.add(item1); // NOTE: 1 is specifically after 10 to make sure we re-index it correctly.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:[0,10]
.
└───X:5
    ├───<
    │   └───📁
    │       └───0'1'
    └───>
        └───📁
            └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item1);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item10);
        assertEquals(item10, nearest);
    }

    @Test
    public void Reindex_111_11_Query_111_11()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(1000), 1);

        // Define the items of interest:
        X item111 = new X(111);
        X item11 = new X(11);

        // Add the first item:
        index.add(item111);

        // Make sure the index is as expected:
        var expectedIndex = """
Index:  from X[x=0] to X[x=1000] with 1 division:
Division Cell Branch Name:
X:[0,1000]
.
└───📁
    └───0'111'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the second item:
        index.add(item11); // NOTE: 1 is specifically after 111 to make sure we re-index it correctly.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=1000] with 1 division:
Division Cell Branch Name:
X:[0,1000]
.
└───X:500
    └───<
        └───X:250
            └───<
                └───X:125
                    └───<
                        └───X:62
                            ├───<
                            │   └───📁
                            │       └───0'11'
                            └───>
                                └───📁
                                    └───0'111'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item111);
        assertEquals(item111, nearest);

        nearest = index.searchNearest(item11);
        assertEquals(item11, nearest);
    }

    @Test
    public void Reindex_10_11_1_Query_1()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(10), 1);

        // Define the items of interest:
        X item10 = new X(10);
        X item11 = new X(11);
        X item1 = new X(1);

        // Add the first item:
        index.add(item10);

        String expectedIndex;

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:[0,10]
.
└───📁
    └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the second item:
        index.add(item11);

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:>10
.
└───📁
    └───0'11'

Division Cell Branch Name:
X:[0,10]
.
└───📁
    └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the third item:
        index.add(item1); // NOTE: 1 is specifically after 10 and 11 to make sure we re-index it correctly.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:>10
.
└───📁
    └───0'11'

Division Cell Branch Name:
X:[0,10]
.
└───X:5
    ├───<
    │   └───📁
    │       └───0'1'
    └───>
        └───📁
            └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item1);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item10);
        assertEquals(item10, nearest);

        nearest = index.searchNearest(item11);
        assertEquals(item11, nearest);
    }

    @Test
    public void Reindex_111_11_1_Query_1()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(10), 1);

        // Define the items of interest:
        X item10 = new X(10);
        X item11 = new X(11);
        X item1 = new X(1);

        // Add the first item:
        index.add(item10);

        String expectedIndex;

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:[0,10]
.
└───📁
    └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the second item:
        index.add(item11);

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:>10
.
└───📁
    └───0'11'

Division Cell Branch Name:
X:[0,10]
.
└───📁
    └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the third item:
        index.add(item1); // NOTE: 1 is specifically after 10 and 11 to make sure we re-index it correctly.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10] with 1 division:
Division Cell Branch Name:
X:>10
.
└───📁
    └───0'11'

Division Cell Branch Name:
X:[0,10]
.
└───X:5
    ├───<
    │   └───📁
    │       └───0'1'
    └───>
        └───📁
            └───0'10'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item1);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item10);
        assertEquals(item10, nearest);

        nearest = index.searchNearest(item11);
        assertEquals(item11, nearest);
    }

    @Test
    public void Reindex_111_1111_11_1_Query_1_11_111_1111()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(10_000), 1);

        // Define the items of interest:
        X item111 = new X(111);
        X item1111 = new X(1111);
        X item11 = new X(11);
        X item1 = new X(1);

        // Add the first item:
        index.add(item111);

        String expectedIndex;

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───📁
    └───0'111'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the second item:
        index.add(item1111);

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───X:5000
    └───<
        └───X:2500
            └───<
                └───X:1250
                    └───<
                        └───X:625
                            ├───<
                            │   └───📁
                            │       └───0'111'
                            └───>
                                └───📁
                                    └───0'1111'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the third item:
        index.add(item11); // NOTE: 1 is specifically after 1111 to make sure we re-index it correctly.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───X:5000
    └───<
        └───X:2500
            └───<
                └───X:1250
                    └───<
                        └───X:625
                            ├───<
                            │   └───X:312
                            │       └───<
                            │           └───X:156
                            │               └───<
                            │                   └───X:78
                            │                       ├───<
                            │                       │   └───📁
                            │                       │       └───0'11'
                            │                       └───>
                            │                           └───📁
                            │                               └───0'111'
                            └───>
                                └───📁
                                    └───0'1111'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the fourth item:
        index.add(item1); // NOTE: 1 is specifically after 11 to make sure we re-index it correctly.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───X:5000
    └───<
        └───X:2500
            └───<
                └───X:1250
                    └───<
                        └───X:625
                            ├───<
                            │   └───X:312
                            │       └───<
                            │           └───X:156
                            │               └───<
                            │                   └───X:78
                            │                       ├───<
                            │                       │   └───X:39
                            │                       │       └───<
                            │                       │           └───X:19
                            │                       │               └───<
                            │                       │                   └───X:9
                            │                       │                       ├───<
                            │                       │                       │   └───📁
                            │                       │                       │       └───0'1'
                            │                       │                       └───>
                            │                       │                           └───📁
                            │                       │                               └───0'11'
                            │                       └───>
                            │                           └───📁
                            │                               └───0'111'
                            └───>
                                └───📁
                                    └───0'1111'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item1);
        assertEquals(item1, nearest);

        nearest = index.searchNearest(item11);
        assertEquals(item11, nearest);

        nearest = index.searchNearest(item111);
        assertEquals(item111, nearest);

        nearest = index.searchNearest(item1111);
        assertEquals(item1111, nearest);
    }

    @Test
    public void Reindex_111_1111_11_11_Query_11_111_1111()
    {
        // Create the index:
        XRepoIndexKD index = new XRepoIndexKD(new X(0), new X(10_000), 1);

        // Define the items of interest:
        X item111 = new X(111);
        X item1111 = new X(1111);
        X item11 = new X(11);

        // Add the first item:
        index.add(item111);

        String expectedIndex;

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───📁
    └───0'111'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the second item:
        index.add(item1111);

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───X:5000
    └───<
        └───X:2500
            └───<
                └───X:1250
                    └───<
                        └───X:625
                            ├───<
                            │   └───📁
                            │       └───0'111'
                            └───>
                                └───📁
                                    └───0'1111'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the third item:
        index.add(item11); // NOTE: 1 is specifically after 1111 to make sure we re-index it correctly.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───X:5000
    └───<
        └───X:2500
            └───<
                └───X:1250
                    └───<
                        └───X:625
                            ├───<
                            │   └───X:312
                            │       └───<
                            │           └───X:156
                            │               └───<
                            │                   └───X:78
                            │                       ├───<
                            │                       │   └───📁
                            │                       │       └───0'11'
                            │                       └───>
                            │                           └───📁
                            │                               └───0'111'
                            └───>
                                └───📁
                                    └───0'1111'
""";
        assertRepoIndex(expectedIndex, index);

        // Add the fourth item:
        index.add(item11); // NOTE: 11 is specifically a duplicate of the previous one to make sure we keep the items the same.

        // Make sure the index is as expected:
        expectedIndex = """
Index:  from X[x=0] to X[x=10000] with 1 division:
Division Cell Branch Name:
X:[0,10000]
.
└───X:5000
    └───<
        └───X:2500
            └───<
                └───X:1250
                    └───<
                        └───X:625
                            ├───<
                            │   └───X:312
                            │       └───<
                            │           └───X:156
                            │               └───<
                            │                   └───X:78
                            │                       ├───<
                            │                       │   └───X:39
                            │                       │       └───<
                            │                       │           └───X:19
                            │                       │               └───<
                            │                       │                   └───X:9
                            │                       │                       └───>
                            │                       │                           └───X:14
                            │                       │                               └───<
                            │                       │                                   └───X:11
                            │                       │                                       └───<
                            │                       │                                           └───X:10
                            │                       │                                               └───>
                            │                       │                                                   └───📁
                            │                       │                                                       ├───0'11'
                            │                       │                                                       └───1'11'
                            │                       └───>
                            │                           └───📁
                            │                               └───0'111'
                            └───>
                                └───📁
                                    └───0'1111'
""";
        assertRepoIndex(expectedIndex, index);

        // Index the items:
        index.index();

        // Query the items:
        X nearest;

        nearest = index.searchNearest(item11);
        assertEquals(item11, nearest);

        nearest = index.searchNearest(item111);
        assertEquals(item111, nearest);

        nearest = index.searchNearest(item1111);
        assertEquals(item1111, nearest);
    }

    public void assertRepoIndex(String expectedIndex, XRepoIndexKD index)
    {
        // Get the representation of the index:
        var actualIndex = index.toString();

        // Make sure it is as expected:
        assertEquals(expectedIndex, actualIndex);
    }
}
