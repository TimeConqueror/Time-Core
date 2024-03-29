package ru.timeconqueror.timecore.api.util;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;

public class MathUtils {
    /**
     * Coerces number in provided range.
     *
     * @param number number to coerce
     * @param min    minimum value, inclusive.
     * @param max    maximum value, inclusive.
     */
    public static int coerceInRange(int number, int min, int max) {
        return Math.min(Math.max(number, min), max);
    }

    /**
     * Coerces number in provided range.
     *
     * @param number number to coerce
     * @param min    minimum value, inclusive.
     * @param max    maximum value, inclusive.
     */
    public static float coerceInRange(float number, float min, float max) {
        return Math.min(Math.max(number, min), max);
    }

    /**
     * Coerces number in provided range.
     *
     * @param number number to coerce
     * @param min    minimum value, inclusive.
     * @param max    maximum value, inclusive.
     */
    public static long coerceInRange(long number, long min, long max) {
        return Math.min(Math.max(number, min), max);
    }

    /**
     * Coerces number in provided range.
     *
     * @param number number to coerce
     * @param min    minimum value, inclusive.
     * @param max    maximum value, inclusive.
     */
    public static double coerceInRange(double number, double min, double max) {
        return Math.min(Math.max(number, min), max);
    }

    /**
     * Calculates the value, that represents the part ({@code percentage}) of the {@code [start-to-end]} range, counting from the {@code start}.
     * Also works if {@code end} is less then {@code start}
     *
     * @param factor percentage value
     * @param start  start point, can be also more than end
     * @param end    end point, can be also less than end
     */
    public static float lerp(float factor, float start, float end) {
        return start + factor * (end - start);
    }

    /**
     * Calculates the value, that represents the part ({@code percentage}) of the {@code [start-to-end]} range, counting from the {@code start}.
     * Also works if {@code end} is less then {@code start}
     *
     * @param factor percentage value
     * @param start  start point, can be also more than end
     * @param end    end point, can be also less than end
     */
    public static double lerp(double factor, double start, double end) {
        return start + factor * (end - start);
    }

    /**
     * Returns the percentage value (from 0 to 1), which represents, what percentage of the {@code [start-to-end]} range {@code current} number takes.
     *
     * @param current number, that is in {@code [start-to-end]} range.
     * @param start   start value (0)
     * @param end     end value (1)
     */
    public static float percentage(float current, float start, float end) {
        return end - start != 0 ? (current - start) / (end - start) : 1;
    }

    /**
     * Returns the percentage value (from 0 to 1), which represents, what percentage of the {@code [start-to-end]} range {@code current} number takes.
     *
     * @param current number, that is in {@code [start-to-end]} range.
     * @param start   start value (0)
     * @param end     end value (1)
     */
    public static double percentage(double current, double start, double end) {
        return end - start != 0 ? (current - start) / (end - start) : 1;
    }

    /**
     * Returns the difference between two numbers.
     */
    public static double difference(double number1, double number2) {
        return Math.abs(number1 - number2);
    }

    /**
     * Returns the arithmetic mean from provided values.
     */
    public static int average(int... vals) {
        if (vals.length == 0) return 0;

        int s = 0;

        for (int val : vals) {
            s += val;
        }

        return s / vals.length;
    }

    public static int min(int... vals) {
        if (vals.length == 0) throw new IllegalArgumentException("Can't get minimum value from zero-sized array.");

        int min = Integer.MAX_VALUE;

        for (int val : vals) {
            if (val < min) {
                min = val;
            }
        }

        return min;
    }

    public static int max(int... vals) {
        if (vals.length == 0) throw new IllegalArgumentException("Can't get maximum value from zero-sized array.");

        int max = Integer.MIN_VALUE;

        for (int val : vals) {
            if (val > max) {
                max = val;
            }
        }

        return max;
    }

    public static float min(float... vals) {
        if (vals.length == 0) throw new IllegalArgumentException("Can't get minimum value from zero-sized array.");

        float min = Float.MAX_VALUE;

        for (float val : vals) {
            min = Math.min(min, val);
        }

        return min;
    }

