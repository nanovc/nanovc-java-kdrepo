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
            "           1.00ns:,           1.02ns:              1\n" +
            "           2.00ns:,           2.04ns:              1\n" +
            "           3.02ns:,           3.09ns:              1\n" +
            "           3.98ns:,           4.07ns:              1\n" +
            "           5.01ns:,           5.13ns:              1\n" +
            "           6.03ns:,           6.17ns:              1\n" +
            "           7.08ns:,           7.24ns:              1\n" +
            "           7.94ns:,           8.13ns:              1\n" +
            "           8.91ns:,           9.12ns:              1\n" +
            "          10.00ns:,          10.23ns:              1\n" +
            "          19.95ns:,          20.42ns:              1\n" +
            "          30.20ns:,          30.90ns:              1\n" +
            "          39.81ns:,          40.74ns:              1\n" +
            "          50.12ns:,          51.29ns:              1\n" +
            "          60.26ns:,          61.66ns:              1\n" +
            "          70.79ns:,          72.44ns:              1\n" +
            "          79.43ns:,          81.28ns:              1\n" +
            "          89.13ns:,          91.20ns:              1\n" +
            "         100.00ns:,         102.33ns:              1\n" +
            "         199.53ns:,         204.17ns:              1\n" +
            "         302.00ns:,         309.03ns:              1\n" +
            "         398.11ns:,         407.38ns:              1\n" +
            "         501.19ns:,         512.86ns:              1\n" +
            "         602.56ns:,         616.60ns:              1\n" +
            "         707.95ns:,         724.44ns:              1\n" +
            "         794.33ns:,         812.83ns:              1\n" +
            "         891.25ns:,         912.01ns:              1\n" +
            "       1,000.00ns:,       1,023.29ns:              1\n" +
            "       1,995.26ns:,       2,041.74ns:              1\n" +
            "       3,019.95ns:,       3,090.30ns:              1\n" +
            "       3,981.07ns:,       4,073.80ns:              1\n" +
            "       5,011.87ns:,       5,128.61ns:              1\n" +
            "       6,025.60ns:,       6,165.95ns:              1\n" +
            "       7,079.46ns:,       7,244.36ns:              1\n" +
            "       7,943.28ns:,       8,128.31ns:              1\n" +
            "       8,912.51ns:,       9,120.11ns:              1\n" +
            "      10,000.00ns:,      10,232.93ns:              1\n" +
            "      19,952.62ns:,      20,417.38ns:              1\n" +
            "      30,199.52ns:,      30,902.95ns:              1\n" +
            "      39,810.72ns:,      40,738.03ns:              1\n" +
            "      50,118.72ns:,      51,286.14ns:              1\n" +
            "      60,255.96ns:,      61,659.50ns:              1\n" +
            "      70,794.58ns:,      72,443.60ns:              1\n" +
            "      79,432.82ns:,      81,283.05ns:              1\n" +
            "      89,125.09ns:,      91,201.08ns:              1\n" +
            "     100,000.00ns:,     102,329.30ns:              1\n" +
            "     199,526.23ns:,     204,173.79ns:              1\n" +
            "     301,995.17ns:,     309,029.54ns:              1\n" +
            "     398,107.17ns:,     407,380.28ns:              1\n" +
            "     501,187.23ns:,     512,861.38ns:              1\n" +
            "     602,559.59ns:,     616,595.00ns:              1\n" +
            "     707,945.78ns:,     724,435.96ns:              1\n" +
            "     794,328.23ns:,     812,830.52ns:              1\n" +
            "     891,250.94ns:,     912,010.84ns:              1\n" +
            "   1,000,000.00ns:,   1,023,292.99ns:              1\n" +
            "   1,995,262.31ns:,   2,041,737.94ns:              1\n" +
            "   3,019,951.72ns:,   3,090,295.43ns:              1\n" +
            "   3,981,071.71ns:,   4,073,802.78ns:              1\n" +
            "   5,011,872.34ns:,   5,128,613.84ns:              1\n" +
            "   6,025,595.86ns:,   6,165,950.02ns:              1\n" +
            "   7,079,457.84ns:,   7,244,359.60ns:              1\n" +
            "   7,943,282.35ns:,   8,128,305.16ns:              1\n" +
            "   8,912,509.38ns:,   9,120,108.39ns:              1\n" +
            "  10,000,000.00ns:,  10,232,929.92ns:              1\n" +
            "  19,952,623.15ns:,  20,417,379.45ns:              1\n" +
            "  30,199,517.20ns:,  30,902,954.33ns:              1\n" +
            "  39,810,717.06ns:,  40,738,027.78ns:              1\n" +
            "  50,118,723.36ns:,  51,286,138.40ns:              1\n" +
            "  60,255,958.61ns:,  61,659,500.19ns:              1\n" +
            "  70,794,578.44ns:,  72,443,596.01ns:              1\n" +
            "  79,432,823.47ns:,  81,283,051.62ns:              1\n" +
            "  89,125,093.81ns:,  91,201,083.94ns:              1\n" +
            " 100,000,000.00ns:, 102,329,299.23ns:              1\n";
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
            "           1.00ns:,           1.19ns:              1\n" +
            "           2.00ns:,           2.38ns:              1\n" +
            "           2.83ns:,           3.36ns:              1\n" +
            "           4.00ns:,           4.76ns:              1\n" +
            "           4.76ns:,           5.66ns:              1\n" +
            "           5.66ns:,           6.73ns:              1\n" +
            "           6.73ns:,           8.00ns:              1\n" +
            "           8.00ns:,           9.51ns:              1\n" +
            "           9.51ns:,          11.31ns:              2\n" +
            "          19.03ns:,          22.63ns:              1\n" +
            "          32.00ns:,          38.05ns:              1\n" +
            "         107.63ns:,         128.00ns:              1\n" +
            "         215.27ns:,         256.00ns:              1\n" +
            "         304.44ns:,         362.04ns:              1\n" +
            "       1,024.00ns:,       1,217.75ns:              1\n" +
            "       2,048.00ns:,       2,435.50ns:              1\n" +
            "       2,896.31ns:,       3,444.31ns:              1\n" +
            "       9,741.98ns:,      11,585.24ns:              1\n" +
            "      19,483.97ns:,      23,170.48ns:              1\n" +
            "      27,554.49ns:,      32,768.00ns:              1\n" +
            "      92,681.90ns:,     110,217.97ns:              1\n" +
            "     185,363.80ns:,     220,435.95ns:              1\n" +
            "     311,743.51ns:,     370,727.60ns:              1\n" +
            "   1,048,576.00ns:,   1,246,974.04ns:              1\n" +
            "   2,097,152.00ns:,   2,493,948.08ns:              1\n" +
            "   2,965,820.80ns:,   3,526,975.20ns:              1\n";
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
                "           1.00ns:,           1.13ns:              1\n" +
                "           2.08ns:,           2.35ns:              1\n" +
                "           3.00ns:,           3.39ns:              1\n" +
                "           3.83ns:,           4.33ns:              1\n" +
                "           4.89ns:,           5.52ns:              1\n" +
                "           6.24ns:,           7.05ns:              1\n" +
                "           7.05ns:,           7.97ns:              1\n" +
                "           7.97ns:,           9.00ns:              1\n" +
                "           9.00ns:,          10.17ns:              1\n" +
                "          10.17ns:,          11.49ns:              1\n" +
                "          21.15ns:,          23.90ns:              1\n" +
                "          30.51ns:,          34.47ns:              1\n" +
                "         103.40ns:,         116.82ns:              1\n" +
                "         190.36ns:,         215.08ns:              1\n" +
                "         310.19ns:,         350.47ns:              1\n" +
                "       1,051.40ns:,       1,187.90ns:              1\n" +
                "       1,935.69ns:,       2,187.00ns:              1\n" +
                "       3,154.20ns:,       3,563.71ns:              1\n" +
                "       9,462.60ns:,      10,691.14ns:              1\n" +
                "      19,683.00ns:,      22,238.46ns:              1\n" +
                "      28,387.80ns:,      32,073.41ns:              1\n" +
                "      96,220.24ns:,     108,712.61ns:              1\n" +
                "     200,146.17ns:,     226,131.34ns:              1\n" +
                "     288,660.72ns:,     326,137.82ns:              1\n" +
                "     978,413.46ns:,   1,105,441.83ns:              1\n" +
                "   2,035,182.02ns:,   2,299,411.66ns:              1\n" +
                "   2,935,240.39ns:,   3,316,325.48ns:              1\n";
        assertEquals(expected, timeHistogram.toString());
    }
}
