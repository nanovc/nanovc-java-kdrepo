package io.nanovc.indexing.repo;

import io.nanovc.indexing.repo.ranges.MinInclusiveMaxInclusiveRange;
import io.nanovc.indexing.repo.ranges.UnBoundedRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link HyperCube}.
 */
class HyperCubeTests
{
    @Test
    public void creationTest()
    {
        new HyperCube(null);
    }

    @Test
    public void cube_1D_UnboundedDim()
    {
        // Define the cube structure:
        HyperCubeDefinition definition = new HyperCubeDefinition();
        Dimension<Integer> xDim = definition.addDimension(Integer::compareTo, Integer::sum, (l, r) -> l - r, "X", new UnBoundedRange<>());

        // Create a hyper cube:
        HyperCube cube = new HyperCube(definition, xDim.rangeBetween(0, 1));

        // Make sure coordinates are in range:
        assertFalse(cube.isCoordinateInRange(-2)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(-1)); // Bounded by range
        assertTrue(cube.isCoordinateInRange(0));
        assertTrue(cube.isCoordinateInRange(1));
        assertFalse(cube.isCoordinateInRange(2)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(3)); // Bounded by range
    }

    @Test
    public void cube_1D_BoundedDim()
    {
        // Define the cube structure:
        HyperCubeDefinition definition = new HyperCubeDefinition();
        Dimension<Integer> xDim = definition.addDimension(Integer::compareTo, Integer::sum, (l, r) -> l - r, "X", new MinInclusiveMaxInclusiveRange<>(0,1));

        // Create a hyper cube:
        HyperCube cube = new HyperCube(definition, xDim.rangeBetween(-1, 2)); // Outside of dimension bounds.

        // Make sure coordinates are in range:
        assertFalse(cube.isCoordinateInRange(-2)); // Bounded by range and dimension
        assertFalse(cube.isCoordinateInRange(-1)); // Bounded by dimension
        assertTrue(cube.isCoordinateInRange(0));
        assertTrue(cube.isCoordinateInRange(1));
        assertFalse(cube.isCoordinateInRange(2)); // Bounded by dimension
        assertFalse(cube.isCoordinateInRange(3)); // Bounded by range and dimension
    }

    @Test
    public void cube_2D_UnboundedDim()
    {
        // Define the cube structure:
        HyperCubeDefinition definition = new HyperCubeDefinition();
        Dimension<Integer> xDim = definition.addDimension(Integer::compareTo, Integer::sum, (l, r) -> l - r, "X", new UnBoundedRange<>());
        Dimension<Double> yDim = definition.addDimension(Double::compareTo, Double::sum, (l, r) -> l - r, "Y", new UnBoundedRange<>());

        // Create a hyper cube:
        HyperCube cube = new HyperCube(definition, xDim.rangeBetween(0, 1), yDim.rangeBetween(0.0, 1.0));

        // Make sure coordinates are in range:
        assertFalse(cube.isCoordinateInRange(-2, -2.0)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(-1, -1.0)); // Bounded by range
        assertTrue(cube.isCoordinateInRange(0, 0.0));
        assertTrue(cube.isCoordinateInRange(1, 1.0));
        assertFalse(cube.isCoordinateInRange(2, 2.0)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(3, 3.0)); // Bounded by range

        assertFalse(cube.isCoordinateInRange(-2, -2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(-1, -2.0)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(0, -2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(1, -2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(2, -2.0)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(3, -2.0)); // Bounded by range

        assertFalse(cube.isCoordinateInRange(-2, 2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(-1, 2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(0, 2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(1, 2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(2, 2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(3, 2.0)); // Bounded by range, outside y range

        assertFalse(cube.isCoordinateInRange(-2, 0.5)); // Bounded by x range
        assertFalse(cube.isCoordinateInRange(-1, 0.5)); // Bounded by x range
        assertTrue(cube.isCoordinateInRange(0, 0.5));
        assertTrue(cube.isCoordinateInRange(1, 0.5));
        assertFalse(cube.isCoordinateInRange(2, 0.5)); // Bounded by x range
        assertFalse(cube.isCoordinateInRange(3, 0.5)); // Bounded by x range
    }

    @Test
    public void cube_2D_BoundedDim()
    {
        // Define the cube structure:
        HyperCubeDefinition definition = new HyperCubeDefinition();
        Dimension<Integer> xDim = definition.addDimension(Integer::compareTo, Integer::sum, (l, r) -> l - r, "X", new MinInclusiveMaxInclusiveRange<>(0,1));
        Dimension<Double> yDim = definition.addDimension(Double::compareTo, Double::sum, (l, r) -> l - r, "Y", new MinInclusiveMaxInclusiveRange<>(0.0,1.0));

        // Create a hyper cube:
        HyperCube cube = new HyperCube(definition, xDim.rangeBetween(-1, 2), yDim.rangeBetween(-1.0, 2.0)); // Outside of dimension bounds.

        // Make sure coordinates are in range:
        assertFalse(cube.isCoordinateInRange(-2, -2.0)); // Bounded by range and dimension
        assertFalse(cube.isCoordinateInRange(-1, -1.0)); // Bounded by dimension
        assertTrue(cube.isCoordinateInRange(0, 0.0));
        assertTrue(cube.isCoordinateInRange(1, 1.0));
        assertFalse(cube.isCoordinateInRange(2, 2.0)); // Bounded by dimension
        assertFalse(cube.isCoordinateInRange(3, 3.0)); // Bounded by range and dimension

        assertFalse(cube.isCoordinateInRange(-2, -2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(-1, -2.0)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(0, -2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(1, -2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(2, -2.0)); // Bounded by range
        assertFalse(cube.isCoordinateInRange(3, -2.0)); // Bounded by range

        assertFalse(cube.isCoordinateInRange(-2, 2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(-1, 2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(0, 2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(1, 2.0)); // outside y range
        assertFalse(cube.isCoordinateInRange(2, 2.0)); // Bounded by range, outside y range
        assertFalse(cube.isCoordinateInRange(3, 2.0)); // Bounded by range, outside y range

        assertFalse(cube.isCoordinateInRange(-2, 0.5)); // Bounded by x range
        assertFalse(cube.isCoordinateInRange(-1, 0.5)); // Bounded by x range
        assertTrue(cube.isCoordinateInRange(0, 0.5));
        assertTrue(cube.isCoordinateInRange(1, 0.5));
        assertFalse(cube.isCoordinateInRange(2, 0.5)); // Bounded by x range
        assertFalse(cube.isCoordinateInRange(3, 0.5)); // Bounded by x range
    }
}
