package ru.timeconqueror.timecore.animation.action;

import ru.timeconqueror.timecore.api.animation.AnimationTicker;

public interface AnimationEventListener {
    default void onAnimationStarted(String layerName, AnimationTicker ticker) {

    }

    default void onAnimationStopped(String layerName, AnimationTicker ticker) {

    }

    default void onAnimationUpdate(String layerName, AnimationTicker ticker, long clockTime) {

    }
}
