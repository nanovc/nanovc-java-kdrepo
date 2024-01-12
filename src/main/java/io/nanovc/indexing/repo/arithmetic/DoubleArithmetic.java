package io.nanovc.indexing.repo.arithmetic;

/**
 * Arithmetic with {@link Double doubles}.
 */
public class DoubleArithmetic extends Arithmetic<Double>
{
    /**
     * Adds the two values.
     * result = leftValue + rightValue
     *
     * @param leftValue  The left value to add.
     * @param rightValue The right value to add.
     * @return The result of adding the two values. result = leftValue + rightValue
     */
    @Override public Double add(Double leftValue, Double rightValue)
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
    @Override public Double subtract(Double leftValue, Double rightValue)
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
    @Override public Double multiply(Double leftValue, Double rightValue)
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
    @Override public Double divide(Double leftValue, Double rightValue)
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
    @Override public Double halveValue(Double value)
    {
        return value / 2.0;
    }

    /**
     * Doubles the value.
     * result = value * 2
     *
     * @param value The value to double.
     * @return The result of doubling the value. result = value * 2
     */
    @Override public Double doubleValue(Double value)
    {
        return value * 2.0;
    }

    /**
     * The singleton reusable instance.
     */
    private final static DoubleArithmetic instance = new DoubleArithmetic();

    /**
     * The singleton reusable instance.
     * @return The singleton reusable instance.
     */
    public static DoubleArithmetic instance()
    {
        return instance;
    }
}
