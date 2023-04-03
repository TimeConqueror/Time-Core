package ru.timeconqueror.timecore.animation.util;

import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;
import ru.timeconqueror.timecore.api.util.Requirements;

import java.util.function.Predicate;

public class StandardDelayPredicates {
    public static Predicate<IAnimationWatcherInfo> onStart() {
        return watcher -> true;
    }

    public static Predicate<IAnimationWatcherInfo> onEnd() {
        return watcher -> watcher.getElapsedTime() == watcher.getLength();
    }

    /**
     * Predicate which will trigger when the animation watcher passes provided <b>animation</b> time.
     * Animation time means the frame time for non sped up animation.
     */
    public static Predicate<IAnimationWatcherInfo> whenPassed(int animationTime) {
        return info -> info.getAnimationTime() >= animationTime;
    }

    public static Predicate<IAnimationWatcherInfo> whenPassed(float percents) {
        Requirements.inRangeInclusive(percents, 0, 1);

        return info -> {
            float length = info.getLength();
            float elapsed = info.getElapsedTime();

            return elapsed >= length * percents;
        };
    }
}
