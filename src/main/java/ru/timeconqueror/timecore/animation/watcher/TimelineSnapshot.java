package ru.timeconqueror.timecore.animation.watcher;

import ru.timeconqueror.timecore.animation.AnimationStarter;

public class TimelineSnapshot {
    private final long savedTimestamp;
    private final int savedAnimationTime;
    private final int savedElapsedTime;
    private TimelineSnapshot(int length, float speed, boolean reversed, long startTimestamp, long savedTimestamp) {
        Timeline timeline = new Timeline(length, speed, reversed, startTimestamp);
        this.savedTimestamp = savedTimestamp;
        this.savedAnimationTime = timeline.getAnimationTime(savedTimestamp);
        this.savedElapsedTime = timeline.getElapsedTime(savedTimestamp);
    }

    public static TimelineSnapshot createForStartTime(AnimationStarter.AnimationData data) {
        long time = System.currentTimeMillis();
        return new TimelineSnapshot(data.getAnimationLength(), data.getSpeed(), data.isReversed(), time, time);
    }

    public int getSavedAnimationTime() {
        return savedAnimationTime;
    }

    public int getSavedElapsedTime() {
        return savedElapsedTime;
    }

    public long getSavedTimestamp() {
        return savedTimestamp;
    }
}