    public static float max(float... vals) {
        if (vals.length == 0) throw new IllegalArgumentException("Can't get maximum value from zero-sized array.");

        float max = Float.MIN_VALUE;

        for (float val : vals) {
            max = Math.max(max, val);
        }

        return max;
    }

    public static double min(double... vals) {
        if (vals.length == 0) throw new IllegalArgumentException("Can't get minimum value from zero-sized array.");

        double min = Double.MAX_VALUE;

        for (double val : vals) {
            min = Math.min(min, val);
        }

        return min;
    }

    public static double max(double... vals) {
        if (vals.length == 0) throw new IllegalArgumentException("Can't get maximum value from zero-sized array.");

        double max = Double.MIN_VALUE;

        for (double val : vals) {
            max = Math.max(max, val);
        }

        return max;
    }

    public static double distSqr(Vec3i vec, Vec3i vec2) {
        return vec.distToCenterSqr(vec2.getX(), vec2.getY(), vec2.getZ());
    }

    public static double distSqr(Vec3i vec, Entity entity) {
        return vec.distToCenterSqr(entity.getX(), entity.getY(), entity.getZ());
    }

    public static float toRadians(float degrees) {
        return (float) Math.toRadians(degrees);
    }

    public static float toDegrees(float radians) {
        return (float) Math.toDegrees(radians);
    }

    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    public static boolean almostEqual(float f1, float f2) {
        return Math.abs(f1 - f2) < 0.001F;
    }

    public static boolean almostEqual(double d1, double d2) {
        return Math.abs(d1 - d2) < 0.001D;
    }

    public static class OutwardSquareSpiral {
        @SuppressWarnings("DuplicateExpressions")
        public static int indexByOffset(Vec2i offset) {
            //From SO, https://stackoverflow.com/a/9971465
            long x = offset.x();
            long y = offset.y();

            long i;

            if (y * y >= x * x) {
                i = Math.subtractExact(Math.subtractExact(Math.multiplyExact(4, y * y), y), x);
                if (y < x) {
                    i = Math.subtractExact(i, Math.multiplyExact(2L, Math.subtractExact(y, x)));
                }
            } else {
                i = Math.subtractExact(Math.subtractExact(Math.multiplyExact(4, x * x), y), x);
                if (y < x) {
                    i = Math.addExact(i, Math.multiplyExact(2L, Math.subtractExact(y, x)));
                }
            }

            return Math.toIntExact(i);
        }

        public static int softIndexByOffset(Vec2i offset) {
            try {
                return indexByOffset(offset);
            } catch (ArithmeticException e) {
                return -1;
            }
        }

        public static Vec2i offsetByIndex(int index) {
            Requirements.greaterOrEquals(index, 0);
            //based on https://stackoverflow.com/a/11551316

            if (index == 0) {
                return new Vec2i(0, 0);
            }

            int c = cycle(index);
            int s = sector(index);
            int offset = index - first(c) - Math.floorDiv(s * length(c), 4);
            // s (sector) also controls the side where spiral will expand on next cycle
            // so changing index-to-side relation will change the side of expanding
            if (s == 0) {
                return new Vec2i(c, -c + offset + 1);
            } else if (s == 1) {
                return new Vec2i(c - offset - 1, c);
            } else if (s == 2) {
                return new Vec2i(-c, c - offset - 1);
            } else {
                return new Vec2i(-c + offset + 1, -c);
            }
        }

        private static int first(int cycle) {
            int x = 2 * cycle - 1;
            return x * x;
        }

        /**
         * distance from center
         */
        private static int cycle(int index) {
            return Math.floorDiv(((int) Math.sqrt(index) + 1), 2);
        }

        private static int length(int cycle) {
            return 8 * cycle;
        }

        /**
         * (north, east, south or west)
         */
        private static int sector(int index) {
            int c = cycle(index);
            int offset = index - first(c);
            int n = length(c);
            return 4 * offset / n;
        }
    }
}
