package ru.timeconqueror.timecore.util;

public interface ThrowingRunnable<E extends Throwable> {
    void run() throws E;
}
