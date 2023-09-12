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

    private final FreezableTime startTime;

    @Getter
    private final int animationStartTime;

    public Timeline(int length, float speed, boolean reversed, long startMillis, int animationStartTime) {
        startTime = new FreezableTime(startMillis);
        this.animationStartTime = animationStartTime;
        this.length = length;
        this.speed = speed;
        this.reversed = reversed;

    }

    public boolean isEnded(long systemTime) {
        return systemTime > startTime.get(systemTime) + getElapsedLength();
    }

    /**
     * Represents the progress within the animation length.
     * Returns the progress time, which may vary from 0 to animation length.
     * Being reversed, it goes backwards.
     *
     * @see #getElapsedTime(long)
     */
    public int getAnimationTime(long systemTime) {
        long animationTime = Math.round((systemTime - startTime.get(systemTime)) * speed);

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
        long elapsed = systemTime - startTime.get(systemTime);

        return (int) MathUtils.coerceInRange(elapsed, 0, getElapsedLength());
    }

    public void freeze(FreezableTime.FreezeCause cause) {
        startTime.freeze(cause);
    }

    public void unfreeze(FreezableTime.FreezeCause cause) {
        startTime.unfreeze(cause);
    }

    public int getElapsedLength() {
        int length = reversed ? animationStartTime : this.length - animationStartTime;
        return speed != 0 ? Math.round(length / speed) : Integer.MAX_VALUE;
    }

    public void reset() {
        startTime.set(System.currentTimeMillis());
    }

    public void setFromElapsed(int elapsedTime) {
        this.startTime.set(System.currentTimeMillis() - elapsedTime);
    }
}
