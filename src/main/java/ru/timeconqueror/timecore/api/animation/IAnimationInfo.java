package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.watcher.EmptyAnimationInfo;

public interface IAnimationInfo {
    IAnimationInfo EMPTY = new EmptyAnimationInfo();

    int getExistingTime();

    int getLength();

    Animation getAnimation();

    /**
     * Returns true if this is a transition which was calculated automatically.
     * (covers all cases except {@link AnimationStarter#setNextAnimation(AnimationStarter)})
     */
    boolean isAutoTransition();

    /**
     * Returns true if there's no animation played.
     */
    boolean isNull();

    /**
     * Returns true if this is an automatic transition, and it leads to provided animation.
     */
    boolean autoTransitsTo(Animation animation);

    /**
     * Returns true if this is an automatic transition, and it goes from provided animation.
     */
    boolean autoTransitsFrom(Animation animation);
}
