package ru.timeconqueror.timecore.animation.watcher;

import lombok.Getter;
import ru.timeconqueror.timecore.animation.AnimationStarter;

@Getter
public class TimelineSnapshot {
    private final long timestamp;
    private final int animationTime;
    private final int elapsedTime;

    private TimelineSnapshot(int length, float speed, boolean reversed, long startTimestamp, long timestamp, int animationTimeStartFrom) {
        Timeline timeline = new Timeline(length, speed, reversed, startTimestamp, animationTimeStartFrom);
        this.timestamp = timestamp;
        this.animationTime = timeline.getAnimationTime(timestamp);
        this.elapsedTime = timeline.getElapsedTime(timestamp);
    }

    public static TimelineSnapshot createForStartTime(AnimationStarter.AnimationData data) {
        long time = System.currentTimeMillis();
        return new TimelineSnapshot(data.getAnimationLength(), data.getSpeed(), data.isReversed(), time, time, data.getStartAnimationTime());
    }
}
