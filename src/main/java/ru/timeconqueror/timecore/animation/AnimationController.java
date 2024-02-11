package ru.timeconqueror.timecore.animation;

import ru.timeconqueror.timecore.animation.watcher.AbstractAnimationTicker;

public interface AnimationController {
    default boolean startAnimation(AnimationData data, long clockTime) {
        return startAnimation(data, clockTime, AnimationCompanionData.EMPTY);
    }

    boolean startAnimation(AnimationData data, long clockTime, AnimationCompanionData companionData);

    void removeAnimation(long clockTime, int transitionTime);

    void setCurrentTicker(AbstractAnimationTicker ticker);
}
