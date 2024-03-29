package io.nanovc.indexing.repo.ranges;

import io.nanovc.indexing.repo.arithmetic.Arithmetic;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs calculations on ranges of values.
 *
 * @param <TUnit> The data type of the unit for the dimension that the range is for.
 */
public class RangeCalculator<TUnit>
{
    /**
     * The logic for arithmetic in this dimension.
     */
    private final Arithmetic<TUnit> arithmetic;

    public RangeCalculator(Arithmetic<TUnit> arithmetic)
    {
        this.arithmetic = arithmetic;
    }

    /**
     * Checks whether the given value is in the range.
     *
     * @param value The value to check whether it is in the range.
     * @param range The range that we want to check.
     * @return True if the value is in the range. False if not.
     */
    public boolean isInRange(TUnit value, Range<TUnit> range)
    {
        return switch (range)
        {
            case NotRange<TUnit> r -> !isInRange(value, r.innerRange());
            case OrRange<TUnit> r -> isInRange(value, r.range()) || isInRange(value, r.other());
            case AndRange<TUnit> r -> isInRange(value, r.range()) && isInRange(value, r.other());
            case UnBoundedRange<TUnit> r -> true;
            case NeverInRange<TUnit> r -> false;
            case SingleValueRange<TUnit> r -> this.arithmetic.compare(r.value(), value) == 0;
            case NotSingleValueRange<TUnit> r -> this.arithmetic.compare(r.value(), value) != 0;
            case MultiValueRange<TUnit> r -> r.values().contains(value);
            case NotMultiValueRange<TUnit> r -> !r.values().contains(value);
            case MinInclusiveRange<TUnit> r -> this.arithmetic.compare(r.min(), value) <= 0;
            case MinExclusiveRange<TUnit> r -> this.arithmetic.compare(r.min(), value) < 0;
            case MaxInclusiveRange<TUnit> r -> this.arithmetic.compare(value, r.max()) <= 0;
            case MaxExclusiveRange<TUnit> r -> this.arithmetic.compare(value, r.max()) < 0;
            case MinInclusiveMaxInclusiveRange<TUnit> r ->
                this.arithmetic.compare(value, r.min()) >= 0 && this.arithmetic.compare(value, r.max()) <= 0;
            case MinInclusiveMaxExclusiveRange<TUnit> r ->
                this.arithmetic.compare(value, r.min()) >= 0 && this.arithmetic.compare(value, r.max()) < 0;
            case MinExclusiveMaxInclusiveRange<TUnit> r ->
                this.arithmetic.compare(value, r.min()) > 0 && this.arithmetic.compare(value, r.max()) <= 0;
            case MinExclusiveMaxExclusiveRange<TUnit> r ->
                this.arithmetic.compare(value, r.min()) > 0 && this.arithmetic.compare(value, r.max()) < 0;
        };
    }

