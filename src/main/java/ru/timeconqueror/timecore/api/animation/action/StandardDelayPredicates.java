package ru.timeconqueror.timecore.api.animation.action;

import ru.timeconqueror.timecore.api.animation.AnimationTickerInfo;
import ru.timeconqueror.timecore.api.util.Requirements;

import java.util.function.Predicate;

public class StandardDelayPredicates {
    public static Predicate<AnimationTickerInfo> onEnd() {
        return AnimationTickerInfo::isAnimationEnded;
    }

    public static Predicate<AnimationTickerInfo> onStart() {
        return StandardDelayPredicates.whenPassesAnimationTime(0);
    }

    /**
     * Predicate which will trigger when the animation watcher passes provided <b>animation</b> time.
     * Animation time means the frame time for non sped up animation.
     */
    public static Predicate<AnimationTickerInfo> whenPassesAnimationTime(int animationTime) {
        return ticker -> {
            if (ticker.isReversed()) {
                return ticker.getAnimationTime() <= animationTime;
            } else {
                return ticker.getAnimationTime() >= animationTime;
            }
        };
    }

    public static Predicate<AnimationTickerInfo> whenPassesPercents(float percents) {
        Requirements.inRangeInclusive(percents, 0, 1);

        return ticker -> {
            float length = ticker.getElapsedLength();
            float elapsed = ticker.getElapsedTime();

            if (ticker.isReversed()) {
                return elapsed <= length * percents;
            } else {
                return elapsed >= length * percents;
            }
        };
    }
}
