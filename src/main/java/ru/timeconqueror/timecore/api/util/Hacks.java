package ru.timeconqueror.timecore.api.util;

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

    /**
     * Can upcast client-side only stuff to common classes without throwing {@link ClassNotFoundException}
     */
    public static <R, T extends R> R safeCast(T obj) {
        return obj;
    }
}
