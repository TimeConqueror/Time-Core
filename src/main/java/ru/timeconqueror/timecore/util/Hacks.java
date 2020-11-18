package ru.timeconqueror.timecore.util;

import javax.annotation.Nonnull;

public class Hacks {
    /**
     * Can be used on fields, that will be initialized later, via reflection.
     * It just promises IDE, that it won't be null
     */
    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public static <T> T promise() {
        return null;
    }

    public static <R, T extends R> R bypassClassChecking(T obj) {
        return obj;
    }
}
