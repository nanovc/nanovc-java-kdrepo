package io.nanovc.indexing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link TimeHistogram}.
 */
class TimeHistogramTests
{
    @Test
    public void creationTest()
    {
        new TimeHistogram();
    }

    @Test
    public void simpleTest_Base10()
    {
        TimeHistogram timeHistogram = new TimeHistogram(10);
        timeHistogram.add(0);
        timeHistogram.add(1);
        timeHistogram.add(2);
        timeHistogram.add(3);
        timeHistogram.add(4);
        timeHistogram.add(5);
        timeHistogram.add(6);
        timeHistogram.add(7);
        timeHistogram.add(8);
        timeHistogram.add(9);
        timeHistogram.add(10);
        timeHistogram.add(20);
        timeHistogram.add(30);
        timeHistogram.add(40);
        timeHistogram.add(50);
        timeHistogram.add(60);
        timeHistogram.add(70);
        timeHistogram.add(80);
        timeHistogram.add(90);
        timeHistogram.add(100);
        timeHistogram.add(200);
        timeHistogram.add(300);
        timeHistogram.add(400);
        timeHistogram.add(500);
        timeHistogram.add(600);
        timeHistogram.add(700);
        timeHistogram.add(800);
        timeHistogram.add(900);
        timeHistogram.add(1_000);
        timeHistogram.add(2_000);
        timeHistogram.add(3_000);
        timeHistogram.add(4_000);
        timeHistogram.add(5_000);
        timeHistogram.add(6_000);
        timeHistogram.add(7_000);
        timeHistogram.add(8_000);
        timeHistogram.add(9_000);
        timeHistogram.add(10_000);
        timeHistogram.add(10_250);
        timeHistogram.add(10_500);
        timeHistogram.add(10_750);
        timeHistogram.add(11_000);
        timeHistogram.add(11_250);
        timeHistogram.add(11_500);
        timeHistogram.add(11_750);
        timeHistogram.add(12_000);
        timeHistogram.add(12_250);
        timeHistogram.add(12_500);
        timeHistogram.add(12_750);
        timeHistogram.add(13_000);
        timeHistogram.add(13_250);
        timeHistogram.add(13_500);
        timeHistogram.add(13_750);
        timeHistogram.add(14_000);
        timeHistogram.add(14_250);
        timeHistogram.add(14_500);
        timeHistogram.add(14_750);
        timeHistogram.add(15_000);
        timeHistogram.add(15_250);
        timeHistogram.add(15_500);
        timeHistogram.add(15_750);
        timeHistogram.add(16_000);
        timeHistogram.add(16_250);
        timeHistogram.add(16_500);
        timeHistogram.add(16_750);
        timeHistogram.add(17_000);
        timeHistogram.add(17_250);
        timeHistogram.add(17_500);
        timeHistogram.add(17_750);
        timeHistogram.add(18_000);
        timeHistogram.add(18_250);
        timeHistogram.add(18_500);
        timeHistogram.add(18_750);
        timeHistogram.add(19_000);
        timeHistogram.add(19_250);
        timeHistogram.add(19_500);
        timeHistogram.add(19_750);
        timeHistogram.add(20_000);
        timeHistogram.add(30_000);
        timeHistogram.add(40_000);
        timeHistogram.add(50_000);
        timeHistogram.add(60_000);
        timeHistogram.add(70_000);
        timeHistogram.add(80_000);
        timeHistogram.add(90_000);
        timeHistogram.add(100_000);
        timeHistogram.add(200_000);
        timeHistogram.add(300_000);
        timeHistogram.add(400_000);
        timeHistogram.add(500_000);
        timeHistogram.add(600_000);
        timeHistogram.add(700_000);
        timeHistogram.add(800_000);
        timeHistogram.add(900_000);
        timeHistogram.add(1_000_000);
        timeHistogram.add(2_000_000);
        timeHistogram.add(3_000_000);
        timeHistogram.add(4_000_000);
        timeHistogram.add(5_000_000);
        timeHistogram.add(6_000_000);
        timeHistogram.add(7_000_000);
        timeHistogram.add(8_000_000);
        timeHistogram.add(9_000_000);
        timeHistogram.add(10_000_000);
        timeHistogram.add(10_250_000);
        timeHistogram.add(10_500_000);
        timeHistogram.add(10_750_000);
        timeHistogram.add(11_000_000);
        timeHistogram.add(11_250_000);
        timeHistogram.add(11_500_000);
        timeHistogram.add(11_750_000);
        timeHistogram.add(12_000_000);
        timeHistogram.add(12_250_000);
        timeHistogram.add(12_500_000);
        timeHistogram.add(12_750_000);
        timeHistogram.add(13_000_000);
        timeHistogram.add(13_250_000);
        timeHistogram.add(13_500_000);
        timeHistogram.add(13_750_000);
        timeHistogram.add(14_000_000);
        timeHistogram.add(14_250_000);
        timeHistogram.add(14_500_000);
        timeHistogram.add(14_750_000);
        timeHistogram.add(15_000_000);
        timeHistogram.add(15_250_000);
        timeHistogram.add(15_500_000);
        timeHistogram.add(15_750_000);
        timeHistogram.add(16_000_000);
        timeHistogram.add(16_250_000);
        timeHistogram.add(16_500_000);
        timeHistogram.add(16_750_000);
        timeHistogram.add(17_000_000);
        timeHistogram.add(17_250_000);
        timeHistogram.add(17_500_000);
        timeHistogram.add(17_750_000);
        timeHistogram.add(18_000_000);
        timeHistogram.add(18_250_000);
        timeHistogram.add(18_500_000);
        timeHistogram.add(18_750_000);
        timeHistogram.add(19_000_000);
        timeHistogram.add(19_250_000);
        timeHistogram.add(19_500_000);
        timeHistogram.add(19_750_000);
        timeHistogram.add(20_000_000);
        timeHistogram.add(30_000_000);
        timeHistogram.add(40_000_000);
        timeHistogram.add(50_000_000);
        timeHistogram.add(60_000_000);
        timeHistogram.add(70_000_000);
        timeHistogram.add(80_000_000);
        timeHistogram.add(90_000_000);
        timeHistogram.add(100_000_000);

        var expected =
            "           0.00ns:,           0.00ns:              1\n" +
            "           1.00ns:,           1.26ns:              1\n" +
            "           2.00ns:,           2.51ns:              1\n" +
            "           3.16ns:,           3.98ns:              1\n" +
            "           3.98ns:,           5.01ns:              1\n" +
            "           5.01ns:,           6.31ns:              1\n" +
            "           6.31ns:,           7.94ns:              2\n" +
            "           7.94ns:,          10.00ns:              1\n" +
            "          10.00ns:,          12.59ns:              2\n" +
            "          19.95ns:,          25.12ns:              1\n" +
            "          31.62ns:,          39.81ns:              1\n" +
            "          39.81ns:,          50.12ns:              1\n" +
            "          50.12ns:,          63.10ns:              1\n" +
            "          63.10ns:,          79.43ns:              2\n" +
            "          79.43ns:,         100.00ns:              1\n" +
            "         100.00ns:,         125.89ns:              2\n" +
            "         199.53ns:,         251.19ns:              1\n" +
            "         316.23ns:,         398.11ns:              1\n" +
            "         398.11ns:,         501.19ns:              1\n" +
            "         501.19ns:,         630.96ns:              1\n" +
            "         630.96ns:,         794.33ns:              2\n" +
            "         794.33ns:,       1,000.00ns:              1\n" +
            "       1,000.00ns:,       1,258.93ns:              2\n" +
            "       1,995.26ns:,       2,511.89ns:              1\n" +
            "       3,162.28ns:,       3,981.07ns:              1\n" +
            "       3,981.07ns:,       5,011.87ns:              1\n" +
            "       5,011.87ns:,       6,309.57ns:              1\n" +
            "       6,309.57ns:,       7,943.28ns:              2\n" +
            "       7,943.28ns:,      10,000.00ns:              1\n" +
            "      10,000.00ns:,      12,589.25ns:              6\n" +
            "      12,589.25ns:,      15,848.93ns:             12\n" +
            "      15,848.93ns:,      19,952.62ns:             15\n" +
            "      19,952.62ns:,      25,118.86ns:              9\n" +
            "      31,622.78ns:,      39,810.72ns:              1\n" +
            "      39,810.72ns:,      50,118.72ns:              1\n" +
            "      50,118.72ns:,      63,095.73ns:              1\n" +
            "      63,095.73ns:,      79,432.82ns:              2\n" +
            "      79,432.82ns:,     100,000.00ns:              1\n" +
            "     100,000.00ns:,     125,892.54ns:              2\n" +
            "     199,526.23ns:,     251,188.64ns:              1\n" +
            "     316,227.77ns:,     398,107.17ns:              1\n" +
            "     398,107.17ns:,     501,187.23ns:              1\n" +
            "     501,187.23ns:,     630,957.34ns:              1\n" +
            "     630,957.34ns:,     794,328.23ns:              2\n" +
            "     794,328.23ns:,   1,000,000.00ns:              1\n" +
            "   1,000,000.00ns:,   1,258,925.41ns:              2\n" +
            "   1,995,262.31ns:,   2,511,886.43ns:              1\n" +
            "   3,162,277.66ns:,   3,981,071.71ns:              1\n" +
            "   3,981,071.71ns:,   5,011,872.34ns:              1\n" +
            "   5,011,872.34ns:,   6,309,573.44ns:              1\n" +
            "   6,309,573.44ns:,   7,943,282.35ns:              2\n" +
            "   7,943,282.35ns:,  10,000,000.00ns:              1\n" +
            "  10,000,000.00ns:,  12,589,254.12ns:              2\n" +
            "  19,952,623.15ns:,  25,118,864.32ns:              1\n" +
            "  31,622,776.60ns:,  39,810,717.06ns:              1\n" +
            "  39,810,717.06ns:,  50,118,723.36ns:              1\n" +
            "  50,118,723.36ns:,  63,095,734.45ns:              1\n" +
            "  63,095,734.45ns:,  79,432,823.47ns:              2\n" +
            "  79,432,823.47ns:, 100,000,000.00ns:              1\n" +
            " 100,000,000.00ns:, 125,892,541.18ns:              2\n";
        assertEquals(expected, timeHistogram.toString());
    }

