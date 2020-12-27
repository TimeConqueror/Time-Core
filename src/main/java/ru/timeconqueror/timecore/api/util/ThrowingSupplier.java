package ru.timeconqueror.timecore.api.util;

public interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
}
