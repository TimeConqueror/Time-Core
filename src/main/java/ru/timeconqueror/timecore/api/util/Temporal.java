package ru.timeconqueror.timecore.api.util;

import java.util.function.Consumer;

/**
 * Represents the wrapper for value, which will eventually be removed, when it becomes useless.
 * Will throw error, if somebody try to get its value, when it is removed.
 */
public class Temporal<T> {
    private static final String DEFAULT_ERROR = "Called too late. Value have already been removed.";

    private T value;
    private final String error;
    private boolean removed;

    public static <T> Temporal<T> of(T value) {
        return new Temporal<>(value, DEFAULT_ERROR);
    }

    public static <T> Temporal<T> of(T value, Class<T> clazz) {
        return new Temporal<>(value, "Called too late. " + clazz.getSimpleName() + " have already removed from Temporal.");
    }

    public static <T> Temporal<T> of(T value, String errorMessage) {
        return new Temporal<>(value, errorMessage);
    }

    protected Temporal(T value, String errorMessage) {
        this.value = value;
        this.error = errorMessage;
    }

    public T get() {
        if (!isPresent()) {
            throw new IllegalStateException(error);
        }

        return value;
    }

    public T remove() {
        if (!isPresent()) {
            throw new IllegalStateException("'remove' method called for already removed value.");
        }

        T val = value;
        value = null;
        removed = true;
        return val;
    }

    public boolean isPresent() {
        return !removed;
    }

    public void doAndRemove(Consumer<T> action) {
        if (isPresent()) {
            action.accept(remove());
        }
    }

    public Temporal<T> ifPresent(Consumer<T> action) {
        if (isPresent()) {
            action.accept(value);
        }

        return this;
    }

    public void orThrow() {
        throw new IllegalStateException(error);
    }

    public void doOrThrow(Consumer<T> action) {
        ifPresent(action).orThrow();
    }
}
