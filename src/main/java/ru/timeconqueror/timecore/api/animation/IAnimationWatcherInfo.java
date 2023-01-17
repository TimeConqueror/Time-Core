package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.watcher.EmptyAnimationWatcherInfo;

public interface IAnimationWatcherInfo {
    IAnimationWatcherInfo EMPTY = new EmptyAnimationWatcherInfo();

    /**
     * Returns how much real time has been passed already for bound animation (in milliseconds).
     * If you need to get animation frame time, use {@link #getCurrentAnimationTime()}
     */
    int getExistingTime(long currentMillis);

    /**
     * Returns how much real time has been passed already for bound animation (in milliseconds).
     * If you need to get animation frame time, use {@link #getCurrentAnimationTime()}
     */
    default int getExistingTime() {
        return getExistingTime(System.currentTimeMillis());
    }

    /**
     * Returns current animation time to be used in animation frame calculation.
     * Animation time means the frame time for non sped up animation.
     * If you need how much real time (in milliseconds) has been passed already, use {@link #getExistingTime()}
     */
    int getCurrentAnimationTime(long currentMillis);

    /**
     * Returns current animation time to be used in animation frame calculation.
     * Animation time means the frame time for non sped up animation.
     * If you need how much real time (in milliseconds) has been passed already, use {@link #getExistingTime()}
     */
    default int getCurrentAnimationTime() {
        return getCurrentAnimationTime(System.currentTimeMillis());
    }

    int getLength();

    float getSpeed();

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
