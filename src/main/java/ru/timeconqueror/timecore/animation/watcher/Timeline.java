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

    public int getAnimationTime(long currentTime) {
        long elapsed = currentTime - time.get();
        if(reversed) {
            elapsed = length - elapsed;
        }

        return (int) MathUtils.coerceInRange(elapsed, 0, length);
    }

    public int getElapsedTime(long currentTime) {
        return speed != 0 ? Math.round(getAnimationTime(currentTime) * speed) : 0;
    }

    public void freeze(FreezableTime.FreezeCause cause) {
        time.freeze(cause);
    }

    public void unfreeze(FreezableTime.FreezeCause cause) {
        time.unfreeze(cause);
    }

    public int getLength() {
        return Math.round(speed != 0 ? Math.round(length / speed) : 0);
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
