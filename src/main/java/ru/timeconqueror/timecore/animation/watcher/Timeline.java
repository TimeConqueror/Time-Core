package ru.timeconqueror.timecore.animation.watcher;

import ru.timeconqueror.timecore.api.util.MathUtils;

public class Timeline {
    private final float speed;
    private final int length;
    private final boolean reversed;

    private final FreezableTime time;

    public Timeline(int length, float speed, boolean reversed, long startTime) {
        time = new FreezableTime(startTime);
        this.length = length;
        this.speed = speed;
        this.reversed = reversed;
    }

    public boolean isEnded(long currentTime) {
        return currentTime > time.get() + length;
    }

    /**
     * Represents the progress within the animation length.
     * Returns the progress time, which may vary from 0 to animation length.
     * Being reversed, it goes backwards.
     *
     * @see #getElapsedTime(long)
     */
    public int getAnimationTime(long currentTime) {
        long animationTime = Math.round((currentTime - time.get()) * speed);
        if (reversed) {
            animationTime = length - animationTime;
        }

        return (int) MathUtils.coerceInRange(animationTime, 0, length);
    }

    /**
     * Returns the amount of <b>real</b> time in milliseconds which is passed from the start of bound animation.
     *
     * @see #getAnimationTime(long)
     */
    public int getElapsedTime(long currentTime) {
        long elapsed = currentTime - time.get();

        return (int) MathUtils.coerceInRange(elapsed, 0, getElapsedLength());
    }

    public void freeze(FreezableTime.FreezeCause cause) {
        time.freeze(cause);
    }

    public void unfreeze(FreezableTime.FreezeCause cause) {
        time.unfreeze(cause);
    }

    public int getElapsedLength() {
        return speed != 0 ? Math.round(length / speed) : 0;
    }

    public void reset() {
        time.set(System.currentTimeMillis());
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void set(long time) {
        this.time.set(time);
    }
}
