package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationData;

public interface AnimationTickerInfo {
    default boolean isAnimationEnded() {
        return isAnimationEnded(System.currentTimeMillis());
    }

    boolean isAnimationEnded(long systemTime);

    boolean isEmpty();

    boolean isTransition();

    boolean isReversed();

    AnimationData getAnimationData();

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
    int getAnimationTime(long systemTime);

    int getAnimationLength();

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
    int getElapsedTime(long systemTime);

    int getElapsedLength();
}
