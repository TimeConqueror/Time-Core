package ru.timeconqueror.timecore.api.auxiliary;

import ru.timeconqueror.timecore.TimeCore;

public class RandHelper {
    /**
     * Returns {@code a} with 50% chance otherwise return {@code b}
     */
    public static <T> T flipCoin(T a, T b) {
        return TimeCore.rand.nextBoolean() ? a : b;
    }

    /**
     * Returns {@code a} with {@code chance}% (from 0 to 100) otherwise return {@code b}
     */
    public static <T> T chance(int chance, T a, T b) {
        return TimeCore.rand.nextInt(100) < chance ? a : b;
    }
}
