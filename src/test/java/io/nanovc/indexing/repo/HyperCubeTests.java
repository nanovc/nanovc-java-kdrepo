package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.MinInclusiveMaxInclusiveRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link HyperCube}.
 */
class HyperCubeTests
{
    @Test
    public void creationTest()
    {
        new HyperCube();
    }

    @Test
    public void testCube_1D_Integer()
    {
        HyperCube cube = new HyperCube();
        Dimension<Integer> xDim = cube.addDimension(Integer::compareTo, "X", new MinInclusiveMaxInclusiveRange<>(0, 1));

        assertFalse(xDim.isInRange(-1));
        assertTrue(xDim.isInRange(0));
        assertTrue(xDim.isInRange(1));
        assertFalse(xDim.isInRange(2));
    }

    @Test
    public void testCube_2D_Integer_Double()
    {
        HyperCube cube = new HyperCube();
        Dimension<Integer> xDim = cube.addDimension(Integer::compareTo, "X", new MinInclusiveMaxInclusiveRange<>(0, 1));

        assertFalse(xDim.isInRange(-1));
        assertTrue(xDim.isInRange(0));
        assertTrue(xDim.isInRange(1));
        assertFalse(xDim.isInRange(2));

        Dimension<Double> yDim = cube.addDimension(Double::compareTo, "Y", new MinInclusiveMaxInclusiveRange<>(0.0, 1.0));

        assertFalse(yDim.isInRange(-1.0));
        assertTrue(yDim.isInRange(0.0));
        assertTrue(yDim.isInRange(1.0));
        assertFalse(yDim.isInRange(2.0));

        // Get the dimensions generically:
        assertSame(xDim, cube.getDimension("X"));
        assertSame(xDim, cube.getDimension(0));
        assertSame(yDim, cube.getDimension("Y"));
        assertSame(yDim, cube.getDimension(1));

        Dimension<Double> wrongDimType = cube.getDimension("X");
        wrongDimType.isInRange(123.456);
    }

    @Test
    public void testWrongDimensionCastsFail()
    {
        HyperCube cube = new HyperCube();
        Dimension<Integer> xDim = cube.addDimension(Integer::compareTo, "X", new MinInclusiveMaxInclusiveRange<>(0, 1));

        // Get the dimensions generically:
        assertSame(xDim, cube.getDimension("X"));
        assertSame(xDim, cube.getDimension(0));

        assertThrows(
            ClassCastException.class,
            () ->
            {
                // Use the wrong data type:
                Dimension<Double> wrongDimType = cube.getDimension("X");
                wrongDimType.isInRange(123.456);
            }
        );
    }
}
