package ru.timeconqueror.timecore.api.util.holder;

public interface ThrowingRunnable<E extends Throwable> {
    void run() throws E;
}
