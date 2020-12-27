package ru.timeconqueror.timecore.api.util;

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
     *
     * @throws IllegalArgumentException if provided chance is more than 100.
     */
    public static <T> T chance(int chance, T a, T b) {
        return chance(RAND, chance, a, b);
    }

    /**
     * Returns {@code a} with {@code chance}% (from 0 to 100) otherwise return {@code b}
     *
     * @throws IllegalArgumentException if provided chance is more than 100.
     */
    public static <T> T chance(Random random, int chance, T a, T b) {
        return chance(random, chance) ? a : b;
    }

    /**
     * Returns true with {@code chance}% (from 0 to 100).
     * Uses built-in random instance.
     *
     * @throws IllegalArgumentException if provided chance is more than 100.
     */
    public static boolean chance(int chance) {
        return chance(RAND, chance);
    }

    /**
     * Returns true with {@code chance}% (from 0 to 100).
     *
     * @throws IllegalArgumentException if provided chance is more than 100.
     */
    public static boolean chance(Random random, int chance) {
        if (chance > 100)
            throw new IllegalArgumentException("Chance shouldn't be greater than 100. Provided: " + chance);

        return random.nextInt(100) < chance;
    }

    /**
     * Chooses with equal probability.
     */
    @SafeVarargs
    public static <T> T chooseEqually(Random r, T... items) {
        return items[r.nextInt(items.length)];
    }

    /**
     * Chooses with equal probability.
     * Uses built-in random instance.
     */
    @SafeVarargs
    public static <T> T chooseEqually(T... items) {
        return chooseEqually(RAND, items);
    }
}
