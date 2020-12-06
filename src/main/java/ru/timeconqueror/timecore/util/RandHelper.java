package ru.timeconqueror.timecore.util;

import java.util.Random;

public class RandHelper {
    public static final Random RAND = new Random();

    /**
     * Returns {@code a} with 50% chance otherwise return {@code b}
     * Uses built-in random instance.
     */
    public static <T> T flipCoin(T a, T b) {
        return flipCoin(RAND, a, b);
    }

    /**
     * Returns {@code a} with 50% chance otherwise return {@code b}
     */
    public static <T> T flipCoin(Random random, T a, T b) {
        return random.nextBoolean() ? a : b;
    }

    /**
     * Returns {@code a} with {@code chance}% (from 0 to 100) otherwise return {@code b}
     * Uses built-in random instance.
     */
    public static <T> T chance(int chance, T a, T b) {
        return chance(RAND, chance, a, b);
    }

    /**
     * Returns {@code a} with {@code chance}% (from 0 to 100) otherwise return {@code b}
     */
    public static <T> T chance(Random random, int chance, T a, T b) {
        return random.nextInt(100) < chance ? a : b;
    }

    /**
     * Chooses with equal probability.
     */
    @SafeVarargs
    public static <T> T chooseEqual(Random r, T... items) {
        return items[r.nextInt(items.length)];
    }

    /**
     * Chooses with equal probability.
     * Uses built-in random instance.
     */
    @SafeVarargs
    public static <T> T chooseEqual(T... items) {
        return chooseEqual(RAND, items);
    }
}
