package ru.timeconqueror.timecore.api.util.holder;

public interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
}
