package ru.timeconqueror.timecore.animation.action;

import ru.timeconqueror.timecore.api.animation.AnimationTickerInfo;

public interface AnimationEventListener {
    default void onAnimationStarted(AnimationTickerInfo ticker) {

    }

    default void onAnimationStopped(AnimationTickerInfo ticker) {

    }

    default void onAnimationEnded(AnimationTickerInfo ticker) {

    }

    default void onAnimationRestarted(AnimationTickerInfo ticker) {

    }

    default void onAnimationUpdate(AnimationTickerInfo ticker) {

    }
}
