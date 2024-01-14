package io.nanovc.indexing.repo.arithmetic;

/**
 * Arithmetic with {@link Double doubles}.
 */
public class DoubleArithmetic extends Arithmetic<Double>
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
    @Override public int compare(Double leftValue, Double rightValue)
    {
        return Double.compare(leftValue, rightValue);
    }

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
     * Gets the midpoint between the two values.
     * result = (leftValue + rightValue) / 2 or mid(left,right), depending on the context.
     *
     * @param leftValue  The left value to get the midpoint between.
     * @param rightValue The right value to get the midpoint between.
     * @return The result of getting the midpoint between the two values. result = (leftValue + rightValue) / 2 or mid(left,right)
     */
    @Override public Double midPoint(Double leftValue, Double rightValue)
    {
        return (leftValue + rightValue) / 2.0;
    }

    /**
     * Gets the scaled value.
     * result = value * scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value * scale
     */
    @Override public Double scaleByMultiplier(Double value, double scale)
    {
        return value * scale;
    }

    /**
     * Gets the scaled value.
     * result = value / scale
     *
     * @param value The value to scale.
     * @param scale The amount to scale by.
     * @return The result of scaling the given value. result = value / scale
     */
    @Override public Double scaleByDivisor(Double value, double scale)
    {
        return value / scale;
    }

    /**
     * Quantizes the value to the smallest steps size given.
     * This is the same as rounding to multiples of a base unit.
     *
     * @param value        The value to quantize.
     * @param smallestStep The smallest size to quantize at.
     * @return The result of quantizing the value.
     */
    @Override public Double quantize(Double value, Double smallestStep)
    {
        return Math.round(value / smallestStep) * smallestStep;
    }

    /**
     * Gets the distance between the two values.
     *
     * @param left  The left value to measure between.
     * @param right The right value to measure between.
     * @return The distance between the two values.
     */
    @Override public Double distanceBetween(Double left, Double right)
    {
        return Math.abs(left - right);
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
