package ru.timeconqueror.timecore.animation.util;

import ru.timeconqueror.timecore.api.animation.IAnimationInfo;
import ru.timeconqueror.timecore.api.util.Requirements;

import java.util.function.Predicate;

public class StandardDelayPredicates {
    public static Predicate<IAnimationInfo> onStart() {
        return watcher -> true;
    }

    public static Predicate<IAnimationInfo> onEnd() {
        return watcher -> watcher.getExistingTime() == watcher.getLength();
    }

    public static Predicate<IAnimationInfo> whenPassed(int animationTime) {
        return info -> info.getExistingTime() >= animationTime;
    }

    public static Predicate<IAnimationInfo> whenPassed(float percents) {
        Requirements.inRangeInclusive(percents, 0, 1);

        return info -> {
            float length = info.getLength();
            float existingTime = info.getExistingTime();

            return existingTime >= length * percents;
        };
    }
}
