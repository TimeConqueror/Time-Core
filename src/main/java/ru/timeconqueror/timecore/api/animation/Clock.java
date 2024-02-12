package ru.timeconqueror.timecore.api.animation;

public interface Clock {
    /**
     * Return current time in milliseconds.
     */
    default long getMillis() {
        return getMillis(0);
    }

    long getMillis(float partialTick);
}
