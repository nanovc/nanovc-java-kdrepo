package io.nanovc.indexing.repo.ranges;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link RangeCalculator}.
 */
class RangeCalculatorTests
{
    @Test
    public void creationTest()
    {
        new RangeCalculator<Double>(Double::compare);
    }

    @Test
    public void testDoubleRanges()
    {
        RangeCalculator<Double> rangeCalculator = new RangeCalculator<>(Double::compare);

        assertRange(rangeCalculator, true, 0.0, new UnBoundedRange<>());
        assertRange(rangeCalculator, false, 0.0, new NeverInRange<>());

        assertRange(rangeCalculator, true , 0.0, new MinInclusiveRange<>(0.0));
        assertRange(rangeCalculator, false, 0.0, new MinExclusiveRange<>(0.0));
        assertRange(rangeCalculator, true , 0.0, new MaxInclusiveRange<>(0.0));
        assertRange(rangeCalculator, false, 0.0, new MaxExclusiveRange<>(0.0));

        assertRange(rangeCalculator, true , 0.0, new MinInclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, true , 0.0, new MinInclusiveMaxExclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, 0.0, new MinExclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, 0.0, new MinExclusiveMaxExclusiveRange<>(0.0, 1.0));

        assertRange(rangeCalculator, true , 1.0, new MinInclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, 1.0, new MinInclusiveMaxExclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, true , 1.0, new MinExclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, 1.0, new MinExclusiveMaxExclusiveRange<>(0.0, 1.0));


        assertRange(rangeCalculator, true , 1.0, new UnBoundedRange<>());
        assertRange(rangeCalculator, false, 1.0, new NeverInRange<>());

        assertRange(rangeCalculator, true , 1.0, new MinInclusiveRange<>(0.0));
        assertRange(rangeCalculator, true , 1.0, new MinExclusiveRange<>(0.0));
        assertRange(rangeCalculator, false, 1.0, new MaxInclusiveRange<>(0.0));
        assertRange(rangeCalculator, false, 1.0, new MaxExclusiveRange<>(0.0));

        assertRange(rangeCalculator, false, -1.0, new MinInclusiveRange<>(0.0));
        assertRange(rangeCalculator, false, -1.0, new MinExclusiveRange<>(0.0));
        assertRange(rangeCalculator, true , -1.0, new MaxInclusiveRange<>(0.0));
        assertRange(rangeCalculator, true , -1.0, new MaxExclusiveRange<>(0.0));

        assertRange(rangeCalculator, false, -1.0, new MinInclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, -1.0, new MinInclusiveMaxExclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, -1.0, new MinExclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, -1.0, new MinExclusiveMaxExclusiveRange<>(0.0, 1.0));

        assertRange(rangeCalculator, true , 0.5, new MinInclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, true , 0.5, new MinInclusiveMaxExclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, true , 0.5, new MinExclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, true , 0.5, new MinExclusiveMaxExclusiveRange<>(0.0, 1.0));

        assertRange(rangeCalculator, false, 2.0, new MinInclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, 2.0, new MinInclusiveMaxExclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, 2.0, new MinExclusiveMaxInclusiveRange<>(0.0, 1.0));
        assertRange(rangeCalculator, false, 2.0, new MinExclusiveMaxExclusiveRange<>(0.0, 1.0));
    }

