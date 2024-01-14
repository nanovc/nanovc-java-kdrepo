package io.nanovc.indexing.repo.arithmetic;

/**
 * Arithmetic with {@link String strings}.
 */
public class StringArithmetic extends Arithmetic<String>
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
    @Override public int compare(String leftValue, String rightValue)
    {
        return leftValue.compareTo(rightValue);
    }

    /**
     * Adds the two values.
     * result = leftValue + rightValue
     *
     * @param leftValue  The left value to add.
     * @param rightValue The right value to add.
     * @return The result of adding the two values. result = leftValue + rightValue
     */
    @Override public String add(String leftValue, String rightValue)
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
    @Override public String subtract(String leftValue, String rightValue)
    {
        throw new UnsupportedOperationException("Cannot subtract strings");
    }

    /**
     * Multiplies the two values.
     * result = leftValue * rightValue
     *
     * @param leftValue  The left value to multiply.
     * @param rightValue The right value to multiply.
     * @return The result of multiplying the two values. result = leftValue * rightValue
     */
    @Override public String multiply(String leftValue, String rightValue)
    {
        throw new UnsupportedOperationException("Cannot multiply strings");
    }

    /**
     * Divides the two values.
     * result = leftValue / rightValue
     *
     * @param leftValue  The left value to divide.
     * @param rightValue The right value to divide.
     * @return The result of dividing the two values. result = leftValue / rightValue
     */
    @Override public String divide(String leftValue, String rightValue)
    {
        throw new UnsupportedOperationException("Cannot divide strings");
    }

    /**
     * Gets the midpoint between the two values.
     * result = (leftValue + rightValue) / 2 or mid(left,right), depending on the context.
     *
     * @param leftValue  The left value to get the midpoint between.
     * @param rightValue The right value to get the midpoint between.
     * @return The result of getting the midpoint between the two values. result = (leftValue + rightValue) / 2 or mid(left,right)
     */
    @Override public String midPoint(String leftValue, String rightValue)
    {
        throw new UnsupportedOperationException("Cannot find midpoint of strings");
    }

    /**
     * Halves the value.
     * result = value / 2
     *
     * @param value The value to half.
     * @return The result of halving the value. result = value / 2
     */
    @Override public String halveValue(String value)
    {
        return value.substring(0, value.length() / 2);
    }

    /**
     * Doubles the value.
     * result = value * 2
     *
     * @param value The value to double.
     * @return The result of doubling the value. result = value * 2
     */
    @Override public String doubleValue(String value)
    {
        return value + value;
    }

    /**
     * Gets the scaled value.
     * result = value * scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value * scale
     */
    @Override public String scaleByMultiplier(String value, double scale)
    {
        throw new UnsupportedOperationException("Cannot scale strings by multiplication");
    }

    /**
     * Gets the scaled value.
     * result = value / scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value / scale
     */
    @Override public String scaleByDivisor(String value, double scale)
    {
        throw new UnsupportedOperationException("Cannot scale strings by division");
    }

    /**
     * Quantizes the value to the smallest steps size given.
     * This is the same as rounding to multiples of a base unit.
     *
     * @param value        The value to quantize.
     * @param smallestStep The smallest size to quantize at.
     * @return The result of quantizing the value.
     */
    @Override public String quantize(String value, String smallestStep)
    {
        throw new UnsupportedOperationException("Cannot quantize strings");
    }

    /**
     * The singleton reusable instance.
     */
    private final static StringArithmetic instance = new StringArithmetic();

    /**
     * The singleton reusable instance.
     * @return The singleton reusable instance.
     */
    public static StringArithmetic instance()
    {
        return instance;
    }
}