    @Test
    public void simpleTest_Base2()
    {
        TimeHistogram timeHistogram = new TimeHistogram(2);
        timeHistogram.add(0);
        timeHistogram.add(1);
        timeHistogram.add(2);
        timeHistogram.add(3);
        timeHistogram.add(4);
        timeHistogram.add(5);
        timeHistogram.add(6);
        timeHistogram.add(7);
        timeHistogram.add(8);
        timeHistogram.add(9);
        timeHistogram.add(10);
        timeHistogram.add(20);
        timeHistogram.add(30);
        timeHistogram.add(100);
        timeHistogram.add(200);
        timeHistogram.add(300);
        timeHistogram.add(1_000);
        timeHistogram.add(2_000);
        timeHistogram.add(3_000);
        timeHistogram.add(10_000);
        timeHistogram.add(20_000);
        timeHistogram.add(30_000);
        timeHistogram.add(100_000);
        timeHistogram.add(200_000);
        timeHistogram.add(300_000);
        timeHistogram.add(1_000_000);
        timeHistogram.add(2_000_000);
        timeHistogram.add(3_000_000);

        var expected =
            "           0.00ns:,           0.00ns:              1\n" +
            "           1.00ns:,           1.41ns:              1\n" +
            "           2.00ns:,           2.83ns:              1\n" +
            "           2.83ns:,           4.00ns:              1\n" +
            "           4.00ns:,           5.66ns:              1\n" +
            "           5.66ns:,           8.00ns:              2\n" +
            "           8.00ns:,          11.31ns:              3\n" +
            "          11.31ns:,          16.00ns:              1\n" +
            "          22.63ns:,          32.00ns:              1\n" +
            "          32.00ns:,          45.25ns:              1\n" +
            "          90.51ns:,         128.00ns:              1\n" +
            "         181.02ns:,         256.00ns:              1\n" +
            "         256.00ns:,         362.04ns:              1\n" +
            "       1,024.00ns:,       1,448.15ns:              1\n" +
            "       2,048.00ns:,       2,896.31ns:              1\n" +
            "       2,896.31ns:,       4,096.00ns:              1\n" +
            "      11,585.24ns:,      16,384.00ns:              1\n" +
            "      23,170.48ns:,      32,768.00ns:              1\n" +
            "      32,768.00ns:,      46,340.95ns:              1\n" +
            "      92,681.90ns:,     131,072.00ns:              1\n" +
            "     185,363.80ns:,     262,144.00ns:              1\n" +
            "     262,144.00ns:,     370,727.60ns:              1\n" +
            "   1,048,576.00ns:,   1,482,910.40ns:              1\n" +
            "   2,097,152.00ns:,   2,965,820.80ns:              1\n" +
            "   2,965,820.80ns:,   4,194,304.00ns:              1\n";
        assertEquals(expected, timeHistogram.toString());
    }

