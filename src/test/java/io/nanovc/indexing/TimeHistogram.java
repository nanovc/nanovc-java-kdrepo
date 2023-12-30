package io.nanovc.indexing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A histogram that is useful for measuring nano-time.
 * We use a log based binning strategy that gives use 10 bins per order of magnitude.
 */
public class TimeHistogram
{
    /**
     * This histogram that we are measuring.
     * The key is the log (base) of the duration multiplied by the base to give is more resolution in the range.
     * You have to divide by the base twice to get the binned duration of the key.
     * This gives us more resolution in the bins.
     */
    public final Map<Long, AtomicLong> histogram = new TreeMap<>();

    /**
     * The base to use when creating the bins.
     */
    public final int base;

    /**
     * The log of the base value.
     * This is used when calculating the log amount.
     */
    private final double logOfBase;

    public TimeHistogram()
    {
        this(10);
    }

    public TimeHistogram(int base)
    {
        this.base = base;
        this.logOfBase = Math.log(base);
    }

    /**
     * Adds the given duration to the histogram.
     *
     * @param duration The duration in nanoseconds that the event took.
     */
    public void add(long duration)
    {
        // Work out the log amount:
        var log = Math.log(duration) / this.logOfBase;

        // Work out the index to store:
        var index = Math.round(log * this.base);

        // Get the counter:
        AtomicLong counter = this.histogram.computeIfAbsent(index, _unused -> new AtomicLong(0));

        // Increment the counter:
        counter.incrementAndGet();
    }

    /**
     * This iterates through each entry of the time histogram.
     *
     * @param consumer The code to process each value. The first value is the duration. The second value is the count.
     */
    public void forEach(BiConsumer<BinRange, Long> consumer)
    {
        // Go through each entry of the map:
        for (Map.Entry<Long, AtomicLong> entry : this.histogram.entrySet())
        {
            // Get the start of the duration:
            double logStartInclusive = ((double)entry.getKey()) / this.base;
            double durationStartInclusive = Math.pow(this.base, logStartInclusive);

            // Get the end of the duration:
            double logEndExclusive = (entry.getKey() + 1d) / this.base;
            double durationEndExclusive = Math.pow(this.base, logEndExclusive);

            // Create the bin range:
            var binRange = new BinRange(durationStartInclusive, durationEndExclusive);

            // Get the count:
            long count = entry.getValue().get();

            // Consume the entry:
            consumer.accept(binRange, count);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        this.forEach(
            (binRange, count) ->
            {
                sb.append(String.format("%,15.2fns:,%,15.2fns:%,15d\n", binRange.startInclusive(), binRange.endExclusive(), count));
            }
        );
        return sb.toString();
    }

    /**
     * The bin range.
     * @param startInclusive The start of the bin range. Inclusive of this value.
     * @param endExclusive The end of the bin range. Exclusive of this value.
     */
    public record BinRange(double startInclusive, double endExclusive){}
}
