package ru.timeconqueror.timecore.api.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;

import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectUtils {
    @Contract("null,_ -> false")
    public static boolean checkIfNotNull(Object object, String errorMessage) {
        if (object == null) {
            TimeCore.LOGGER.error(errorMessage, new NullPointerException());
            return false;
        }

        return true;
    }

    /**
     * Converts object to string. Can handle nulls.
     */
    public static <T> String toString(@Nullable T obj, Function<T, String> converter) {
        return obj != null ? converter.apply(obj) : "null";
    }

    public static <E extends Throwable> void runWithCatching(Class<E> exceptionToHandle, ThrowingRunnable<E> runnable) {
        runWithCatching(exceptionToHandle, runnable, Throwable::printStackTrace);
    }

    public static <E extends Throwable> void runWithCatching(Class<E> exceptionToHandle, ThrowingRunnable<E> runnable, Consumer<E> onError) {
        try {
            runnable.run();
        } catch (Throwable e) {
            if (exceptionToHandle.isInstance(e)) {
                onError.accept((E) e);
            } else throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T, E extends Throwable> T getWithCatching(Class<E> exceptionToHandle, ThrowingSupplier<T, E> supplier) {
        return getWithCatching(exceptionToHandle, supplier, (T) null);
    }

    public static <T, E extends Throwable> T getWithCatching(Class<E> exceptionToHandle, ThrowingSupplier<T, E> supplier, T valueOnError) {
        return getWithCatching(exceptionToHandle, supplier, (Function<E, T>) e -> {
            e.printStackTrace();
            return valueOnError;
        });
    }

    public static <T, E extends Throwable> T getWithCatching(Class<E> exceptionToHandle, ThrowingSupplier<T, E> supplier, Function<E, T> onError) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            if (exceptionToHandle.isInstance(e)) {
                return onError.apply((E) e);
            } else throw new RuntimeException(e);
        }
    }
}
