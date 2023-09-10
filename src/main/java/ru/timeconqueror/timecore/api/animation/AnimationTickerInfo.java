package ru.timeconqueror.timecore.api.animation;

public interface AnimationTickerInfo extends PlayingAnimationInfo {
    default boolean isAnimationEnded() {
        return isAnimationEnded(System.currentTimeMillis());
    }

    boolean isAnimationEnded(long systemTime);

    boolean isEmpty();

    boolean isTransition();

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
}
