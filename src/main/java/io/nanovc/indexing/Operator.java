package io.nanovc.indexing;

/**
 * This extracts the specific dimensional value from an item.
 * @param <T> The type of item that we want to perform an operation on.
 */
@FunctionalInterface
public interface Operator<T>
{
    /**
     * Extracts the specific dimensional value from an item.
     * @param left      The left (first) item that we want to perform an operation on.
     * @param right      The right (second) item that we want to perform an operation on.
     * @return The value after performing an operation.
     */
    public T performOperation(T left, T right);
}
