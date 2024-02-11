package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationData;

public interface PlayingAnimationInfo {
    int getAnimationLength();

    boolean isReversed();

    boolean isLooped();

    AnimationData getAnimationData();

    String print(long clockTime);
}
