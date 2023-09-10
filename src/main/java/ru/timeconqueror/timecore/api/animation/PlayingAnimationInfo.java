package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.AnimationData;
import ru.timeconqueror.timecore.animation.watcher.AnimationTickerImpl;

public interface PlayingAnimationInfo {
    /**
     * Returns extra information about provided animation, which is based on the {@link AnimationStarter} configuration.
     * This includes the calculation of real animation length (which could be modified by speed configuration f.e.) and other useful parameters.
     */
    static PlayingAnimationInfo of(AnimationStarter starter) {
        return new AnimationTickerImpl(starter.getData(), AnimationCompanionData.EMPTY);
    }

    int getAnimationLength();

    int getElapsedLength();

    boolean isReversed();

    AnimationData getAnimationData();
}
