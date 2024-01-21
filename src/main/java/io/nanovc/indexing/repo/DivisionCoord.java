package io.nanovc.indexing.repo;

import java.util.Arrays;
import java.util.Objects;

/**
 * A coordinate in a multidimensional {@link DivisionCube division cube}.
 * Each value of the coordinate is the division index in the corresponding {@link DivisionDimension division dimension} for that {@link DivisionCube division cube}.
 * @param values The values in this {@link DivisionCoord division coordinate}.
 */
public record DivisionCoord(int... values)
{

    /**
     * Gets the value for a specific dimension.
     * @param dimension The dimension that we want to get.
     * @return The value for that dimension.
     */
    public int getValue(Dimension<?> dimension)
    {
        return getValue(dimension.getDimensionIndex());
    }

    /**
     * Gets the value for a specific dimension.
     * @param dimensionIndex The index of the dimension that we want to get.
     * @return The value for that dimension.
     */
    public int getValue(int dimensionIndex)
    {
        return values()[dimensionIndex];
    }

    @Override public String toString()
    {
        return "<" +
               Arrays.toString(values) +
               ">";
    }

    /**
     * Returns a new coordinate with the additional coordinate value added.
     * @param nextCoordinateValue The additional coordinate value to add to the current one.
     * @return A new coordinate that has our original values plus the next coordinate value added.
     */
    public DivisionCoord withNextValue(int nextCoordinateValue)
    {
        // Get the current values:
        var currentValues = values();

        // Create the values with space for an additional value:
        var nextValues = Arrays.copyOf(values(), currentValues.length + 1);

        // Set the next value:
        nextValues[currentValues.length] = nextCoordinateValue;

        return new DivisionCoord(nextValues);
    }

    /**
     * Indicates whether some other object is "equal to" this one.  In addition
     * to the general contract of {@link Object#equals(Object) Object.equals},
     * record classes must further obey the invariant that when
     * a record instance is "copied" by passing the result of the record component
     * accessor methods to the canonical constructor, as follows:
     * <pre>
     *     R copy = new R(r.c1(), r.c2(), ..., r.cn());
     * </pre>
     * then it must be the case that {@code r.equals(copy)}.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this record is equal to the
     *     argument; {@code false} otherwise.
     * @implSpec The implicitly provided implementation returns {@code true} if
     *     and only if the argument is an instance of the same record class
     *     as this record, and each component of this record is equal to
     *     the corresponding component of the argument; otherwise, {@code
     *     false} is returned. Equality of a component {@code c} is
     *     determined as follows:
     *     <ul>
     *
     *     <li> If the component is of a reference type, the component is
     *     considered equal if and only if {@link
     *     Objects#equals(Object, Object)
     *     Objects.equals(this.c, r.c)} would return {@code true}.
     *
     *     <li> If the component is of a primitive type, using the
     *     corresponding primitive wrapper class {@code PW} (the
     *     corresponding wrapper class for {@code int} is {@code
     *     java.lang.Integer}, and so on), the component is considered
     *     equal if and only if {@code
     *     PW.compare(this.c, r.c)} would return {@code 0}.
     *
     *     </ul>
     *     <p>
     *     Apart from the semantics described above, the precise algorithm
     *     used in the implicitly provided implementation is unspecified
     *     and is subject to change. The implementation may or may not use
     *     calls to the particular methods listed, and may or may not
     *     perform comparisons in the order of component declaration.
     * @see Objects#equals(Object, Object)
     */
    @Override public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof DivisionCoord other)
        {
            return Arrays.equals(values(), other.values());
        }
        else return false;
    }

    /**
     * Returns a hash code value for the record.
     * Obeys the general contract of {@link Object#hashCode Object.hashCode}.
     * For records, hashing behavior is constrained by the refined contract
     * of {@link Record#equals Record.equals}, so that any two records
     * created from the same components must have the same hash code.
     *
     * @return a hash code value for this record.
     * @implSpec The implicitly provided implementation returns a hash code value derived
     *     by combining appropriate hashes from each component.
     *     The precise algorithm used in the implicitly provided implementation
     *     is unspecified and is subject to change within the above limits.
     *     The resulting integer need not remain consistent from one
     *     execution of an application to another execution of the same
     *     application, even if the hashes of the component values were to
     *     remain consistent in this way.  Also, a component of primitive
     *     type may contribute its bits to the hash code differently than
     *     the {@code hashCode} of its primitive wrapper class.
     * @see Object#hashCode()
     */
    @Override public int hashCode()
    {
        var vals = values();
        return vals == null ? 0 : Arrays.hashCode(values());
    }
}
