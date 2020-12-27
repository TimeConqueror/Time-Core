package ru.timeconqueror.timecore.api.util;

public interface ThrowingRunnable<E extends Throwable> {
    void run() throws E;
}
