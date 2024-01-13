package io.nanovc.indexing.repo.arithmetic;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Arithmetic where the implementations can be plugged in.
 *
 * @param <TUnit> The data type of the unit for the dimension that this arithmetic is for.
 */
public class PluggableArithmetic<TUnit> extends Arithmetic<TUnit>
{
    public PluggableArithmetic(
        Comparator<TUnit> comparator,
        BiFunction<TUnit, TUnit, TUnit> adder,
        BiFunction<TUnit, TUnit, TUnit> subtractor,
        BiFunction<TUnit, TUnit, TUnit> multiplier,
        BiFunction<TUnit, TUnit, TUnit> divider,
        BiFunction<TUnit, TUnit, TUnit> midPointFinder,
        Function<TUnit, TUnit> halver,
        Function<TUnit, TUnit> doubler
    )
    {
        this.comparator = comparator;
        this.adder = adder;
        this.subtractor = subtractor;
        this.multiplier = multiplier;
        this.divider = divider;
        this.midPointFinder = midPointFinder;
        this.halver = halver;
        this.doubler = doubler;
    }

    /**
     * The logic to compare values.
     */
    private final Comparator<TUnit> comparator;

    /**
     * The logic for adding.
     */
    private final BiFunction<TUnit, TUnit, TUnit> adder;

    /**
     * The logic for adding.
     */
    private final BiFunction<TUnit, TUnit, TUnit> subtractor;

    /**
     * The logic for adding.
     */
    private final BiFunction<TUnit, TUnit, TUnit> multiplier;

    /**
     * The logic for adding.
     */
    private final BiFunction<TUnit, TUnit, TUnit> divider;

    /**
     * The logic for finding the midpoint.
     */
    private final BiFunction<TUnit, TUnit, TUnit> midPointFinder;

    /**
     * The logic for adding.
     */
    private final Function<TUnit, TUnit> halver;

    /**
     * The logic for adding.
     */
    private final Function<TUnit, TUnit> doubler;

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
    @Override public int compare(TUnit leftValue, TUnit rightValue)
    {
        return comparator.compare(leftValue, rightValue);
    }

    /**
     * Adds the two values.
     * result = leftValue + rightValue
     *
     * @param leftValue  The left value to add.
     * @param rightValue The right value to add.
     * @return The result of adding the two values. result = leftValue + rightValue
     */
    @Override public TUnit add(TUnit leftValue, TUnit rightValue)
    {
        return adder.apply(leftValue, rightValue);
    }

    /**
     * Subtracts the two values.
     * result = leftValue - rightValue
     *
     * @param leftValue  The left value to subtract.
     * @param rightValue The right value to subtract.
     * @return The result of subtracting the two values. result = leftValue - rightValue
     */
    @Override public TUnit subtract(TUnit leftValue, TUnit rightValue)
    {
        return subtractor.apply(leftValue, rightValue);
    }

    /**
     * Multiplies the two values.
     * result = leftValue * rightValue
     *
     * @param leftValue  The left value to multiply.
     * @param rightValue The right value to multiply.
     * @return The result of multiplying the two values. result = leftValue * rightValue
     */
    @Override public TUnit multiply(TUnit leftValue, TUnit rightValue)
    {
        return multiplier.apply(leftValue, rightValue);
    }

    /**
     * Divides the two values.
     * result = leftValue / rightValue
     *
     * @param leftValue  The left value to divide.
     * @param rightValue The right value to divide.
     * @return The result of dividing the two values. result = leftValue / rightValue
     */
    @Override public TUnit divide(TUnit leftValue, TUnit rightValue)
    {
        return divider.apply(leftValue, rightValue);
    }

    /**
     * Gets the midpoint between the two values.
     * result = (leftValue + rightValue) / 2 or mid(left,right), depending on the context.
     *
     * @param leftValue  The left value to get the midpoint between.
     * @param rightValue The right value to get the midpoint between.
     * @return The result of getting the midpoint between the two values. result = (leftValue + rightValue) / 2 or mid(left,right)
     */
    @Override public TUnit midPoint(TUnit leftValue, TUnit rightValue)
    {
        return midPointFinder.apply(leftValue, rightValue);
    }

    /**
     * Halves the value.
     * result = value / 2
     *
     * @param value The value to half.
     * @return The result of halving the value. result = value / 2
     */
    @Override public TUnit halveValue(TUnit value)
    {
        return halver.apply(value);
    }

    /**
     * Doubles the value.
     * result = value * 2
     *
     * @param value The value to double.
     * @return The result of doubling the value. result = value * 2
     */
    @Override public TUnit doubleValue(TUnit value)
    {
        return doubler.apply(value);
    }

}