    @Test
    public void simpleTest_Base3()
    {
        TimeHistogram timeHistogram = new TimeHistogram(3);
        timeHistogram.add(0);
        timeHistogram.add(1);
        timeHistogram.add(2);
        timeHistogram.add(3);
        timeHistogram.add(4);
        timeHistogram.add(5);
        timeHistogram.add(6);
        timeHistogram.add(7);
        timeHistogram.add(8);
        timeHistogram.add(9);
        timeHistogram.add(10);
        timeHistogram.add(20);
        timeHistogram.add(30);
        timeHistogram.add(100);
        timeHistogram.add(200);
        timeHistogram.add(300);
        timeHistogram.add(1_000);
        timeHistogram.add(2_000);
        timeHistogram.add(3_000);
        timeHistogram.add(10_000);
        timeHistogram.add(20_000);
        timeHistogram.add(30_000);
        timeHistogram.add(100_000);
        timeHistogram.add(200_000);
        timeHistogram.add(300_000);
        timeHistogram.add(1_000_000);
        timeHistogram.add(2_000_000);
        timeHistogram.add(3_000_000);

        var expected =
                "           0.00ns:,           0.00ns:              1\n" +
                "           1.00ns:,           1.44ns:              1\n" +
                "           2.08ns:,           3.00ns:              1\n" +
                "           3.00ns:,           4.33ns:              1\n" +
                "           4.33ns:,           6.24ns:              2\n" +
                "           6.24ns:,           9.00ns:              2\n" +
                "           9.00ns:,          12.98ns:              3\n" +
                "          18.72ns:,          27.00ns:              1\n" +
                "          27.00ns:,          38.94ns:              1\n" +
                "         116.82ns:,         168.49ns:              1\n" +
                "         168.49ns:,         243.00ns:              1\n" +
                "         350.47ns:,         505.46ns:              1\n" +
                "       1,051.40ns:,       1,516.38ns:              1\n" +
                "       2,187.00ns:,       3,154.20ns:              1\n" +
                "       3,154.20ns:,       4,549.14ns:              1\n" +
                "       9,462.60ns:,      13,647.43ns:              1\n" +
                "      19,683.00ns:,      28,387.80ns:              1\n" +
                "      28,387.80ns:,      40,942.29ns:              1\n" +
                "      85,163.39ns:,     122,826.87ns:              1\n" +
                "     177,147.00ns:,     255,490.18ns:              1\n" +
                "     255,490.18ns:,     368,480.61ns:              1\n" +
                "   1,105,441.83ns:,   1,594,323.00ns:              1\n" +
                "   2,299,411.66ns:,   3,316,325.48ns:              1\n" +
                "   3,316,325.48ns:,   4,782,969.00ns:              1\n";
        assertEquals(expected, timeHistogram.toString());
    }
}
