package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationStarter;

public interface TickerInfo {
    boolean isEmpty();

    boolean isTransition();

    AnimationStarter.AnimationData getAnimationData();
}