    @Test
    public void testIntegerRanges()
    {
        RangeCalculator<Integer> rangeCalculator = new RangeCalculator<>(Integer::compare);

        assertRange(rangeCalculator, true , 0, new UnBoundedRange<>());
        assertRange(rangeCalculator, false, 0, new NeverInRange<>());

        assertRange(rangeCalculator, true , 0, new MinInclusiveRange<>(0));
        assertRange(rangeCalculator, false, 0, new MinExclusiveRange<>(0));
        assertRange(rangeCalculator, true , 0, new MaxInclusiveRange<>(0));
        assertRange(rangeCalculator, false, 0, new MaxExclusiveRange<>(0));

        assertRange(rangeCalculator, true , 0, new MinInclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, true , 0, new MinInclusiveMaxExclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, 0, new MinExclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, 0, new MinExclusiveMaxExclusiveRange<>(0, 1));

        assertRange(rangeCalculator, true , 1, new MinInclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, 1, new MinInclusiveMaxExclusiveRange<>(0, 1));
        assertRange(rangeCalculator, true , 1, new MinExclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, 1, new MinExclusiveMaxExclusiveRange<>(0, 1));


        assertRange(rangeCalculator, true , 1, new UnBoundedRange<>());
        assertRange(rangeCalculator, false, 1, new NeverInRange<>());

        assertRange(rangeCalculator, true , 1, new MinInclusiveRange<>(0));
        assertRange(rangeCalculator, true , 1, new MinExclusiveRange<>(0));
        assertRange(rangeCalculator, false, 1, new MaxInclusiveRange<>(0));
        assertRange(rangeCalculator, false, 1, new MaxExclusiveRange<>(0));

        assertRange(rangeCalculator, false, -1, new MinInclusiveRange<>(0));
        assertRange(rangeCalculator, false, -1, new MinExclusiveRange<>(0));
        assertRange(rangeCalculator, true , -1, new MaxInclusiveRange<>(0));
        assertRange(rangeCalculator, true , -1, new MaxExclusiveRange<>(0));

        assertRange(rangeCalculator, false, -1, new MinInclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, -1, new MinInclusiveMaxExclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, -1, new MinExclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, -1, new MinExclusiveMaxExclusiveRange<>(0, 1));

        assertRange(rangeCalculator, true , 1, new MinInclusiveMaxInclusiveRange<>(0, 2));
        assertRange(rangeCalculator, true , 1, new MinInclusiveMaxExclusiveRange<>(0, 2));
        assertRange(rangeCalculator, true , 1, new MinExclusiveMaxInclusiveRange<>(0, 2));
        assertRange(rangeCalculator, true , 1, new MinExclusiveMaxExclusiveRange<>(0, 2));

        assertRange(rangeCalculator, false, 2, new MinInclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, 2, new MinInclusiveMaxExclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, 2, new MinExclusiveMaxInclusiveRange<>(0, 1));
        assertRange(rangeCalculator, false, 2, new MinExclusiveMaxExclusiveRange<>(0, 1));
    }

    @Test
    public void testStringRanges()
    {
        RangeCalculator<String> rangeCalculator = new RangeCalculator<>(String::compareTo);

        assertRange(rangeCalculator, true , "A", new UnBoundedRange<>());
        assertRange(rangeCalculator, false, "A", new NeverInRange<>());

        assertRange(rangeCalculator, true , "A", new MinInclusiveRange<>("A"));
        assertRange(rangeCalculator, false, "A", new MinExclusiveRange<>("A"));

        assertRange(rangeCalculator, true , "A", new MinInclusiveMaxInclusiveRange<>("A", "C"));
        assertRange(rangeCalculator, true , "B", new MinInclusiveMaxInclusiveRange<>("A", "C"));
        assertRange(rangeCalculator, true , "C", new MinInclusiveMaxInclusiveRange<>("A", "C"));

        assertRange(rangeCalculator, false, "A", new MinExclusiveMaxExclusiveRange<>("A", "C"));
        assertRange(rangeCalculator, true , "B", new MinExclusiveMaxExclusiveRange<>("A", "C"));
        assertRange(rangeCalculator, false, "C", new MinExclusiveMaxExclusiveRange<>("A", "C"));
    }

    public <TUnit> void assertRange(RangeCalculator<TUnit> rangeCalculator, boolean expectedToBeInRange, TUnit value, Range<TUnit> range)
    {
        assertEquals(
            expectedToBeInRange,
            rangeCalculator.isInRange(value, range),
            () -> "The value " + value + " " + (expectedToBeInRange ? "was" : "was not") + " " + (expectedToBeInRange ? "in" : "out of") + " " + range + " when it should " + (expectedToBeInRange? "not " : "") + "have been."
        );
    }
}
