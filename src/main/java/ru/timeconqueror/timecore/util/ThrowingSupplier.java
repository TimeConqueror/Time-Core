package ru.timeconqueror.timecore.util;

public interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
}
