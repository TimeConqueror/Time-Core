package ru.timeconqueror.timecore.api.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.watcher.EmptyAnimationWatcherInfo;

public interface IAnimationWatcherInfo {
    IAnimationWatcherInfo EMPTY = new EmptyAnimationWatcherInfo();

    /**
     * Returns how much <b>real</b> time has been passed already for bound animation (in milliseconds).
     * If you need to get animation frame time, use {@link #getAnimationTime()}
     */
    default int getElapsedTime() {
        return getElapsedTime(System.currentTimeMillis());
    }

    /**
     * Returns how much <b>real</b> time has been passed already for bound animation (in milliseconds).
     * If you need to get animation frame time, use {@link #getAnimationTime()}
     */
    int getElapsedTime(long time);

    /**
     * Returns current animation time to be used in animation frame calculation.
     * Animation time means the frame time for non <b>non sped up</b> animation.
     * If you need how much real time (in milliseconds) has been passed already, use {@link #getElapsedTime()}
     */
    default int getAnimationTime() {
        return getAnimationTime(System.currentTimeMillis());
    }

    /**
     * Returns current animation time to be used in animation frame calculation.
     * Animation time means the frame time for non <b>non sped up</b> animation.
     * If you need how much real time (in milliseconds) has been passed already, use {@link #getElapsedTime()}
     */
    int getAnimationTime(long time);

    /**
     * Returns the real length of the animation basing on given speed.
     */
    int getLength();

    LoopMode getLoopMode();

    float getSpeed();

    boolean isReversed();

    Animation getAnimation();

    @Nullable
    AnimationStarter.AnimationData getNextAnimation();

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