    /**
     * @param value                   The value to check.
     * @param distance                The distance within which we should check. If this is null then it implies infinity and should probably return true.
     * @param returnForAmbiguousCases The return value for ambiguous cases or when we can't determine exactly. This is helpful if we prefer to search more ranges than is strictly needed, just to be sure.
     * @param range                   The range to check in. If the value is in the range then it should return true.
     * @return True if the given value is within the given distance from the range.
     */
    public boolean isWithinDistanceOfRange(TUnit value, TUnit distance, boolean returnForAmbiguousCases, Range<TUnit> range)
    {
        // First check whether it is within the range:
        boolean isInRange = isInRange(value, range);
        if (isInRange) return true;
        // Now we know that it is not in the range.

        // Check the extents of the range in regard to infinity:
        switch (range)
        {
            case NeverInRange<TUnit> r ->
            {
                // Never in range no matter what distance.
                return false;
            }
            case MultiValueRange<TUnit> r -> {if (distance == null) return true;}
            case NotMultiValueRange<TUnit> r -> {if (distance == null) return true;}
            case MinInclusiveRange<TUnit> r -> {if (distance == null) return true;}
            case MinExclusiveRange<TUnit> r -> {if (distance == null) return true;}
            case MaxInclusiveRange<TUnit> r -> {if (distance == null) return true;}
            case MaxExclusiveRange<TUnit> r -> {if (distance == null) return true;}
            case MinInclusiveMaxInclusiveRange<TUnit> r -> {if (distance == null) return true;}
            case MinInclusiveMaxExclusiveRange<TUnit> r -> {if (distance == null) return true;}
            case MinExclusiveMaxInclusiveRange<TUnit> r -> {if (distance == null) return true;}
            case MinExclusiveMaxExclusiveRange<TUnit> r -> {if (distance == null) return true;}
            default -> {}
        }
        // Now we know that the distance is not null for cases that we handle.

        // Check the extents of the range:
        switch (range)
        {
            case MinInclusiveRange<TUnit> r ->
            {
                // Get the distance to the range:
                TUnit rangeDistance = arithmetic.distanceBetween(r.min(), value);

                // Check whether it is in range:
                return arithmetic.compare(rangeDistance, distance) <= 0;
            }
            case MinExclusiveRange<TUnit> r ->
            {
                // Get the distance to the range:
                TUnit rangeDistance = arithmetic.distanceBetween(r.min(), value);

                // Check whether it is in range:
                return arithmetic.compare(rangeDistance, distance) < 0;
            }
            case MaxInclusiveRange<TUnit> r ->
            {
                // Get the distance to the range:
                TUnit rangeDistance = arithmetic.distanceBetween(r.max(), value);

                // Check whether it is in range:
                return arithmetic.compare(rangeDistance, distance) <= 0;
            }
            case MaxExclusiveRange<TUnit> r ->
            {
                // Get the distance to the range:
                TUnit rangeDistance = arithmetic.distanceBetween(r.max(), value);

                // Check whether it is in range:
                return arithmetic.compare(rangeDistance, distance) < 0;
            }
            case MinInclusiveMaxInclusiveRange<TUnit> r ->
            {
                TUnit rangeDistance;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.min(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) <= 0) return true;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.max(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) <= 0) return true;

                // If we get here then we are not within the distance:
                return false;
            }
            case MinInclusiveMaxExclusiveRange<TUnit> r ->
            {
                TUnit rangeDistance;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.min(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) <= 0) return true;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.max(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) < 0) return true;

                // If we get here then we are not within the distance:
                return false;
            }
            case MinExclusiveMaxInclusiveRange<TUnit> r ->
            {
                TUnit rangeDistance;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.min(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) < 0) return true;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.max(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) <= 0) return true;

                // If we get here then we are not within the distance:
                return false;
            }
            case MinExclusiveMaxExclusiveRange<TUnit> r ->
            {
                TUnit rangeDistance;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.min(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) < 0) return true;

                // Get the distance to the range:
                rangeDistance = arithmetic.distanceBetween(r.max(), value);

                // Check whether it is in range:
                if (arithmetic.compare(rangeDistance, distance) < 0) return true;

                // If we get here then we are not within the distance:
                return false;
            }
            default ->
            {
                // For all cases that we don't explicitly define,
                // use the preferred result:
                return returnForAmbiguousCases;
            }
        }
    }

    /**
     * Checks whether the range can still be split into a smaller range.
     *
     * @param range      The range to split.
     * @param splitValue The value to divide the range at. It is assumed to be in the middle of the range.
     * @return True if the range can still be split. False if it can't be split further.
     */
    public boolean canSplitRange(Range<TUnit> range, TUnit splitValue)
    {
        return switch (range)
        {
            //case NotRange<TUnit>                      r -> null;
            //case OrRange<TUnit>                       r -> null;
            //case AndRange<TUnit>                      r -> null;
            //case UnBoundedRange<TUnit>                r -> null;
            //case NeverInRange<TUnit>                  r -> null;
            //case SingleValueRange<TUnit>              r -> null;
            //case NotSingleValueRange<TUnit>           r -> null;
            //case MultiValueRange<TUnit>               r -> null;
            //case NotMultiValueRange<TUnit>            r -> null;

            case MinInclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.min()) > 0;
            case MinExclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.min()) > 0;
            case MaxInclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.max()) < 0;
            case MaxExclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.max()) < 0;


            case MinInclusiveMaxInclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.min()) > 0 && getArithmetic().compare(splitValue, r.max()) < 0;
            case MinInclusiveMaxExclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.min()) > 0 && getArithmetic().compare(splitValue, r.max()) < 0;
            case MinExclusiveMaxInclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.min()) > 0 && getArithmetic().compare(splitValue, r.max()) < 0;
            case MinExclusiveMaxExclusiveRange<TUnit> r -> getArithmetic().compare(splitValue, r.min()) > 0 && getArithmetic().compare(splitValue, r.max()) < 0;

            default -> throw new UnsupportedOperationException("Cannot split the given range");
        };
    }

    /**
     * Splits the range at the given point.
     *
     * @param range      The range to split.
     * @param splitValue The value to divide the range at. It is assumed to be in the middle of the range.
     * @param inclusion  The way in which to include the splitValue in the split range result. In the lower, higher or both range splits.
     * @return The split range.
     */
    public RangeSplit<TUnit> splitRange(Range<TUnit> range, TUnit splitValue, RangeSplitInclusion inclusion)
    {
        return switch (range)
        {
            //case NotRange<TUnit>                      r -> null;
            //case OrRange<TUnit>                       r -> null;
            //case AndRange<TUnit>                      r -> null;
            //case UnBoundedRange<TUnit>                r -> null;
            //case NeverInRange<TUnit>                  r -> null;
            //case SingleValueRange<TUnit>              r -> null;
            //case NotSingleValueRange<TUnit>           r -> null;
            //case MultiValueRange<TUnit>               r -> null;
            //case NotMultiValueRange<TUnit>            r -> null;

            case MinInclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveRange<>(splitValue));
                case Higher ->
                    new RangeSplit<>(new MinInclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
                case Both ->
                    new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
            };
            case MinExclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveRange<>(splitValue));
                case Higher ->
                    new RangeSplit<>(new MinExclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
                case Both ->
                    new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveRange<>(splitValue));
            };
            case MaxInclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinExclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Higher ->
                    new RangeSplit<>(new MaxExclusiveRange<>(splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Both ->
                    new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
            };
            case MaxExclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinExclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Higher ->
                    new RangeSplit<>(new MaxExclusiveRange<>(splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Both ->
                    new RangeSplit<>(new MaxInclusiveRange<>(splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
            };


            case MinInclusiveMaxInclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Higher ->
                    new RangeSplit<>(new MinInclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Both ->
                    new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
            };
            case MinInclusiveMaxExclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Higher ->
                    new RangeSplit<>(new MinInclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Both ->
                    new RangeSplit<>(new MinInclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
            };
            case MinExclusiveMaxInclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Higher ->
                    new RangeSplit<>(new MinExclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
                case Both ->
                    new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxInclusiveRange<>(splitValue, r.max()));
            };
            case MinExclusiveMaxExclusiveRange<TUnit> r -> switch (inclusion)
            {
                case Lower ->
                    new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinExclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Higher ->
                    new RangeSplit<>(new MinExclusiveMaxExclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
                case Both ->
                    new RangeSplit<>(new MinExclusiveMaxInclusiveRange<>(r.min(), splitValue), new MinInclusiveMaxExclusiveRange<>(splitValue, r.max()));
            };

            default -> throw new UnsupportedOperationException("Cannot split the given range");
        };
    }

    /**
     * Gets the midpoint of the given range.
     *
     * @param range The range to get the midpoint of.
     * @return The midpoint of the given range.
     */
    public TUnit midPoint(Range<TUnit> range)
    {
        return switch (range)
        {
            //case NotRange<TUnit>                      r -> null;
            //case OrRange<TUnit>                       r -> null;
            //case AndRange<TUnit>                      r -> null;
            //case UnBoundedRange<TUnit>                r -> null;
            //case NeverInRange<TUnit>                  r -> null;
            //case SingleValueRange<TUnit>              r -> null;
            //case NotSingleValueRange<TUnit>           r -> null;
            //case MultiValueRange<TUnit>               r -> null;
            //case NotMultiValueRange<TUnit>            r -> null;

            case MinInclusiveRange<TUnit>             r -> r.min();
            case MinExclusiveRange<TUnit>             r -> r.min();
            case MaxInclusiveRange<TUnit>             r -> r.max();
            case MaxExclusiveRange<TUnit>             r -> r.max();

            case MinInclusiveMaxInclusiveRange<TUnit> r -> arithmetic.midPoint(r.min(), r.max());
            case MinInclusiveMaxExclusiveRange<TUnit> r -> arithmetic.midPoint(r.min(), r.max());
            case MinExclusiveMaxInclusiveRange<TUnit> r -> arithmetic.midPoint(r.min(), r.max());
            case MinExclusiveMaxExclusiveRange<TUnit> r -> arithmetic.midPoint(r.min(), r.max());

            default -> throw new UnsupportedOperationException("Cannot get midpoint of the given range");
        };
    }

    /**
     * This calculates the range splits for the given range
     * by dividing that range into the given number of divisions.
     *
     * @param range         The range that we want to split.
     * @param divisions     The number of divisions to split the range into.
     * @param smallestStep  The smallest step size that we want to split the range into.
     * @param createAdditionalLeftRange  True to add an additional range at the beginning for everything to the left of the given range. False to just split the range given.
     * @param createAdditionalRightRange True to add an additional range at the end for everything to the right of the given range. False to just split the range given.
     * @param splitsToAddTo The list to add th range splits to.
     */
    public void calculateRangeSplits(Range<TUnit> range, int divisions, TUnit smallestStep, boolean createAdditionalLeftRange, boolean createAdditionalRightRange, List<Range<TUnit>> splitsToAddTo)
    {
        // Get the arithmetic implementation so that we can work out things:
        Arithmetic<TUnit> arithmetic = getArithmetic();

        // Get the starting value:
        TUnit startingValue = switch (range)
        {
            case MinInclusiveMaxInclusiveRange<TUnit> r -> r.min();
            case MinInclusiveMaxExclusiveRange<TUnit> r -> r.min();
            case MinExclusiveMaxInclusiveRange<TUnit> r -> r.min();
            case MinExclusiveMaxExclusiveRange<TUnit> r -> r.min();

            default -> throw new UnsupportedOperationException("Cannot split the given range into divisions");
        };

        // Get the ending value:
        TUnit endingValue = switch (range)
        {
            case MinInclusiveMaxInclusiveRange<TUnit> r -> r.max();
            case MinInclusiveMaxExclusiveRange<TUnit> r -> r.max();
            case MinExclusiveMaxInclusiveRange<TUnit> r -> r.max();
            case MinExclusiveMaxExclusiveRange<TUnit> r -> r.max();

            default -> throw new UnsupportedOperationException("Cannot split the given range into divisions");
        };

        // Work out the extent of the values:
        TUnit diff = arithmetic.subtract(endingValue, startingValue);

        // Get the step:
        TUnit step = arithmetic.scaleByDivisor(diff, divisions);

        // Make sure the step is not smaller than the smallest step size:
        if (arithmetic.compare(step, smallestStep) < 0) step = smallestStep;

        // Work out the split values:
        // REMEMBER: It's possible that because of rounding or the minimum smallest step, we don't get the exact number of divisions that we expect.
        ArrayList<TUnit> values = new ArrayList<>();
        TUnit currentValue = startingValue;
        int stepIndex = 0;
        while (arithmetic.compare(currentValue, endingValue) <= 0)
        {
            // We need to keep stepping.

            // Save this value:
            values.add(currentValue);

            // Take the next step:
            stepIndex++;
            currentValue = arithmetic.add(currentValue, step);

            // Quantize the value to the smallest step size:
            currentValue = arithmetic.quantize(currentValue, smallestStep);
        }
        // Now we know what the values are.

        // Get the first and last indexes:
        int firstIndex = 0;
        int secondIndex = 1;
        int lastIndex = values.size() - 1;


        // Check whether we want to add a range to the left of the given range (to catch out of bounds values):
        if (createAdditionalLeftRange)
        {
            // We want to add a range to the left of the given range (to catch out of bounds values).

            // Create the range to the left:
            Range<TUnit> additionalLeftRange = switch (range)
            {
                case MinInclusiveMaxInclusiveRange<TUnit> r -> new MaxExclusiveRange<>(r.min());
                case MinInclusiveMaxExclusiveRange<TUnit> r -> new MaxExclusiveRange<>(r.min());
                case MinExclusiveMaxInclusiveRange<TUnit> r -> new MaxInclusiveRange<>(r.min());
                case MinExclusiveMaxExclusiveRange<TUnit> r -> new MaxInclusiveRange<>(r.min());

                default -> throw new UnsupportedOperationException("Cannot add the additional left range to the given range");
            };

            // Add this to the result:
            splitsToAddTo.add(additionalLeftRange);
        }

        // Create the ranges:
        TUnit previousValue = null;
        for (int i = 0; i < values.size(); i++)
        {
            // Get the current value:
            currentValue = values.get(i);

            // Check if this is the first value:
            if (i == firstIndex)
            {
                // This is the first value.

                // Check if this is the last value too:
                if (i == lastIndex)
                {
                    // This is the first and last value, meaning there is only one value.
                }
                else
                {
                    // There are many values and this is the first one.
                }
            }
            else if (i == secondIndex)
            {
                // This is the second value. This is enough to make the first range.

                // Check if this is the second and also the last value:
                if (i == lastIndex)
                {
                    // This is the second value, but also the last value.

                    switch (range)
                    {
                        case MinInclusiveMaxInclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinInclusiveMaxInclusiveRange<>(previousValue, currentValue));
                        case MinInclusiveMaxExclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));
                        case MinExclusiveMaxInclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinExclusiveMaxInclusiveRange<>(previousValue, currentValue));
                        case MinExclusiveMaxExclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinExclusiveMaxExclusiveRange<>(previousValue, currentValue));

                        default ->
                            throw new UnsupportedOperationException("Cannot split the given range into divisions");
                    }
                }
                else
                {
                    // This is the second value, and there are more values.

                    switch (range)
                    {
                        case MinInclusiveMaxInclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));
                        case MinInclusiveMaxExclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));
                        case MinExclusiveMaxInclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinExclusiveMaxExclusiveRange<>(previousValue, currentValue));
                        case MinExclusiveMaxExclusiveRange<TUnit> r ->
                            splitsToAddTo.add(new MinExclusiveMaxExclusiveRange<>(previousValue, currentValue));

                        default ->
                            throw new UnsupportedOperationException("Cannot split the given range into divisions");
                    }
                }
            }
            else if (i == lastIndex)
            {
                // This is the last value and there were more than two values.

                switch (range)
                {
                    case MinInclusiveMaxInclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxInclusiveRange<>(previousValue, currentValue));
                    case MinInclusiveMaxExclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));
                    case MinExclusiveMaxInclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxInclusiveRange<>(previousValue, currentValue));
                    case MinExclusiveMaxExclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));

                    default -> throw new UnsupportedOperationException("Cannot split the given range into divisions");
                }
            }
            else
            {
                // This is not the first value, not the second and not the last value. It's an intermediate value.

                switch (range)
                {
                    case MinInclusiveMaxInclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));
                    case MinInclusiveMaxExclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));
                    case MinExclusiveMaxInclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));
                    case MinExclusiveMaxExclusiveRange<TUnit> r ->
                        splitsToAddTo.add(new MinInclusiveMaxExclusiveRange<>(previousValue, currentValue));

                    default -> throw new UnsupportedOperationException("Cannot split the given range into divisions");
                }
            }

            // Move to the next value:
            previousValue = currentValue;
        }

        // Check whether we want to add a range to the right of the given range (to catch out of bounds values):
        if (createAdditionalRightRange)
        {
            // We want to add a range to the right of the given range (to catch out of bounds values).

            // Create the range to the right:
            Range<TUnit> additionalRightRange = switch (range)
            {
                case MinInclusiveMaxInclusiveRange<TUnit> r -> new MinExclusiveRange<>(r.max());
                case MinInclusiveMaxExclusiveRange<TUnit> r -> new MinInclusiveRange<>(r.max());
                case MinExclusiveMaxInclusiveRange<TUnit> r -> new MinExclusiveRange<>(r.max());
                case MinExclusiveMaxExclusiveRange<TUnit> r -> new MinInclusiveRange<>(r.max());

                default -> throw new UnsupportedOperationException("Cannot add the additional left range to the given range");
            };

            // Add this to the result:
            splitsToAddTo.add(additionalRightRange);
        }
    }

    public Range<TUnit> createRangeLessThanOrEqualTo(TUnit value)
    {
        return new MaxInclusiveRange<>(value);
    }

    public Range<TUnit> createRangeLessThan(TUnit value)
    {
        return new MaxExclusiveRange<>(value);
    }

    public Range<TUnit> createRangeEqualTo(TUnit value)
    {
        return new SingleValueRange<>(value);
    }

    public Range<TUnit> createRangeGreaterThan(TUnit value)
    {
        return new MinExclusiveRange<>(value);
    }

    public Range<TUnit> createRangeGreaterThanOrEqualTo(TUnit value)
    {
        return new MinInclusiveRange<>(value);
    }


    public Range<TUnit> createBoundedRangeLessThanOrEqualTo(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MaxInclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeLessThan(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MaxExclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeEqualTo(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new SingleValueRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeGreaterThan(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MinExclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeGreaterThanOrEqualTo(TUnit value, Range<TUnit> bounds)
    {
        return new AndRange<>(new MinInclusiveRange<>(value), bounds);
    }

    public Range<TUnit> createBoundedRangeBetween(TUnit minInclusive, TUnit maxInclusive, Range<TUnit> bounds)
    {
        return new AndRange<>(new MinInclusiveMaxInclusiveRange<>(minInclusive, maxInclusive), bounds);
    }

    /**
     * Gets the logic for arithmetic in this dimension.
     *
     * @return The logic for arithmetic in this dimension.
     */
    public Arithmetic<TUnit> getArithmetic()
    {
        return arithmetic;
    }


}
