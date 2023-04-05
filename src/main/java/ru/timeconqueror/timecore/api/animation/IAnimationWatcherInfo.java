package ru.timeconqueror.timecore.api.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.watcher.EmptyAnimationWatcherInfo;

public interface IAnimationWatcherInfo {
    IAnimationWatcherInfo EMPTY = new EmptyAnimationWatcherInfo();

    /**
     * Returns the amount of <b>real</b> time in milliseconds which is passed from the start of bound animation.
     *
     * @see #getAnimationTime(long)
     */
    default int getElapsedTime() {
        return getElapsedTime(System.currentTimeMillis());
    }

    /**
     * Returns the amount of <b>real</b> time in milliseconds which is passed from the start of bound animation.
     *
     * @see #getAnimationTime(long)
     */
    int getElapsedTime(long time);

    /**
     * Represents the progress within the animation length.
     * Returns the progress time, which may vary from 0 to animation length.
     * Being reversed, it goes backwards.
     *
     * @see #getElapsedTime(long)
     */
    default int getAnimationTime() {
        return getAnimationTime(System.currentTimeMillis());
    }

    /**
     * Represents the progress within the animation length.
     * Returns the progress time, which may vary from 0 to animation length.
     * Being reversed, it goes backwards.
     *
     * @see #getElapsedTime(long)
     */
    int getAnimationTime(long time);

    /**
     * Returns the real length of the animation in milliseconds basing on given speed.
     */
    int getElapsedLength();

    LoopMode getLoopMode();

    float getSpeed();

    boolean isReversed();

    Animation getAnimation();

    @Nullable
    AnimationStarter.AnimationData getNextAnimation();

    /**
     * Returns true if this is a transition which was calculated automatically.
     * (covers all cases except {@link AnimationStarter#withNextAnimation(AnimationStarter)})
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
