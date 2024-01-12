package io.nanovc.indexing.repo.arithmetic;

/**
 * Arithmetic with {@link Integer integers}.
 */
public class IntegerArithmetic extends Arithmetic<Integer>
{
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
