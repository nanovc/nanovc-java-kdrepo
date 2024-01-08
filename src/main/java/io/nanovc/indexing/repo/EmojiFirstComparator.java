package io.nanovc.indexing.repo;

import java.util.Comparator;
import java.util.Objects;

/**
 * This sorts strings that start with emojis (actually, any character in the supplimentary unicode character plane) as first.
 */
public class EmojiFirstComparator implements Comparator<String>
{
    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * <p>
     * The implementor must ensure that {@link Integer#signum
     * signum}{@code (compare(x, y)) == -signum(compare(y, x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * compare(x, y)} must throw an exception if and only if {@code
     * compare(y, x)} throws an exception.)<p>
     * <p>
     * The implementor must also ensure that the relation is transitive:
     * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
     * {@code compare(x, z)>0}.<p>
     * <p>
     * Finally, the implementor must ensure that {@code compare(x,
     * y)==0} implies that {@code signum(compare(x,
     * z))==signum(compare(y, z))} for all {@code z}.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *     first argument is less than, equal to, or greater than the
     *     second.
     * @throws NullPointerException if an argument is null and this
     *                              comparator does not permit null arguments
     * @throws ClassCastException   if the arguments' types prevent them from
     *                              being compared by this comparator.
     * @apiNote It is generally the case, but <i>not</i> strictly required that
     *     {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
     *     any comparator that violates this condition should clearly indicate
     *     this fact.  The recommended language is "Note: this comparator
     *     imposes orderings that are inconsistent with equals."
     */
    @Override public int compare(String o1, String o2)
    {
        return compareTo(o1, o2);
    }

    /**
     * Makes sure that emojis are always before other strings.
     *
     * @param o1 The first string to compare.
     * @param o2 The second string to compare.
     * @return a negative integer, zero, or a positive integer as the
     *     first argument is less than, equal to, or greater than the
     *     second.
     */
    public static int compareTo(String o1, String o2)
    {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);

        // Make sure both strings have lengths:
        if (o1.length() == 0 || o2.length() == 0) return o1.compareTo(o2);

        // Check whether the first character of the strings are emoji's:
        char o1Char = o1.charAt(0);
        char o2Char = o2.charAt(0);
        boolean o1StartsWithEmoji = Character.isSurrogate(o1Char);
        boolean o2StartsWithEmoji = Character.isSurrogate(o2Char);

        // Give preference to the emoji's:
        if (o1StartsWithEmoji)
        {
            // O1 starts with an emoji.
            if (o2StartsWithEmoji)
            {
                // O1 and O2 starts with emojis.
                // Use default sorting:
                return o1.compareTo(o2);
            }
            else
            {
                // o1 starts with an emoji but o2 doesn't.
                // o1 is less than o2
                return -1;
            }
        }
        else
        {
            // O1 doesn't start with an emoji.
            if (o2StartsWithEmoji)
            {
                // o2 starts with an emoji but o1 doesn't.
                // o1 is greater than o1
                return 1;
            }
            else
            {
                // Both don't start with an emoji.
                return o1.compareTo(o2);
            }
        }
    }
}
