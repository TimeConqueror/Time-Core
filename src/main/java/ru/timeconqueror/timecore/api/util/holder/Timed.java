package ru.timeconqueror.timecore.api.util.holder;

import java.util.function.Supplier;

public abstract class Timed<T> implements Supplier<T> {
    private final T value;

    protected Timed(T value) {
        this.value = value;
    }

    public static <T> Timed<T> of(T value, int endTime) {
        return new TimedImpl<>(value, endTime);
    }

    public static <T> Timed<T> of(T value, int startTime, int period) {
        return new TimedImpl<>(value, startTime + period);
    }

    public static <T> Timed<T> infinite(T value) {
        return new Infinite<>(value);
    }

    @Override
    public T get() {
        return value;
    }

    public abstract boolean isEnded(int currentTime);
    public abstract boolean isInfinite();

    public static class TimedImpl<T> extends Timed<T> {
        private final int endTime;

        private TimedImpl(T value, int endTime) {
            super(value);
            this.endTime = endTime;
        }

        public boolean isEnded(int currentTime) {
            return currentTime > endTime;
        }

        @Override
        public boolean isInfinite() {
            return false;
        }
    }

    public static class Infinite<T> extends Timed<T> {
        private Infinite(T value) {
            super(value);
        }

        @Override
        public boolean isEnded(int currentTime) {
            return false;
        }

        @Override
        public boolean isInfinite() {
            return true;
        }
    }
}
