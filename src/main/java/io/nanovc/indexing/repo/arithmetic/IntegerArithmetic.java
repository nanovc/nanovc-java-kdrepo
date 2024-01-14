package io.nanovc.indexing.repo.arithmetic;

/**
 * Arithmetic with {@link Integer integers}.
 */
public class IntegerArithmetic extends Arithmetic<Integer>
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
    @Override public int compare(Integer leftValue, Integer rightValue)
    {
        return Integer.compare(leftValue, rightValue);
    }

    /**
     * Adds the two values.
     * result = leftValue + rightValue
     *
     * @param leftValue  The left value to add.
     * @param rightValue The right value to add.
     * @return The result of adding the two values. result = leftValue + rightValue
     */
    @Override public Integer add(Integer leftValue, Integer rightValue)
    {
        return leftValue + rightValue;
    }

    /**
     * Subtracts the two values.
     * result = leftValue - rightValue
     *
     * @param leftValue  The left value to subtract.
     * @param rightValue The right value to subtract.
     * @return The result of subtracting the two values. result = leftValue - rightValue
     */
    @Override public Integer subtract(Integer leftValue, Integer rightValue)
    {
        return leftValue - rightValue;
    }

    /**
     * Multiplies the two values.
     * result = leftValue * rightValue
     *
     * @param leftValue  The left value to multiply.
     * @param rightValue The right value to multiply.
     * @return The result of multiplying the two values. result = leftValue * rightValue
     */
    @Override public Integer multiply(Integer leftValue, Integer rightValue)
    {
        return leftValue * rightValue;
    }

    /**
     * Divides the two values.
     * result = leftValue / rightValue
     *
     * @param leftValue  The left value to divide.
     * @param rightValue The right value to divide.
     * @return The result of dividing the two values. result = leftValue / rightValue
     */
    @Override public Integer divide(Integer leftValue, Integer rightValue)
    {
        return leftValue / rightValue;
    }

    /**
     * Halves the value.
     * result = value / 2
     *
     * @param value The value to half.
     * @return The result of halving the value. result = value / 2
     */
    @Override public Integer halveValue(Integer value)
    {
        return value >> 1;
    }

    /**
     * Doubles the value.
     * result = value * 2
     *
     * @param value The value to double.
     * @return The result of doubling the value. result = value * 2
     */
    @Override public Integer doubleValue(Integer value)
    {
        return value << 1;
    }

    /**
     * Gets the midpoint between the two values.
     * result = (leftValue + rightValue) / 2 or mid(left,right), depending on the context.
     *
     * @param leftValue  The left value to get the midpoint between.
     * @param rightValue The right value to get the midpoint between.
     * @return The result of getting the midpoint between the two values. result = (leftValue + rightValue) / 2 or mid(left,right)
     */
    @Override public Integer midPoint(Integer leftValue, Integer rightValue)
    {
        return (leftValue + rightValue) / 2;
    }

    /**
     * Gets the scaled value.
     * result = value * scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value * scale
     */
    @Override public Integer scaleByMultiplier(Integer value, double scale)
    {
        return (int) (value * scale);
    }

    /**
     * Gets the scaled value.
     * result = value / scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value / scale
     */
    @Override public Integer scaleByDivisor(Integer value, double scale)
    {
        return (int) (value / scale);
    }

    /**
     * Quantizes the value to the smallest steps size given.
     * This is the same as rounding to multiples of a base unit.
     *
     * @param value        The value to quantize.
     * @param smallestStep The smallest size to quantize at.
     * @return The result of quantizing the value.
     */
    @Override public Integer quantize(Integer value, Integer smallestStep)
    {
        return (int) (Math.round((double)value / (double)smallestStep) * smallestStep);
    }

    /**
     * The singleton reusable instance.
     */
    private final static IntegerArithmetic instance = new IntegerArithmetic();

    /**
     * The singleton reusable instance.
     * @return The singleton reusable instance.
     */
    public static IntegerArithmetic instance()
    {
        return instance;
    }
}