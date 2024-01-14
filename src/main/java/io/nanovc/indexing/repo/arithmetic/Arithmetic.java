package io.nanovc.indexing.repo.arithmetic;

/**
 * Contains implementations for performing arithmetic for dimensions.
 *
 * @param <TUnit> The data type of the unit for the dimension that this arithmetic is for.
 */
public abstract class Arithmetic<TUnit>
{
    /**
     * Compares the left value to the right value.
     * <p>
     * You can use it like this:
     * compare(left, right) <  0 // for left <= right
     * compare(left, right) <= 0 // for left <= right
     * compare(left, right) == 0 // for left == right
     * compare(left, right) != 0 // for left != right
     * compare(left, right) >= 0 // for left >= right
     * compare(left, right) >  0 // for left >  right
     *
     * @param leftValue  The left value to compare.
     * @param rightValue The right value to compare.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    public abstract int compare(TUnit leftValue, TUnit rightValue);

    /**
     * Adds the two values.
     * result = leftValue + rightValue
     *
     * @param leftValue  The left value to add.
     * @param rightValue The right value to add.
     * @return The result of adding the two values. result = leftValue + rightValue
     */
    public abstract TUnit add(TUnit leftValue, TUnit rightValue);

    /**
     * Subtracts the two values.
     * result = leftValue - rightValue
     *
     * @param leftValue  The left value to subtract.
     * @param rightValue The right value to subtract.
     * @return The result of subtracting the two values. result = leftValue - rightValue
     */
    public abstract TUnit subtract(TUnit leftValue, TUnit rightValue);

    /**
     * Multiplies the two values.
     * result = leftValue * rightValue
     *
     * @param leftValue  The left value to multiply.
     * @param rightValue The right value to multiply.
     * @return The result of multiplying the two values. result = leftValue * rightValue
     */
    public abstract TUnit multiply(TUnit leftValue, TUnit rightValue);

    /**
     * Divides the two values.
     * result = leftValue / rightValue
     *
     * @param leftValue  The left value to divide.
     * @param rightValue The right value to divide.
     * @return The result of dividing the two values. result = leftValue - rightValue
     */
    public abstract TUnit divide(TUnit leftValue, TUnit rightValue);

    /**
     * Halves the value.
     * result = value / 2
     *
     * @param value The value to half.
     * @return The result of halving the value. result = value / 2
     */
    public abstract TUnit halveValue(TUnit value);

    /**
     * Doubles the value.
     * result = value * 2
     *
     * @param value The value to double.
     * @return The result of doubling the value. result = value * 2
     */
    public abstract TUnit doubleValue(TUnit value);

    /**
     * Gets the midpoint between the two values.
     * result = (leftValue + rightValue) / 2 or mid(left,right), depending on the context.
     *
     * @param leftValue  The left value to get the midpoint between.
     * @param rightValue The right value to get the midpoint between.
     * @return The result of getting the midpoint between the two values. result = (leftValue + rightValue) / 2 or mid(left,right)
     */
    public abstract TUnit midPoint(TUnit leftValue, TUnit rightValue);

    /**
     * Gets the scaled value.
     * result = value * scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value * scale
     */
    public abstract TUnit scaleByMultiplier(TUnit value, double scale);

    /**
     * Gets the scaled value.
     * result = value / scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value / scale
     */
    public abstract TUnit scaleByDivisor(TUnit value, double scale);

    /**
     * Quantizes the value to the smallest steps size given.
     * This is the same as rounding to multiples of a base unit.
     *
     * @param value        The value to quantize.
     * @param smallestStep The smallest size to quantize at.
     * @return The result of quantizing the value.
     */
    public abstract TUnit quantize(TUnit value, TUnit smallestStep);
}
