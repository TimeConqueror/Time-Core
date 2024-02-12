package ru.timeconqueror.timecore.api.util.holder;

@FunctionalInterface
public interface ToFloatBiFunction<T, U> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    float applyAsFloat(T t, U u);
}