package ru.timeconqueror.timecore.animation.action;

import ru.timeconqueror.timecore.api.animation.AnimationTicker;

public interface AnimationEventListener {
    default void onAnimationStarted(AnimationTicker ticker) {

    }

    default void onAnimationStopped(AnimationTicker ticker) {

    }

    default void onAnimationUpdate(AnimationTicker ticker, long clockTime) {

    }
}
