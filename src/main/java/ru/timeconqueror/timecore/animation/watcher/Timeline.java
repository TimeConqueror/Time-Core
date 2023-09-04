package ru.timeconqueror.timecore.animation.watcher;

import lombok.Getter;
import ru.timeconqueror.timecore.api.util.MathUtils;

public class Timeline {
    @Getter
    private final float speed;
    @Getter
    private final int length;
    @Getter
    private final boolean reversed;

    private final FreezableTime time;

    @Getter
    private final int animationStartTime;

    public Timeline(int length, float speed, boolean reversed, long startMillis, int animationStartTime) {
        time = new FreezableTime(startMillis);
        this.animationStartTime = animationStartTime;
        this.length = length;
        this.speed = speed;
        this.reversed = reversed;

    }

    public boolean isEnded(long systemTime) {
        return systemTime > time.get() + getElapsedLength();
    }

    /**
     * Represents the progress within the animation length.
     * Returns the progress time, which may vary from 0 to animation length.
     * Being reversed, it goes backwards.
     *
     * @see #getElapsedTime(long)
     */
    public int getAnimationTime(long systemTime) {
        long animationTime = Math.round((systemTime - time.get()) * speed);

        if (reversed) {
            animationTime = animationStartTime - animationTime;
        } else {
            animationTime += animationStartTime;
        }

        return (int) MathUtils.coerceInRange(animationTime, 0, length);
    }

    /**
     * Returns the amount of <b>real</b> time in milliseconds which is passed from the start of bound animation.
     *
     * @see #getAnimationTime(long)
     */
    public int getElapsedTime(long systemTime) {
        long elapsed = systemTime - time.get();

        return (int) MathUtils.coerceInRange(elapsed, 0, getElapsedLength());
    }

    public void freeze(FreezableTime.FreezeCause cause) {
        time.freeze(cause);
    }

    public void unfreeze(FreezableTime.FreezeCause cause) {
        time.unfreeze(cause);
    }

    public int getElapsedLength() {
        int length = reversed ? animationStartTime : this.length - animationStartTime;
        return speed != 0 ? Math.round(length / speed) : Integer.MAX_VALUE;
    }

    public void reset() {
        time.set(System.currentTimeMillis());
    }

    public void setFromElapsed(int elapsedTime) {
        this.time.set(System.currentTimeMillis() - elapsedTime);
    }
}
